package org.bouncycastle.pqc.crypto.sphincs;

class Wots {
  static final int WOTS_LOGW = 4;
  
  static final int WOTS_W = 16;
  
  static final int WOTS_L1 = 64;
  
  static final int WOTS_L = 67;
  
  static final int WOTS_LOG_L = 7;
  
  static final int WOTS_SIGBYTES = 2144;
  
  static void expand_seed(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    clear(paramArrayOfbyte1, paramInt1, 2144);
    Seed.prg(paramArrayOfbyte1, paramInt1, 2144L, paramArrayOfbyte2, paramInt2);
  }
  
  private static void clear(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    for (int i = 0; i != paramInt2; i++)
      paramArrayOfbyte[i + paramInt1] = 0; 
  }
  
  static void gen_chain(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, int paramInt3, int paramInt4) {
    for (byte b2 = 0; b2 < 32; b2++)
      paramArrayOfbyte1[b2 + paramInt1] = paramArrayOfbyte2[b2 + paramInt2]; 
    for (byte b1 = 0; b1 < paramInt4 && b1 < 16; b1++)
      paramHashFunctions.hash_n_n_mask(paramArrayOfbyte1, paramInt1, paramArrayOfbyte1, paramInt1, paramArrayOfbyte3, paramInt3 + b1 * 32); 
  }
  
  void wots_pkgen(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, int paramInt3) {
    expand_seed(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    for (byte b = 0; b < 67; b++)
      gen_chain(paramHashFunctions, paramArrayOfbyte1, paramInt1 + b * 32, paramArrayOfbyte1, paramInt1 + b * 32, paramArrayOfbyte3, paramInt3, 15); 
  }
  
  void wots_sign(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    int[] arrayOfInt = new int[67];
    int i = 0;
    byte b;
    for (b = 0; b < 64; b += 2) {
      arrayOfInt[b] = paramArrayOfbyte2[b / 2] & 0xF;
      arrayOfInt[b + 1] = (paramArrayOfbyte2[b / 2] & 0xFF) >>> 4;
      i += 15 - arrayOfInt[b];
      i += 15 - arrayOfInt[b + 1];
    } 
    while (b < 67) {
      arrayOfInt[b] = i & 0xF;
      i >>>= 4;
      b++;
    } 
    expand_seed(paramArrayOfbyte1, paramInt, paramArrayOfbyte3, 0);
    for (b = 0; b < 67; b++)
      gen_chain(paramHashFunctions, paramArrayOfbyte1, paramInt + b * 32, paramArrayOfbyte1, paramInt + b * 32, paramArrayOfbyte4, 0, arrayOfInt[b]); 
  }
  
  void wots_verify(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    int[] arrayOfInt = new int[67];
    int i = 0;
    byte b;
    for (b = 0; b < 64; b += 2) {
      arrayOfInt[b] = paramArrayOfbyte3[b / 2] & 0xF;
      arrayOfInt[b + 1] = (paramArrayOfbyte3[b / 2] & 0xFF) >>> 4;
      i += 15 - arrayOfInt[b];
      i += 15 - arrayOfInt[b + 1];
    } 
    while (b < 67) {
      arrayOfInt[b] = i & 0xF;
      i >>>= 4;
      b++;
    } 
    for (b = 0; b < 67; b++)
      gen_chain(paramHashFunctions, paramArrayOfbyte1, b * 32, paramArrayOfbyte2, paramInt + b * 32, paramArrayOfbyte4, arrayOfInt[b] * 32, 15 - arrayOfInt[b]); 
  }
}
