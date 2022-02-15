package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DSAValidationParameters {
  private int usageIndex;
  
  private byte[] seed;
  
  private int counter;
  
  public DSAValidationParameters(byte[] paramArrayOfbyte, int paramInt) {
    this(paramArrayOfbyte, paramInt, -1);
  }
  
  public DSAValidationParameters(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.seed = paramArrayOfbyte;
    this.counter = paramInt1;
    this.usageIndex = paramInt2;
  }
  
  public int getCounter() {
    return this.counter;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
  
  public int getUsageIndex() {
    return this.usageIndex;
  }
  
  public int hashCode() {
    return this.counter ^ Arrays.hashCode(this.seed);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DSAValidationParameters))
      return false; 
    DSAValidationParameters dSAValidationParameters = (DSAValidationParameters)paramObject;
    return (dSAValidationParameters.counter != this.counter) ? false : Arrays.areEqual(this.seed, dSAValidationParameters.seed);
  }
}
