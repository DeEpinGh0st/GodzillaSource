package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.util.Arrays;

public class PKCS12KeyWithParameters extends PKCS12Key implements PBEKey {
  private final byte[] salt;
  
  private final int iterationCount;
  
  public PKCS12KeyWithParameters(char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt) {
    super(paramArrayOfchar);
    this.salt = Arrays.clone(paramArrayOfbyte);
    this.iterationCount = paramInt;
  }
  
  public PKCS12KeyWithParameters(char[] paramArrayOfchar, boolean paramBoolean, byte[] paramArrayOfbyte, int paramInt) {
    super(paramArrayOfchar, paramBoolean);
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
