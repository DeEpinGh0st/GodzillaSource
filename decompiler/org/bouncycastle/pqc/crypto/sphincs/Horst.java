package org.bouncycastle.pqc.crypto.sphincs;

class Horst {
  static final int HORST_LOGT = 16;
  
  static final int HORST_T = 65536;
  
  static final int HORST_K = 32;
  
  static final int HORST_SKBYTES = 32;
  
  static final int HORST_SIGBYTES = 13312;
  
  static final int N_MASKS = 32;
  
  static void expand_seed(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    Seed.prg(paramArrayOfbyte1, 0, 2097152L, paramArrayOfbyte2, 0);
  }
  
  static int horst_sign(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, byte[] paramArrayOfbyte5) {
    byte[] arrayOfByte1 = new byte[2097152];
    int i = paramInt;
    byte[] arrayOfByte2 = new byte[4194272];
    expand_seed(arrayOfByte1, paramArrayOfbyte3);
    byte b;
    for (b = 0; b < 65536; b++)
      paramHashFunctions.hash_n_n(arrayOfByte2, (65535 + b) * 32, arrayOfByte1, b * 32); 
    for (b = 0; b < 16; b++) {
      long l1 = ((1 << 16 - b) - 1);
      long l2 = ((1 << 16 - b - 1) - 1);
      for (byte b1 = 0; b1 < 1 << 16 - b - 1; b1++)
        paramHashFunctions.hash_2n_n_mask(arrayOfByte2, (int)((l2 + b1) * 32L), arrayOfByte2, (int)((l1 + (2 * b1)) * 32L), paramArrayOfbyte4, 2 * b * 32); 
    } 
    char c;
    for (c = 'ߠ'; c < '࿠'; c++)
      paramArrayOfbyte1[i++] = arrayOfByte2[c]; 
    for (b = 0; b < 32; b++) {
      int j = (paramArrayOfbyte5[2 * b] & 0xFF) + ((paramArrayOfbyte5[2 * b + 1] & 0xFF) << 8);
      byte b1;
      for (b1 = 0; b1 < 32; b1++)
        paramArrayOfbyte1[i++] = arrayOfByte1[j * 32 + b1]; 
      j += 65535;
      for (c = Character.MIN_VALUE; c < '\n'; c++) {
        j = ((j & 0x1) != 0) ? (j + 1) : (j - 1);
        for (b1 = 0; b1 < 32; b1++)
          paramArrayOfbyte1[i++] = arrayOfByte2[j * 32 + b1]; 
        j = (j - 1) / 2;
      } 
    } 
    for (b = 0; b < 32; b++)
      paramArrayOfbyte2[b] = arrayOfByte2[b]; 
    return 13312;
  }
  
  static int horst_verify(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    byte[] arrayOfByte = new byte[1024];
    int i = paramInt + 2048;
    for (byte b1 = 0; b1 < 32; b1++) {
      int j = (paramArrayOfbyte4[2 * b1] & 0xFF) + ((paramArrayOfbyte4[2 * b1 + 1] & 0xFF) << 8);
      if ((j & 0x1) == 0) {
        paramHashFunctions.hash_n_n(arrayOfByte, 0, paramArrayOfbyte2, i);
        for (byte b = 0; b < 32; b++)
          arrayOfByte[32 + b] = paramArrayOfbyte2[i + 32 + b]; 
      } else {
        paramHashFunctions.hash_n_n(arrayOfByte, 32, paramArrayOfbyte2, i);
        for (byte b = 0; b < 32; b++)
          arrayOfByte[b] = paramArrayOfbyte2[i + 32 + b]; 
      } 
      i += 64;
      for (byte b3 = 1; b3 < 10; b3++) {
        j >>>= 1;
        if ((j & 0x1) == 0) {
          paramHashFunctions.hash_2n_n_mask(arrayOfByte, 0, arrayOfByte, 0, paramArrayOfbyte3, 2 * (b3 - 1) * 32);
          for (byte b = 0; b < 32; b++)
            arrayOfByte[32 + b] = paramArrayOfbyte2[i + b]; 
        } else {
          paramHashFunctions.hash_2n_n_mask(arrayOfByte, 32, arrayOfByte, 0, paramArrayOfbyte3, 2 * (b3 - 1) * 32);
          for (byte b = 0; b < 32; b++)
            arrayOfByte[b] = paramArrayOfbyte2[i + b]; 
        } 
        i += 32;
      } 
      j >>>= 1;
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, 0, arrayOfByte, 0, paramArrayOfbyte3, 576);
      for (byte b4 = 0; b4 < 32; b4++) {
        if (paramArrayOfbyte2[paramInt + j * 32 + b4] != arrayOfByte[b4]) {
          for (b4 = 0; b4 < 32; b4++)
            paramArrayOfbyte1[b4] = 0; 
          return -1;
        } 
      } 
    } 
    byte b2;
    for (b2 = 0; b2 < 32; b2++)
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, b2 * 32, paramArrayOfbyte2, paramInt + 2 * b2 * 32, paramArrayOfbyte3, 640); 
    for (b2 = 0; b2 < 16; b2++)
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, b2 * 32, arrayOfByte, 2 * b2 * 32, paramArrayOfbyte3, 704); 
    for (b2 = 0; b2 < 8; b2++)
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, b2 * 32, arrayOfByte, 2 * b2 * 32, paramArrayOfbyte3, 768); 
    for (b2 = 0; b2 < 4; b2++)
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, b2 * 32, arrayOfByte, 2 * b2 * 32, paramArrayOfbyte3, 832); 
    for (b2 = 0; b2 < 2; b2++)
      paramHashFunctions.hash_2n_n_mask(arrayOfByte, b2 * 32, arrayOfByte, 2 * b2 * 32, paramArrayOfbyte3, 896); 
    paramHashFunctions.hash_2n_n_mask(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte3, 960);
    return 0;
  }
}
