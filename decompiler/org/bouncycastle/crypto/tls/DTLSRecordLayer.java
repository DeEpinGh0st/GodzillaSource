package org.bouncycastle.crypto.tls;

import java.io.IOException;

class DTLSRecordLayer implements DatagramTransport {
  private static final int RECORD_HEADER_LENGTH = 13;
  
  private static final int MAX_FRAGMENT_LENGTH = 16384;
  
  private static final long TCP_MSL = 120000L;
  
  private static final long RETRANSMIT_TIMEOUT = 240000L;
  
  private final DatagramTransport transport;
  
  private final TlsContext context;
  
  private final TlsPeer peer;
  
  private final ByteQueue recordQueue = new ByteQueue();
  
  private volatile boolean closed = false;
  
  private volatile boolean failed = false;
  
  private volatile ProtocolVersion readVersion = null;
  
  private volatile ProtocolVersion writeVersion = null;
  
  private volatile boolean inHandshake;
  
  private volatile int plaintextLimit;
  
  private DTLSEpoch currentEpoch;
  
  private DTLSEpoch pendingEpoch;
  
  private DTLSEpoch readEpoch;
  
  private DTLSEpoch writeEpoch;
  
  private DTLSHandshakeRetransmit retransmit = null;
  
  private DTLSEpoch retransmitEpoch = null;
  
  private long retransmitExpiry = 0L;
  
  DTLSRecordLayer(DatagramTransport paramDatagramTransport, TlsContext paramTlsContext, TlsPeer paramTlsPeer, short paramShort) {
    this.transport = paramDatagramTransport;
    this.context = paramTlsContext;
    this.peer = paramTlsPeer;
    this.inHandshake = true;
    this.currentEpoch = new DTLSEpoch(0, new TlsNullCipher(paramTlsContext));
    this.pendingEpoch = null;
    this.readEpoch = this.currentEpoch;
    this.writeEpoch = this.currentEpoch;
    setPlaintextLimit(16384);
  }
  
  void setPlaintextLimit(int paramInt) {
    this.plaintextLimit = paramInt;
  }
  
  int getReadEpoch() {
    return this.readEpoch.getEpoch();
  }
  
  ProtocolVersion getReadVersion() {
    return this.readVersion;
  }
  
  void setReadVersion(ProtocolVersion paramProtocolVersion) {
    this.readVersion = paramProtocolVersion;
  }
  
  void setWriteVersion(ProtocolVersion paramProtocolVersion) {
    this.writeVersion = paramProtocolVersion;
  }
  
  void initPendingEpoch(TlsCipher paramTlsCipher) {
    if (this.pendingEpoch != null)
      throw new IllegalStateException(); 
    this.pendingEpoch = new DTLSEpoch(this.writeEpoch.getEpoch() + 1, paramTlsCipher);
  }
  
  void handshakeSuccessful(DTLSHandshakeRetransmit paramDTLSHandshakeRetransmit) {
    if (this.readEpoch == this.currentEpoch || this.writeEpoch == this.currentEpoch)
      throw new IllegalStateException(); 
    if (paramDTLSHandshakeRetransmit != null) {
      this.retransmit = paramDTLSHandshakeRetransmit;
      this.retransmitEpoch = this.currentEpoch;
      this.retransmitExpiry = System.currentTimeMillis() + 240000L;
    } 
    this.inHandshake = false;
    this.currentEpoch = this.pendingEpoch;
    this.pendingEpoch = null;
  }
  
  void resetWriteEpoch() {
    if (this.retransmitEpoch != null) {
      this.writeEpoch = this.retransmitEpoch;
    } else {
      this.writeEpoch = this.currentEpoch;
    } 
  }
  
  public int getReceiveLimit() throws IOException {
    return Math.min(this.plaintextLimit, this.readEpoch.getCipher().getPlaintextLimit(this.transport.getReceiveLimit() - 13));
  }
  
  public int getSendLimit() throws IOException {
    return Math.min(this.plaintextLimit, this.writeEpoch.getCipher().getPlaintextLimit(this.transport.getSendLimit() - 13));
  }
  
  public int receive(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    byte[] arrayOfByte = null;
    while (true) {
      int i = Math.min(paramInt2, getReceiveLimit()) + 13;
      if (arrayOfByte == null || arrayOfByte.length < i)
        arrayOfByte = new byte[i]; 
      try {
        byte b;
        if (this.retransmit != null && System.currentTimeMillis() > this.retransmitExpiry) {
          this.retransmit = null;
          this.retransmitEpoch = null;
        } 
        int j = receiveRecord(arrayOfByte, 0, i, paramInt3);
        if (j < 0)
          return j; 
        if (j < 13)
          continue; 
        int k = TlsUtils.readUint16(arrayOfByte, 11);
        if (j != k + 13)
          continue; 
        short s = TlsUtils.readUint8(arrayOfByte, 0);
        switch (s) {
          case 20:
          case 21:
          case 22:
          case 23:
          case 24:
            break;
          default:
            continue;
        } 
        int m = TlsUtils.readUint16(arrayOfByte, 3);
        DTLSEpoch dTLSEpoch = null;
        if (m == this.readEpoch.getEpoch()) {
          dTLSEpoch = this.readEpoch;
        } else if (s == 22 && this.retransmitEpoch != null && m == this.retransmitEpoch.getEpoch()) {
          dTLSEpoch = this.retransmitEpoch;
        } 
        if (dTLSEpoch == null)
          continue; 
        long l = TlsUtils.readUint48(arrayOfByte, 5);
        if (dTLSEpoch.getReplayWindow().shouldDiscard(l))
          continue; 
        ProtocolVersion protocolVersion = TlsUtils.readVersion(arrayOfByte, 1);
        if (!protocolVersion.isDTLS() || (this.readVersion != null && !this.readVersion.equals(protocolVersion)))
          continue; 
        byte[] arrayOfByte1 = dTLSEpoch.getCipher().decodeCiphertext(getMacSequenceNumber(dTLSEpoch.getEpoch(), l), s, arrayOfByte, 13, j - 13);
        dTLSEpoch.getReplayWindow().reportAuthenticated(l);
        if (arrayOfByte1.length > this.plaintextLimit)
          continue; 
        if (this.readVersion == null)
          this.readVersion = protocolVersion; 
        switch (s) {
          case 21:
            if (arrayOfByte1.length == 2) {
              short s1 = (short)arrayOfByte1[0];
              short s2 = (short)arrayOfByte1[1];
              this.peer.notifyAlertReceived(s1, s2);
              if (s1 == 2) {
                failed();
                throw new TlsFatalAlert(s2);
              } 
              if (s2 == 0)
                closeTransport(); 
            } 
            continue;
          case 23:
            if (this.inHandshake)
              continue; 
            break;
          case 20:
            for (b = 0; b < arrayOfByte1.length; b++) {
              short s1 = TlsUtils.readUint8(arrayOfByte1, b);
              if (s1 == 1 && this.pendingEpoch != null)
                this.readEpoch = this.pendingEpoch; 
            } 
            continue;
          case 22:
            if (!this.inHandshake) {
              if (this.retransmit != null)
                this.retransmit.receivedHandshakeRecord(m, arrayOfByte1, 0, arrayOfByte1.length); 
              continue;
            } 
            break;
          case 24:
            continue;
        } 
        if (!this.inHandshake && this.retransmit != null) {
          this.retransmit = null;
          this.retransmitEpoch = null;
        } 
        System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1, arrayOfByte1.length);
        return arrayOfByte1.length;
      } catch (IOException iOException) {
        throw iOException;
      } 
    } 
  }
  
  public void send(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    byte b = 23;
    if (this.inHandshake || this.writeEpoch == this.retransmitEpoch) {
      b = 22;
      short s = TlsUtils.readUint8(paramArrayOfbyte, paramInt1);
      if (s == 20) {
        DTLSEpoch dTLSEpoch = null;
        if (this.inHandshake) {
          dTLSEpoch = this.pendingEpoch;
        } else if (this.writeEpoch == this.retransmitEpoch) {
          dTLSEpoch = this.currentEpoch;
        } 
        if (dTLSEpoch == null)
          throw new IllegalStateException(); 
        byte[] arrayOfByte = { 1 };
        sendRecord((short)20, arrayOfByte, 0, arrayOfByte.length);
        this.writeEpoch = dTLSEpoch;
      } 
    } 
    sendRecord(b, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      if (this.inHandshake)
        warn((short)90, "User canceled handshake"); 
      closeTransport();
    } 
  }
  
  void fail(short paramShort) {
    if (!this.closed) {
      try {
        raiseAlert((short)2, paramShort, null, null);
      } catch (Exception exception) {}
      this.failed = true;
      closeTransport();
    } 
  }
  
  void failed() {
    if (!this.closed) {
      this.failed = true;
      closeTransport();
    } 
  }
  
  void warn(short paramShort, String paramString) throws IOException {
    raiseAlert((short)1, paramShort, paramString, null);
  }
  
  private void closeTransport() {
    if (!this.closed) {
      try {
        if (!this.failed)
          warn((short)0, null); 
        this.transport.close();
      } catch (Exception exception) {}
      this.closed = true;
    } 
  }
  
  private void raiseAlert(short paramShort1, short paramShort2, String paramString, Throwable paramThrowable) throws IOException {
    this.peer.notifyAlertRaised(paramShort1, paramShort2, paramString, paramThrowable);
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)paramShort1;
    arrayOfByte[1] = (byte)paramShort2;
    sendRecord((short)21, arrayOfByte, 0, 2);
  }
  
  private int receiveRecord(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    if (this.recordQueue.available() > 0) {
      int j = 0;
      if (this.recordQueue.available() >= 13) {
        byte[] arrayOfByte = new byte[2];
        this.recordQueue.read(arrayOfByte, 0, 2, 11);
        j = TlsUtils.readUint16(arrayOfByte, 0);
      } 
      int k = Math.min(this.recordQueue.available(), 13 + j);
      this.recordQueue.removeData(paramArrayOfbyte, paramInt1, k, 0);
      return k;
    } 
    int i = this.transport.receive(paramArrayOfbyte, paramInt1, paramInt2, paramInt3);
    if (i >= 13) {
      int j = TlsUtils.readUint16(paramArrayOfbyte, paramInt1 + 11);
      int k = 13 + j;
      if (i > k) {
        this.recordQueue.addData(paramArrayOfbyte, paramInt1 + k, i - k);
        i = k;
      } 
    } 
    return i;
  }
  
  private void sendRecord(short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.writeVersion == null)
      return; 
    if (paramInt2 > this.plaintextLimit)
      throw new TlsFatalAlert((short)80); 
    if (paramInt2 < 1 && paramShort != 23)
      throw new TlsFatalAlert((short)80); 
    int i = this.writeEpoch.getEpoch();
    long l = this.writeEpoch.allocateSequenceNumber();
    byte[] arrayOfByte1 = this.writeEpoch.getCipher().encodePlaintext(getMacSequenceNumber(i, l), paramShort, paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 13];
    TlsUtils.writeUint8(paramShort, arrayOfByte2, 0);
    TlsUtils.writeVersion(this.writeVersion, arrayOfByte2, 1);
    TlsUtils.writeUint16(i, arrayOfByte2, 3);
    TlsUtils.writeUint48(l, arrayOfByte2, 5);
    TlsUtils.writeUint16(arrayOfByte1.length, arrayOfByte2, 11);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 13, arrayOfByte1.length);
    this.transport.send(arrayOfByte2, 0, arrayOfByte2.length);
  }
  
  private static long getMacSequenceNumber(int paramInt, long paramLong) {
    return (paramInt & 0xFFFFFFFFL) << 48L | paramLong;
  }
}
