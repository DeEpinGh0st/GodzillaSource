package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Digest;

public class DigestOutputStream extends OutputStream {
  protected Digest digest;
  
  public DigestOutputStream(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void write(int paramInt) throws IOException {
    this.digest.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] getDigest() {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
}
