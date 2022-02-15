package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;

public class SecT233Field {
  private static final long M41 = 2199023255551L;
  
  private static final long M59 = 576460752303423487L;
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
  }
  
  public static void addExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
    paramArrayOflong3[4] = paramArrayOflong1[4] ^ paramArrayOflong2[4];
    paramArrayOflong3[5] = paramArrayOflong1[5] ^ paramArrayOflong2[5];
    paramArrayOflong3[6] = paramArrayOflong1[6] ^ paramArrayOflong2[6];
    paramArrayOflong3[7] = paramArrayOflong1[7] ^ paramArrayOflong2[7];
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    paramArrayOflong2[1] = paramArrayOflong1[1];
    paramArrayOflong2[2] = paramArrayOflong1[2];
    paramArrayOflong2[3] = paramArrayOflong1[3];
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat256.fromBigInteger64(paramBigInteger);
    reduce23(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat256.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat256.create64();
    long[] arrayOfLong2 = Nat256.create64();
    square(paramArrayOflong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    square(arrayOfLong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 3, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, arrayOfLong2);
    multiply(arrayOfLong2, paramArrayOflong1, arrayOfLong2);
    squareN(arrayOfLong2, 7, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 14, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, arrayOfLong2);
    multiply(arrayOfLong2, paramArrayOflong1, arrayOfLong2);
    squareN(arrayOfLong2, 29, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 58, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    squareN(arrayOfLong2, 116, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    square(arrayOfLong1, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat256.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat256.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    addExt(paramArrayOflong3, arrayOfLong, paramArrayOflong3);
  }
  
  public static void reduce(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    long l5 = paramArrayOflong1[4];
    long l6 = paramArrayOflong1[5];
    long l7 = paramArrayOflong1[6];
    long l8 = paramArrayOflong1[7];
    l4 ^= l8 << 23L;
    l5 ^= l8 >>> 41L ^ l8 << 33L;
    l6 ^= l8 >>> 31L;
    l3 ^= l7 << 23L;
    l4 ^= l7 >>> 41L ^ l7 << 33L;
    l5 ^= l7 >>> 31L;
    l2 ^= l6 << 23L;
    l3 ^= l6 >>> 41L ^ l6 << 33L;
    l4 ^= l6 >>> 31L;
    l1 ^= l5 << 23L;
    l2 ^= l5 >>> 41L ^ l5 << 33L;
    l3 ^= l5 >>> 31L;
    long l9 = l4 >>> 41L;
    paramArrayOflong2[0] = l1 ^ l9;
    paramArrayOflong2[1] = l2 ^ l9 << 10L;
    paramArrayOflong2[2] = l3;
    paramArrayOflong2[3] = l4 & 0x1FFFFFFFFFFL;
  }
  
  public static void reduce23(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 3];
    long l2 = l1 >>> 41L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2;
    paramArrayOflong[paramInt + 1] = paramArrayOflong[paramInt + 1] ^ l2 << 10L;
    paramArrayOflong[paramInt + 3] = l1 & 0x1FFFFFFFFFFL;
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat256.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat256.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat256.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
    while (--paramInt > 0) {
      implSquare(paramArrayOflong2, arrayOfLong);
      reduce(arrayOfLong, paramArrayOflong2);
    } 
  }
  
  public static void sqrt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = Interleave.unshuffle(paramArrayOflong1[0]);
    long l2 = Interleave.unshuffle(paramArrayOflong1[1]);
    long l3 = l1 & 0xFFFFFFFFL | l2 << 32L;
    long l4 = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    l1 = Interleave.unshuffle(paramArrayOflong1[2]);
    l2 = Interleave.unshuffle(paramArrayOflong1[3]);
    long l5 = l1 & 0xFFFFFFFFL | l2 << 32L;
    long l6 = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    long l7 = l6 >>> 27L;
    l6 ^= l4 >>> 27L | l6 << 37L;
    l4 ^= l4 << 37L;
    long[] arrayOfLong = Nat256.createExt64();
    int[] arrayOfInt = { 32, 117, 191 };
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int i = arrayOfInt[b] >>> 6;
      int j = arrayOfInt[b] & 0x3F;
      arrayOfLong[i] = arrayOfLong[i] ^ l4 << j;
      arrayOfLong[i + 1] = arrayOfLong[i + 1] ^ (l6 << j | l4 >>> -j);
      arrayOfLong[i + 2] = arrayOfLong[i + 2] ^ (l7 << j | l6 >>> -j);
      arrayOfLong[i + 3] = arrayOfLong[i + 3] ^ l7 >>> -j;
    } 
    reduce(arrayOfLong, paramArrayOflong2);
    paramArrayOflong2[0] = paramArrayOflong2[0] ^ l3;
    paramArrayOflong2[1] = paramArrayOflong2[1] ^ l5;
  }
  
  public static int trace(long[] paramArrayOflong) {
    return (int)(paramArrayOflong[0] ^ paramArrayOflong[2] >>> 31L) & 0x1;
  }
  
  protected static void implCompactExt(long[] paramArrayOflong) {
    long l1 = paramArrayOflong[0];
    long l2 = paramArrayOflong[1];
    long l3 = paramArrayOflong[2];
    long l4 = paramArrayOflong[3];
    long l5 = paramArrayOflong[4];
    long l6 = paramArrayOflong[5];
    long l7 = paramArrayOflong[6];
    long l8 = paramArrayOflong[7];
    paramArrayOflong[0] = l1 ^ l2 << 59L;
    paramArrayOflong[1] = l2 >>> 5L ^ l3 << 54L;
    paramArrayOflong[2] = l3 >>> 10L ^ l4 << 49L;
    paramArrayOflong[3] = l4 >>> 15L ^ l5 << 44L;
    paramArrayOflong[4] = l5 >>> 20L ^ l6 << 39L;
    paramArrayOflong[5] = l6 >>> 25L ^ l7 << 34L;
    paramArrayOflong[6] = l7 >>> 30L ^ l8 << 29L;
    paramArrayOflong[7] = l8 >>> 35L;
  }
  
  protected static void implExpand(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    paramArrayOflong2[0] = l1 & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[1] = (l1 >>> 59L ^ l2 << 5L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[2] = (l2 >>> 54L ^ l3 << 10L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[3] = l3 >>> 49L ^ l4 << 15L;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong1 = new long[4];
    long[] arrayOfLong2 = new long[4];
    implExpand(paramArrayOflong1, arrayOfLong1);
    implExpand(paramArrayOflong2, arrayOfLong2);
    implMulwAcc(arrayOfLong1[0], arrayOfLong2[0], paramArrayOflong3, 0);
    implMulwAcc(arrayOfLong1[1], arrayOfLong2[1], paramArrayOflong3, 1);
    implMulwAcc(arrayOfLong1[2], arrayOfLong2[2], paramArrayOflong3, 2);
    implMulwAcc(arrayOfLong1[3], arrayOfLong2[3], paramArrayOflong3, 3);
    byte b;
    for (b = 5; b > 0; b--)
      paramArrayOflong3[b] = paramArrayOflong3[b] ^ paramArrayOflong3[b - 1]; 
    implMulwAcc(arrayOfLong1[0] ^ arrayOfLong1[1], arrayOfLong2[0] ^ arrayOfLong2[1], paramArrayOflong3, 1);
    implMulwAcc(arrayOfLong1[2] ^ arrayOfLong1[3], arrayOfLong2[2] ^ arrayOfLong2[3], paramArrayOflong3, 3);
    for (b = 7; b > 1; b--)
      paramArrayOflong3[b] = paramArrayOflong3[b] ^ paramArrayOflong3[b - 2]; 
    long l1 = arrayOfLong1[0] ^ arrayOfLong1[2];
    long l2 = arrayOfLong1[1] ^ arrayOfLong1[3];
    long l3 = arrayOfLong2[0] ^ arrayOfLong2[2];
    long l4 = arrayOfLong2[1] ^ arrayOfLong2[3];
    implMulwAcc(l1 ^ l2, l3 ^ l4, paramArrayOflong3, 3);
    long[] arrayOfLong3 = new long[3];
    implMulwAcc(l1, l3, arrayOfLong3, 0);
    implMulwAcc(l2, l4, arrayOfLong3, 1);
    long l5 = arrayOfLong3[0];
    long l6 = arrayOfLong3[1];
    long l7 = arrayOfLong3[2];
    paramArrayOflong3[2] = paramArrayOflong3[2] ^ l5;
    paramArrayOflong3[3] = paramArrayOflong3[3] ^ l5 ^ l6;
    paramArrayOflong3[4] = paramArrayOflong3[4] ^ l7 ^ l6;
    paramArrayOflong3[5] = paramArrayOflong3[5] ^ l7;
    implCompactExt(paramArrayOflong3);
  }
  
  protected static void implMulwAcc(long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt) {
    long[] arrayOfLong = new long[8];
    arrayOfLong[1] = paramLong2;
    arrayOfLong[2] = arrayOfLong[1] << 1L;
    arrayOfLong[3] = arrayOfLong[2] ^ paramLong2;
    arrayOfLong[4] = arrayOfLong[2] << 1L;
    arrayOfLong[5] = arrayOfLong[4] ^ paramLong2;
    arrayOfLong[6] = arrayOfLong[3] << 1L;
    arrayOfLong[7] = arrayOfLong[6] ^ paramLong2;
    int i = (int)paramLong1;
    long l1 = 0L;
    long l2 = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L;
    byte b = 54;
    while (true) {
      i = (int)(paramLong1 >>> b);
      long l = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L;
      l2 ^= l << b;
      l1 ^= l >>> -b;
      b -= 6;
      if (b <= 0) {
        paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2 & 0x7FFFFFFFFFFFFFFL;
        paramArrayOflong[paramInt + 1] = paramArrayOflong[paramInt + 1] ^ l2 >>> 59L ^ l1 << 5L;
        return;
      } 
    } 
  }
  
  protected static void implSquare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    Interleave.expand64To128(paramArrayOflong1[0], paramArrayOflong2, 0);
    Interleave.expand64To128(paramArrayOflong1[1], paramArrayOflong2, 2);
    Interleave.expand64To128(paramArrayOflong1[2], paramArrayOflong2, 4);
    long l = paramArrayOflong1[3];
    paramArrayOflong2[6] = Interleave.expand32to64((int)l);
    paramArrayOflong2[7] = Interleave.expand16to32((int)(l >>> 32L)) & 0xFFFFFFFFL;
  }
}
