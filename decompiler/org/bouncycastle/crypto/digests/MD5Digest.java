package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class MD5Digest extends GeneralDigest implements EncodableDigest {
  private static final int DIGEST_LENGTH = 16;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int H4;
  
  private int[] X = new int[16];
  
  private int xOff;
  
  private static final int S11 = 7;
  
  private static final int S12 = 12;
  
  private static final int S13 = 17;
  
  private static final int S14 = 22;
  
  private static final int S21 = 5;
  
  private static final int S22 = 9;
  
  private static final int S23 = 14;
  
  private static final int S24 = 20;
  
  private static final int S31 = 4;
  
  private static final int S32 = 11;
  
  private static final int S33 = 16;
  
  private static final int S34 = 23;
  
  private static final int S41 = 6;
  
  private static final int S42 = 10;
  
  private static final int S43 = 15;
  
  private static final int S44 = 21;
  
  public MD5Digest() {
    reset();
  }
  
  public MD5Digest(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    this.H1 = Pack.bigEndianToInt(paramArrayOfbyte, 16);
    this.H2 = Pack.bigEndianToInt(paramArrayOfbyte, 20);
    this.H3 = Pack.bigEndianToInt(paramArrayOfbyte, 24);
    this.H4 = Pack.bigEndianToInt(paramArrayOfbyte, 28);
    this.xOff = Pack.bigEndianToInt(paramArrayOfbyte, 32);
    for (byte b = 0; b != this.xOff; b++)
      this.X[b] = Pack.bigEndianToInt(paramArrayOfbyte, 36 + b * 4); 
  }
  
  public MD5Digest(MD5Digest paramMD5Digest) {
    super(paramMD5Digest);
    copyIn(paramMD5Digest);
  }
  
  private void copyIn(MD5Digest paramMD5Digest) {
    copyIn(paramMD5Digest);
    this.H1 = paramMD5Digest.H1;
    this.H2 = paramMD5Digest.H2;
    this.H3 = paramMD5Digest.H3;
    this.H4 = paramMD5Digest.H4;
    System.arraycopy(paramMD5Digest.X, 0, this.X, 0, paramMD5Digest.X.length);
    this.xOff = paramMD5Digest.xOff;
  }
  
  public String getAlgorithmName() {
    return "MD5";
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
    return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
  }
  
  private int H(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt1 ^ paramInt2 ^ paramInt3;
  }
  
  private int K(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt2 ^ (paramInt1 | paramInt3 ^ 0xFFFFFFFF);
  }
  
  protected void processBlock() {
    int i = this.H1;
    int j = this.H2;
    int k = this.H3;
    int m = this.H4;
    i = rotateLeft(i + F(j, k, m) + this.X[0] + -680876936, 7) + j;
    m = rotateLeft(m + F(i, j, k) + this.X[1] + -389564586, 12) + i;
    k = rotateLeft(k + F(m, i, j) + this.X[2] + 606105819, 17) + m;
    j = rotateLeft(j + F(k, m, i) + this.X[3] + -1044525330, 22) + k;
    i = rotateLeft(i + F(j, k, m) + this.X[4] + -176418897, 7) + j;
    m = rotateLeft(m + F(i, j, k) + this.X[5] + 1200080426, 12) + i;
    k = rotateLeft(k + F(m, i, j) + this.X[6] + -1473231341, 17) + m;
    j = rotateLeft(j + F(k, m, i) + this.X[7] + -45705983, 22) + k;
    i = rotateLeft(i + F(j, k, m) + this.X[8] + 1770035416, 7) + j;
    m = rotateLeft(m + F(i, j, k) + this.X[9] + -1958414417, 12) + i;
    k = rotateLeft(k + F(m, i, j) + this.X[10] + -42063, 17) + m;
    j = rotateLeft(j + F(k, m, i) + this.X[11] + -1990404162, 22) + k;
    i = rotateLeft(i + F(j, k, m) + this.X[12] + 1804603682, 7) + j;
    m = rotateLeft(m + F(i, j, k) + this.X[13] + -40341101, 12) + i;
    k = rotateLeft(k + F(m, i, j) + this.X[14] + -1502002290, 17) + m;
    j = rotateLeft(j + F(k, m, i) + this.X[15] + 1236535329, 22) + k;
    i = rotateLeft(i + G(j, k, m) + this.X[1] + -165796510, 5) + j;
    m = rotateLeft(m + G(i, j, k) + this.X[6] + -1069501632, 9) + i;
    k = rotateLeft(k + G(m, i, j) + this.X[11] + 643717713, 14) + m;
    j = rotateLeft(j + G(k, m, i) + this.X[0] + -373897302, 20) + k;
    i = rotateLeft(i + G(j, k, m) + this.X[5] + -701558691, 5) + j;
    m = rotateLeft(m + G(i, j, k) + this.X[10] + 38016083, 9) + i;
    k = rotateLeft(k + G(m, i, j) + this.X[15] + -660478335, 14) + m;
    j = rotateLeft(j + G(k, m, i) + this.X[4] + -405537848, 20) + k;
    i = rotateLeft(i + G(j, k, m) + this.X[9] + 568446438, 5) + j;
    m = rotateLeft(m + G(i, j, k) + this.X[14] + -1019803690, 9) + i;
    k = rotateLeft(k + G(m, i, j) + this.X[3] + -187363961, 14) + m;
    j = rotateLeft(j + G(k, m, i) + this.X[8] + 1163531501, 20) + k;
    i = rotateLeft(i + G(j, k, m) + this.X[13] + -1444681467, 5) + j;
    m = rotateLeft(m + G(i, j, k) + this.X[2] + -51403784, 9) + i;
    k = rotateLeft(k + G(m, i, j) + this.X[7] + 1735328473, 14) + m;
    j = rotateLeft(j + G(k, m, i) + this.X[12] + -1926607734, 20) + k;
    i = rotateLeft(i + H(j, k, m) + this.X[5] + -378558, 4) + j;
    m = rotateLeft(m + H(i, j, k) + this.X[8] + -2022574463, 11) + i;
    k = rotateLeft(k + H(m, i, j) + this.X[11] + 1839030562, 16) + m;
    j = rotateLeft(j + H(k, m, i) + this.X[14] + -35309556, 23) + k;
    i = rotateLeft(i + H(j, k, m) + this.X[1] + -1530992060, 4) + j;
    m = rotateLeft(m + H(i, j, k) + this.X[4] + 1272893353, 11) + i;
    k = rotateLeft(k + H(m, i, j) + this.X[7] + -155497632, 16) + m;
    j = rotateLeft(j + H(k, m, i) + this.X[10] + -1094730640, 23) + k;
    i = rotateLeft(i + H(j, k, m) + this.X[13] + 681279174, 4) + j;
    m = rotateLeft(m + H(i, j, k) + this.X[0] + -358537222, 11) + i;
    k = rotateLeft(k + H(m, i, j) + this.X[3] + -722521979, 16) + m;
    j = rotateLeft(j + H(k, m, i) + this.X[6] + 76029189, 23) + k;
    i = rotateLeft(i + H(j, k, m) + this.X[9] + -640364487, 4) + j;
    m = rotateLeft(m + H(i, j, k) + this.X[12] + -421815835, 11) + i;
    k = rotateLeft(k + H(m, i, j) + this.X[15] + 530742520, 16) + m;
    j = rotateLeft(j + H(k, m, i) + this.X[2] + -995338651, 23) + k;
    i = rotateLeft(i + K(j, k, m) + this.X[0] + -198630844, 6) + j;
    m = rotateLeft(m + K(i, j, k) + this.X[7] + 1126891415, 10) + i;
    k = rotateLeft(k + K(m, i, j) + this.X[14] + -1416354905, 15) + m;
    j = rotateLeft(j + K(k, m, i) + this.X[5] + -57434055, 21) + k;
    i = rotateLeft(i + K(j, k, m) + this.X[12] + 1700485571, 6) + j;
    m = rotateLeft(m + K(i, j, k) + this.X[3] + -1894986606, 10) + i;
    k = rotateLeft(k + K(m, i, j) + this.X[10] + -1051523, 15) + m;
    j = rotateLeft(j + K(k, m, i) + this.X[1] + -2054922799, 21) + k;
    i = rotateLeft(i + K(j, k, m) + this.X[8] + 1873313359, 6) + j;
    m = rotateLeft(m + K(i, j, k) + this.X[15] + -30611744, 10) + i;
    k = rotateLeft(k + K(m, i, j) + this.X[6] + -1560198380, 15) + m;
    j = rotateLeft(j + K(k, m, i) + this.X[13] + 1309151649, 21) + k;
    i = rotateLeft(i + K(j, k, m) + this.X[4] + -145523070, 6) + j;
    m = rotateLeft(m + K(i, j, k) + this.X[11] + -1120210379, 10) + i;
    k = rotateLeft(k + K(m, i, j) + this.X[2] + 718787259, 15) + m;
    j = rotateLeft(j + K(k, m, i) + this.X[9] + -343485551, 21) + k;
    this.H1 += i;
    this.H2 += j;
    this.H3 += k;
    this.H4 += m;
    this.xOff = 0;
    for (byte b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
  }
  
  public Memoable copy() {
    return new MD5Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    MD5Digest mD5Digest = (MD5Digest)paramMemoable;
    copyIn(mD5Digest);
  }
  
  public byte[] getEncodedState() {
    byte[] arrayOfByte = new byte[36 + this.xOff * 4];
    populateState(arrayOfByte);
    Pack.intToBigEndian(this.H1, arrayOfByte, 16);
    Pack.intToBigEndian(this.H2, arrayOfByte, 20);
    Pack.intToBigEndian(this.H3, arrayOfByte, 24);
    Pack.intToBigEndian(this.H4, arrayOfByte, 28);
    Pack.intToBigEndian(this.xOff, arrayOfByte, 32);
    for (byte b = 0; b != this.xOff; b++)
      Pack.intToBigEndian(this.X[b], arrayOfByte, 36 + b * 4); 
    return arrayOfByte;
  }
}
