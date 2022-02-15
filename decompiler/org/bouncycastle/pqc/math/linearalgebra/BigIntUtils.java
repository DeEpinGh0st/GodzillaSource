package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;

public final class BigIntUtils {
  public static boolean equals(BigInteger[] paramArrayOfBigInteger1, BigInteger[] paramArrayOfBigInteger2) {
    int i = 0;
    if (paramArrayOfBigInteger1.length != paramArrayOfBigInteger2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfBigInteger1.length; b++)
      i |= paramArrayOfBigInteger1[b].compareTo(paramArrayOfBigInteger2[b]); 
    return (i == 0);
  }
  
  public static void fill(BigInteger[] paramArrayOfBigInteger, BigInteger paramBigInteger) {
    for (int i = paramArrayOfBigInteger.length - 1; i >= 0; i--)
      paramArrayOfBigInteger[i] = paramBigInteger; 
  }
  
  public static BigInteger[] subArray(BigInteger[] paramArrayOfBigInteger, int paramInt1, int paramInt2) {
    BigInteger[] arrayOfBigInteger = new BigInteger[paramInt2 - paramInt1];
    System.arraycopy(paramArrayOfBigInteger, paramInt1, arrayOfBigInteger, 0, paramInt2 - paramInt1);
    return arrayOfBigInteger;
  }
  
  public static int[] toIntArray(BigInteger[] paramArrayOfBigInteger) {
    int[] arrayOfInt = new int[paramArrayOfBigInteger.length];
    for (byte b = 0; b < paramArrayOfBigInteger.length; b++)
      arrayOfInt[b] = paramArrayOfBigInteger[b].intValue(); 
    return arrayOfInt;
  }
  
  public static int[] toIntArrayModQ(int paramInt, BigInteger[] paramArrayOfBigInteger) {
    BigInteger bigInteger = BigInteger.valueOf(paramInt);
    int[] arrayOfInt = new int[paramArrayOfBigInteger.length];
    for (byte b = 0; b < paramArrayOfBigInteger.length; b++)
      arrayOfInt[b] = paramArrayOfBigInteger[b].mod(bigInteger).intValue(); 
    return arrayOfInt;
  }
  
  public static byte[] toMinimalByteArray(BigInteger paramBigInteger) {
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (arrayOfByte1.length == 1 || (paramBigInteger.bitLength() & 0x7) != 0)
      return arrayOfByte1; 
    byte[] arrayOfByte2 = new byte[paramBigInteger.bitLength() >> 3];
    System.arraycopy(arrayOfByte1, 1, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
}
