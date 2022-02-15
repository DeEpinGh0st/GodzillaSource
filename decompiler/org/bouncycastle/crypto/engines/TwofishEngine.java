package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public final class TwofishEngine implements BlockCipher {
  private static final byte[][] P = new byte[][] { { 
        -87, 103, -77, -24, 4, -3, -93, 118, -102, -110, 
        Byte.MIN_VALUE, 120, -28, -35, -47, 56, 13, -58, 53, -104, 
        24, -9, -20, 108, 67, 117, 55, 38, -6, 19, 
        -108, 72, -14, -48, -117, 48, -124, 84, -33, 35, 
        25, 91, 61, 89, -13, -82, -94, -126, 99, 1, 
        -125, 46, -39, 81, -101, 124, -90, -21, -91, -66, 
        22, 12, -29, 97, -64, -116, 58, -11, 115, 44, 
        37, 11, -69, 78, -119, 107, 83, 106, -76, -15, 
        -31, -26, -67, 69, -30, -12, -74, 102, -52, -107, 
        3, 86, -44, 28, 30, -41, -5, -61, -114, -75, 
        -23, -49, -65, -70, -22, 119, 57, -81, 51, -55, 
        98, 113, -127, 121, 9, -83, 36, -51, -7, -40, 
        -27, -59, -71, 77, 68, 8, -122, -25, -95, 29, 
        -86, -19, 6, 112, -78, -46, 65, 123, -96, 17, 
        49, -62, 39, -112, 32, -10, 96, -1, -106, 92, 
        -79, -85, -98, -100, 82, 27, 95, -109, 10, -17, 
        -111, -123, 73, -18, 45, 79, -113, 59, 71, -121, 
        109, 70, -42, 62, 105, 100, 42, -50, -53, 47, 
        -4, -105, 5, 122, -84, Byte.MAX_VALUE, -43, 26, 75, 14, 
        -89, 90, 40, 20, 63, 41, -120, 60, 76, 2, 
        -72, -38, -80, 23, 85, 31, -118, 125, 87, -57, 
        -115, 116, -73, -60, -97, 114, 126, 21, 34, 18, 
        88, 7, -103, 52, 110, 80, -34, 104, 101, -68, 
        -37, -8, -56, -88, 43, 64, -36, -2, 50, -92, 
        -54, 16, 33, -16, -45, 93, 15, 0, 111, -99, 
        54, 66, 74, 94, -63, -32 }, { 
        117, -13, -58, -12, -37, 123, -5, -56, 74, -45, 
        -26, 107, 69, 125, -24, 75, -42, 50, -40, -3, 
        55, 113, -15, -31, 48, 15, -8, 27, -121, -6, 
        6, 63, 94, -70, -82, 91, -118, 0, -68, -99, 
        109, -63, -79, 14, Byte.MIN_VALUE, 93, -46, -43, -96, -124, 
        7, 20, -75, -112, 44, -93, -78, 115, 76, 84, 
        -110, 116, 54, 81, 56, -80, -67, 90, -4, 96, 
        98, -106, 108, 66, -9, 16, 124, 40, 39, -116, 
        19, -107, -100, -57, 36, 70, 59, 112, -54, -29, 
        -123, -53, 17, -48, -109, -72, -90, -125, 32, -1, 
        -97, 119, -61, -52, 3, 111, 8, -65, 64, -25, 
        43, -30, 121, 12, -86, -126, 65, 58, -22, -71, 
        -28, -102, -92, -105, 126, -38, 122, 23, 102, -108, 
        -95, 29, 61, -16, -34, -77, 11, 114, -89, 28, 
        -17, -47, 83, 62, -113, 51, 38, 95, -20, 118, 
        42, 73, -127, -120, -18, 33, -60, 26, -21, -39, 
        -59, 57, -103, -51, -83, 49, -117, 1, 24, 35, 
        -35, 31, 78, 45, -7, 72, 79, -14, 101, -114, 
        120, 92, 88, 25, -115, -27, -104, 87, 103, Byte.MAX_VALUE, 
        5, 100, -81, 99, -74, -2, -11, -73, 60, -91, 
        -50, -23, 104, 68, -32, 77, 67, 105, 41, 46, 
        -84, 21, 89, -88, 10, -98, 110, 71, -33, 52, 
        53, 106, -49, -36, 34, -55, -64, -101, -119, -44, 
        -19, -85, 18, -94, 13, 82, -69, 2, 47, -87, 
        -41, 97, 30, -76, 80, 4, -10, -62, 22, 37, 
        -122, 86, 85, 9, -66, -111 } };
  
  private static final int P_00 = 1;
  
  private static final int P_01 = 0;
  
  private static final int P_02 = 0;
  
  private static final int P_03 = 1;
  
  private static final int P_04 = 1;
  
  private static final int P_10 = 0;
  
  private static final int P_11 = 0;
  
  private static final int P_12 = 1;
  
  private static final int P_13 = 1;
  
  private static final int P_14 = 0;
  
  private static final int P_20 = 1;
  
  private static final int P_21 = 1;
  
  private static final int P_22 = 0;
  
  private static final int P_23 = 0;
  
  private static final int P_24 = 0;
  
  private static final int P_30 = 0;
  
  private static final int P_31 = 1;
  
  private static final int P_32 = 1;
  
  private static final int P_33 = 0;
  
  private static final int P_34 = 1;
  
  private static final int GF256_FDBK = 361;
  
  private static final int GF256_FDBK_2 = 180;
  
  private static final int GF256_FDBK_4 = 90;
  
  private static final int RS_GF_FDBK = 333;
  
  private static final int ROUNDS = 16;
  
  private static final int MAX_ROUNDS = 16;
  
  private static final int BLOCK_SIZE = 16;
  
  private static final int MAX_KEY_BITS = 256;
  
  private static final int INPUT_WHITEN = 0;
  
  private static final int OUTPUT_WHITEN = 4;
  
  private static final int ROUND_SUBKEYS = 8;
  
  private static final int TOTAL_SUBKEYS = 40;
  
  private static final int SK_STEP = 33686018;
  
  private static final int SK_BUMP = 16843009;
  
  private static final int SK_ROTL = 9;
  
  private boolean encrypting = false;
  
  private int[] gMDS0 = new int[256];
  
  private int[] gMDS1 = new int[256];
  
  private int[] gMDS2 = new int[256];
  
  private int[] gMDS3 = new int[256];
  
  private int[] gSubKeys;
  
  private int[] gSBox;
  
  private int k64Cnt = 0;
  
  private byte[] workingKey = null;
  
  public TwofishEngine() {
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    int[] arrayOfInt3 = new int[2];
    for (byte b = 0; b < 'Ā'; b++) {
      int i = P[0][b] & 0xFF;
      arrayOfInt1[0] = i;
      arrayOfInt2[0] = Mx_X(i) & 0xFF;
      arrayOfInt3[0] = Mx_Y(i) & 0xFF;
      i = P[1][b] & 0xFF;
      arrayOfInt1[1] = i;
      arrayOfInt2[1] = Mx_X(i) & 0xFF;
      arrayOfInt3[1] = Mx_Y(i) & 0xFF;
      this.gMDS0[b] = arrayOfInt1[1] | arrayOfInt2[1] << 8 | arrayOfInt3[1] << 16 | arrayOfInt3[1] << 24;
      this.gMDS1[b] = arrayOfInt3[0] | arrayOfInt3[0] << 8 | arrayOfInt2[0] << 16 | arrayOfInt1[0] << 24;
      this.gMDS2[b] = arrayOfInt2[1] | arrayOfInt3[1] << 8 | arrayOfInt1[1] << 16 | arrayOfInt3[1] << 24;
      this.gMDS3[b] = arrayOfInt2[0] | arrayOfInt1[0] << 8 | arrayOfInt3[0] << 16 | arrayOfInt2[0] << 24;
    } 
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof KeyParameter) {
      this.encrypting = paramBoolean;
      this.workingKey = ((KeyParameter)paramCipherParameters).getKey();
      this.k64Cnt = this.workingKey.length / 8;
      setKey(this.workingKey);
      return;
    } 
    throw new IllegalArgumentException("invalid parameter passed to Twofish init - " + paramCipherParameters.getClass().getName());
  }
  
  public String getAlgorithmName() {
    return "Twofish";
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey == null)
      throw new IllegalStateException("Twofish not initialised"); 
    if (paramInt1 + 16 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 16 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.encrypting) {
      encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } else {
      decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } 
    return 16;
  }
  
  public void reset() {
    if (this.workingKey != null)
      setKey(this.workingKey); 
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  private void setKey(byte[] paramArrayOfbyte) {
    int[] arrayOfInt1 = new int[4];
    int[] arrayOfInt2 = new int[4];
    int[] arrayOfInt3 = new int[4];
    this.gSubKeys = new int[40];
    if (this.k64Cnt < 1)
      throw new IllegalArgumentException("Key size less than 64 bits"); 
    if (this.k64Cnt > 4)
      throw new IllegalArgumentException("Key size larger than 256 bits"); 
    int i;
    for (i = 0; i < this.k64Cnt; i++) {
      int i1 = i * 8;
      arrayOfInt1[i] = BytesTo32Bits(paramArrayOfbyte, i1);
      arrayOfInt2[i] = BytesTo32Bits(paramArrayOfbyte, i1 + 4);
      arrayOfInt3[this.k64Cnt - 1 - i] = RS_MDS_Encode(arrayOfInt1[i], arrayOfInt2[i]);
    } 
    int j;
    for (j = 0; j < 20; j++) {
      i = j * 33686018;
      int i1 = F32(i, arrayOfInt1);
      int i2 = F32(i + 16843009, arrayOfInt2);
      i2 = i2 << 8 | i2 >>> 24;
      i1 += i2;
      this.gSubKeys[j * 2] = i1;
      i1 += i2;
      this.gSubKeys[j * 2 + 1] = i1 << 9 | i1 >>> 23;
    } 
    j = arrayOfInt3[0];
    int k = arrayOfInt3[1];
    int m = arrayOfInt3[2];
    int n = arrayOfInt3[3];
    this.gSBox = new int[1024];
    for (byte b = 0; b < 'Ā'; b++) {
      int i4 = b;
      int i3 = i4;
      int i2 = i3;
      int i1 = i2;
      switch (this.k64Cnt & 0x3) {
        case 1:
          this.gSBox[b * 2] = this.gMDS0[P[0][i1] & 0xFF ^ b0(j)];
          this.gSBox[b * 2 + 1] = this.gMDS1[P[0][i2] & 0xFF ^ b1(j)];
          this.gSBox[b * 2 + 512] = this.gMDS2[P[1][i3] & 0xFF ^ b2(j)];
          this.gSBox[b * 2 + 513] = this.gMDS3[P[1][i4] & 0xFF ^ b3(j)];
          break;
        case 0:
          i1 = P[1][i1] & 0xFF ^ b0(n);
          i2 = P[0][i2] & 0xFF ^ b1(n);
          i3 = P[0][i3] & 0xFF ^ b2(n);
          i4 = P[1][i4] & 0xFF ^ b3(n);
        case 3:
          i1 = P[1][i1] & 0xFF ^ b0(m);
          i2 = P[1][i2] & 0xFF ^ b1(m);
          i3 = P[0][i3] & 0xFF ^ b2(m);
          i4 = P[0][i4] & 0xFF ^ b3(m);
        case 2:
          this.gSBox[b * 2] = this.gMDS0[P[0][P[0][i1] & 0xFF ^ b0(k)] & 0xFF ^ b0(j)];
          this.gSBox[b * 2 + 1] = this.gMDS1[P[0][P[1][i2] & 0xFF ^ b1(k)] & 0xFF ^ b1(j)];
          this.gSBox[b * 2 + 512] = this.gMDS2[P[1][P[0][i3] & 0xFF ^ b2(k)] & 0xFF ^ b2(j)];
          this.gSBox[b * 2 + 513] = this.gMDS3[P[1][P[1][i4] & 0xFF ^ b3(k)] & 0xFF ^ b3(j)];
          break;
      } 
    } 
  }
  
  private void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = BytesTo32Bits(paramArrayOfbyte1, paramInt1) ^ this.gSubKeys[0];
    int j = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 4) ^ this.gSubKeys[1];
    int k = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 8) ^ this.gSubKeys[2];
    int m = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 12) ^ this.gSubKeys[3];
    byte b1 = 8;
    for (byte b2 = 0; b2 < 16; b2 += 2) {
      int n = Fe32_0(i);
      int i1 = Fe32_3(j);
      k ^= n + i1 + this.gSubKeys[b1++];
      k = k >>> 1 | k << 31;
      m = (m << 1 | m >>> 31) ^ n + 2 * i1 + this.gSubKeys[b1++];
      n = Fe32_0(k);
      i1 = Fe32_3(m);
      i ^= n + i1 + this.gSubKeys[b1++];
      i = i >>> 1 | i << 31;
      j = (j << 1 | j >>> 31) ^ n + 2 * i1 + this.gSubKeys[b1++];
    } 
    Bits32ToBytes(k ^ this.gSubKeys[4], paramArrayOfbyte2, paramInt2);
    Bits32ToBytes(m ^ this.gSubKeys[5], paramArrayOfbyte2, paramInt2 + 4);
    Bits32ToBytes(i ^ this.gSubKeys[6], paramArrayOfbyte2, paramInt2 + 8);
    Bits32ToBytes(j ^ this.gSubKeys[7], paramArrayOfbyte2, paramInt2 + 12);
  }
  
  private void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = BytesTo32Bits(paramArrayOfbyte1, paramInt1) ^ this.gSubKeys[4];
    int j = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 4) ^ this.gSubKeys[5];
    int k = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 8) ^ this.gSubKeys[6];
    int m = BytesTo32Bits(paramArrayOfbyte1, paramInt1 + 12) ^ this.gSubKeys[7];
    byte b1 = 39;
    for (byte b2 = 0; b2 < 16; b2 += 2) {
      int n = Fe32_0(i);
      int i1 = Fe32_3(j);
      m ^= n + 2 * i1 + this.gSubKeys[b1--];
      k = (k << 1 | k >>> 31) ^ n + i1 + this.gSubKeys[b1--];
      m = m >>> 1 | m << 31;
      n = Fe32_0(k);
      i1 = Fe32_3(m);
      j ^= n + 2 * i1 + this.gSubKeys[b1--];
      i = (i << 1 | i >>> 31) ^ n + i1 + this.gSubKeys[b1--];
      j = j >>> 1 | j << 31;
    } 
    Bits32ToBytes(k ^ this.gSubKeys[0], paramArrayOfbyte2, paramInt2);
    Bits32ToBytes(m ^ this.gSubKeys[1], paramArrayOfbyte2, paramInt2 + 4);
    Bits32ToBytes(i ^ this.gSubKeys[2], paramArrayOfbyte2, paramInt2 + 8);
    Bits32ToBytes(j ^ this.gSubKeys[3], paramArrayOfbyte2, paramInt2 + 12);
  }
  
  private int F32(int paramInt, int[] paramArrayOfint) {
    int i = b0(paramInt);
    int j = b1(paramInt);
    int k = b2(paramInt);
    int m = b3(paramInt);
    int n = paramArrayOfint[0];
    int i1 = paramArrayOfint[1];
    int i2 = paramArrayOfint[2];
    int i3 = paramArrayOfint[3];
    int i4 = 0;
    switch (this.k64Cnt & 0x3) {
      case 1:
        i4 = this.gMDS0[P[0][i] & 0xFF ^ b0(n)] ^ this.gMDS1[P[0][j] & 0xFF ^ b1(n)] ^ this.gMDS2[P[1][k] & 0xFF ^ b2(n)] ^ this.gMDS3[P[1][m] & 0xFF ^ b3(n)];
        break;
      case 0:
        i = P[1][i] & 0xFF ^ b0(i3);
        j = P[0][j] & 0xFF ^ b1(i3);
        k = P[0][k] & 0xFF ^ b2(i3);
        m = P[1][m] & 0xFF ^ b3(i3);
      case 3:
        i = P[1][i] & 0xFF ^ b0(i2);
        j = P[1][j] & 0xFF ^ b1(i2);
        k = P[0][k] & 0xFF ^ b2(i2);
        m = P[0][m] & 0xFF ^ b3(i2);
      case 2:
        i4 = this.gMDS0[P[0][P[0][i] & 0xFF ^ b0(i1)] & 0xFF ^ b0(n)] ^ this.gMDS1[P[0][P[1][j] & 0xFF ^ b1(i1)] & 0xFF ^ b1(n)] ^ this.gMDS2[P[1][P[0][k] & 0xFF ^ b2(i1)] & 0xFF ^ b2(n)] ^ this.gMDS3[P[1][P[1][m] & 0xFF ^ b3(i1)] & 0xFF ^ b3(n)];
        break;
    } 
    return i4;
  }
  
  private int RS_MDS_Encode(int paramInt1, int paramInt2) {
    int i = paramInt2;
    byte b;
    for (b = 0; b < 4; b++)
      i = RS_rem(i); 
    i ^= paramInt1;
    for (b = 0; b < 4; b++)
      i = RS_rem(i); 
    return i;
  }
  
  private int RS_rem(int paramInt) {
    int i = paramInt >>> 24 & 0xFF;
    int j = (i << 1 ^ (((i & 0x80) != 0) ? 333 : 0)) & 0xFF;
    int k = i >>> 1 ^ (((i & 0x1) != 0) ? 166 : 0) ^ j;
    return paramInt << 8 ^ k << 24 ^ j << 16 ^ k << 8 ^ i;
  }
  
  private int LFSR1(int paramInt) {
    return paramInt >> 1 ^ (((paramInt & 0x1) != 0) ? 180 : 0);
  }
  
  private int LFSR2(int paramInt) {
    return paramInt >> 2 ^ (((paramInt & 0x2) != 0) ? 180 : 0) ^ (((paramInt & 0x1) != 0) ? 90 : 0);
  }
  
  private int Mx_X(int paramInt) {
    return paramInt ^ LFSR2(paramInt);
  }
  
  private int Mx_Y(int paramInt) {
    return paramInt ^ LFSR1(paramInt) ^ LFSR2(paramInt);
  }
  
  private int b0(int paramInt) {
    return paramInt & 0xFF;
  }
  
  private int b1(int paramInt) {
    return paramInt >>> 8 & 0xFF;
  }
  
  private int b2(int paramInt) {
    return paramInt >>> 16 & 0xFF;
  }
  
  private int b3(int paramInt) {
    return paramInt >>> 24 & 0xFF;
  }
  
  private int Fe32_0(int paramInt) {
    return this.gSBox[0 + 2 * (paramInt & 0xFF)] ^ this.gSBox[1 + 2 * (paramInt >>> 8 & 0xFF)] ^ this.gSBox[512 + 2 * (paramInt >>> 16 & 0xFF)] ^ this.gSBox[513 + 2 * (paramInt >>> 24 & 0xFF)];
  }
  
  private int Fe32_3(int paramInt) {
    return this.gSBox[0 + 2 * (paramInt >>> 24 & 0xFF)] ^ this.gSBox[1 + 2 * (paramInt & 0xFF)] ^ this.gSBox[512 + 2 * (paramInt >>> 8 & 0xFF)] ^ this.gSBox[513 + 2 * (paramInt >>> 16 & 0xFF)];
  }
  
  private int BytesTo32Bits(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt] & 0xFF | (paramArrayOfbyte[paramInt + 1] & 0xFF) << 8 | (paramArrayOfbyte[paramInt + 2] & 0xFF) << 16 | (paramArrayOfbyte[paramInt + 3] & 0xFF) << 24;
  }
  
  private void Bits32ToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
  }
}
