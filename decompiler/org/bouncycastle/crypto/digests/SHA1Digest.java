package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA1Digest extends GeneralDigest implements EncodableDigest {
  private static final int DIGEST_LENGTH = 20;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int H4;
  
  private int H5;
  
  private int[] X = new int[80];
  
  private int xOff;
  
  private static final int Y1 = 1518500249;
  
  private static final int Y2 = 1859775393;
  
  private static final int Y3 = -1894007588;
  
  private static final int Y4 = -899497514;
  
  public SHA1Digest() {
    reset();
  }
  
  public SHA1Digest(SHA1Digest paramSHA1Digest) {
    super(paramSHA1Digest);
    copyIn(paramSHA1Digest);
  }
  
  public SHA1Digest(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    this.H1 = Pack.bigEndianToInt(paramArrayOfbyte, 16);
    this.H2 = Pack.bigEndianToInt(paramArrayOfbyte, 20);
    this.H3 = Pack.bigEndianToInt(paramArrayOfbyte, 24);
    this.H4 = Pack.bigEndianToInt(paramArrayOfbyte, 28);
    this.H5 = Pack.bigEndianToInt(paramArrayOfbyte, 32);
    this.xOff = Pack.bigEndianToInt(paramArrayOfbyte, 36);
    for (byte b = 0; b != this.xOff; b++)
      this.X[b] = Pack.bigEndianToInt(paramArrayOfbyte, 40 + b * 4); 
  }
  
  private void copyIn(SHA1Digest paramSHA1Digest) {
    this.H1 = paramSHA1Digest.H1;
    this.H2 = paramSHA1Digest.H2;
    this.H3 = paramSHA1Digest.H3;
    this.H4 = paramSHA1Digest.H4;
    this.H5 = paramSHA1Digest.H5;
    System.arraycopy(paramSHA1Digest.X, 0, this.X, 0, paramSHA1Digest.X.length);
    this.xOff = paramSHA1Digest.xOff;
  }
  
  public String getAlgorithmName() {
    return "SHA-1";
  }
  
  public int getDigestSize() {
    return 20;
  }
  
  protected void processWord(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte[paramInt] << 24;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 16;
    i |= (paramArrayOfbyte[++paramInt] & 0xFF) << 8;
    i |= paramArrayOfbyte[++paramInt] & 0xFF;
    this.X[this.xOff] = i;
    if (++this.xOff == 16)
      processBlock(); 
  }
  
  protected void processLength(long paramLong) {
    if (this.xOff > 14)
      processBlock(); 
    this.X[14] = (int)(paramLong >>> 32L);
    this.X[15] = (int)(paramLong & 0xFFFFFFFFFFFFFFFFL);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    Pack.intToBigEndian(this.H1, paramArrayOfbyte, paramInt);
    Pack.intToBigEndian(this.H2, paramArrayOfbyte, paramInt + 4);
    Pack.intToBigEndian(this.H3, paramArrayOfbyte, paramInt + 8);
    Pack.intToBigEndian(this.H4, paramArrayOfbyte, paramInt + 12);
    Pack.intToBigEndian(this.H5, paramArrayOfbyte, paramInt + 16);
    reset();
    return 20;
  }
  
  public void reset() {
    super.reset();
    this.H1 = 1732584193;
    this.H2 = -271733879;
    this.H3 = -1732584194;
    this.H4 = 271733878;
    this.H5 = -1009589776;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  private int f(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
  }
  
  private int h(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 ^ paramInt2 ^ paramInt3;
  }
  
  private int g(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 & paramInt2 | paramInt1 & paramInt3 | paramInt2 & paramInt3;
  }
  
  protected void processBlock() {
    int i;
    for (i = 16; i < 80; i++) {
      int i1 = this.X[i - 3] ^ this.X[i - 8] ^ this.X[i - 14] ^ this.X[i - 16];
      this.X[i] = i1 << 1 | i1 >>> 31;
    } 
    i = this.H1;
    int j = this.H2;
    int k = this.H3;
    int m = this.H4;
    int n = this.H5;
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < 4; b2++) {
      n += (i << 5 | i >>> 27) + f(j, k, m) + this.X[b1++] + 1518500249;
      j = j << 30 | j >>> 2;
      m += (n << 5 | n >>> 27) + f(i, j, k) + this.X[b1++] + 1518500249;
      i = i << 30 | i >>> 2;
      k += (m << 5 | m >>> 27) + f(n, i, j) + this.X[b1++] + 1518500249;
      n = n << 30 | n >>> 2;
      j += (k << 5 | k >>> 27) + f(m, n, i) + this.X[b1++] + 1518500249;
      m = m << 30 | m >>> 2;
      i += (j << 5 | j >>> 27) + f(k, m, n) + this.X[b1++] + 1518500249;
      k = k << 30 | k >>> 2;
    } 
    for (b2 = 0; b2 < 4; b2++) {
      n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[b1++] + 1859775393;
      j = j << 30 | j >>> 2;
      m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[b1++] + 1859775393;
      i = i << 30 | i >>> 2;
      k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[b1++] + 1859775393;
      n = n << 30 | n >>> 2;
      j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[b1++] + 1859775393;
      m = m << 30 | m >>> 2;
      i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[b1++] + 1859775393;
      k = k << 30 | k >>> 2;
    } 
    for (b2 = 0; b2 < 4; b2++) {
      n += (i << 5 | i >>> 27) + g(j, k, m) + this.X[b1++] + -1894007588;
      j = j << 30 | j >>> 2;
      m += (n << 5 | n >>> 27) + g(i, j, k) + this.X[b1++] + -1894007588;
      i = i << 30 | i >>> 2;
      k += (m << 5 | m >>> 27) + g(n, i, j) + this.X[b1++] + -1894007588;
      n = n << 30 | n >>> 2;
      j += (k << 5 | k >>> 27) + g(m, n, i) + this.X[b1++] + -1894007588;
      m = m << 30 | m >>> 2;
      i += (j << 5 | j >>> 27) + g(k, m, n) + this.X[b1++] + -1894007588;
      k = k << 30 | k >>> 2;
    } 
    for (b2 = 0; b2 <= 3; b2++) {
      n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[b1++] + -899497514;
      j = j << 30 | j >>> 2;
      m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[b1++] + -899497514;
      i = i << 30 | i >>> 2;
      k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[b1++] + -899497514;
      n = n << 30 | n >>> 2;
      j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[b1++] + -899497514;
      m = m << 30 | m >>> 2;
      i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[b1++] + -899497514;
      k = k << 30 | k >>> 2;
    } 
    this.H1 += i;
    this.H2 += j;
    this.H3 += k;
    this.H4 += m;
    this.H5 += n;
    this.xOff = 0;
    for (b2 = 0; b2 < 16; b2++)
      this.X[b2] = 0; 
  }
  
  public Memoable copy() {
    return new SHA1Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    SHA1Digest sHA1Digest = (SHA1Digest)paramMemoable;
    copyIn(sHA1Digest);
    copyIn(sHA1Digest);
  }
  
  public byte[] getEncodedState() {
    byte[] arrayOfByte = new byte[40 + this.xOff * 4];
    populateState(arrayOfByte);
    Pack.intToBigEndian(this.H1, arrayOfByte, 16);
    Pack.intToBigEndian(this.H2, arrayOfByte, 20);
    Pack.intToBigEndian(this.H3, arrayOfByte, 24);
    Pack.intToBigEndian(this.H4, arrayOfByte, 28);
    Pack.intToBigEndian(this.H5, arrayOfByte, 32);
    Pack.intToBigEndian(this.xOff, arrayOfByte, 36);
    for (byte b = 0; b != this.xOff; b++)
      Pack.intToBigEndian(this.X[b], arrayOfByte, 40 + b * 4); 
    return arrayOfByte;
  }
}
