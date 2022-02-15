package org.bouncycastle.pqc.crypto.mceliece;

import java.math.BigInteger;
import org.bouncycastle.pqc.math.linearalgebra.BigIntUtils;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.pqc.math.linearalgebra.IntegerFunctions;

final class Conversions {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  public static GF2Vector encode(int paramInt1, int paramInt2, byte[] paramArrayOfbyte) {
    if (paramInt1 < paramInt2)
      throw new IllegalArgumentException("n < t"); 
    BigInteger bigInteger1 = IntegerFunctions.binomial(paramInt1, paramInt2);
    BigInteger bigInteger2 = new BigInteger(1, paramArrayOfbyte);
    if (bigInteger2.compareTo(bigInteger1) >= 0)
      throw new IllegalArgumentException("Encoded number too large."); 
    GF2Vector gF2Vector = new GF2Vector(paramInt1);
    int i = paramInt1;
    int j = paramInt2;
    for (byte b = 0; b < paramInt1; b++) {
      bigInteger1 = bigInteger1.multiply(BigInteger.valueOf((i - j))).divide(BigInteger.valueOf(i));
      i--;
      if (bigInteger1.compareTo(bigInteger2) <= 0) {
        gF2Vector.setBit(b);
        bigInteger2 = bigInteger2.subtract(bigInteger1);
        if (i == --j) {
          bigInteger1 = ONE;
        } else {
          bigInteger1 = bigInteger1.multiply(BigInteger.valueOf((j + 1))).divide(BigInteger.valueOf((i - j)));
        } 
      } 
    } 
    return gF2Vector;
  }
  
  public static byte[] decode(int paramInt1, int paramInt2, GF2Vector paramGF2Vector) {
    if (paramGF2Vector.getLength() != paramInt1 || paramGF2Vector.getHammingWeight() != paramInt2)
      throw new IllegalArgumentException("vector has wrong length or hamming weight"); 
    int[] arrayOfInt = paramGF2Vector.getVecArray();
    BigInteger bigInteger1 = IntegerFunctions.binomial(paramInt1, paramInt2);
    BigInteger bigInteger2 = ZERO;
    int i = paramInt1;
    int j = paramInt2;
    for (byte b = 0; b < paramInt1; b++) {
      bigInteger1 = bigInteger1.multiply(BigInteger.valueOf((i - j))).divide(BigInteger.valueOf(i));
      i--;
      int k = b >> 5;
      int m = arrayOfInt[k] & 1 << (b & 0x1F);
      if (m != 0) {
        bigInteger2 = bigInteger2.add(bigInteger1);
        if (i == --j) {
          bigInteger1 = ONE;
        } else {
          bigInteger1 = bigInteger1.multiply(BigInteger.valueOf((j + 1))).divide(BigInteger.valueOf((i - j)));
        } 
      } 
    } 
    return BigIntUtils.toMinimalByteArray(bigInteger2);
  }
  
  public static byte[] signConversion(int paramInt1, int paramInt2, byte[] paramArrayOfbyte) {
    if (paramInt1 < paramInt2)
      throw new IllegalArgumentException("n < t"); 
    BigInteger bigInteger1 = IntegerFunctions.binomial(paramInt1, paramInt2);
    int i = bigInteger1.bitLength() - 1;
    int j = i >> 3;
    int k = i & 0x7;
    if (k == 0) {
      j--;
      k = 8;
    } 
    int m = paramInt1 >> 3;
    int n = paramInt1 & 0x7;
    if (n == 0) {
      m--;
      n = 8;
    } 
    byte[] arrayOfByte1 = new byte[m + 1];
    if (paramArrayOfbyte.length < arrayOfByte1.length) {
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, paramArrayOfbyte.length);
      for (int i3 = paramArrayOfbyte.length; i3 < arrayOfByte1.length; i3++)
        arrayOfByte1[i3] = 0; 
    } else {
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, m);
      int i3 = (1 << n) - 1;
      arrayOfByte1[m] = (byte)(i3 & paramArrayOfbyte[m]);
    } 
    BigInteger bigInteger2 = ZERO;
    int i1 = paramInt1;
    int i2 = paramInt2;
    for (byte b = 0; b < paramInt1; b++) {
      bigInteger1 = bigInteger1.multiply(new BigInteger(Integer.toString(i1 - i2))).divide(new BigInteger(Integer.toString(i1)));
      i1--;
      int i3 = b >>> 3;
      int i4 = b & 0x7;
      i4 = 1 << i4;
      byte b1 = (byte)(i4 & arrayOfByte1[i3]);
      if (b1 != 0) {
        bigInteger2 = bigInteger2.add(bigInteger1);
        if (i1 == --i2) {
          bigInteger1 = ONE;
        } else {
          bigInteger1 = bigInteger1.multiply(new BigInteger(Integer.toString(i2 + 1))).divide(new BigInteger(Integer.toString(i1 - i2)));
        } 
      } 
    } 
    byte[] arrayOfByte2 = new byte[j + 1];
    byte[] arrayOfByte3 = bigInteger2.toByteArray();
    if (arrayOfByte3.length < arrayOfByte2.length) {
      System.arraycopy(arrayOfByte3, 0, arrayOfByte2, 0, arrayOfByte3.length);
      for (int i3 = arrayOfByte3.length; i3 < arrayOfByte2.length; i3++)
        arrayOfByte2[i3] = 0; 
    } else {
      System.arraycopy(arrayOfByte3, 0, arrayOfByte2, 0, j);
      arrayOfByte2[j] = (byte)((1 << k) - 1 & arrayOfByte3[j]);
    } 
    return arrayOfByte2;
  }
}
