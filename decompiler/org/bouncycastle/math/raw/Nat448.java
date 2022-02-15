package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat448 {
  public static void copy64(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0];
    paramArrayOflong2[1] = paramArrayOflong1[1];
    paramArrayOflong2[2] = paramArrayOflong1[2];
    paramArrayOflong2[3] = paramArrayOflong1[3];
    paramArrayOflong2[4] = paramArrayOflong1[4];
    paramArrayOflong2[5] = paramArrayOflong1[5];
    paramArrayOflong2[6] = paramArrayOflong1[6];
  }
  
  public static long[] create64() {
    return new long[7];
  }
  
  public static long[] createExt64() {
    return new long[14];
  }
  
  public static boolean eq64(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    for (byte b = 6; b >= 0; b--) {
      if (paramArrayOflong1[b] != paramArrayOflong2[b])
        return false; 
    } 
    return true;
  }
  
  public static long[] fromBigInteger64(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 448)
      throw new IllegalArgumentException(); 
    long[] arrayOfLong = create64();
    byte b = 0;
    while (paramBigInteger.signum() != 0) {
      arrayOfLong[b++] = paramBigInteger.longValue();
      paramBigInteger = paramBigInteger.shiftRight(64);
    } 
    return arrayOfLong;
  }
  
  public static boolean isOne64(long[] paramArrayOflong) {
    if (paramArrayOflong[0] != 1L)
      return false; 
    for (byte b = 1; b < 7; b++) {
      if (paramArrayOflong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public static boolean isZero64(long[] paramArrayOflong) {
    for (byte b = 0; b < 7; b++) {
      if (paramArrayOflong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public static BigInteger toBigInteger64(long[] paramArrayOflong) {
    byte[] arrayOfByte = new byte[56];
    for (byte b = 0; b < 7; b++) {
      long l = paramArrayOflong[b];
      if (l != 0L)
        Pack.longToBigEndian(l, arrayOfByte, 6 - b << 3); 
    } 
    return new BigInteger(1, arrayOfByte);
  }
}
