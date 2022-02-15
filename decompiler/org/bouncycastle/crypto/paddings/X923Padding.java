package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class X923Padding implements BlockCipherPadding {
  SecureRandom random = null;
  
  public void init(SecureRandom paramSecureRandom) throws IllegalArgumentException {
    this.random = paramSecureRandom;
  }
  
  public String getPaddingName() {
    return "X9.23";
  }
  
  public int addPadding(byte[] paramArrayOfbyte, int paramInt) {
    byte b = (byte)(paramArrayOfbyte.length - paramInt);
    while (paramInt < paramArrayOfbyte.length - 1) {
      if (this.random == null) {
        paramArrayOfbyte[paramInt] = 0;
      } else {
        paramArrayOfbyte[paramInt] = (byte)this.random.nextInt();
      } 
      paramInt++;
    } 
    paramArrayOfbyte[paramInt] = b;
    return b;
  }
  
  public int padCount(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    int i = paramArrayOfbyte[paramArrayOfbyte.length - 1] & 0xFF;
    if (i > paramArrayOfbyte.length)
      throw new InvalidCipherTextException("pad block corrupted"); 
    return i;
  }
}
