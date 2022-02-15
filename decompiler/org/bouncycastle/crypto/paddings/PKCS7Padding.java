package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class PKCS7Padding implements BlockCipherPadding {
  public void init(SecureRandom paramSecureRandom) throws IllegalArgumentException {}
  
  public String getPaddingName() {
    return "PKCS7";
  }
  
  public int addPadding(byte[] paramArrayOfbyte, int paramInt) {
    byte b = (byte)(paramArrayOfbyte.length - paramInt);
    while (paramInt < paramArrayOfbyte.length) {
      paramArrayOfbyte[paramInt] = b;
      paramInt++;
    } 
    return b;
  }
  
  public int padCount(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    int i = paramArrayOfbyte[paramArrayOfbyte.length - 1] & 0xFF;
    byte b = (byte)i;
    int j = ((i > paramArrayOfbyte.length) ? 1 : 0) | ((i == 0) ? 1 : 0);
    for (byte b1 = 0; b1 < paramArrayOfbyte.length; b1++)
      j |= ((paramArrayOfbyte.length - b1 <= i) ? 1 : 0) & ((paramArrayOfbyte[b1] != b) ? 1 : 0); 
    if (j != 0)
      throw new InvalidCipherTextException("pad block corrupted"); 
    return i;
  }
}
