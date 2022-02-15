package org.bouncycastle.crypto.params;

public class RC2Parameters extends KeyParameter {
  private int bits;
  
  public RC2Parameters(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, (paramArrayOfbyte.length > 128) ? 1024 : (paramArrayOfbyte.length * 8));
  }
  
  public RC2Parameters(byte[] paramArrayOfbyte, int paramInt) {
    super(paramArrayOfbyte);
    this.bits = paramInt;
  }
  
  public int getEffectiveKeyBits() {
    return this.bits;
  }
}
