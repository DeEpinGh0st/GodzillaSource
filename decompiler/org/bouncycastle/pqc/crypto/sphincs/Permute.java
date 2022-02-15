package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Pack;

class Permute {
  private static final int CHACHA_ROUNDS = 12;
  
  protected static int rotl(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  public static void permute(int paramInt, int[] paramArrayOfint) {
    if (paramArrayOfint.length != 16)
      throw new IllegalArgumentException(); 
    if (paramInt % 2 != 0)
      throw new IllegalArgumentException("Number of rounds must be even"); 
    int i = paramArrayOfint[0];
    int j = paramArrayOfint[1];
    int k = paramArrayOfint[2];
    int m = paramArrayOfint[3];
    int n = paramArrayOfint[4];
    int i1 = paramArrayOfint[5];
    int i2 = paramArrayOfint[6];
    int i3 = paramArrayOfint[7];
    int i4 = paramArrayOfint[8];
    int i5 = paramArrayOfint[9];
    int i6 = paramArrayOfint[10];
    int i7 = paramArrayOfint[11];
    int i8 = paramArrayOfint[12];
    int i9 = paramArrayOfint[13];
    int i10 = paramArrayOfint[14];
    int i11 = paramArrayOfint[15];
    for (int i12 = paramInt; i12 > 0; i12 -= 2) {
      i += n;
      i8 = rotl(i8 ^ i, 16);
      i4 += i8;
      n = rotl(n ^ i4, 12);
      i += n;
      i8 = rotl(i8 ^ i, 8);
      i4 += i8;
      n = rotl(n ^ i4, 7);
      j += i1;
      i9 = rotl(i9 ^ j, 16);
      i5 += i9;
      i1 = rotl(i1 ^ i5, 12);
      j += i1;
      i9 = rotl(i9 ^ j, 8);
      i5 += i9;
      i1 = rotl(i1 ^ i5, 7);
      k += i2;
      i10 = rotl(i10 ^ k, 16);
      i6 += i10;
      i2 = rotl(i2 ^ i6, 12);
      k += i2;
      i10 = rotl(i10 ^ k, 8);
      i6 += i10;
      i2 = rotl(i2 ^ i6, 7);
      m += i3;
      i11 = rotl(i11 ^ m, 16);
      i7 += i11;
      i3 = rotl(i3 ^ i7, 12);
      m += i3;
      i11 = rotl(i11 ^ m, 8);
      i7 += i11;
      i3 = rotl(i3 ^ i7, 7);
      i += i1;
      i11 = rotl(i11 ^ i, 16);
      i6 += i11;
      i1 = rotl(i1 ^ i6, 12);
      i += i1;
      i11 = rotl(i11 ^ i, 8);
      i6 += i11;
      i1 = rotl(i1 ^ i6, 7);
      j += i2;
      i8 = rotl(i8 ^ j, 16);
      i7 += i8;
      i2 = rotl(i2 ^ i7, 12);
      j += i2;
      i8 = rotl(i8 ^ j, 8);
      i7 += i8;
      i2 = rotl(i2 ^ i7, 7);
      k += i3;
      i9 = rotl(i9 ^ k, 16);
      i4 += i9;
      i3 = rotl(i3 ^ i4, 12);
      k += i3;
      i9 = rotl(i9 ^ k, 8);
      i4 += i9;
      i3 = rotl(i3 ^ i4, 7);
      m += n;
      i10 = rotl(i10 ^ m, 16);
      i5 += i10;
      n = rotl(n ^ i5, 12);
      m += n;
      i10 = rotl(i10 ^ m, 8);
      i5 += i10;
      n = rotl(n ^ i5, 7);
    } 
    paramArrayOfint[0] = i;
    paramArrayOfint[1] = j;
    paramArrayOfint[2] = k;
    paramArrayOfint[3] = m;
    paramArrayOfint[4] = n;
    paramArrayOfint[5] = i1;
    paramArrayOfint[6] = i2;
    paramArrayOfint[7] = i3;
    paramArrayOfint[8] = i4;
    paramArrayOfint[9] = i5;
    paramArrayOfint[10] = i6;
    paramArrayOfint[11] = i7;
    paramArrayOfint[12] = i8;
    paramArrayOfint[13] = i9;
    paramArrayOfint[14] = i10;
    paramArrayOfint[15] = i11;
  }
  
  void chacha_permute(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int[] arrayOfInt = new int[16];
    byte b;
    for (b = 0; b < 16; b++)
      arrayOfInt[b] = Pack.littleEndianToInt(paramArrayOfbyte2, 4 * b); 
    permute(12, arrayOfInt);
    for (b = 0; b < 16; b++)
      Pack.intToLittleEndian(arrayOfInt[b], paramArrayOfbyte1, 4 * b); 
  }
}
