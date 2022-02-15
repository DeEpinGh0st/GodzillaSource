package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class BigIntegers {
  private static final int MAX_ITERATIONS = 1000;
  
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  public static byte[] asUnsignedByteArray(BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  public static byte[] asUnsignedByteArray(int paramInt, BigInteger paramBigInteger) {
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (arrayOfByte1.length == paramInt)
      return arrayOfByte1; 
    byte b = (arrayOfByte1[0] == 0) ? 1 : 0;
    int i = arrayOfByte1.length - b;
    if (i > paramInt)
      throw new IllegalArgumentException("standard length exceeded for value"); 
    byte[] arrayOfByte2 = new byte[paramInt];
    System.arraycopy(arrayOfByte1, b, arrayOfByte2, arrayOfByte2.length - i, i);
    return arrayOfByte2;
  }
  
  public static BigInteger createRandomInRange(BigInteger paramBigInteger1, BigInteger paramBigInteger2, SecureRandom paramSecureRandom) {
    int i = paramBigInteger1.compareTo(paramBigInteger2);
    if (i >= 0) {
      if (i > 0)
        throw new IllegalArgumentException("'min' may not be greater than 'max'"); 
      return paramBigInteger1;
    } 
    if (paramBigInteger1.bitLength() > paramBigInteger2.bitLength() / 2)
      return createRandomInRange(ZERO, paramBigInteger2.subtract(paramBigInteger1), paramSecureRandom).add(paramBigInteger1); 
    for (byte b = 0; b < 'Ϩ'; b++) {
      BigInteger bigInteger = new BigInteger(paramBigInteger2.bitLength(), paramSecureRandom);
      if (bigInteger.compareTo(paramBigInteger1) >= 0 && bigInteger.compareTo(paramBigInteger2) <= 0)
        return bigInteger; 
    } 
    return (new BigInteger(paramBigInteger2.subtract(paramBigInteger1).bitLength() - 1, paramSecureRandom)).add(paramBigInteger1);
  }
  
  public static BigInteger fromUnsignedByteArray(byte[] paramArrayOfbyte) {
    return new BigInteger(1, paramArrayOfbyte);
  }
  
  public static BigInteger fromUnsignedByteArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = paramArrayOfbyte;
    if (paramInt1 != 0 || paramInt2 != paramArrayOfbyte.length) {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    } 
    return new BigInteger(1, arrayOfByte);
  }
}
