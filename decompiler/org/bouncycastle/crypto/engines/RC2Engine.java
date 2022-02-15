package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC2Parameters;

public class RC2Engine implements BlockCipher {
  private static byte[] piTable = new byte[] { 
      -39, 120, -7, -60, 25, -35, -75, -19, 40, -23, 
      -3, 121, 74, -96, -40, -99, -58, 126, 55, -125, 
      43, 118, 83, -114, 98, 76, 100, -120, 68, -117, 
      -5, -94, 23, -102, 89, -11, -121, -77, 79, 19, 
      97, 69, 109, -115, 9, -127, 125, 50, -67, -113, 
      64, -21, -122, -73, 123, 11, -16, -107, 33, 34, 
      92, 107, 78, -126, 84, -42, 101, -109, -50, 96, 
      -78, 28, 115, 86, -64, 20, -89, -116, -15, -36, 
      18, 117, -54, 31, 59, -66, -28, -47, 66, 61, 
      -44, 48, -93, 60, -74, 38, 111, -65, 14, -38, 
      70, 105, 7, 87, 39, -14, 29, -101, -68, -108, 
      67, 3, -8, 17, -57, -10, -112, -17, 62, -25, 
      6, -61, -43, 47, -56, 102, 30, -41, 8, -24, 
      -22, -34, Byte.MIN_VALUE, 82, -18, -9, -124, -86, 114, -84, 
      53, 77, 106, 42, -106, 26, -46, 113, 90, 21, 
      73, 116, 75, -97, -48, 94, 4, 24, -92, -20, 
      -62, -32, 65, 110, 15, 81, -53, -52, 36, -111, 
      -81, 80, -95, -12, 112, 57, -103, 124, 58, -123, 
      35, -72, -76, 122, -4, 2, 54, 91, 37, 85, 
      -105, 49, 45, 93, -6, -104, -29, -118, -110, -82, 
      5, -33, 41, 16, 103, 108, -70, -55, -45, 0, 
      -26, -49, -31, -98, -88, 44, 99, 22, 1, 63, 
      88, -30, -119, -87, 13, 56, 52, 27, -85, 51, 
      -1, -80, -69, 72, 12, 95, -71, -79, -51, 46, 
      -59, -13, -37, 71, -27, -91, -100, 119, 10, -90, 
      32, 104, -2, Byte.MAX_VALUE, -63, -83 };
  
  private static final int BLOCK_SIZE = 8;
  
  private int[] workingKey;
  
  private boolean encrypting;
  
  private int[] generateWorkingKey(byte[] paramArrayOfbyte, int paramInt) {
    int[] arrayOfInt1 = new int[128];
    int j;
    for (j = 0; j != paramArrayOfbyte.length; j++)
      arrayOfInt1[j] = paramArrayOfbyte[j] & 0xFF; 
    j = paramArrayOfbyte.length;
    if (j < 128) {
      byte b1 = 0;
      int m = arrayOfInt1[j - 1];
      do {
        m = piTable[m + arrayOfInt1[b1++] & 0xFF] & 0xFF;
        arrayOfInt1[j++] = m;
      } while (j < 128);
    } 
    j = paramInt + 7 >> 3;
    int i = piTable[arrayOfInt1[128 - j] & 255 >> (0x7 & -paramInt)] & 0xFF;
    arrayOfInt1[128 - j] = i;
    for (int k = 128 - j - 1; k >= 0; k--) {
      i = piTable[i ^ arrayOfInt1[k + j]] & 0xFF;
      arrayOfInt1[k] = i;
    } 
    int[] arrayOfInt2 = new int[64];
    for (byte b = 0; b != arrayOfInt2.length; b++)
      arrayOfInt2[b] = arrayOfInt1[2 * b] + (arrayOfInt1[2 * b + 1] << 8); 
    return arrayOfInt2;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.encrypting = paramBoolean;
    if (paramCipherParameters instanceof RC2Parameters) {
      RC2Parameters rC2Parameters = (RC2Parameters)paramCipherParameters;
      this.workingKey = generateWorkingKey(rC2Parameters.getKey(), rC2Parameters.getEffectiveKeyBits());
    } else if (paramCipherParameters instanceof KeyParameter) {
      byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
      this.workingKey = generateWorkingKey(arrayOfByte, arrayOfByte.length * 8);
    } else {
      throw new IllegalArgumentException("invalid parameter passed to RC2 init - " + paramCipherParameters.getClass().getName());
    } 
  }
  
  public void reset() {}
  
  public String getAlgorithmName() {
    return "RC2";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public final int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey == null)
      throw new IllegalStateException("RC2 engine not initialised"); 
    if (paramInt1 + 8 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 8 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.encrypting) {
      encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } else {
      decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } 
    return 8;
  }
  
  private int rotateWordLeft(int paramInt1, int paramInt2) {
    paramInt1 &= 0xFFFF;
    return paramInt1 << paramInt2 | paramInt1 >> 16 - paramInt2;
  }
  
  private void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = ((paramArrayOfbyte1[paramInt1 + 7] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 6] & 0xFF);
    int j = ((paramArrayOfbyte1[paramInt1 + 5] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 4] & 0xFF);
    int k = ((paramArrayOfbyte1[paramInt1 + 3] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 2] & 0xFF);
    int m = ((paramArrayOfbyte1[paramInt1 + 1] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 0] & 0xFF);
    byte b;
    for (b = 0; b <= 16; b += 4) {
      m = rotateWordLeft(m + (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b], 1);
      k = rotateWordLeft(k + (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1], 2);
      j = rotateWordLeft(j + (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2], 3);
      i = rotateWordLeft(i + (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3], 5);
    } 
    m += this.workingKey[i & 0x3F];
    k += this.workingKey[m & 0x3F];
    j += this.workingKey[k & 0x3F];
    i += this.workingKey[j & 0x3F];
    for (b = 20; b <= 40; b += 4) {
      m = rotateWordLeft(m + (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b], 1);
      k = rotateWordLeft(k + (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1], 2);
      j = rotateWordLeft(j + (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2], 3);
      i = rotateWordLeft(i + (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3], 5);
    } 
    m += this.workingKey[i & 0x3F];
    k += this.workingKey[m & 0x3F];
    j += this.workingKey[k & 0x3F];
    i += this.workingKey[j & 0x3F];
    for (b = 44; b < 64; b += 4) {
      m = rotateWordLeft(m + (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b], 1);
      k = rotateWordLeft(k + (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1], 2);
      j = rotateWordLeft(j + (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2], 3);
      i = rotateWordLeft(i + (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3], 5);
    } 
    paramArrayOfbyte2[paramInt2 + 0] = (byte)m;
    paramArrayOfbyte2[paramInt2 + 1] = (byte)(m >> 8);
    paramArrayOfbyte2[paramInt2 + 2] = (byte)k;
    paramArrayOfbyte2[paramInt2 + 3] = (byte)(k >> 8);
    paramArrayOfbyte2[paramInt2 + 4] = (byte)j;
    paramArrayOfbyte2[paramInt2 + 5] = (byte)(j >> 8);
    paramArrayOfbyte2[paramInt2 + 6] = (byte)i;
    paramArrayOfbyte2[paramInt2 + 7] = (byte)(i >> 8);
  }
  
  private void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = ((paramArrayOfbyte1[paramInt1 + 7] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 6] & 0xFF);
    int j = ((paramArrayOfbyte1[paramInt1 + 5] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 4] & 0xFF);
    int k = ((paramArrayOfbyte1[paramInt1 + 3] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 2] & 0xFF);
    int m = ((paramArrayOfbyte1[paramInt1 + 1] & 0xFF) << 8) + (paramArrayOfbyte1[paramInt1 + 0] & 0xFF);
    byte b;
    for (b = 60; b >= 44; b -= 4) {
      i = rotateWordLeft(i, 11) - (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3];
      j = rotateWordLeft(j, 13) - (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2];
      k = rotateWordLeft(k, 14) - (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1];
      m = rotateWordLeft(m, 15) - (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b];
    } 
    i -= this.workingKey[j & 0x3F];
    j -= this.workingKey[k & 0x3F];
    k -= this.workingKey[m & 0x3F];
    m -= this.workingKey[i & 0x3F];
    for (b = 40; b >= 20; b -= 4) {
      i = rotateWordLeft(i, 11) - (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3];
      j = rotateWordLeft(j, 13) - (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2];
      k = rotateWordLeft(k, 14) - (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1];
      m = rotateWordLeft(m, 15) - (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b];
    } 
    i -= this.workingKey[j & 0x3F];
    j -= this.workingKey[k & 0x3F];
    k -= this.workingKey[m & 0x3F];
    m -= this.workingKey[i & 0x3F];
    for (b = 16; b >= 0; b -= 4) {
      i = rotateWordLeft(i, 11) - (m & (j ^ 0xFFFFFFFF)) + (k & j) + this.workingKey[b + 3];
      j = rotateWordLeft(j, 13) - (i & (k ^ 0xFFFFFFFF)) + (m & k) + this.workingKey[b + 2];
      k = rotateWordLeft(k, 14) - (j & (m ^ 0xFFFFFFFF)) + (i & m) + this.workingKey[b + 1];
      m = rotateWordLeft(m, 15) - (k & (i ^ 0xFFFFFFFF)) + (j & i) + this.workingKey[b];
    } 
    paramArrayOfbyte2[paramInt2 + 0] = (byte)m;
    paramArrayOfbyte2[paramInt2 + 1] = (byte)(m >> 8);
    paramArrayOfbyte2[paramInt2 + 2] = (byte)k;
    paramArrayOfbyte2[paramInt2 + 3] = (byte)(k >> 8);
    paramArrayOfbyte2[paramInt2 + 4] = (byte)j;
    paramArrayOfbyte2[paramInt2 + 5] = (byte)(j >> 8);
    paramArrayOfbyte2[paramInt2 + 6] = (byte)i;
    paramArrayOfbyte2[paramInt2 + 7] = (byte)(i >> 8);
  }
}
