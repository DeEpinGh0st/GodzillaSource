package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

public class RIPEMD160Digest extends GeneralDigest {
  private static final int DIGEST_LENGTH = 20;
  
  private int H0;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int H4;
  
  private int[] X = new int[16];
  
  private int xOff;
  
  public RIPEMD160Digest() {
    reset();
  }
  
  public RIPEMD160Digest(RIPEMD160Digest paramRIPEMD160Digest) {
    super(paramRIPEMD160Digest);
    copyIn(paramRIPEMD160Digest);
  }
  
  private void copyIn(RIPEMD160Digest paramRIPEMD160Digest) {
    copyIn(paramRIPEMD160Digest);
    this.H0 = paramRIPEMD160Digest.H0;
    this.H1 = paramRIPEMD160Digest.H1;
    this.H2 = paramRIPEMD160Digest.H2;
    this.H3 = paramRIPEMD160Digest.H3;
    this.H4 = paramRIPEMD160Digest.H4;
    System.arraycopy(paramRIPEMD160Digest.X, 0, this.X, 0, paramRIPEMD160Digest.X.length);
    this.xOff = paramRIPEMD160Digest.xOff;
  }
  
  public String getAlgorithmName() {
    return "RIPEMD160";
  }
  
  public int getDigestSize() {
    return 20;
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
    unpackWord(this.H4, paramArrayOfbyte, paramInt + 16);
    reset();
    return 20;
  }
  
  public void reset() {
    super.reset();
    this.H0 = 1732584193;
    this.H1 = -271733879;
    this.H2 = -1732584194;
    this.H3 = 271733878;
    this.H4 = -1009589776;
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
  
  private int f5(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 ^ (paramInt2 | paramInt3 ^ 0xFFFFFFFF);
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
    int i5 = this.H4;
    int i4 = i5;
    i = RL(i + f1(k, n, i2) + this.X[0], 11) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f1(i, k, n) + this.X[1], 14) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f1(i4, i, k) + this.X[2], 15) + n;
    i = RL(i, 10);
    n = RL(n + f1(i2, i4, i) + this.X[3], 12) + k;
    i4 = RL(i4, 10);
    k = RL(k + f1(n, i2, i4) + this.X[4], 5) + i;
    i2 = RL(i2, 10);
    i = RL(i + f1(k, n, i2) + this.X[5], 8) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f1(i, k, n) + this.X[6], 7) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f1(i4, i, k) + this.X[7], 9) + n;
    i = RL(i, 10);
    n = RL(n + f1(i2, i4, i) + this.X[8], 11) + k;
    i4 = RL(i4, 10);
    k = RL(k + f1(n, i2, i4) + this.X[9], 13) + i;
    i2 = RL(i2, 10);
    i = RL(i + f1(k, n, i2) + this.X[10], 14) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f1(i, k, n) + this.X[11], 15) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f1(i4, i, k) + this.X[12], 6) + n;
    i = RL(i, 10);
    n = RL(n + f1(i2, i4, i) + this.X[13], 7) + k;
    i4 = RL(i4, 10);
    k = RL(k + f1(n, i2, i4) + this.X[14], 9) + i;
    i2 = RL(i2, 10);
    i = RL(i + f1(k, n, i2) + this.X[15], 8) + i4;
    n = RL(n, 10);
    j = RL(j + f5(m, i1, i3) + this.X[5] + 1352829926, 8) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f5(j, m, i1) + this.X[14] + 1352829926, 9) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f5(i5, j, m) + this.X[7] + 1352829926, 9) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f5(i3, i5, j) + this.X[0] + 1352829926, 11) + m;
    i5 = RL(i5, 10);
    m = RL(m + f5(i1, i3, i5) + this.X[9] + 1352829926, 13) + j;
    i3 = RL(i3, 10);
    j = RL(j + f5(m, i1, i3) + this.X[2] + 1352829926, 15) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f5(j, m, i1) + this.X[11] + 1352829926, 15) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f5(i5, j, m) + this.X[4] + 1352829926, 5) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f5(i3, i5, j) + this.X[13] + 1352829926, 7) + m;
    i5 = RL(i5, 10);
    m = RL(m + f5(i1, i3, i5) + this.X[6] + 1352829926, 7) + j;
    i3 = RL(i3, 10);
    j = RL(j + f5(m, i1, i3) + this.X[15] + 1352829926, 8) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f5(j, m, i1) + this.X[8] + 1352829926, 11) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f5(i5, j, m) + this.X[1] + 1352829926, 14) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f5(i3, i5, j) + this.X[10] + 1352829926, 14) + m;
    i5 = RL(i5, 10);
    m = RL(m + f5(i1, i3, i5) + this.X[3] + 1352829926, 12) + j;
    i3 = RL(i3, 10);
    j = RL(j + f5(m, i1, i3) + this.X[12] + 1352829926, 6) + i5;
    i1 = RL(i1, 10);
    i4 = RL(i4 + f2(i, k, n) + this.X[7] + 1518500249, 7) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f2(i4, i, k) + this.X[4] + 1518500249, 6) + n;
    i = RL(i, 10);
    n = RL(n + f2(i2, i4, i) + this.X[13] + 1518500249, 8) + k;
    i4 = RL(i4, 10);
    k = RL(k + f2(n, i2, i4) + this.X[1] + 1518500249, 13) + i;
    i2 = RL(i2, 10);
    i = RL(i + f2(k, n, i2) + this.X[10] + 1518500249, 11) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f2(i, k, n) + this.X[6] + 1518500249, 9) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f2(i4, i, k) + this.X[15] + 1518500249, 7) + n;
    i = RL(i, 10);
    n = RL(n + f2(i2, i4, i) + this.X[3] + 1518500249, 15) + k;
    i4 = RL(i4, 10);
    k = RL(k + f2(n, i2, i4) + this.X[12] + 1518500249, 7) + i;
    i2 = RL(i2, 10);
    i = RL(i + f2(k, n, i2) + this.X[0] + 1518500249, 12) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f2(i, k, n) + this.X[9] + 1518500249, 15) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f2(i4, i, k) + this.X[5] + 1518500249, 9) + n;
    i = RL(i, 10);
    n = RL(n + f2(i2, i4, i) + this.X[2] + 1518500249, 11) + k;
    i4 = RL(i4, 10);
    k = RL(k + f2(n, i2, i4) + this.X[14] + 1518500249, 7) + i;
    i2 = RL(i2, 10);
    i = RL(i + f2(k, n, i2) + this.X[11] + 1518500249, 13) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f2(i, k, n) + this.X[8] + 1518500249, 12) + i2;
    k = RL(k, 10);
    i5 = RL(i5 + f4(j, m, i1) + this.X[6] + 1548603684, 9) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f4(i5, j, m) + this.X[11] + 1548603684, 13) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f4(i3, i5, j) + this.X[3] + 1548603684, 15) + m;
    i5 = RL(i5, 10);
    m = RL(m + f4(i1, i3, i5) + this.X[7] + 1548603684, 7) + j;
    i3 = RL(i3, 10);
    j = RL(j + f4(m, i1, i3) + this.X[0] + 1548603684, 12) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f4(j, m, i1) + this.X[13] + 1548603684, 8) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f4(i5, j, m) + this.X[5] + 1548603684, 9) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f4(i3, i5, j) + this.X[10] + 1548603684, 11) + m;
    i5 = RL(i5, 10);
    m = RL(m + f4(i1, i3, i5) + this.X[14] + 1548603684, 7) + j;
    i3 = RL(i3, 10);
    j = RL(j + f4(m, i1, i3) + this.X[15] + 1548603684, 7) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f4(j, m, i1) + this.X[8] + 1548603684, 12) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f4(i5, j, m) + this.X[12] + 1548603684, 7) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f4(i3, i5, j) + this.X[4] + 1548603684, 6) + m;
    i5 = RL(i5, 10);
    m = RL(m + f4(i1, i3, i5) + this.X[9] + 1548603684, 15) + j;
    i3 = RL(i3, 10);
    j = RL(j + f4(m, i1, i3) + this.X[1] + 1548603684, 13) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f4(j, m, i1) + this.X[2] + 1548603684, 11) + i3;
    m = RL(m, 10);
    i2 = RL(i2 + f3(i4, i, k) + this.X[3] + 1859775393, 11) + n;
    i = RL(i, 10);
    n = RL(n + f3(i2, i4, i) + this.X[10] + 1859775393, 13) + k;
    i4 = RL(i4, 10);
    k = RL(k + f3(n, i2, i4) + this.X[14] + 1859775393, 6) + i;
    i2 = RL(i2, 10);
    i = RL(i + f3(k, n, i2) + this.X[4] + 1859775393, 7) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f3(i, k, n) + this.X[9] + 1859775393, 14) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f3(i4, i, k) + this.X[15] + 1859775393, 9) + n;
    i = RL(i, 10);
    n = RL(n + f3(i2, i4, i) + this.X[8] + 1859775393, 13) + k;
    i4 = RL(i4, 10);
    k = RL(k + f3(n, i2, i4) + this.X[1] + 1859775393, 15) + i;
    i2 = RL(i2, 10);
    i = RL(i + f3(k, n, i2) + this.X[2] + 1859775393, 14) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f3(i, k, n) + this.X[7] + 1859775393, 8) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f3(i4, i, k) + this.X[0] + 1859775393, 13) + n;
    i = RL(i, 10);
    n = RL(n + f3(i2, i4, i) + this.X[6] + 1859775393, 6) + k;
    i4 = RL(i4, 10);
    k = RL(k + f3(n, i2, i4) + this.X[13] + 1859775393, 5) + i;
    i2 = RL(i2, 10);
    i = RL(i + f3(k, n, i2) + this.X[11] + 1859775393, 12) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f3(i, k, n) + this.X[5] + 1859775393, 7) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f3(i4, i, k) + this.X[12] + 1859775393, 5) + n;
    i = RL(i, 10);
    i3 = RL(i3 + f3(i5, j, m) + this.X[15] + 1836072691, 9) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f3(i3, i5, j) + this.X[5] + 1836072691, 7) + m;
    i5 = RL(i5, 10);
    m = RL(m + f3(i1, i3, i5) + this.X[1] + 1836072691, 15) + j;
    i3 = RL(i3, 10);
    j = RL(j + f3(m, i1, i3) + this.X[3] + 1836072691, 11) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f3(j, m, i1) + this.X[7] + 1836072691, 8) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f3(i5, j, m) + this.X[14] + 1836072691, 6) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f3(i3, i5, j) + this.X[6] + 1836072691, 6) + m;
    i5 = RL(i5, 10);
    m = RL(m + f3(i1, i3, i5) + this.X[9] + 1836072691, 14) + j;
    i3 = RL(i3, 10);
    j = RL(j + f3(m, i1, i3) + this.X[11] + 1836072691, 12) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f3(j, m, i1) + this.X[8] + 1836072691, 13) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f3(i5, j, m) + this.X[12] + 1836072691, 5) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f3(i3, i5, j) + this.X[2] + 1836072691, 14) + m;
    i5 = RL(i5, 10);
    m = RL(m + f3(i1, i3, i5) + this.X[10] + 1836072691, 13) + j;
    i3 = RL(i3, 10);
    j = RL(j + f3(m, i1, i3) + this.X[0] + 1836072691, 13) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f3(j, m, i1) + this.X[4] + 1836072691, 7) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f3(i5, j, m) + this.X[13] + 1836072691, 5) + i1;
    j = RL(j, 10);
    n = RL(n + f4(i2, i4, i) + this.X[1] + -1894007588, 11) + k;
    i4 = RL(i4, 10);
    k = RL(k + f4(n, i2, i4) + this.X[9] + -1894007588, 12) + i;
    i2 = RL(i2, 10);
    i = RL(i + f4(k, n, i2) + this.X[11] + -1894007588, 14) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f4(i, k, n) + this.X[10] + -1894007588, 15) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f4(i4, i, k) + this.X[0] + -1894007588, 14) + n;
    i = RL(i, 10);
    n = RL(n + f4(i2, i4, i) + this.X[8] + -1894007588, 15) + k;
    i4 = RL(i4, 10);
    k = RL(k + f4(n, i2, i4) + this.X[12] + -1894007588, 9) + i;
    i2 = RL(i2, 10);
    i = RL(i + f4(k, n, i2) + this.X[4] + -1894007588, 8) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f4(i, k, n) + this.X[13] + -1894007588, 9) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f4(i4, i, k) + this.X[3] + -1894007588, 14) + n;
    i = RL(i, 10);
    n = RL(n + f4(i2, i4, i) + this.X[7] + -1894007588, 5) + k;
    i4 = RL(i4, 10);
    k = RL(k + f4(n, i2, i4) + this.X[15] + -1894007588, 6) + i;
    i2 = RL(i2, 10);
    i = RL(i + f4(k, n, i2) + this.X[14] + -1894007588, 8) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f4(i, k, n) + this.X[5] + -1894007588, 6) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f4(i4, i, k) + this.X[6] + -1894007588, 5) + n;
    i = RL(i, 10);
    n = RL(n + f4(i2, i4, i) + this.X[2] + -1894007588, 12) + k;
    i4 = RL(i4, 10);
    i1 = RL(i1 + f2(i3, i5, j) + this.X[8] + 2053994217, 15) + m;
    i5 = RL(i5, 10);
    m = RL(m + f2(i1, i3, i5) + this.X[6] + 2053994217, 5) + j;
    i3 = RL(i3, 10);
    j = RL(j + f2(m, i1, i3) + this.X[4] + 2053994217, 8) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f2(j, m, i1) + this.X[1] + 2053994217, 11) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f2(i5, j, m) + this.X[3] + 2053994217, 14) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f2(i3, i5, j) + this.X[11] + 2053994217, 14) + m;
    i5 = RL(i5, 10);
    m = RL(m + f2(i1, i3, i5) + this.X[15] + 2053994217, 6) + j;
    i3 = RL(i3, 10);
    j = RL(j + f2(m, i1, i3) + this.X[0] + 2053994217, 14) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f2(j, m, i1) + this.X[5] + 2053994217, 6) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f2(i5, j, m) + this.X[12] + 2053994217, 9) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f2(i3, i5, j) + this.X[2] + 2053994217, 12) + m;
    i5 = RL(i5, 10);
    m = RL(m + f2(i1, i3, i5) + this.X[13] + 2053994217, 9) + j;
    i3 = RL(i3, 10);
    j = RL(j + f2(m, i1, i3) + this.X[9] + 2053994217, 12) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f2(j, m, i1) + this.X[7] + 2053994217, 5) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f2(i5, j, m) + this.X[10] + 2053994217, 15) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f2(i3, i5, j) + this.X[14] + 2053994217, 8) + m;
    i5 = RL(i5, 10);
    k = RL(k + f5(n, i2, i4) + this.X[4] + -1454113458, 9) + i;
    i2 = RL(i2, 10);
    i = RL(i + f5(k, n, i2) + this.X[0] + -1454113458, 15) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f5(i, k, n) + this.X[5] + -1454113458, 5) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f5(i4, i, k) + this.X[9] + -1454113458, 11) + n;
    i = RL(i, 10);
    n = RL(n + f5(i2, i4, i) + this.X[7] + -1454113458, 6) + k;
    i4 = RL(i4, 10);
    k = RL(k + f5(n, i2, i4) + this.X[12] + -1454113458, 8) + i;
    i2 = RL(i2, 10);
    i = RL(i + f5(k, n, i2) + this.X[2] + -1454113458, 13) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f5(i, k, n) + this.X[10] + -1454113458, 12) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f5(i4, i, k) + this.X[14] + -1454113458, 5) + n;
    i = RL(i, 10);
    n = RL(n + f5(i2, i4, i) + this.X[1] + -1454113458, 12) + k;
    i4 = RL(i4, 10);
    k = RL(k + f5(n, i2, i4) + this.X[3] + -1454113458, 13) + i;
    i2 = RL(i2, 10);
    i = RL(i + f5(k, n, i2) + this.X[8] + -1454113458, 14) + i4;
    n = RL(n, 10);
    i4 = RL(i4 + f5(i, k, n) + this.X[11] + -1454113458, 11) + i2;
    k = RL(k, 10);
    i2 = RL(i2 + f5(i4, i, k) + this.X[6] + -1454113458, 8) + n;
    i = RL(i, 10);
    n = RL(n + f5(i2, i4, i) + this.X[15] + -1454113458, 5) + k;
    i4 = RL(i4, 10);
    k = RL(k + f5(n, i2, i4) + this.X[13] + -1454113458, 6) + i;
    i2 = RL(i2, 10);
    m = RL(m + f1(i1, i3, i5) + this.X[12], 8) + j;
    i3 = RL(i3, 10);
    j = RL(j + f1(m, i1, i3) + this.X[15], 5) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f1(j, m, i1) + this.X[10], 12) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f1(i5, j, m) + this.X[4], 9) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f1(i3, i5, j) + this.X[1], 12) + m;
    i5 = RL(i5, 10);
    m = RL(m + f1(i1, i3, i5) + this.X[5], 5) + j;
    i3 = RL(i3, 10);
    j = RL(j + f1(m, i1, i3) + this.X[8], 14) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f1(j, m, i1) + this.X[7], 6) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f1(i5, j, m) + this.X[6], 8) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f1(i3, i5, j) + this.X[2], 13) + m;
    i5 = RL(i5, 10);
    m = RL(m + f1(i1, i3, i5) + this.X[13], 6) + j;
    i3 = RL(i3, 10);
    j = RL(j + f1(m, i1, i3) + this.X[14], 5) + i5;
    i1 = RL(i1, 10);
    i5 = RL(i5 + f1(j, m, i1) + this.X[0], 15) + i3;
    m = RL(m, 10);
    i3 = RL(i3 + f1(i5, j, m) + this.X[3], 13) + i1;
    j = RL(j, 10);
    i1 = RL(i1 + f1(i3, i5, j) + this.X[9], 11) + m;
    i5 = RL(i5, 10);
    m = RL(m + f1(i1, i3, i5) + this.X[11], 11) + j;
    i3 = RL(i3, 10);
    i3 += n + this.H1;
    this.H1 = this.H2 + i2 + i5;
    this.H2 = this.H3 + i4 + j;
    this.H3 = this.H4 + i + m;
    this.H4 = this.H0 + k + i1;
    this.H0 = i3;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  public Memoable copy() {
    return new RIPEMD160Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    RIPEMD160Digest rIPEMD160Digest = (RIPEMD160Digest)paramMemoable;
    copyIn(rIPEMD160Digest);
  }
}
