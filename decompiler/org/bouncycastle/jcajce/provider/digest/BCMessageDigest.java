package org.bouncycastle.jcajce.provider.digest;

import java.security.MessageDigest;
import org.bouncycastle.crypto.Digest;

public class BCMessageDigest extends MessageDigest {
  protected Digest digest;
  
  protected BCMessageDigest(Digest paramDigest) {
    super(paramDigest.getAlgorithmName());
    this.digest = paramDigest;
  }
  
  public void engineReset() {
    this.digest.reset();
  }
  
  public void engineUpdate(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] engineDigest() {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
}
