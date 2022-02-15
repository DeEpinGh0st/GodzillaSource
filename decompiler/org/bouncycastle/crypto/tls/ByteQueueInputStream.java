package org.bouncycastle.crypto.tls;

import java.io.InputStream;

public class ByteQueueInputStream extends InputStream {
  private ByteQueue buffer = new ByteQueue();
  
  public void addBytes(byte[] paramArrayOfbyte) {
    this.buffer.addData(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public int peek(byte[] paramArrayOfbyte) {
    int i = Math.min(this.buffer.available(), paramArrayOfbyte.length);
    this.buffer.read(paramArrayOfbyte, 0, i, 0);
    return i;
  }
  
  public int read() {
    return (this.buffer.available() == 0) ? -1 : (this.buffer.removeData(1, 0)[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte) {
    return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = Math.min(this.buffer.available(), paramInt2);
    this.buffer.removeData(paramArrayOfbyte, paramInt1, i, 0);
    return i;
  }
  
  public long skip(long paramLong) {
    int i = Math.min((int)paramLong, this.buffer.available());
    this.buffer.removeData(i);
    return i;
  }
  
  public int available() {
    return this.buffer.available();
  }
  
  public void close() {}
}
