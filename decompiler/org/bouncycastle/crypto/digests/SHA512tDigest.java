package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.MemoableResetException;
import org.bouncycastle.util.Pack;

public class SHA512tDigest extends LongDigest {
  private int digestLength;
  
  private long H1t;
  
  private long H2t;
  
  private long H3t;
  
  private long H4t;
  
  private long H5t;
  
  private long H6t;
  
  private long H7t;
  
  private long H8t;
  
  public SHA512tDigest(int paramInt) {
    if (paramInt >= 512)
      throw new IllegalArgumentException("bitLength cannot be >= 512"); 
    if (paramInt % 8 != 0)
      throw new IllegalArgumentException("bitLength needs to be a multiple of 8"); 
    if (paramInt == 384)
      throw new IllegalArgumentException("bitLength cannot be 384 use SHA384 instead"); 
    this.digestLength = paramInt / 8;
    tIvGenerate(this.digestLength * 8);
    reset();
  }
  
  public SHA512tDigest(SHA512tDigest paramSHA512tDigest) {
    super(paramSHA512tDigest);
    this.digestLength = paramSHA512tDigest.digestLength;
    reset(paramSHA512tDigest);
  }
  
  public SHA512tDigest(byte[] paramArrayOfbyte) {
    this(readDigestLength(paramArrayOfbyte));
    restoreState(paramArrayOfbyte);
  }
  
  private static int readDigestLength(byte[] paramArrayOfbyte) {
    return Pack.bigEndianToInt(paramArrayOfbyte, paramArrayOfbyte.length - 4);
  }
  
  public String getAlgorithmName() {
    return "SHA-512/" + Integer.toString(this.digestLength * 8);
  }
  
  public int getDigestSize() {
    return this.digestLength;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    longToBigEndian(this.H1, paramArrayOfbyte, paramInt, this.digestLength);
    longToBigEndian(this.H2, paramArrayOfbyte, paramInt + 8, this.digestLength - 8);
    longToBigEndian(this.H3, paramArrayOfbyte, paramInt + 16, this.digestLength - 16);
    longToBigEndian(this.H4, paramArrayOfbyte, paramInt + 24, this.digestLength - 24);
    longToBigEndian(this.H5, paramArrayOfbyte, paramInt + 32, this.digestLength - 32);
    longToBigEndian(this.H6, paramArrayOfbyte, paramInt + 40, this.digestLength - 40);
    longToBigEndian(this.H7, paramArrayOfbyte, paramInt + 48, this.digestLength - 48);
    longToBigEndian(this.H8, paramArrayOfbyte, paramInt + 56, this.digestLength - 56);
    reset();
    return this.digestLength;
  }
  
  public void reset() {
    super.reset();
    this.H1 = this.H1t;
    this.H2 = this.H2t;
    this.H3 = this.H3t;
    this.H4 = this.H4t;
    this.H5 = this.H5t;
    this.H6 = this.H6t;
    this.H7 = this.H7t;
    this.H8 = this.H8t;
  }
  
  private void tIvGenerate(int paramInt) {
    this.H1 = -3482333909917012819L;
    this.H2 = 2216346199247487646L;
    this.H3 = -7364697282686394994L;
    this.H4 = 65953792586715988L;
    this.H5 = -816286391624063116L;
    this.H6 = 4512832404995164602L;
    this.H7 = -5033199132376557362L;
    this.H8 = -124578254951840548L;
    update((byte)83);
    update((byte)72);
    update((byte)65);
    update((byte)45);
    update((byte)53);
    update((byte)49);
    update((byte)50);
    update((byte)47);
    if (paramInt > 100) {
      update((byte)(paramInt / 100 + 48));
      paramInt %= 100;
      update((byte)(paramInt / 10 + 48));
      paramInt %= 10;
      update((byte)(paramInt + 48));
    } else if (paramInt > 10) {
      update((byte)(paramInt / 10 + 48));
      paramInt %= 10;
      update((byte)(paramInt + 48));
    } else {
      update((byte)(paramInt + 48));
    } 
    finish();
    this.H1t = this.H1;
    this.H2t = this.H2;
    this.H3t = this.H3;
    this.H4t = this.H4;
    this.H5t = this.H5;
    this.H6t = this.H6;
    this.H7t = this.H7;
    this.H8t = this.H8;
  }
  
  private static void longToBigEndian(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 > 0) {
      intToBigEndian((int)(paramLong >>> 32L), paramArrayOfbyte, paramInt1, paramInt2);
      if (paramInt2 > 4)
        intToBigEndian((int)(paramLong & 0xFFFFFFFFL), paramArrayOfbyte, paramInt1 + 4, paramInt2 - 4); 
    } 
  }
  
  private static void intToBigEndian(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    int i = Math.min(4, paramInt3);
    while (--i >= 0) {
      int j = 8 * (3 - i);
      paramArrayOfbyte[paramInt2 + i] = (byte)(paramInt1 >>> j);
    } 
  }
  
  public Memoable copy() {
    return new SHA512tDigest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    SHA512tDigest sHA512tDigest = (SHA512tDigest)paramMemoable;
    if (this.digestLength != sHA512tDigest.digestLength)
      throw new MemoableResetException("digestLength inappropriate in other"); 
    copyIn(sHA512tDigest);
    this.H1t = sHA512tDigest.H1t;
    this.H2t = sHA512tDigest.H2t;
    this.H3t = sHA512tDigest.H3t;
    this.H4t = sHA512tDigest.H4t;
    this.H5t = sHA512tDigest.H5t;
    this.H6t = sHA512tDigest.H6t;
    this.H7t = sHA512tDigest.H7t;
    this.H8t = sHA512tDigest.H8t;
  }
  
  public byte[] getEncodedState() {
    int i = getEncodedStateSize();
    byte[] arrayOfByte = new byte[i + 4];
    populateState(arrayOfByte);
    Pack.intToBigEndian(this.digestLength * 8, arrayOfByte, i);
    return arrayOfByte;
  }
}
