package org.bouncycastle.crypto.tls;

public class MaxFragmentLength {
  public static final short pow2_9 = 1;
  
  public static final short pow2_10 = 2;
  
  public static final short pow2_11 = 3;
  
  public static final short pow2_12 = 4;
  
  public static boolean isValid(short paramShort) {
    return (paramShort >= 1 && paramShort <= 4);
  }
}
