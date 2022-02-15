package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Mac;

public class MacOutputStream extends OutputStream {
  protected Mac mac;
  
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
    byte[] arrayOfByte = new byte[this.mac.getMacSize()];
    this.mac.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
}
