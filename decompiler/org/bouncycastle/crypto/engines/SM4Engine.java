package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class SM4Engine implements BlockCipher {
  private static final int BLOCK_SIZE = 16;
  
  private static final byte[] Sbox = new byte[] { 
      -42, -112, -23, -2, -52, -31, 61, -73, 22, -74, 
      20, -62, 40, -5, 44, 5, 43, 103, -102, 118, 
      42, -66, 4, -61, -86, 68, 19, 38, 73, -122, 
      6, -103, -100, 66, 80, -12, -111, -17, -104, 122, 
      51, 84, 11, 67, -19, -49, -84, 98, -28, -77, 
      28, -87, -55, 8, -24, -107, Byte.MIN_VALUE, -33, -108, -6, 
      117, -113, 63, -90, 71, 7, -89, -4, -13, 115, 
      23, -70, -125, 89, 60, 25, -26, -123, 79, -88, 
      104, 107, -127, -78, 113, 100, -38, -117, -8, -21, 
      15, 75, 112, 86, -99, 53, 30, 36, 14, 94, 
      99, 88, -47, -94, 37, 34, 124, 59, 1, 33, 
      120, -121, -44, 0, 70, 87, -97, -45, 39, 82, 
      76, 54, 2, -25, -96, -60, -56, -98, -22, -65, 
      -118, -46, 64, -57, 56, -75, -93, -9, -14, -50, 
      -7, 97, 21, -95, -32, -82, 93, -92, -101, 52, 
      26, 85, -83, -109, 50, 48, -11, -116, -79, -29, 
      29, -10, -30, 46, -126, 102, -54, 96, -64, 41, 
      35, -85, 13, 83, 78, 111, -43, -37, 55, 69, 
      -34, -3, -114, 47, 3, -1, 106, 114, 109, 108, 
      91, 81, -115, 27, -81, -110, -69, -35, -68, Byte.MAX_VALUE, 
      17, -39, 92, 65, 31, 16, 90, -40, 10, -63, 
      49, -120, -91, -51, 123, -67, 45, 116, -48, 18, 
      -72, -27, -76, -80, -119, 105, -105, 74, 12, -106, 
      119, 126, 101, -71, -15, 9, -59, 110, -58, -124, 
      24, -16, 125, -20, 58, -36, 77, 32, 121, -18, 
      95, 62, -41, -53, 57, 72 };
  
  private static final int[] CK = new int[] { 
      462357, 472066609, 943670861, 1415275113, 1886879365, -1936483679, -1464879427, -993275175, -521670923, -66909679, 
      404694573, 876298825, 1347903077, 1819507329, -2003855715, -1532251463, -1060647211, -589042959, -117504499, 337322537, 
      808926789, 1280531041, 1752135293, -2071227751, -1599623499, -1128019247, -656414995, -184876535, 269950501, 741554753, 
      1213159005, 1684763257 };
  
  private static final int[] FK = new int[] { -1548633402, 1453994832, 1736282519, -1301273892 };
  
  private final int[] X = new int[4];
  
  private int[] rk;
  
  private int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  private int tau(int paramInt) {
    int i = Sbox[paramInt >> 24 & 0xFF] & 0xFF;
    int j = Sbox[paramInt >> 16 & 0xFF] & 0xFF;
    int k = Sbox[paramInt >> 8 & 0xFF] & 0xFF;
    int m = Sbox[paramInt & 0xFF] & 0xFF;
    return i << 24 | j << 16 | k << 8 | m;
  }
  
  private int L_ap(int paramInt) {
    return paramInt ^ rotateLeft(paramInt, 13) ^ rotateLeft(paramInt, 23);
  }
  
  private int T_ap(int paramInt) {
    return L_ap(tau(paramInt));
  }
  
  private int[] expandKey(boolean paramBoolean, byte[] paramArrayOfbyte) {
    int[] arrayOfInt1 = new int[32];
    int[] arrayOfInt2 = new int[4];
    arrayOfInt2[0] = Pack.bigEndianToInt(paramArrayOfbyte, 0);
    arrayOfInt2[1] = Pack.bigEndianToInt(paramArrayOfbyte, 4);
    arrayOfInt2[2] = Pack.bigEndianToInt(paramArrayOfbyte, 8);
    arrayOfInt2[3] = Pack.bigEndianToInt(paramArrayOfbyte, 12);
    int[] arrayOfInt3 = new int[4];
    arrayOfInt3[0] = arrayOfInt2[0] ^ FK[0];
    arrayOfInt3[1] = arrayOfInt2[1] ^ FK[1];
    arrayOfInt3[2] = arrayOfInt2[2] ^ FK[2];
    arrayOfInt3[3] = arrayOfInt2[3] ^ FK[3];
    if (paramBoolean) {
      arrayOfInt1[0] = arrayOfInt3[0] ^ T_ap(arrayOfInt3[1] ^ arrayOfInt3[2] ^ arrayOfInt3[3] ^ CK[0]);
      arrayOfInt1[1] = arrayOfInt3[1] ^ T_ap(arrayOfInt3[2] ^ arrayOfInt3[3] ^ arrayOfInt1[0] ^ CK[1]);
      arrayOfInt1[2] = arrayOfInt3[2] ^ T_ap(arrayOfInt3[3] ^ arrayOfInt1[0] ^ arrayOfInt1[1] ^ CK[2]);
      arrayOfInt1[3] = arrayOfInt3[3] ^ T_ap(arrayOfInt1[0] ^ arrayOfInt1[1] ^ arrayOfInt1[2] ^ CK[3]);
      for (byte b = 4; b < 32; b++)
        arrayOfInt1[b] = arrayOfInt1[b - 4] ^ T_ap(arrayOfInt1[b - 3] ^ arrayOfInt1[b - 2] ^ arrayOfInt1[b - 1] ^ CK[b]); 
    } else {
      arrayOfInt1[31] = arrayOfInt3[0] ^ T_ap(arrayOfInt3[1] ^ arrayOfInt3[2] ^ arrayOfInt3[3] ^ CK[0]);
      arrayOfInt1[30] = arrayOfInt3[1] ^ T_ap(arrayOfInt3[2] ^ arrayOfInt3[3] ^ arrayOfInt1[31] ^ CK[1]);
      arrayOfInt1[29] = arrayOfInt3[2] ^ T_ap(arrayOfInt3[3] ^ arrayOfInt1[31] ^ arrayOfInt1[30] ^ CK[2]);
      arrayOfInt1[28] = arrayOfInt3[3] ^ T_ap(arrayOfInt1[31] ^ arrayOfInt1[30] ^ arrayOfInt1[29] ^ CK[3]);
      for (byte b = 27; b >= 0; b--)
        arrayOfInt1[b] = arrayOfInt1[b + 4] ^ T_ap(arrayOfInt1[b + 3] ^ arrayOfInt1[b + 2] ^ arrayOfInt1[b + 1] ^ CK[31 - b]); 
    } 
    return arrayOfInt1;
  }
  
  private int L(int paramInt) {
    return paramInt ^ rotateLeft(paramInt, 2) ^ rotateLeft(paramInt, 10) ^ rotateLeft(paramInt, 18) ^ rotateLeft(paramInt, 24);
  }
  
  private int T(int paramInt) {
    return L(tau(paramInt));
  }
  
  private void R(int[] paramArrayOfint, int paramInt) {
    int i = paramInt;
    int j = paramInt + 1;
    int k = paramInt + 2;
    int m = paramInt + 3;
    paramArrayOfint[i] = paramArrayOfint[i] ^ paramArrayOfint[m];
    paramArrayOfint[m] = paramArrayOfint[i] ^ paramArrayOfint[m];
    paramArrayOfint[i] = paramArrayOfint[i] ^ paramArrayOfint[m];
    paramArrayOfint[j] = paramArrayOfint[j] ^ paramArrayOfint[k];
    paramArrayOfint[k] = paramArrayOfint[j] ^ paramArrayOfint[k];
    paramArrayOfint[j] = paramArrayOfint[j] ^ paramArrayOfint[k];
  }
  
  private int F0(int[] paramArrayOfint, int paramInt) {
    return paramArrayOfint[0] ^ T(paramArrayOfint[1] ^ paramArrayOfint[2] ^ paramArrayOfint[3] ^ paramInt);
  }
  
  private int F1(int[] paramArrayOfint, int paramInt) {
    return paramArrayOfint[1] ^ T(paramArrayOfint[2] ^ paramArrayOfint[3] ^ paramArrayOfint[0] ^ paramInt);
  }
  
  private int F2(int[] paramArrayOfint, int paramInt) {
    return paramArrayOfint[2] ^ T(paramArrayOfint[3] ^ paramArrayOfint[0] ^ paramArrayOfint[1] ^ paramInt);
  }
  
  private int F3(int[] paramArrayOfint, int paramInt) {
    return paramArrayOfint[3] ^ T(paramArrayOfint[0] ^ paramArrayOfint[1] ^ paramArrayOfint[2] ^ paramInt);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof KeyParameter) {
      byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
      if (arrayOfByte.length != 16)
        throw new IllegalArgumentException("SM4 requires a 128 bit key"); 
      this.rk = expandKey(paramBoolean, arrayOfByte);
    } else {
      throw new IllegalArgumentException("invalid parameter passed to SM4 init - " + paramCipherParameters.getClass().getName());
    } 
  }
  
  public String getAlgorithmName() {
    return "SM4";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (this.rk == null)
      throw new IllegalStateException("SM4 not initialised"); 
    if (paramInt1 + 16 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 16 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    this.X[0] = Pack.bigEndianToInt(paramArrayOfbyte1, paramInt1);
    this.X[1] = Pack.bigEndianToInt(paramArrayOfbyte1, paramInt1 + 4);
    this.X[2] = Pack.bigEndianToInt(paramArrayOfbyte1, paramInt1 + 8);
    this.X[3] = Pack.bigEndianToInt(paramArrayOfbyte1, paramInt1 + 12);
    for (byte b = 0; b < 32; b += 4) {
      this.X[0] = F0(this.X, this.rk[b]);
      this.X[1] = F1(this.X, this.rk[b + 1]);
      this.X[2] = F2(this.X, this.rk[b + 2]);
      this.X[3] = F3(this.X, this.rk[b + 3]);
    } 
    R(this.X, 0);
    Pack.intToBigEndian(this.X[0], paramArrayOfbyte2, paramInt2);
    Pack.intToBigEndian(this.X[1], paramArrayOfbyte2, paramInt2 + 4);
    Pack.intToBigEndian(this.X[2], paramArrayOfbyte2, paramInt2 + 8);
    Pack.intToBigEndian(this.X[3], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  public void reset() {}
}
