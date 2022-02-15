package org.bouncycastle.crypto.params;

public class DESParameters extends KeyParameter {
  public static final int DES_KEY_LENGTH = 8;
  
  private static final int N_DES_WEAK_KEYS = 16;
  
  private static byte[] DES_weak_keys = new byte[] { 
      1, 1, 1, 1, 1, 1, 1, 1, 31, 31, 
      31, 31, 14, 14, 14, 14, -32, -32, -32, -32, 
      -15, -15, -15, -15, -2, -2, -2, -2, -2, -2, 
      -2, -2, 1, -2, 1, -2, 1, -2, 1, -2, 
      31, -32, 31, -32, 14, -15, 14, -15, 1, -32, 
      1, -32, 1, -15, 1, -15, 31, -2, 31, -2, 
      14, -2, 14, -2, 1, 31, 1, 31, 1, 14, 
      1, 14, -32, -2, -32, -2, -15, -2, -15, -2, 
      -2, 1, -2, 1, -2, 1, -2, 1, -32, 31, 
      -32, 31, -15, 14, -15, 14, -32, 1, -32, 1, 
      -15, 1, -15, 1, -2, 31, -2, 31, -2, 14, 
      -2, 14, 31, 1, 31, 1, 14, 1, 14, 1, 
      -2, -32, -2, -32, -2, -15, -2, -15 };
  
  public DESParameters(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    if (isWeakKey(paramArrayOfbyte, 0))
      throw new IllegalArgumentException("attempt to create weak DES key"); 
  }
  
  public static boolean isWeakKey(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte.length - paramInt < 8)
      throw new IllegalArgumentException("key material too short."); 
    for (byte b = 0; b < 16; b++) {
      byte b1 = 0;
      while (true) {
        if (b1 < 8) {
          if (paramArrayOfbyte[b1 + paramInt] != DES_weak_keys[b * 8 + b1])
            break; 
          b1++;
          continue;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public static void setOddParity(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      paramArrayOfbyte[b] = (byte)(b1 & 0xFE | (b1 >> 1 ^ b1 >> 2 ^ b1 >> 3 ^ b1 >> 4 ^ b1 >> 5 ^ b1 >> 6 ^ b1 >> 7 ^ 0x1) & 0x1);
    } 
  }
}
