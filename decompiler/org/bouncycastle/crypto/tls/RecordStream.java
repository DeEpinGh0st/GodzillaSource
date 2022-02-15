package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.io.SimpleOutputStream;

class RecordStream {
  private static int DEFAULT_PLAINTEXT_LIMIT = 16384;
  
  static final int TLS_HEADER_SIZE = 5;
  
  static final int TLS_HEADER_TYPE_OFFSET = 0;
  
  static final int TLS_HEADER_VERSION_OFFSET = 1;
  
  static final int TLS_HEADER_LENGTH_OFFSET = 3;
  
  private TlsProtocol handler;
  
  private InputStream input;
  
  private OutputStream output;
  
  private TlsCompression pendingCompression = null;
  
  private TlsCompression readCompression = null;
  
  private TlsCompression writeCompression = null;
  
  private TlsCipher pendingCipher = null;
  
  private TlsCipher readCipher = null;
  
  private TlsCipher writeCipher = null;
  
  private SequenceNumber readSeqNo = new SequenceNumber();
  
  private SequenceNumber writeSeqNo = new SequenceNumber();
  
  private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  
  private TlsHandshakeHash handshakeHash = null;
  
  private SimpleOutputStream handshakeHashUpdater = new SimpleOutputStream() {
      public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
        RecordStream.this.handshakeHash.update(param1ArrayOfbyte, param1Int1, param1Int2);
      }
    };
  
  private ProtocolVersion readVersion = null;
  
  private ProtocolVersion writeVersion = null;
  
  private boolean restrictReadVersion = true;
  
  private int plaintextLimit;
  
  private int compressedLimit;
  
  private int ciphertextLimit;
  
  RecordStream(TlsProtocol paramTlsProtocol, InputStream paramInputStream, OutputStream paramOutputStream) {
    this.handler = paramTlsProtocol;
    this.input = paramInputStream;
    this.output = paramOutputStream;
    this.readCompression = new TlsNullCompression();
    this.writeCompression = this.readCompression;
  }
  
  void init(TlsContext paramTlsContext) {
    this.readCipher = new TlsNullCipher(paramTlsContext);
    this.writeCipher = this.readCipher;
    this.handshakeHash = new DeferredHash();
    this.handshakeHash.init(paramTlsContext);
    setPlaintextLimit(DEFAULT_PLAINTEXT_LIMIT);
  }
  
  int getPlaintextLimit() {
    return this.plaintextLimit;
  }
  
  void setPlaintextLimit(int paramInt) {
    this.plaintextLimit = paramInt;
    this.compressedLimit = this.plaintextLimit + 1024;
    this.ciphertextLimit = this.compressedLimit + 1024;
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
  
  void setRestrictReadVersion(boolean paramBoolean) {
    this.restrictReadVersion = paramBoolean;
  }
  
  void setPendingConnectionState(TlsCompression paramTlsCompression, TlsCipher paramTlsCipher) {
    this.pendingCompression = paramTlsCompression;
    this.pendingCipher = paramTlsCipher;
  }
  
  void sentWriteCipherSpec() throws IOException {
    if (this.pendingCompression == null || this.pendingCipher == null)
      throw new TlsFatalAlert((short)40); 
    this.writeCompression = this.pendingCompression;
    this.writeCipher = this.pendingCipher;
    this.writeSeqNo = new SequenceNumber();
  }
  
  void receivedReadCipherSpec() throws IOException {
    if (this.pendingCompression == null || this.pendingCipher == null)
      throw new TlsFatalAlert((short)40); 
    this.readCompression = this.pendingCompression;
    this.readCipher = this.pendingCipher;
    this.readSeqNo = new SequenceNumber();
  }
  
  void finaliseHandshake() throws IOException {
    if (this.readCompression != this.pendingCompression || this.writeCompression != this.pendingCompression || this.readCipher != this.pendingCipher || this.writeCipher != this.pendingCipher)
      throw new TlsFatalAlert((short)40); 
    this.pendingCompression = null;
    this.pendingCipher = null;
  }
  
  void checkRecordHeader(byte[] paramArrayOfbyte) throws IOException {
    short s = TlsUtils.readUint8(paramArrayOfbyte, 0);
    checkType(s, (short)10);
    if (!this.restrictReadVersion) {
      int j = TlsUtils.readVersionRaw(paramArrayOfbyte, 1);
      if ((j & 0xFFFFFF00) != 768)
        throw new TlsFatalAlert((short)47); 
    } else {
      ProtocolVersion protocolVersion = TlsUtils.readVersion(paramArrayOfbyte, 1);
      if (this.readVersion != null && !protocolVersion.equals(this.readVersion))
        throw new TlsFatalAlert((short)47); 
    } 
    int i = TlsUtils.readUint16(paramArrayOfbyte, 3);
    checkLength(i, this.ciphertextLimit, (short)22);
  }
  
  boolean readRecord() throws IOException {
    byte[] arrayOfByte1 = TlsUtils.readAllOrNothing(5, this.input);
    if (arrayOfByte1 == null)
      return false; 
    short s = TlsUtils.readUint8(arrayOfByte1, 0);
    checkType(s, (short)10);
    if (!this.restrictReadVersion) {
      int j = TlsUtils.readVersionRaw(arrayOfByte1, 1);
      if ((j & 0xFFFFFF00) != 768)
        throw new TlsFatalAlert((short)47); 
    } else {
      ProtocolVersion protocolVersion = TlsUtils.readVersion(arrayOfByte1, 1);
      if (this.readVersion == null) {
        this.readVersion = protocolVersion;
      } else if (!protocolVersion.equals(this.readVersion)) {
        throw new TlsFatalAlert((short)47);
      } 
    } 
    int i = TlsUtils.readUint16(arrayOfByte1, 3);
    checkLength(i, this.ciphertextLimit, (short)22);
    byte[] arrayOfByte2 = decodeAndVerify(s, this.input, i);
    this.handler.processRecord(s, arrayOfByte2, 0, arrayOfByte2.length);
    return true;
  }
  
  byte[] decodeAndVerify(short paramShort, InputStream paramInputStream, int paramInt) throws IOException {
    byte[] arrayOfByte1 = TlsUtils.readFully(paramInt, paramInputStream);
    long l = this.readSeqNo.nextValue((short)10);
    byte[] arrayOfByte2 = this.readCipher.decodeCiphertext(l, paramShort, arrayOfByte1, 0, arrayOfByte1.length);
    checkLength(arrayOfByte2.length, this.compressedLimit, (short)22);
    OutputStream outputStream = this.readCompression.decompress(this.buffer);
    if (outputStream != this.buffer) {
      outputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
      outputStream.flush();
      arrayOfByte2 = getBufferContents();
    } 
    checkLength(arrayOfByte2.length, this.plaintextLimit, (short)30);
    if (arrayOfByte2.length < 1 && paramShort != 23)
      throw new TlsFatalAlert((short)47); 
    return arrayOfByte2;
  }
  
  void writeRecord(short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    byte[] arrayOfByte1;
    if (this.writeVersion == null)
      return; 
    checkType(paramShort, (short)80);
    checkLength(paramInt2, this.plaintextLimit, (short)80);
    if (paramInt2 < 1 && paramShort != 23)
      throw new TlsFatalAlert((short)80); 
    OutputStream outputStream = this.writeCompression.compress(this.buffer);
    long l = this.writeSeqNo.nextValue((short)80);
    if (outputStream == this.buffer) {
      arrayOfByte1 = this.writeCipher.encodePlaintext(l, paramShort, paramArrayOfbyte, paramInt1, paramInt2);
    } else {
      outputStream.write(paramArrayOfbyte, paramInt1, paramInt2);
      outputStream.flush();
      byte[] arrayOfByte = getBufferContents();
      checkLength(arrayOfByte.length, paramInt2 + 1024, (short)80);
      arrayOfByte1 = this.writeCipher.encodePlaintext(l, paramShort, arrayOfByte, 0, arrayOfByte.length);
    } 
    checkLength(arrayOfByte1.length, this.ciphertextLimit, (short)80);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 5];
    TlsUtils.writeUint8(paramShort, arrayOfByte2, 0);
    TlsUtils.writeVersion(this.writeVersion, arrayOfByte2, 1);
    TlsUtils.writeUint16(arrayOfByte1.length, arrayOfByte2, 3);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 5, arrayOfByte1.length);
    this.output.write(arrayOfByte2);
    this.output.flush();
  }
  
  void notifyHelloComplete() {
    this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
  }
  
  TlsHandshakeHash getHandshakeHash() {
    return this.handshakeHash;
  }
  
  OutputStream getHandshakeHashUpdater() {
    return (OutputStream)this.handshakeHashUpdater;
  }
  
  TlsHandshakeHash prepareToFinish() {
    TlsHandshakeHash tlsHandshakeHash = this.handshakeHash;
    this.handshakeHash = this.handshakeHash.stopTracking();
    return tlsHandshakeHash;
  }
  
  void safeClose() {
    try {
      this.input.close();
    } catch (IOException iOException) {}
    try {
      this.output.close();
    } catch (IOException iOException) {}
  }
  
  void flush() throws IOException {
    this.output.flush();
  }
  
  private byte[] getBufferContents() {
    byte[] arrayOfByte = this.buffer.toByteArray();
    this.buffer.reset();
    return arrayOfByte;
  }
  
  private static void checkType(short paramShort1, short paramShort2) throws IOException {
    switch (paramShort1) {
      case 20:
      case 21:
      case 22:
      case 23:
        return;
    } 
    throw new TlsFatalAlert(paramShort2);
  }
  
  private static void checkLength(int paramInt1, int paramInt2, short paramShort) throws IOException {
    if (paramInt1 > paramInt2)
      throw new TlsFatalAlert(paramShort); 
  }
  
  private static class SequenceNumber {
    private long value = 0L;
    
    private boolean exhausted = false;
    
    private SequenceNumber() {}
    
    synchronized long nextValue(short param1Short) throws TlsFatalAlert {
      if (this.exhausted)
        throw new TlsFatalAlert(param1Short); 
      long l = this.value;
      if (++this.value == 0L)
        this.exhausted = true; 
      return l;
    }
  }
}
