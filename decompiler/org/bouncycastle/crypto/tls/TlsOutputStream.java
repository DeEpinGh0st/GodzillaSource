package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

class TlsOutputStream extends OutputStream {
  private byte[] buf = new byte[1];
  
  private TlsProtocol handler;
  
  TlsOutputStream(TlsProtocol paramTlsProtocol) {
    this.handler = paramTlsProtocol;
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.handler.writeData(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void write(int paramInt) throws IOException {
    this.buf[0] = (byte)paramInt;
    write(this.buf, 0, 1);
  }
  
  public void close() throws IOException {
    this.handler.close();
  }
  
  public void flush() throws IOException {
    this.handler.flush();
  }
}
