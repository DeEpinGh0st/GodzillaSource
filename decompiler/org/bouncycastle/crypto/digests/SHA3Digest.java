package org.bouncycastle.crypto.digests;

public class SHA3Digest extends KeccakDigest {
  private static int checkBitLength(int paramInt) {
    switch (paramInt) {
      case 224:
      case 256:
      case 384:
      case 512:
        return paramInt;
    } 
    throw new IllegalArgumentException("'bitLength' " + paramInt + " not supported for SHA-3");
  }
  
  public SHA3Digest() {
    this(256);
  }
  
  public SHA3Digest(int paramInt) {
    super(checkBitLength(paramInt));
  }
  
  public SHA3Digest(SHA3Digest paramSHA3Digest) {
    super(paramSHA3Digest);
  }
  
  public String getAlgorithmName() {
    return "SHA3-" + this.fixedOutputLength;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    absorbBits(2, 2);
    return super.doFinal(paramArrayOfbyte, paramInt);
  }
  
  protected int doFinal(byte[] paramArrayOfbyte, int paramInt1, byte paramByte, int paramInt2) {
    if (paramInt2 < 0 || paramInt2 > 7)
      throw new IllegalArgumentException("'partialBits' must be in the range [0,7]"); 
    int i = paramByte & (1 << paramInt2) - 1 | 2 << paramInt2;
    int j = paramInt2 + 2;
    if (j >= 8) {
      absorb(new byte[] { (byte)i }, 0, 1);
      j -= 8;
      i >>>= 8;
    } 
    return super.doFinal(paramArrayOfbyte, paramInt1, (byte)i, j);
  }
}
