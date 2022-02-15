package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Integers;

class DTLSReliableHandshake {
  private static final int MAX_RECEIVE_AHEAD = 16;
  
  private static final int MESSAGE_HEADER_LENGTH = 12;
  
  private DTLSRecordLayer recordLayer;
  
  private TlsHandshakeHash handshakeHash;
  
  private Hashtable currentInboundFlight = new Hashtable<Object, Object>();
  
  private Hashtable previousInboundFlight = null;
  
  private Vector outboundFlight = new Vector();
  
  private boolean sending = true;
  
  private int message_seq = 0;
  
  private int next_receive_seq = 0;
  
  DTLSReliableHandshake(TlsContext paramTlsContext, DTLSRecordLayer paramDTLSRecordLayer) {
    this.recordLayer = paramDTLSRecordLayer;
    this.handshakeHash = new DeferredHash();
    this.handshakeHash.init(paramTlsContext);
  }
  
  void notifyHelloComplete() {
    this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
  }
  
  TlsHandshakeHash getHandshakeHash() {
    return this.handshakeHash;
  }
  
  TlsHandshakeHash prepareToFinish() {
    TlsHandshakeHash tlsHandshakeHash = this.handshakeHash;
    this.handshakeHash = this.handshakeHash.stopTracking();
    return tlsHandshakeHash;
  }
  
  void sendMessage(short paramShort, byte[] paramArrayOfbyte) throws IOException {
    TlsUtils.checkUint24(paramArrayOfbyte.length);
    if (!this.sending) {
      checkInboundFlight();
      this.sending = true;
      this.outboundFlight.removeAllElements();
    } 
    Message message = new Message(this.message_seq++, paramShort, paramArrayOfbyte);
    this.outboundFlight.addElement(message);
    writeMessage(message);
    updateHandshakeMessagesDigest(message);
  }
  
  byte[] receiveMessageBody(short paramShort) throws IOException {
    Message message = receiveMessage();
    if (message.getType() != paramShort)
      throw new TlsFatalAlert((short)10); 
    return message.getBody();
  }
  
  Message receiveMessage() throws IOException {
    if (this.sending) {
      this.sending = false;
      prepareInboundFlight(new Hashtable<Object, Object>());
    } 
    byte[] arrayOfByte = null;
    for (int i = 1000;; i = backOff(i)) {
      try {
        Message message = getPendingMessage();
        if (message != null)
          return message; 
        int j = this.recordLayer.getReceiveLimit();
        if (arrayOfByte == null || arrayOfByte.length < j)
          arrayOfByte = new byte[j]; 
        int k = this.recordLayer.receive(arrayOfByte, 0, j, i);
        if (k >= 0) {
          boolean bool = processRecord(16, this.recordLayer.getReadEpoch(), arrayOfByte, 0, k);
          if (bool)
            i = backOff(i); 
          continue;
        } 
      } catch (IOException iOException) {}
      resendOutboundFlight();
    } 
  }
  
  void finish() {
    DTLSHandshakeRetransmit dTLSHandshakeRetransmit = null;
    if (!this.sending) {
      checkInboundFlight();
    } else {
      prepareInboundFlight(null);
      if (this.previousInboundFlight != null)
        dTLSHandshakeRetransmit = new DTLSHandshakeRetransmit() {
            public void receivedHandshakeRecord(int param1Int1, byte[] param1ArrayOfbyte, int param1Int2, int param1Int3) throws IOException {
              DTLSReliableHandshake.this.processRecord(0, param1Int1, param1ArrayOfbyte, param1Int2, param1Int3);
            }
          }; 
    } 
    this.recordLayer.handshakeSuccessful(dTLSHandshakeRetransmit);
  }
  
  void resetHandshakeMessagesDigest() {
    this.handshakeHash.reset();
  }
  
  private int backOff(int paramInt) {
    return Math.min(paramInt * 2, 60000);
  }
  
  private void checkInboundFlight() {
    Enumeration<Integer> enumeration = this.currentInboundFlight.keys();
    while (enumeration.hasMoreElements()) {
      Integer integer = enumeration.nextElement();
      if (integer.intValue() >= this.next_receive_seq);
    } 
  }
  
  private Message getPendingMessage() throws IOException {
    DTLSReassembler dTLSReassembler = (DTLSReassembler)this.currentInboundFlight.get(Integers.valueOf(this.next_receive_seq));
    if (dTLSReassembler != null) {
      byte[] arrayOfByte = dTLSReassembler.getBodyIfComplete();
      if (arrayOfByte != null) {
        this.previousInboundFlight = null;
        return updateHandshakeMessagesDigest(new Message(this.next_receive_seq++, dTLSReassembler.getMsgType(), arrayOfByte));
      } 
    } 
    return null;
  }
  
  private void prepareInboundFlight(Hashtable paramHashtable) {
    resetAll(this.currentInboundFlight);
    this.previousInboundFlight = this.currentInboundFlight;
    this.currentInboundFlight = paramHashtable;
  }
  
  private boolean processRecord(int paramInt1, int paramInt2, byte[] paramArrayOfbyte, int paramInt3, int paramInt4) throws IOException {
    boolean bool1 = false;
    while (paramInt4 >= 12) {
      int i = TlsUtils.readUint24(paramArrayOfbyte, paramInt3 + 9);
      int j = i + 12;
      if (paramInt4 < j)
        break; 
      int k = TlsUtils.readUint24(paramArrayOfbyte, paramInt3 + 1);
      int m = TlsUtils.readUint24(paramArrayOfbyte, paramInt3 + 6);
      if (m + i > k)
        break; 
      short s = TlsUtils.readUint8(paramArrayOfbyte, paramInt3 + 0);
      byte b = (s == 20) ? 1 : 0;
      if (paramInt2 != b)
        break; 
      int n = TlsUtils.readUint16(paramArrayOfbyte, paramInt3 + 4);
      if (n < this.next_receive_seq + paramInt1)
        if (n >= this.next_receive_seq) {
          DTLSReassembler dTLSReassembler = (DTLSReassembler)this.currentInboundFlight.get(Integers.valueOf(n));
          if (dTLSReassembler == null) {
            dTLSReassembler = new DTLSReassembler(s, k);
            this.currentInboundFlight.put(Integers.valueOf(n), dTLSReassembler);
          } 
          dTLSReassembler.contributeFragment(s, k, paramArrayOfbyte, paramInt3 + 12, m, i);
        } else if (this.previousInboundFlight != null) {
          DTLSReassembler dTLSReassembler = (DTLSReassembler)this.previousInboundFlight.get(Integers.valueOf(n));
          if (dTLSReassembler != null) {
            dTLSReassembler.contributeFragment(s, k, paramArrayOfbyte, paramInt3 + 12, m, i);
            bool1 = true;
          } 
        }  
      paramInt3 += j;
      paramInt4 -= j;
    } 
    boolean bool2 = (bool1 && checkAll(this.previousInboundFlight)) ? true : false;
    if (bool2) {
      resendOutboundFlight();
      resetAll(this.previousInboundFlight);
    } 
    return bool2;
  }
  
  private void resendOutboundFlight() throws IOException {
    this.recordLayer.resetWriteEpoch();
    for (byte b = 0; b < this.outboundFlight.size(); b++)
      writeMessage(this.outboundFlight.elementAt(b)); 
  }
  
  private Message updateHandshakeMessagesDigest(Message paramMessage) throws IOException {
    if (paramMessage.getType() != 0) {
      byte[] arrayOfByte1 = paramMessage.getBody();
      byte[] arrayOfByte2 = new byte[12];
      TlsUtils.writeUint8(paramMessage.getType(), arrayOfByte2, 0);
      TlsUtils.writeUint24(arrayOfByte1.length, arrayOfByte2, 1);
      TlsUtils.writeUint16(paramMessage.getSeq(), arrayOfByte2, 4);
      TlsUtils.writeUint24(0, arrayOfByte2, 6);
      TlsUtils.writeUint24(arrayOfByte1.length, arrayOfByte2, 9);
      this.handshakeHash.update(arrayOfByte2, 0, arrayOfByte2.length);
      this.handshakeHash.update(arrayOfByte1, 0, arrayOfByte1.length);
    } 
    return paramMessage;
  }
  
  private void writeMessage(Message paramMessage) throws IOException {
    int i = this.recordLayer.getSendLimit();
    int j = i - 12;
    if (j < 1)
      throw new TlsFatalAlert((short)80); 
    int k = (paramMessage.getBody()).length;
    int m = 0;
    do {
      int n = Math.min(k - m, j);
      writeHandshakeFragment(paramMessage, m, n);
      m += n;
    } while (m < k);
  }
  
  private void writeHandshakeFragment(Message paramMessage, int paramInt1, int paramInt2) throws IOException {
    RecordLayerBuffer recordLayerBuffer = new RecordLayerBuffer(12 + paramInt2);
    TlsUtils.writeUint8(paramMessage.getType(), recordLayerBuffer);
    TlsUtils.writeUint24((paramMessage.getBody()).length, recordLayerBuffer);
    TlsUtils.writeUint16(paramMessage.getSeq(), recordLayerBuffer);
    TlsUtils.writeUint24(paramInt1, recordLayerBuffer);
    TlsUtils.writeUint24(paramInt2, recordLayerBuffer);
    recordLayerBuffer.write(paramMessage.getBody(), paramInt1, paramInt2);
    recordLayerBuffer.sendToRecordLayer(this.recordLayer);
  }
  
  private static boolean checkAll(Hashtable paramHashtable) {
    Enumeration<DTLSReassembler> enumeration = paramHashtable.elements();
    while (enumeration.hasMoreElements()) {
      if (((DTLSReassembler)enumeration.nextElement()).getBodyIfComplete() == null)
        return false; 
    } 
    return true;
  }
  
  private static void resetAll(Hashtable paramHashtable) {
    Enumeration<DTLSReassembler> enumeration = paramHashtable.elements();
    while (enumeration.hasMoreElements())
      ((DTLSReassembler)enumeration.nextElement()).reset(); 
  }
  
  static class Message {
    private final int message_seq;
    
    private final short msg_type;
    
    private final byte[] body;
    
    private Message(int param1Int, short param1Short, byte[] param1ArrayOfbyte) {
      this.message_seq = param1Int;
      this.msg_type = param1Short;
      this.body = param1ArrayOfbyte;
    }
    
    public int getSeq() {
      return this.message_seq;
    }
    
    public short getType() {
      return this.msg_type;
    }
    
    public byte[] getBody() {
      return this.body;
    }
  }
  
  static class RecordLayerBuffer extends ByteArrayOutputStream {
    RecordLayerBuffer(int param1Int) {
      super(param1Int);
    }
    
    void sendToRecordLayer(DTLSRecordLayer param1DTLSRecordLayer) throws IOException {
      param1DTLSRecordLayer.send(this.buf, 0, this.count);
      this.buf = null;
    }
  }
}
