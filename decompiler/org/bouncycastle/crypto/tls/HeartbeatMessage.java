package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class HeartbeatMessage {
  protected short type;
  
  protected byte[] payload;
  
  protected int paddingLength;
  
  public HeartbeatMessage(short paramShort, byte[] paramArrayOfbyte, int paramInt) {
    if (!HeartbeatMessageType.isValid(paramShort))
      throw new IllegalArgumentException("'type' is not a valid HeartbeatMessageType value"); 
    if (paramArrayOfbyte == null || paramArrayOfbyte.length >= 65536)
      throw new IllegalArgumentException("'payload' must have length < 2^16"); 
    if (paramInt < 16)
      throw new IllegalArgumentException("'paddingLength' must be at least 16"); 
    this.type = paramShort;
    this.payload = paramArrayOfbyte;
    this.paddingLength = paramInt;
  }
  
  public void encode(TlsContext paramTlsContext, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeUint8(this.type, paramOutputStream);
    TlsUtils.checkUint16(this.payload.length);
    TlsUtils.writeUint16(this.payload.length, paramOutputStream);
    paramOutputStream.write(this.payload);
    byte[] arrayOfByte = new byte[this.paddingLength];
    paramTlsContext.getNonceRandomGenerator().nextBytes(arrayOfByte);
    paramOutputStream.write(arrayOfByte);
  }
  
  public static HeartbeatMessage parse(InputStream paramInputStream) throws IOException {
    short s = TlsUtils.readUint8(paramInputStream);
    if (!HeartbeatMessageType.isValid(s))
      throw new TlsFatalAlert((short)47); 
    int i = TlsUtils.readUint16(paramInputStream);
    PayloadBuffer payloadBuffer = new PayloadBuffer();
    Streams.pipeAll(paramInputStream, payloadBuffer);
    byte[] arrayOfByte = payloadBuffer.toTruncatedByteArray(i);
    if (arrayOfByte == null)
      return null; 
    int j = payloadBuffer.size() - arrayOfByte.length;
    return new HeartbeatMessage(s, arrayOfByte, j);
  }
  
  static class PayloadBuffer extends ByteArrayOutputStream {
    byte[] toTruncatedByteArray(int param1Int) {
      int i = param1Int + 16;
      return (this.count < i) ? null : Arrays.copyOf(this.buf, param1Int);
    }
  }
}
