package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;

class TlsInputStream extends InputStream {
  private byte[] buf = new byte[1];
  
  private TlsProtocol handler = null;
  
  TlsInputStream(TlsProtocol paramTlsProtocol) {
    this.handler = paramTlsProtocol;
  }
  
  public int available() throws IOException {
    return this.handler.applicationDataAvailable();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    return this.handler.readApplicationData(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int read() throws IOException {
    return (read(this.buf) < 0) ? -1 : (this.buf[0] & 0xFF);
  }
  
  public void close() throws IOException {
    this.handler.close();
  }
}
