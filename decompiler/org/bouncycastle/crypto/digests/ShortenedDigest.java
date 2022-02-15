package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class ShortenedDigest implements ExtendedDigest {
  private ExtendedDigest baseDigest;
  
  private int length;
  
  public ShortenedDigest(ExtendedDigest paramExtendedDigest, int paramInt) {
    if (paramExtendedDigest == null)
      throw new IllegalArgumentException("baseDigest must not be null"); 
    if (paramInt > paramExtendedDigest.getDigestSize())
      throw new IllegalArgumentException("baseDigest output not large enough to support length"); 
    this.baseDigest = paramExtendedDigest;
    this.length = paramInt;
  }
  
  public String getAlgorithmName() {
    return this.baseDigest.getAlgorithmName() + "(" + (this.length * 8) + ")";
  }
  
  public int getDigestSize() {
    return this.length;
  }
  
  public void update(byte paramByte) {
    this.baseDigest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.baseDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[this.baseDigest.getDigestSize()];
    this.baseDigest.doFinal(arrayOfByte, 0);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, this.length);
    return this.length;
  }
  
  public void reset() {
    this.baseDigest.reset();
  }
  
  public int getByteLength() {
    return this.baseDigest.getByteLength();
  }
}
