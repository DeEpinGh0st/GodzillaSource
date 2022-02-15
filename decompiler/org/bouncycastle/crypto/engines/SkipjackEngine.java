package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class SkipjackEngine implements BlockCipher {
  static final int BLOCK_SIZE = 8;
  
  static short[] ftable = new short[] { 
      163, 215, 9, 131, 248, 72, 246, 244, 179, 33, 
      21, 120, 153, 177, 175, 249, 231, 45, 77, 138, 
      206, 76, 202, 46, 82, 149, 217, 30, 78, 56, 
      68, 40, 10, 223, 2, 160, 23, 241, 96, 104, 
      18, 183, 122, 195, 233, 250, 61, 83, 150, 132, 
      107, 186, 242, 99, 154, 25, 124, 174, 229, 245, 
      247, 22, 106, 162, 57, 182, 123, 15, 193, 147, 
      129, 27, 238, 180, 26, 234, 208, 145, 47, 184, 
      85, 185, 218, 133, 63, 65, 191, 224, 90, 88, 
      128, 95, 102, 11, 216, 144, 53, 213, 192, 167, 
      51, 6, 101, 105, 69, 0, 148, 86, 109, 152, 
      155, 118, 151, 252, 178, 194, 176, 254, 219, 32, 
      225, 235, 214, 228, 221, 71, 74, 29, 66, 237, 
      158, 110, 73, 60, 205, 67, 39, 210, 7, 212, 
      222, 199, 103, 24, 137, 203, 48, 31, 141, 198, 
      143, 170, 200, 116, 220, 201, 93, 92, 49, 164, 
      112, 136, 97, 44, 159, 13, 43, 135, 80, 130, 
      84, 100, 38, 125, 3, 64, 52, 75, 28, 115, 
      209, 196, 253, 59, 204, 251, 127, 171, 230, 62, 
      91, 165, 173, 4, 35, 156, 20, 81, 34, 240, 
      41, 121, 113, 126, 255, 140, 14, 226, 12, 239, 
      188, 114, 117, 111, 55, 161, 236, 211, 142, 98, 
      139, 134, 16, 232, 8, 119, 17, 190, 146, 79, 
      36, 197, 50, 54, 157, 207, 243, 166, 187, 172, 
      94, 108, 169, 19, 87, 37, 181, 227, 189, 168, 
      58, 1, 5, 89, 42, 70 };
  
  private int[] key0;
  
  private int[] key1;
  
  private int[] key2;
  
  private int[] key3;
  
  private boolean encrypting;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to SKIPJACK init - " + paramCipherParameters.getClass().getName()); 
    byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
    this.encrypting = paramBoolean;
    this.key0 = new int[32];
    this.key1 = new int[32];
    this.key2 = new int[32];
    this.key3 = new int[32];
    for (byte b = 0; b < 32; b++) {
      this.key0[b] = arrayOfByte[b * 4 % 10] & 0xFF;
      this.key1[b] = arrayOfByte[(b * 4 + 1) % 10] & 0xFF;
      this.key2[b] = arrayOfByte[(b * 4 + 2) % 10] & 0xFF;
      this.key3[b] = arrayOfByte[(b * 4 + 3) % 10] & 0xFF;
    } 
  }
  
  public String getAlgorithmName() {
    return "SKIPJACK";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.key1 == null)
      throw new IllegalStateException("SKIPJACK engine not initialised"); 
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
  
  public void reset() {}
  
  private int g(int paramInt1, int paramInt2) {
    int i = paramInt2 >> 8 & 0xFF;
    int j = paramInt2 & 0xFF;
    int k = ftable[j ^ this.key0[paramInt1]] ^ i;
    int m = ftable[k ^ this.key1[paramInt1]] ^ j;
    int n = ftable[m ^ this.key2[paramInt1]] ^ k;
    int i1 = ftable[n ^ this.key3[paramInt1]] ^ m;
    return (n << 8) + i1;
  }
  
  public int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = (paramArrayOfbyte1[paramInt1 + 0] << 8) + (paramArrayOfbyte1[paramInt1 + 1] & 0xFF);
    int j = (paramArrayOfbyte1[paramInt1 + 2] << 8) + (paramArrayOfbyte1[paramInt1 + 3] & 0xFF);
    int k = (paramArrayOfbyte1[paramInt1 + 4] << 8) + (paramArrayOfbyte1[paramInt1 + 5] & 0xFF);
    int m = (paramArrayOfbyte1[paramInt1 + 6] << 8) + (paramArrayOfbyte1[paramInt1 + 7] & 0xFF);
    byte b1 = 0;
    for (byte b2 = 0; b2 < 2; b2++) {
      byte b;
      for (b = 0; b < 8; b++) {
        int n = m;
        m = k;
        k = j;
        j = g(b1, i);
        i = j ^ n ^ b1 + 1;
        b1++;
      } 
      for (b = 0; b < 8; b++) {
        int n = m;
        m = k;
        k = i ^ j ^ b1 + 1;
        j = g(b1, i);
        i = n;
        b1++;
      } 
    } 
    paramArrayOfbyte2[paramInt2 + 0] = (byte)(i >> 8);
    paramArrayOfbyte2[paramInt2 + 1] = (byte)i;
    paramArrayOfbyte2[paramInt2 + 2] = (byte)(j >> 8);
    paramArrayOfbyte2[paramInt2 + 3] = (byte)j;
    paramArrayOfbyte2[paramInt2 + 4] = (byte)(k >> 8);
    paramArrayOfbyte2[paramInt2 + 5] = (byte)k;
    paramArrayOfbyte2[paramInt2 + 6] = (byte)(m >> 8);
    paramArrayOfbyte2[paramInt2 + 7] = (byte)m;
    return 8;
  }
  
  private int h(int paramInt1, int paramInt2) {
    int i = paramInt2 & 0xFF;
    int j = paramInt2 >> 8 & 0xFF;
    int k = ftable[j ^ this.key3[paramInt1]] ^ i;
    int m = ftable[k ^ this.key2[paramInt1]] ^ j;
    int n = ftable[m ^ this.key1[paramInt1]] ^ k;
    int i1 = ftable[n ^ this.key0[paramInt1]] ^ m;
    return (i1 << 8) + n;
  }
  
  public int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = (paramArrayOfbyte1[paramInt1 + 0] << 8) + (paramArrayOfbyte1[paramInt1 + 1] & 0xFF);
    int j = (paramArrayOfbyte1[paramInt1 + 2] << 8) + (paramArrayOfbyte1[paramInt1 + 3] & 0xFF);
    int k = (paramArrayOfbyte1[paramInt1 + 4] << 8) + (paramArrayOfbyte1[paramInt1 + 5] & 0xFF);
    int m = (paramArrayOfbyte1[paramInt1 + 6] << 8) + (paramArrayOfbyte1[paramInt1 + 7] & 0xFF);
    byte b1 = 31;
    for (byte b2 = 0; b2 < 2; b2++) {
      byte b;
      for (b = 0; b < 8; b++) {
        int n = k;
        k = m;
        m = i;
        i = h(b1, j);
        j = i ^ n ^ b1 + 1;
        b1--;
      } 
      for (b = 0; b < 8; b++) {
        int n = k;
        k = m;
        m = j ^ i ^ b1 + 1;
        i = h(b1, j);
        j = n;
        b1--;
      } 
    } 
    paramArrayOfbyte2[paramInt2 + 0] = (byte)(i >> 8);
    paramArrayOfbyte2[paramInt2 + 1] = (byte)i;
    paramArrayOfbyte2[paramInt2 + 2] = (byte)(j >> 8);
    paramArrayOfbyte2[paramInt2 + 3] = (byte)j;
    paramArrayOfbyte2[paramInt2 + 4] = (byte)(k >> 8);
    paramArrayOfbyte2[paramInt2 + 5] = (byte)k;
    paramArrayOfbyte2[paramInt2 + 6] = (byte)(m >> 8);
    paramArrayOfbyte2[paramInt2 + 7] = (byte)m;
    return 8;
  }
}
