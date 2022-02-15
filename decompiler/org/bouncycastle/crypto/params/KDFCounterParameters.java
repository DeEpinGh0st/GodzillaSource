package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFCounterParameters implements DerivationParameters {
  private byte[] ki;
  
  private byte[] fixedInputDataCounterPrefix;
  
  private byte[] fixedInputDataCounterSuffix;
  
  private int r;
  
  public KDFCounterParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    this(paramArrayOfbyte1, null, paramArrayOfbyte2, paramInt);
  }
  
  public KDFCounterParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) {
    if (paramArrayOfbyte1 == null)
      throw new IllegalArgumentException("A KDF requires Ki (a seed) as input"); 
    this.ki = Arrays.clone(paramArrayOfbyte1);
    if (paramArrayOfbyte2 == null) {
      this.fixedInputDataCounterPrefix = new byte[0];
    } else {
      this.fixedInputDataCounterPrefix = Arrays.clone(paramArrayOfbyte2);
    } 
    if (paramArrayOfbyte3 == null) {
      this.fixedInputDataCounterSuffix = new byte[0];
    } else {
      this.fixedInputDataCounterSuffix = Arrays.clone(paramArrayOfbyte3);
    } 
    if (paramInt != 8 && paramInt != 16 && paramInt != 24 && paramInt != 32)
      throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32"); 
    this.r = paramInt;
  }
  
  public byte[] getKI() {
    return this.ki;
  }
  
  public byte[] getFixedInputData() {
    return Arrays.clone(this.fixedInputDataCounterSuffix);
  }
  
  public byte[] getFixedInputDataCounterPrefix() {
    return Arrays.clone(this.fixedInputDataCounterPrefix);
  }
  
  public byte[] getFixedInputDataCounterSuffix() {
    return Arrays.clone(this.fixedInputDataCounterSuffix);
  }
  
  public int getR() {
    return this.r;
  }
}
