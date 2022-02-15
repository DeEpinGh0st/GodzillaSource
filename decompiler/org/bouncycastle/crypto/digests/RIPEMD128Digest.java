package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

public class RIPEMD128Digest extends GeneralDigest {
  private static final int DIGEST_LENGTH = 16;
  
  private int H0;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int[] X = new int[16];
  
  private int xOff;
  
  public RIPEMD128Digest() {
    reset();
  }
  
  public RIPEMD128Digest(RIPEMD128Digest paramRIPEMD128Digest) {
    super(paramRIPEMD128Digest);
    copyIn(paramRIPEMD128Digest);
  }
  
  private void copyIn(RIPEMD128Digest paramRIPEMD128Digest) {
    copyIn(paramRIPEMD128Digest);
    this.H0 = paramRIPEMD128Digest.H0;
    this.H1 = paramRIPEMD128Digest.H1;
    this.H2 = paramRIPEMD128Digest.H2;
    this.H3 = paramRIPEMD128Digest.H3;
    System.arraycopy(paramRIPEMD128Digest.X, 0, this.X, 0, paramRIPEMD128Digest.X.length);
    this.xOff = paramRIPEMD128Digest.xOff;
  }
  
  public String getAlgorithmName() {
    return "RIPEMD128";
  }
  
  public int getDigestSize() {
    return 16;
  }
  
  protected void processWord(byte[] paramArrayOfbyte, int paramInt) {
    this.X[this.xOff++] = paramArrayOfbyte[paramInt] & 0xFF | (paramArrayOfbyte[paramInt + 1] & 0xFF) << 8 | (paramArrayOfbyte[paramInt + 2] & 0xFF) << 16 | (paramArrayOfbyte[paramInt + 3] & 0xFF) << 24;
    if (this.xOff == 16)
      processBlock(); 
  }
  
  protected void processLength(long paramLong) {
    if (this.xOff > 14)
      processBlock(); 
    this.X[14] = (int)(paramLong & 0xFFFFFFFFFFFFFFFFL);
    this.X[15] = (int)(paramLong >>> 32L);
  }
  
  private void unpackWord(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >>> 24);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    unpackWord(this.H0, paramArrayOfbyte, paramInt);
    unpackWord(this.H1, paramArrayOfbyte, paramInt + 4);
    unpackWord(this.H2, paramArrayOfbyte, paramInt + 8);
    unpackWord(this.H3, paramArrayOfbyte, paramInt + 12);
    reset();
    return 16;
  }
  
  public void reset() {
    super.reset();
    this.H0 = 1732584193;
    this.H1 = -271733879;
    this.H2 = -1732584194;
    this.H3 = 271733878;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  private int RL(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
  }
  
  private int f1(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 ^ paramInt2 ^ paramInt3;
  }
  
  private int f2(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
  }
  
  private int f3(int paramInt1, int paramInt2, int paramInt3) {
    return (paramInt1 | paramInt2 ^ 0xFFFFFFFF) ^ paramInt3;
  }
  
  private int f4(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
  }
  
  private int F1(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f1(paramInt2, paramInt3, paramInt4) + paramInt5, paramInt6);
  }
  
  private int F2(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f2(paramInt2, paramInt3, paramInt4) + paramInt5 + 1518500249, paramInt6);
  }
  
  private int F3(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f3(paramInt2, paramInt3, paramInt4) + paramInt5 + 1859775393, paramInt6);
  }
  
  private int F4(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f4(paramInt2, paramInt3, paramInt4) + paramInt5 + -1894007588, paramInt6);
  }
  
  private int FF1(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f1(paramInt2, paramInt3, paramInt4) + paramInt5, paramInt6);
  }
  
  private int FF2(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f2(paramInt2, paramInt3, paramInt4) + paramInt5 + 1836072691, paramInt6);
  }
  
  private int FF3(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f3(paramInt2, paramInt3, paramInt4) + paramInt5 + 1548603684, paramInt6);
  }
  
  private int FF4(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return RL(paramInt1 + f4(paramInt2, paramInt3, paramInt4) + paramInt5 + 1352829926, paramInt6);
  }
  
  protected void processBlock() {
    int j = this.H0;
    int i = j;
    int m = this.H1;
    int k = m;
    int i1 = this.H2;
    int n = i1;
    int i3 = this.H3;
    int i2 = i3;
    i = F1(i, k, n, i2, this.X[0], 11);
    i2 = F1(i2, i, k, n, this.X[1], 14);
    n = F1(n, i2, i, k, this.X[2], 15);
    k = F1(k, n, i2, i, this.X[3], 12);
    i = F1(i, k, n, i2, this.X[4], 5);
    i2 = F1(i2, i, k, n, this.X[5], 8);
    n = F1(n, i2, i, k, this.X[6], 7);
    k = F1(k, n, i2, i, this.X[7], 9);
    i = F1(i, k, n, i2, this.X[8], 11);
    i2 = F1(i2, i, k, n, this.X[9], 13);
    n = F1(n, i2, i, k, this.X[10], 14);
    k = F1(k, n, i2, i, this.X[11], 15);
    i = F1(i, k, n, i2, this.X[12], 6);
    i2 = F1(i2, i, k, n, this.X[13], 7);
    n = F1(n, i2, i, k, this.X[14], 9);
    k = F1(k, n, i2, i, this.X[15], 8);
    i = F2(i, k, n, i2, this.X[7], 7);
    i2 = F2(i2, i, k, n, this.X[4], 6);
    n = F2(n, i2, i, k, this.X[13], 8);
    k = F2(k, n, i2, i, this.X[1], 13);
    i = F2(i, k, n, i2, this.X[10], 11);
    i2 = F2(i2, i, k, n, this.X[6], 9);
    n = F2(n, i2, i, k, this.X[15], 7);
    k = F2(k, n, i2, i, this.X[3], 15);
    i = F2(i, k, n, i2, this.X[12], 7);
    i2 = F2(i2, i, k, n, this.X[0], 12);
    n = F2(n, i2, i, k, this.X[9], 15);
    k = F2(k, n, i2, i, this.X[5], 9);
    i = F2(i, k, n, i2, this.X[2], 11);
    i2 = F2(i2, i, k, n, this.X[14], 7);
    n = F2(n, i2, i, k, this.X[11], 13);
    k = F2(k, n, i2, i, this.X[8], 12);
    i = F3(i, k, n, i2, this.X[3], 11);
    i2 = F3(i2, i, k, n, this.X[10], 13);
    n = F3(n, i2, i, k, this.X[14], 6);
    k = F3(k, n, i2, i, this.X[4], 7);
    i = F3(i, k, n, i2, this.X[9], 14);
    i2 = F3(i2, i, k, n, this.X[15], 9);
    n = F3(n, i2, i, k, this.X[8], 13);
    k = F3(k, n, i2, i, this.X[1], 15);
    i = F3(i, k, n, i2, this.X[2], 14);
    i2 = F3(i2, i, k, n, this.X[7], 8);
    n = F3(n, i2, i, k, this.X[0], 13);
    k = F3(k, n, i2, i, this.X[6], 6);
    i = F3(i, k, n, i2, this.X[13], 5);
    i2 = F3(i2, i, k, n, this.X[11], 12);
    n = F3(n, i2, i, k, this.X[5], 7);
    k = F3(k, n, i2, i, this.X[12], 5);
    i = F4(i, k, n, i2, this.X[1], 11);
    i2 = F4(i2, i, k, n, this.X[9], 12);
    n = F4(n, i2, i, k, this.X[11], 14);
    k = F4(k, n, i2, i, this.X[10], 15);
    i = F4(i, k, n, i2, this.X[0], 14);
    i2 = F4(i2, i, k, n, this.X[8], 15);
    n = F4(n, i2, i, k, this.X[12], 9);
    k = F4(k, n, i2, i, this.X[4], 8);
    i = F4(i, k, n, i2, this.X[13], 9);
    i2 = F4(i2, i, k, n, this.X[3], 14);
    n = F4(n, i2, i, k, this.X[7], 5);
    k = F4(k, n, i2, i, this.X[15], 6);
    i = F4(i, k, n, i2, this.X[14], 8);
    i2 = F4(i2, i, k, n, this.X[5], 6);
    n = F4(n, i2, i, k, this.X[6], 5);
    k = F4(k, n, i2, i, this.X[2], 12);
    j = FF4(j, m, i1, i3, this.X[5], 8);
    i3 = FF4(i3, j, m, i1, this.X[14], 9);
    i1 = FF4(i1, i3, j, m, this.X[7], 9);
    m = FF4(m, i1, i3, j, this.X[0], 11);
    j = FF4(j, m, i1, i3, this.X[9], 13);
    i3 = FF4(i3, j, m, i1, this.X[2], 15);
    i1 = FF4(i1, i3, j, m, this.X[11], 15);
    m = FF4(m, i1, i3, j, this.X[4], 5);
    j = FF4(j, m, i1, i3, this.X[13], 7);
    i3 = FF4(i3, j, m, i1, this.X[6], 7);
    i1 = FF4(i1, i3, j, m, this.X[15], 8);
    m = FF4(m, i1, i3, j, this.X[8], 11);
    j = FF4(j, m, i1, i3, this.X[1], 14);
    i3 = FF4(i3, j, m, i1, this.X[10], 14);
    i1 = FF4(i1, i3, j, m, this.X[3], 12);
    m = FF4(m, i1, i3, j, this.X[12], 6);
    j = FF3(j, m, i1, i3, this.X[6], 9);
    i3 = FF3(i3, j, m, i1, this.X[11], 13);
    i1 = FF3(i1, i3, j, m, this.X[3], 15);
    m = FF3(m, i1, i3, j, this.X[7], 7);
    j = FF3(j, m, i1, i3, this.X[0], 12);
    i3 = FF3(i3, j, m, i1, this.X[13], 8);
    i1 = FF3(i1, i3, j, m, this.X[5], 9);
    m = FF3(m, i1, i3, j, this.X[10], 11);
    j = FF3(j, m, i1, i3, this.X[14], 7);
    i3 = FF3(i3, j, m, i1, this.X[15], 7);
    i1 = FF3(i1, i3, j, m, this.X[8], 12);
    m = FF3(m, i1, i3, j, this.X[12], 7);
    j = FF3(j, m, i1, i3, this.X[4], 6);
    i3 = FF3(i3, j, m, i1, this.X[9], 15);
    i1 = FF3(i1, i3, j, m, this.X[1], 13);
    m = FF3(m, i1, i3, j, this.X[2], 11);
    j = FF2(j, m, i1, i3, this.X[15], 9);
    i3 = FF2(i3, j, m, i1, this.X[5], 7);
    i1 = FF2(i1, i3, j, m, this.X[1], 15);
    m = FF2(m, i1, i3, j, this.X[3], 11);
    j = FF2(j, m, i1, i3, this.X[7], 8);
    i3 = FF2(i3, j, m, i1, this.X[14], 6);
    i1 = FF2(i1, i3, j, m, this.X[6], 6);
    m = FF2(m, i1, i3, j, this.X[9], 14);
    j = FF2(j, m, i1, i3, this.X[11], 12);
    i3 = FF2(i3, j, m, i1, this.X[8], 13);
    i1 = FF2(i1, i3, j, m, this.X[12], 5);
    m = FF2(m, i1, i3, j, this.X[2], 14);
    j = FF2(j, m, i1, i3, this.X[10], 13);
    i3 = FF2(i3, j, m, i1, this.X[0], 13);
    i1 = FF2(i1, i3, j, m, this.X[4], 7);
    m = FF2(m, i1, i3, j, this.X[13], 5);
    j = FF1(j, m, i1, i3, this.X[8], 15);
    i3 = FF1(i3, j, m, i1, this.X[6], 5);
    i1 = FF1(i1, i3, j, m, this.X[4], 8);
    m = FF1(m, i1, i3, j, this.X[1], 11);
    j = FF1(j, m, i1, i3, this.X[3], 14);
    i3 = FF1(i3, j, m, i1, this.X[11], 14);
    i1 = FF1(i1, i3, j, m, this.X[15], 6);
    m = FF1(m, i1, i3, j, this.X[0], 14);
    j = FF1(j, m, i1, i3, this.X[5], 6);
    i3 = FF1(i3, j, m, i1, this.X[12], 9);
    i1 = FF1(i1, i3, j, m, this.X[2], 12);
    m = FF1(m, i1, i3, j, this.X[13], 9);
    j = FF1(j, m, i1, i3, this.X[9], 12);
    i3 = FF1(i3, j, m, i1, this.X[7], 5);
    i1 = FF1(i1, i3, j, m, this.X[10], 15);
    m = FF1(m, i1, i3, j, this.X[14], 8);
    i3 += n + this.H1;
    this.H1 = this.H2 + i2 + j;
    this.H2 = this.H3 + i + m;
    this.H3 = this.H0 + k + i1;
    this.H0 = i3;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  public Memoable copy() {
    return new RIPEMD128Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    RIPEMD128Digest rIPEMD128Digest = (RIPEMD128Digest)paramMemoable;
    copyIn(rIPEMD128Digest);
  }
}
