package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;

public class SHAKEDigest extends KeccakDigest implements Xof {
  private static int checkBitLength(int paramInt) {
    switch (paramInt) {
      case 128:
      case 256:
        return paramInt;
    } 
    throw new IllegalArgumentException("'bitLength' " + paramInt + " not supported for SHAKE");
  }
  
  public SHAKEDigest() {
    this(128);
  }
  
  public SHAKEDigest(int paramInt) {
    super(checkBitLength(paramInt));
  }
  
  public SHAKEDigest(SHAKEDigest paramSHAKEDigest) {
    super(paramSHAKEDigest);
  }
  
  public String getAlgorithmName() {
    return "SHAKE" + this.fixedOutputLength;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    return doFinal(paramArrayOfbyte, paramInt, getDigestSize());
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = doOutput(paramArrayOfbyte, paramInt1, paramInt2);
    reset();
    return i;
  }
  
  public int doOutput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (!this.squeezing)
      absorbBits(15, 4); 
    squeeze(paramArrayOfbyte, paramInt1, paramInt2 * 8L);
    return paramInt2;
  }
  
  protected int doFinal(byte[] paramArrayOfbyte, int paramInt1, byte paramByte, int paramInt2) {
    return doFinal(paramArrayOfbyte, paramInt1, getDigestSize(), paramByte, paramInt2);
  }
  
  protected int doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, byte paramByte, int paramInt3) {
    if (paramInt3 < 0 || paramInt3 > 7)
      throw new IllegalArgumentException("'partialBits' must be in the range [0,7]"); 
    int i = paramByte & (1 << paramInt3) - 1 | 15 << paramInt3;
    int j = paramInt3 + 4;
    if (j >= 8) {
      absorb(new byte[] { (byte)i }, 0, 1);
      j -= 8;
      i >>>= 8;
    } 
    if (j > 0)
      absorbBits(i, j); 
    squeeze(paramArrayOfbyte, paramInt1, paramInt2 * 8L);
    reset();
    return paramInt2;
  }
}
