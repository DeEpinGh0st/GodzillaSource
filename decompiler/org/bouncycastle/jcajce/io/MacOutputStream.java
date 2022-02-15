package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

public final class MacOutputStream extends OutputStream {
  private Mac mac;
  
  public MacOutputStream(Mac paramMac) {
    this.mac = paramMac;
  }
  
  public void write(int paramInt) throws IOException {
    this.mac.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.mac.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] getMac() {
    return this.mac.doFinal();
  }
}
