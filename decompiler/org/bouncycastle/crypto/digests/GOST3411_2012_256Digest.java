package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

public final class GOST3411_2012_256Digest extends GOST3411_2012Digest {
  private static final byte[] IV = new byte[] { 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1 };
  
  public GOST3411_2012_256Digest() {
    super(IV);
  }
  
  public GOST3411_2012_256Digest(GOST3411_2012_256Digest paramGOST3411_2012_256Digest) {
    super(IV);
    reset(paramGOST3411_2012_256Digest);
  }
  
  public String getAlgorithmName() {
    return "GOST3411-2012-256";
  }
  
  public int getDigestSize() {
    return 32;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[64];
    super.doFinal(arrayOfByte, 0);
    System.arraycopy(arrayOfByte, 32, paramArrayOfbyte, paramInt, 32);
    return 32;
  }
  
  public Memoable copy() {
    return new GOST3411_2012_256Digest(this);
  }
}
