package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Arrays;

public class BasicGCMExponentiator implements GCMExponentiator {
  private int[] x;
  
  public void init(byte[] paramArrayOfbyte) {
    this.x = GCMUtil.asInts(paramArrayOfbyte);
  }
  
  public void exponentiateX(long paramLong, byte[] paramArrayOfbyte) {
    int[] arrayOfInt = GCMUtil.oneAsInts();
    if (paramLong > 0L) {
      int[] arrayOfInt1 = Arrays.clone(this.x);
      do {
        if ((paramLong & 0x1L) != 0L)
          GCMUtil.multiply(arrayOfInt, arrayOfInt1); 
        GCMUtil.multiply(arrayOfInt1, arrayOfInt1);
        paramLong >>>= 1L;
      } while (paramLong > 0L);
    } 
    GCMUtil.asBytes(arrayOfInt, paramArrayOfbyte);
  }
}
