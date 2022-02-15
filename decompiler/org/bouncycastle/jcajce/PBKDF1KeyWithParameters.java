package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.util.Arrays;

public class PBKDF1KeyWithParameters extends PBKDF1Key implements PBEKey {
  private final byte[] salt;
  
  private final int iterationCount;
  
  public PBKDF1KeyWithParameters(char[] paramArrayOfchar, CharToByteConverter paramCharToByteConverter, byte[] paramArrayOfbyte, int paramInt) {
    super(paramArrayOfchar, paramCharToByteConverter);
    this.salt = Arrays.clone(paramArrayOfbyte);
    this.iterationCount = paramInt;
  }
  
  public byte[] getSalt() {
    return this.salt;
  }
  
  public int getIterationCount() {
    return this.iterationCount;
  }
}
