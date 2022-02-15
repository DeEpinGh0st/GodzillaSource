package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends InputStream {
  private final InputStream input;
  
  private final OutputStream output;
  
  public TeeInputStream(InputStream paramInputStream, OutputStream paramOutputStream) {
    this.input = paramInputStream;
    this.output = paramOutputStream;
  }
  
  public int read(byte[] paramArrayOfbyte) throws IOException {
    return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = this.input.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (i > 0)
      this.output.write(paramArrayOfbyte, paramInt1, i); 
    return i;
  }
  
  public int read() throws IOException {
    int i = this.input.read();
    if (i >= 0)
      this.output.write(i); 
    return i;
  }
  
  public void close() throws IOException {
    this.input.close();
    this.output.close();
  }
  
  public OutputStream getOutputStream() {
    return this.output;
  }
}
