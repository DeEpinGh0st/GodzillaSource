package org.bouncycastle.crypto.params;

public class DESedeParameters extends DESParameters {
  public static final int DES_EDE_KEY_LENGTH = 24;
  
  public DESedeParameters(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    if (isWeakKey(paramArrayOfbyte, 0, paramArrayOfbyte.length))
      throw new IllegalArgumentException("attempt to create weak DESede key"); 
  }
  
  public static boolean isWeakKey(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i += 8) {
      if (DESParameters.isWeakKey(paramArrayOfbyte, i))
        return true; 
    } 
    return false;
  }
  
  public static boolean isWeakKey(byte[] paramArrayOfbyte, int paramInt) {
    return isWeakKey(paramArrayOfbyte, paramInt, paramArrayOfbyte.length - paramInt);
  }
  
  public static boolean isRealEDEKey(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte.length == 16) ? isReal2Key(paramArrayOfbyte, paramInt) : isReal3Key(paramArrayOfbyte, paramInt);
  }
  
  public static boolean isReal2Key(byte[] paramArrayOfbyte, int paramInt) {
    boolean bool = false;
    for (int i = paramInt; i != paramInt + 8; i++) {
      if (paramArrayOfbyte[i] != paramArrayOfbyte[i + 8])
        bool = true; 
    } 
    return bool;
  }
  
  public static boolean isReal3Key(byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    int j = 0;
    int k = 0;
    for (int m = paramInt; m != paramInt + 8; m++) {
      i |= (paramArrayOfbyte[m] != paramArrayOfbyte[m + 8]) ? 1 : 0;
      j |= (paramArrayOfbyte[m] != paramArrayOfbyte[m + 16]) ? 1 : 0;
      k |= (paramArrayOfbyte[m + 8] != paramArrayOfbyte[m + 16]) ? 1 : 0;
    } 
    return (i != 0 && j != 0 && k != 0);
  }
}
