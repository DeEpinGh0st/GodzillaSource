package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFDoublePipelineIterationParameters implements DerivationParameters {
  private static final int UNUSED_R = 32;
  
  private final byte[] ki;
  
  private final boolean useCounter;
  
  private final int r;
  
  private final byte[] fixedInputData;
  
  private KDFDoublePipelineIterationParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt, boolean paramBoolean) {
    if (paramArrayOfbyte1 == null)
      throw new IllegalArgumentException("A KDF requires Ki (a seed) as input"); 
    this.ki = Arrays.clone(paramArrayOfbyte1);
    if (paramArrayOfbyte2 == null) {
      this.fixedInputData = new byte[0];
    } else {
      this.fixedInputData = Arrays.clone(paramArrayOfbyte2);
    } 
    if (paramInt != 8 && paramInt != 16 && paramInt != 24 && paramInt != 32)
      throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32"); 
    this.r = paramInt;
    this.useCounter = paramBoolean;
  }
  
  public static KDFDoublePipelineIterationParameters createWithCounter(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    return new KDFDoublePipelineIterationParameters(paramArrayOfbyte1, paramArrayOfbyte2, paramInt, true);
  }
  
  public static KDFDoublePipelineIterationParameters createWithoutCounter(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return new KDFDoublePipelineIterationParameters(paramArrayOfbyte1, paramArrayOfbyte2, 32, false);
  }
  
  public byte[] getKI() {
    return this.ki;
  }
  
  public boolean useCounter() {
    return this.useCounter;
  }
  
  public int getR() {
    return this.r;
  }
  
  public byte[] getFixedInputData() {
    return Arrays.clone(this.fixedInputData);
  }
}
