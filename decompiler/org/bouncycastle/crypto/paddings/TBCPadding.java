package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class TBCPadding implements BlockCipherPadding {
  public void init(SecureRandom paramSecureRandom) throws IllegalArgumentException {}
  
  public String getPaddingName() {
    return "TBC";
  }
  
  public int addPadding(byte[] paramArrayOfbyte, int paramInt) {
    byte b;
    int i = paramArrayOfbyte.length - paramInt;
    if (paramInt > 0) {
      b = (byte)(((paramArrayOfbyte[paramInt - 1] & 0x1) == 0) ? 255 : 0);
    } else {
      b = (byte)(((paramArrayOfbyte[paramArrayOfbyte.length - 1] & 0x1) == 0) ? 255 : 0);
    } 
    while (paramInt < paramArrayOfbyte.length) {
      paramArrayOfbyte[paramInt] = b;
      paramInt++;
    } 
    return i;
  }
  
  public int padCount(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    byte b = paramArrayOfbyte[paramArrayOfbyte.length - 1];
    int i;
    for (i = paramArrayOfbyte.length - 1; i > 0 && paramArrayOfbyte[i - 1] == b; i--);
    return paramArrayOfbyte.length - i;
  }
}
