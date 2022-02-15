package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFFeedbackParameters implements DerivationParameters {
  private static final int UNUSED_R = -1;
  
  private final byte[] ki;
  
  private final byte[] iv;
  
  private final boolean useCounter;
  
  private final int r;
  
  private final byte[] fixedInputData;
  
  private KDFFeedbackParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt, boolean paramBoolean) {
    if (paramArrayOfbyte1 == null)
      throw new IllegalArgumentException("A KDF requires Ki (a seed) as input"); 
    this.ki = Arrays.clone(paramArrayOfbyte1);
    if (paramArrayOfbyte3 == null) {
      this.fixedInputData = new byte[0];
    } else {
      this.fixedInputData = Arrays.clone(paramArrayOfbyte3);
    } 
    this.r = paramInt;
    if (paramArrayOfbyte2 == null) {
      this.iv = new byte[0];
    } else {
      this.iv = Arrays.clone(paramArrayOfbyte2);
    } 
    this.useCounter = paramBoolean;
  }
  
  public static KDFFeedbackParameters createWithCounter(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) {
    if (paramInt != 8 && paramInt != 16 && paramInt != 24 && paramInt != 32)
      throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32"); 
    return new KDFFeedbackParameters(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramInt, true);
  }
  
  public static KDFFeedbackParameters createWithoutCounter(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    return new KDFFeedbackParameters(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, -1, false);
  }
  
  public byte[] getKI() {
    return this.ki;
  }
  
  public byte[] getIV() {
    return this.iv;
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
