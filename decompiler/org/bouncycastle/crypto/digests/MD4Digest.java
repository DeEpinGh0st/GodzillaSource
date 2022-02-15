package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

public class MD4Digest extends GeneralDigest {
  private static final int DIGEST_LENGTH = 16;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int H4;
  
  private int[] X = new int[16];
  
  private int xOff;
  
  private static final int S11 = 3;
  
  private static final int S12 = 7;
  
  private static final int S13 = 11;
  
  private static final int S14 = 19;
  
  private static final int S21 = 3;
  
  private static final int S22 = 5;
  
  private static final int S23 = 9;
  
  private static final int S24 = 13;
  
  private static final int S31 = 3;
  
  private static final int S32 = 9;
  
  private static final int S33 = 11;
  
  private static final int S34 = 15;
  
  public MD4Digest() {
    reset();
  }
  
  public MD4Digest(MD4Digest paramMD4Digest) {
    super(paramMD4Digest);
    copyIn(paramMD4Digest);
  }
  
  private void copyIn(MD4Digest paramMD4Digest) {
    copyIn(paramMD4Digest);
    this.H1 = paramMD4Digest.H1;
    this.H2 = paramMD4Digest.H2;
    this.H3 = paramMD4Digest.H3;
    this.H4 = paramMD4Digest.H4;
    System.arraycopy(paramMD4Digest.X, 0, this.X, 0, paramMD4Digest.X.length);
    this.xOff = paramMD4Digest.xOff;
  }
  
  public String getAlgorithmName() {
    return "MD4";
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
    unpackWord(this.H1, paramArrayOfbyte, paramInt);
    unpackWord(this.H2, paramArrayOfbyte, paramInt + 4);
    unpackWord(this.H3, paramArrayOfbyte, paramInt + 8);
    unpackWord(this.H4, paramArrayOfbyte, paramInt + 12);
    reset();
    return 16;
  }
  
  public void reset() {
    super.reset();
    this.H1 = 1732584193;
    this.H2 = -271733879;
    this.H3 = -1732584194;
    this.H4 = 271733878;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  private int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
  }
  
  private int F(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
  }
  
  private int G(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt2 | paramInt1 & paramInt3 | paramInt2 & paramInt3;
  }
  
  private int H(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 ^ paramInt2 ^ paramInt3;
  }
  
  protected void processBlock() {
    int i = this.H1;
    int j = this.H2;
    int k = this.H3;
    int m = this.H4;
    i = rotateLeft(i + F(j, k, m) + this.X[0], 3);
    m = rotateLeft(m + F(i, j, k) + this.X[1], 7);
    k = rotateLeft(k + F(m, i, j) + this.X[2], 11);
    j = rotateLeft(j + F(k, m, i) + this.X[3], 19);
    i = rotateLeft(i + F(j, k, m) + this.X[4], 3);
    m = rotateLeft(m + F(i, j, k) + this.X[5], 7);
    k = rotateLeft(k + F(m, i, j) + this.X[6], 11);
    j = rotateLeft(j + F(k, m, i) + this.X[7], 19);
    i = rotateLeft(i + F(j, k, m) + this.X[8], 3);
    m = rotateLeft(m + F(i, j, k) + this.X[9], 7);
    k = rotateLeft(k + F(m, i, j) + this.X[10], 11);
    j = rotateLeft(j + F(k, m, i) + this.X[11], 19);
    i = rotateLeft(i + F(j, k, m) + this.X[12], 3);
    m = rotateLeft(m + F(i, j, k) + this.X[13], 7);
    k = rotateLeft(k + F(m, i, j) + this.X[14], 11);
    j = rotateLeft(j + F(k, m, i) + this.X[15], 19);
    i = rotateLeft(i + G(j, k, m) + this.X[0] + 1518500249, 3);
    m = rotateLeft(m + G(i, j, k) + this.X[4] + 1518500249, 5);
    k = rotateLeft(k + G(m, i, j) + this.X[8] + 1518500249, 9);
    j = rotateLeft(j + G(k, m, i) + this.X[12] + 1518500249, 13);
    i = rotateLeft(i + G(j, k, m) + this.X[1] + 1518500249, 3);
    m = rotateLeft(m + G(i, j, k) + this.X[5] + 1518500249, 5);
    k = rotateLeft(k + G(m, i, j) + this.X[9] + 1518500249, 9);
    j = rotateLeft(j + G(k, m, i) + this.X[13] + 1518500249, 13);
    i = rotateLeft(i + G(j, k, m) + this.X[2] + 1518500249, 3);
    m = rotateLeft(m + G(i, j, k) + this.X[6] + 1518500249, 5);
    k = rotateLeft(k + G(m, i, j) + this.X[10] + 1518500249, 9);
    j = rotateLeft(j + G(k, m, i) + this.X[14] + 1518500249, 13);
    i = rotateLeft(i + G(j, k, m) + this.X[3] + 1518500249, 3);
    m = rotateLeft(m + G(i, j, k) + this.X[7] + 1518500249, 5);
    k = rotateLeft(k + G(m, i, j) + this.X[11] + 1518500249, 9);
    j = rotateLeft(j + G(k, m, i) + this.X[15] + 1518500249, 13);
    i = rotateLeft(i + H(j, k, m) + this.X[0] + 1859775393, 3);
    m = rotateLeft(m + H(i, j, k) + this.X[8] + 1859775393, 9);
    k = rotateLeft(k + H(m, i, j) + this.X[4] + 1859775393, 11);
    j = rotateLeft(j + H(k, m, i) + this.X[12] + 1859775393, 15);
    i = rotateLeft(i + H(j, k, m) + this.X[2] + 1859775393, 3);
    m = rotateLeft(m + H(i, j, k) + this.X[10] + 1859775393, 9);
    k = rotateLeft(k + H(m, i, j) + this.X[6] + 1859775393, 11);
    j = rotateLeft(j + H(k, m, i) + this.X[14] + 1859775393, 15);
    i = rotateLeft(i + H(j, k, m) + this.X[1] + 1859775393, 3);
    m = rotateLeft(m + H(i, j, k) + this.X[9] + 1859775393, 9);
    k = rotateLeft(k + H(m, i, j) + this.X[5] + 1859775393, 11);
    j = rotateLeft(j + H(k, m, i) + this.X[13] + 1859775393, 15);
    i = rotateLeft(i + H(j, k, m) + this.X[3] + 1859775393, 3);
    m = rotateLeft(m + H(i, j, k) + this.X[11] + 1859775393, 9);
    k = rotateLeft(k + H(m, i, j) + this.X[7] + 1859775393, 11);
    j = rotateLeft(j + H(k, m, i) + this.X[15] + 1859775393, 15);
    this.H1 += i;
    this.H2 += j;
    this.H3 += k;
    this.H4 += m;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  public Memoable copy() {
    return new MD4Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    MD4Digest mD4Digest = (MD4Digest)paramMemoable;
    copyIn(mD4Digest);
  }
}
