package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public class BufferingOutputStream extends OutputStream {
  private final OutputStream other;
  
  private final byte[] buf;
  
  private int bufOff;
  
  public BufferingOutputStream(OutputStream paramOutputStream) {
    this.other = paramOutputStream;
    this.buf = new byte[4096];
  }
  
  public BufferingOutputStream(OutputStream paramOutputStream, int paramInt) {
    this.other = paramOutputStream;
    this.buf = new byte[paramInt];
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < this.buf.length - this.bufOff) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
      this.bufOff += paramInt2;
    } else {
      int i = this.buf.length - this.bufOff;
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, i);
      this.bufOff += i;
      flush();
      paramInt1 += i;
      for (paramInt2 -= i; paramInt2 >= this.buf.length; paramInt2 -= this.buf.length) {
        this.other.write(paramArrayOfbyte, paramInt1, this.buf.length);
        paramInt1 += this.buf.length;
      } 
      if (paramInt2 > 0) {
        System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
        this.bufOff += paramInt2;
      } 
    } 
  }
  
  public void write(int paramInt) throws IOException {
    this.buf[this.bufOff++] = (byte)paramInt;
    if (this.bufOff == this.buf.length)
      flush(); 
  }
  
  public void flush() throws IOException {
    this.other.write(this.buf, 0, this.bufOff);
    this.bufOff = 0;
    Arrays.fill(this.buf, (byte)0);
  }
  
  public void close() throws IOException {
    flush();
    this.other.close();
  }
}
