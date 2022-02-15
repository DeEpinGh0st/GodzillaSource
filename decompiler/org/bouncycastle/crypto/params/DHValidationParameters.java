package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DHValidationParameters {
  private byte[] seed;
  
  private int counter;
  
  public DHValidationParameters(byte[] paramArrayOfbyte, int paramInt) {
    this.seed = paramArrayOfbyte;
    this.counter = paramInt;
  }
  
  public int getCounter() {
    return this.counter;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHValidationParameters))
      return false; 
    DHValidationParameters dHValidationParameters = (DHValidationParameters)paramObject;
    return (dHValidationParameters.counter != this.counter) ? false : Arrays.areEqual(this.seed, dHValidationParameters.seed);
  }
  
  public int hashCode() {
    return this.counter ^ Arrays.hashCode(this.seed);
  }
}
