package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA512Digest extends LongDigest {
  private static final int DIGEST_LENGTH = 64;
  
  public SHA512Digest() {}
  
  public SHA512Digest(SHA512Digest paramSHA512Digest) {
    super(paramSHA512Digest);
  }
  
  public SHA512Digest(byte[] paramArrayOfbyte) {
    restoreState(paramArrayOfbyte);
  }
  
  public String getAlgorithmName() {
    return "SHA-512";
  }
  
  public int getDigestSize() {
    return 64;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    Pack.longToBigEndian(this.H1, paramArrayOfbyte, paramInt);
    Pack.longToBigEndian(this.H2, paramArrayOfbyte, paramInt + 8);
    Pack.longToBigEndian(this.H3, paramArrayOfbyte, paramInt + 16);
    Pack.longToBigEndian(this.H4, paramArrayOfbyte, paramInt + 24);
    Pack.longToBigEndian(this.H5, paramArrayOfbyte, paramInt + 32);
    Pack.longToBigEndian(this.H6, paramArrayOfbyte, paramInt + 40);
    Pack.longToBigEndian(this.H7, paramArrayOfbyte, paramInt + 48);
    Pack.longToBigEndian(this.H8, paramArrayOfbyte, paramInt + 56);
    reset();
    return 64;
  }
  
  public void reset() {
    super.reset();
    this.H1 = 7640891576956012808L;
    this.H2 = -4942790177534073029L;
    this.H3 = 4354685564936845355L;
    this.H4 = -6534734903238641935L;
    this.H5 = 5840696475078001361L;
    this.H6 = -7276294671716946913L;
    this.H7 = 2270897969802886507L;
    this.H8 = 6620516959819538809L;
  }
  
  public Memoable copy() {
    return new SHA512Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    SHA512Digest sHA512Digest = (SHA512Digest)paramMemoable;
    copyIn(sHA512Digest);
  }
  
  public byte[] getEncodedState() {
    byte[] arrayOfByte = new byte[getEncodedStateSize()];
    populateState(arrayOfByte);
    return arrayOfByte;
  }
}
