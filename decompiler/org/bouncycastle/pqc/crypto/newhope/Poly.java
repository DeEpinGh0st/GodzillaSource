package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class Poly {
  static void add(short[] paramArrayOfshort1, short[] paramArrayOfshort2, short[] paramArrayOfshort3) {
    for (byte b = 0; b < 'Ѐ'; b++)
      paramArrayOfshort3[b] = Reduce.barrett((short)(paramArrayOfshort1[b] + paramArrayOfshort2[b])); 
  }
  
  static void fromBytes(short[] paramArrayOfshort, byte[] paramArrayOfbyte) {
    for (byte b = 0; b < 'Ā'; b++) {
      int i = 7 * b;
      int j = paramArrayOfbyte[i + 0] & 0xFF;
      int k = paramArrayOfbyte[i + 1] & 0xFF;
      int m = paramArrayOfbyte[i + 2] & 0xFF;
      int n = paramArrayOfbyte[i + 3] & 0xFF;
      int i1 = paramArrayOfbyte[i + 4] & 0xFF;
      int i2 = paramArrayOfbyte[i + 5] & 0xFF;
      int i3 = paramArrayOfbyte[i + 6] & 0xFF;
      int i4 = 4 * b;
      paramArrayOfshort[i4 + 0] = (short)(j | (k & 0x3F) << 8);
      paramArrayOfshort[i4 + 1] = (short)(k >>> 6 | m << 2 | (n & 0xF) << 10);
      paramArrayOfshort[i4 + 2] = (short)(n >>> 4 | i1 << 4 | (i2 & 0x3) << 12);
      paramArrayOfshort[i4 + 3] = (short)(i2 >>> 2 | i3 << 6);
    } 
  }
  
  static void fromNTT(short[] paramArrayOfshort) {
    NTT.bitReverse(paramArrayOfshort);
    NTT.core(paramArrayOfshort, Precomp.OMEGAS_INV_MONTGOMERY);
    NTT.mulCoefficients(paramArrayOfshort, Precomp.PSIS_INV_MONTGOMERY);
  }
  
  static void getNoise(short[] paramArrayOfshort, byte[] paramArrayOfbyte, byte paramByte) {
    byte[] arrayOfByte1 = new byte[8];
    arrayOfByte1[0] = paramByte;
    byte[] arrayOfByte2 = new byte[4096];
    ChaCha20.process(paramArrayOfbyte, arrayOfByte1, arrayOfByte2, 0, arrayOfByte2.length);
    for (byte b = 0; b < 'Ѐ'; b++) {
      int i = Pack.bigEndianToInt(arrayOfByte2, b * 4);
      int j = 0;
      int k;
      for (k = 0; k < 8; k++)
        j += i >> k & 0x1010101; 
      k = (j >>> 24) + (j >>> 0) & 0xFF;
      int m = (j >>> 16) + (j >>> 8) & 0xFF;
      paramArrayOfshort[b] = (short)(k + 12289 - m);
    } 
  }
  
  static void pointWise(short[] paramArrayOfshort1, short[] paramArrayOfshort2, short[] paramArrayOfshort3) {
    for (byte b = 0; b < 'Ѐ'; b++) {
      int i = paramArrayOfshort1[b] & 0xFFFF;
      int j = paramArrayOfshort2[b] & 0xFFFF;
      short s = Reduce.montgomery(3186 * j);
      paramArrayOfshort3[b] = Reduce.montgomery(i * (s & 0xFFFF));
    } 
  }
  
  static void toBytes(byte[] paramArrayOfbyte, short[] paramArrayOfshort) {
    for (byte b = 0; b < 'Ā'; b++) {
      int i = 4 * b;
      short s1 = normalize(paramArrayOfshort[i + 0]);
      short s2 = normalize(paramArrayOfshort[i + 1]);
      short s3 = normalize(paramArrayOfshort[i + 2]);
      short s4 = normalize(paramArrayOfshort[i + 3]);
      int j = 7 * b;
      paramArrayOfbyte[j + 0] = (byte)s1;
      paramArrayOfbyte[j + 1] = (byte)(s1 >> 8 | s2 << 6);
      paramArrayOfbyte[j + 2] = (byte)(s2 >> 2);
      paramArrayOfbyte[j + 3] = (byte)(s2 >> 10 | s3 << 4);
      paramArrayOfbyte[j + 4] = (byte)(s3 >> 4);
      paramArrayOfbyte[j + 5] = (byte)(s3 >> 12 | s4 << 2);
      paramArrayOfbyte[j + 6] = (byte)(s4 >> 6);
    } 
  }
  
  static void toNTT(short[] paramArrayOfshort) {
    NTT.mulCoefficients(paramArrayOfshort, Precomp.PSIS_BITREV_MONTGOMERY);
    NTT.core(paramArrayOfshort, Precomp.OMEGAS_MONTGOMERY);
  }
  
  static void uniform(short[] paramArrayOfshort, byte[] paramArrayOfbyte) {
    SHAKEDigest sHAKEDigest = new SHAKEDigest(128);
    sHAKEDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte b = 0;
    while (true) {
      byte[] arrayOfByte = new byte[256];
      sHAKEDigest.doOutput(arrayOfByte, 0, arrayOfByte.length);
      for (byte b1 = 0; b1 < arrayOfByte.length; b1 += 2) {
        int i = arrayOfByte[b1] & 0xFF | (arrayOfByte[b1 + 1] & 0xFF) << 8;
        i &= 0x3FFF;
        if (i < 12289) {
          paramArrayOfshort[b++] = (short)i;
          if (b == 'Ѐ')
            return; 
        } 
      } 
    } 
  }
  
  private static short normalize(short paramShort) {
    short s = Reduce.barrett(paramShort);
    int j = s - 12289;
    int k = j >> 31;
    int i = j ^ (s ^ j) & k;
    return (short)i;
  }
}
