package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Mac;

public class MacInputStream extends FilterInputStream {
  protected Mac mac;
  
  public MacInputStream(InputStream paramInputStream, Mac paramMac) {
    super(paramInputStream);
    this.mac = paramMac;
  }
  
  public int read() throws IOException {
    int i = this.in.read();
    if (i >= 0)
      this.mac.update((byte)i); 
    return i;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = this.in.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (i >= 0)
      this.mac.update(paramArrayOfbyte, paramInt1, i); 
    return i;
  }
  
  public Mac getMac() {
    return this.mac;
  }
}
