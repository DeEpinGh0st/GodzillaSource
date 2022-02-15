package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64;

class CTEBase64InputStream extends InputStream {
  protected final InputStream src;
  
  protected final byte[] rawBuf = new byte[1024];
  
  protected final byte[] data = new byte[768];
  
  protected final OutputStream dataOutputStream;
  
  protected final Long max;
  
  protected int rp;
  
  protected int wp;
  
  protected boolean end;
  
  protected long read;
  
  public CTEBase64InputStream(InputStream paramInputStream, Long paramLong) {
    this.src = paramInputStream;
    this.dataOutputStream = new OutputStream() {
        public void write(int param1Int) throws IOException {
          CTEBase64InputStream.this.data[CTEBase64InputStream.this.wp++] = (byte)param1Int;
        }
      };
    this.max = paramLong;
  }
  
  protected int pullFromSrc() throws IOException {
    if (this.read >= this.max.longValue())
      return -1; 
    int i = 0;
    byte b = 0;
    do {
      i = this.src.read();
      if (i >= 33 || i == 13 || i == 10) {
        if (b >= this.rawBuf.length)
          throw new IOException("Content Transfer Encoding, base64 line length > 1024"); 
        this.rawBuf[b++] = (byte)i;
        this.read++;
      } else if (i >= 0) {
        this.read++;
      } 
    } while (i > -1 && b < this.rawBuf.length && i != 10 && this.read < this.max.longValue());
    if (b > 0) {
      try {
        Base64.decode(this.rawBuf, 0, b, this.dataOutputStream);
      } catch (Exception exception) {
        throw new IOException("Decode Base64 Content-Transfer-Encoding: " + exception);
      } 
    } else if (i == -1) {
      return -1;
    } 
    return this.wp;
  }
  
  public int read() throws IOException {
    if (this.rp == this.wp) {
      this.rp = 0;
      this.wp = 0;
      int i = pullFromSrc();
      if (i == -1)
        return i; 
    } 
    return this.data[this.rp++] & 0xFF;
  }
  
  public void close() throws IOException {
    this.src.close();
  }
}
