package org.bouncycastle.crypto.modes.gcm;

public class BasicGCMMultiplier implements GCMMultiplier {
  private int[] H;
  
  public void init(byte[] paramArrayOfbyte) {
    this.H = GCMUtil.asInts(paramArrayOfbyte);
  }
  
  public void multiplyH(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = GCMUtil.asInts(paramArrayOfbyte);
    GCMUtil.multiply(arrayOfInt, this.H);
    GCMUtil.asBytes(arrayOfInt, paramArrayOfbyte);
  }
}
