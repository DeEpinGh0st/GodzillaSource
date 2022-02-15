package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;

public class MD2Digest implements ExtendedDigest, Memoable {
  private static final int DIGEST_LENGTH = 16;
  
  private byte[] X = new byte[48];
  
  private int xOff;
  
  private byte[] M = new byte[16];
  
  private int mOff;
  
  private byte[] C = new byte[16];
  
  private int COff;
  
  private static final byte[] S = new byte[] { 
      41, 46, 67, -55, -94, -40, 124, 1, 61, 54, 
      84, -95, -20, -16, 6, 19, 98, -89, 5, -13, 
      -64, -57, 115, -116, -104, -109, 43, -39, -68, 76, 
      -126, -54, 30, -101, 87, 60, -3, -44, -32, 22, 
      103, 66, 111, 24, -118, 23, -27, 18, -66, 78, 
      -60, -42, -38, -98, -34, 73, -96, -5, -11, -114, 
      -69, 47, -18, 122, -87, 104, 121, -111, 21, -78, 
      7, 63, -108, -62, 16, -119, 11, 34, 95, 33, 
      Byte.MIN_VALUE, Byte.MAX_VALUE, 93, -102, 90, -112, 50, 39, 53, 62, 
      -52, -25, -65, -9, -105, 3, -1, 25, 48, -77, 
      72, -91, -75, -47, -41, 94, -110, 42, -84, 86, 
      -86, -58, 79, -72, 56, -46, -106, -92, 125, -74, 
      118, -4, 107, -30, -100, 116, 4, -15, 69, -99, 
      112, 89, 100, 113, -121, 32, -122, 91, -49, 101, 
      -26, 45, -88, 2, 27, 96, 37, -83, -82, -80, 
      -71, -10, 28, 70, 97, 105, 52, 64, 126, 15, 
      85, 71, -93, 35, -35, 81, -81, 58, -61, 92, 
      -7, -50, -70, -59, -22, 38, 44, 83, 13, 110, 
      -123, 40, -124, 9, -45, -33, -51, -12, 65, -127, 
      77, 82, 106, -36, 55, -56, 108, -63, -85, -6, 
      36, -31, 123, 8, 12, -67, -79, 74, 120, -120, 
      -107, -117, -29, 99, -24, 109, -23, -53, -43, -2, 
      59, 0, 29, 57, -14, -17, -73, 14, 102, 88, 
      -48, -28, -90, 119, 114, -8, -21, 117, 75, 10, 
      49, 68, 80, -76, -113, -19, 31, 26, -37, -103, 
      -115, 51, -97, 17, -125, 20 };
  
  public MD2Digest() {
    reset();
  }
  
  public MD2Digest(MD2Digest paramMD2Digest) {
    copyIn(paramMD2Digest);
  }
  
  private void copyIn(MD2Digest paramMD2Digest) {
    System.arraycopy(paramMD2Digest.X, 0, this.X, 0, paramMD2Digest.X.length);
    this.xOff = paramMD2Digest.xOff;
    System.arraycopy(paramMD2Digest.M, 0, this.M, 0, paramMD2Digest.M.length);
    this.mOff = paramMD2Digest.mOff;
    System.arraycopy(paramMD2Digest.C, 0, this.C, 0, paramMD2Digest.C.length);
    this.COff = paramMD2Digest.COff;
  }
  
  public String getAlgorithmName() {
    return "MD2";
  }
  
  public int getDigestSize() {
    return 16;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte b = (byte)(this.M.length - this.mOff);
    for (int i = this.mOff; i < this.M.length; i++)
      this.M[i] = b; 
    processCheckSum(this.M);
    processBlock(this.M);
    processBlock(this.C);
    System.arraycopy(this.X, this.xOff, paramArrayOfbyte, paramInt, 16);
    reset();
    return 16;
  }
  
  public void reset() {
    this.xOff = 0;
    byte b;
    for (b = 0; b != this.X.length; b++)
      this.X[b] = 0; 
    this.mOff = 0;
    for (b = 0; b != this.M.length; b++)
      this.M[b] = 0; 
    this.COff = 0;
    for (b = 0; b != this.C.length; b++)
      this.C[b] = 0; 
  }
  
  public void update(byte paramByte) {
    this.M[this.mOff++] = paramByte;
    if (this.mOff == 16) {
      processCheckSum(this.M);
      processBlock(this.M);
      this.mOff = 0;
    } 
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (this.mOff != 0 && paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
    while (paramInt2 > 16) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.M, 0, 16);
      processCheckSum(this.M);
      processBlock(this.M);
      paramInt2 -= 16;
      paramInt1 += 16;
    } 
    while (paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
  }
  
  protected void processCheckSum(byte[] paramArrayOfbyte) {
    byte b = this.C[15];
    for (byte b1 = 0; b1 < 16; b1++) {
      this.C[b1] = (byte)(this.C[b1] ^ S[(paramArrayOfbyte[b1] ^ b) & 0xFF]);
      b = this.C[b1];
    } 
  }
  
  protected void processBlock(byte[] paramArrayOfbyte) {
    int i;
    for (i = 0; i < 16; i++) {
      this.X[i + 16] = paramArrayOfbyte[i];
      this.X[i + 32] = (byte)(paramArrayOfbyte[i] ^ this.X[i]);
    } 
    i = 0;
    for (byte b = 0; b < 18; b++) {
      for (byte b1 = 0; b1 < 48; b1++) {
        i = this.X[b1] = (byte)(this.X[b1] ^ S[i]);
        i &= 0xFF;
      } 
      i = (i + b) % 256;
    } 
  }
  
  public int getByteLength() {
    return 16;
  }
  
  public Memoable copy() {
    return new MD2Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    MD2Digest mD2Digest = (MD2Digest)paramMemoable;
    copyIn(mD2Digest);
  }
}
