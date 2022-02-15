package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.util.Arrays;

class ErrorCorrection {
  static int abs(int paramInt) {
    int i = paramInt >> 31;
    return (paramInt ^ i) - i;
  }
  
  static int f(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3) {
    int m = paramInt3 * 2730;
    int j = m >> 25;
    m = paramInt3 - j * 12289;
    m = 12288 - m;
    m >>= 31;
    j -= m;
    int k = j & 0x1;
    int i = j >> 1;
    paramArrayOfint[paramInt1] = i + k;
    k = --j & 0x1;
    paramArrayOfint[paramInt2] = (j >> 1) + k;
    return abs(paramInt3 - paramArrayOfint[paramInt1] * 2 * 12289);
  }
  
  static int g(int paramInt) {
    int k = paramInt * 2730;
    int i = k >> 27;
    k = paramInt - i * 49156;
    k = 49155 - k;
    k >>= 31;
    i -= k;
    int j = i & 0x1;
    i = (i >> 1) + j;
    i *= 98312;
    return abs(i - paramInt);
  }
  
  static void helpRec(short[] paramArrayOfshort1, short[] paramArrayOfshort2, byte[] paramArrayOfbyte, byte paramByte) {
    byte[] arrayOfByte1 = new byte[8];
    arrayOfByte1[0] = paramByte;
    byte[] arrayOfByte2 = new byte[32];
    ChaCha20.process(paramArrayOfbyte, arrayOfByte1, arrayOfByte2, 0, arrayOfByte2.length);
    int[] arrayOfInt1 = new int[8];
    int[] arrayOfInt2 = new int[4];
    for (byte b = 0; b < 'Ā'; b++) {
      int j = arrayOfByte2[b >>> 3] >>> (b & 0x7) & 0x1;
      int i = f(arrayOfInt1, 0, 4, 8 * paramArrayOfshort2[0 + b] + 4 * j);
      i += f(arrayOfInt1, 1, 5, 8 * paramArrayOfshort2[256 + b] + 4 * j);
      i += f(arrayOfInt1, 2, 6, 8 * paramArrayOfshort2[512 + b] + 4 * j);
      i += f(arrayOfInt1, 3, 7, 8 * paramArrayOfshort2[768 + b] + 4 * j);
      i = 24577 - i >> 31;
      arrayOfInt2[0] = (i ^ 0xFFFFFFFF) & arrayOfInt1[0] ^ i & arrayOfInt1[4];
      arrayOfInt2[1] = (i ^ 0xFFFFFFFF) & arrayOfInt1[1] ^ i & arrayOfInt1[5];
      arrayOfInt2[2] = (i ^ 0xFFFFFFFF) & arrayOfInt1[2] ^ i & arrayOfInt1[6];
      arrayOfInt2[3] = (i ^ 0xFFFFFFFF) & arrayOfInt1[3] ^ i & arrayOfInt1[7];
      paramArrayOfshort1[0 + b] = (short)(arrayOfInt2[0] - arrayOfInt2[3] & 0x3);
      paramArrayOfshort1[256 + b] = (short)(arrayOfInt2[1] - arrayOfInt2[3] & 0x3);
      paramArrayOfshort1[512 + b] = (short)(arrayOfInt2[2] - arrayOfInt2[3] & 0x3);
      paramArrayOfshort1[768 + b] = (short)(-i + 2 * arrayOfInt2[3] & 0x3);
    } 
  }
  
  static short LDDecode(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = g(paramInt1);
    i += g(paramInt2);
    i += g(paramInt3);
    i += g(paramInt4);
    i -= 98312;
    return (short)(i >>> 31);
  }
  
  static void rec(byte[] paramArrayOfbyte, short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    Arrays.fill(paramArrayOfbyte, (byte)0);
    int[] arrayOfInt = new int[4];
    for (byte b = 0; b < 'Ā'; b++) {
      arrayOfInt[0] = 196624 + 8 * paramArrayOfshort1[0 + b] - 12289 * (2 * paramArrayOfshort2[0 + b] + paramArrayOfshort2[768 + b]);
      arrayOfInt[1] = 196624 + 8 * paramArrayOfshort1[256 + b] - 12289 * (2 * paramArrayOfshort2[256 + b] + paramArrayOfshort2[768 + b]);
      arrayOfInt[2] = 196624 + 8 * paramArrayOfshort1[512 + b] - 12289 * (2 * paramArrayOfshort2[512 + b] + paramArrayOfshort2[768 + b]);
      arrayOfInt[3] = 196624 + 8 * paramArrayOfshort1[768 + b] - 12289 * paramArrayOfshort2[768 + b];
      paramArrayOfbyte[b >>> 3] = (byte)(paramArrayOfbyte[b >>> 3] | LDDecode(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]) << (b & 0x7));
    } 
  }
}
