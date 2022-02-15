package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class ISO10126d2Padding implements BlockCipherPadding {
  SecureRandom random;
  
  public void init(SecureRandom paramSecureRandom) throws IllegalArgumentException {
    if (paramSecureRandom != null) {
      this.random = paramSecureRandom;
    } else {
      this.random = new SecureRandom();
    } 
  }
  
  public String getPaddingName() {
    return "ISO10126-2";
  }
  
  public int addPadding(byte[] paramArrayOfbyte, int paramInt) {
    byte b = (byte)(paramArrayOfbyte.length - paramInt);
    while (paramInt < paramArrayOfbyte.length - 1) {
      paramArrayOfbyte[paramInt] = (byte)this.random.nextInt();
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
