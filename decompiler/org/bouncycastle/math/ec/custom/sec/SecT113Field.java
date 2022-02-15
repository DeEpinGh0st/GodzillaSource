package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat128;

public class SecT113Field {
  private static final long M49 = 562949953421311L;
  
  private static final long M57 = 144115188075855871L;
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
  }
  
  public static void addExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    paramArrayOflong2[1] = paramArrayOflong1[1];
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat128.fromBigInteger64(paramBigInteger);
    reduce15(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat128.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat128.create64();
    long[] arrayOfLong2 = Nat128.create64();
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
    squareN(arrayOfLong2, 28, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 56, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat128.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat128.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    addExt(paramArrayOflong3, arrayOfLong, paramArrayOflong3);
  }
  
  public static void reduce(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    l2 ^= l4 << 15L ^ l4 << 24L;
    l3 ^= l4 >>> 49L ^ l4 >>> 40L;
    l1 ^= l3 << 15L ^ l3 << 24L;
    l2 ^= l3 >>> 49L ^ l3 >>> 40L;
    long l5 = l2 >>> 49L;
    paramArrayOflong2[0] = l1 ^ l5 ^ l5 << 9L;
    paramArrayOflong2[1] = l2 & 0x1FFFFFFFFFFFFL;
  }
  
  public static void reduce15(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 1];
    long l2 = l1 >>> 49L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2 ^ l2 << 9L;
    paramArrayOflong[paramInt + 1] = l1 & 0x1FFFFFFFFFFFFL;
  }
  
  public static void sqrt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = Interleave.unshuffle(paramArrayOflong1[0]);
    long l2 = Interleave.unshuffle(paramArrayOflong1[1]);
    long l3 = l1 & 0xFFFFFFFFL | l2 << 32L;
    long l4 = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    paramArrayOflong2[0] = l3 ^ l4 << 57L ^ l4 << 5L;
    paramArrayOflong2[1] = l4 >>> 7L ^ l4 >>> 59L;
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat128.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat128.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat128.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
    while (--paramInt > 0) {
      implSquare(paramArrayOflong2, arrayOfLong);
      reduce(arrayOfLong, paramArrayOflong2);
    } 
  }
  
  public static int trace(long[] paramArrayOflong) {
    return (int)paramArrayOflong[0] & 0x1;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    l2 = (l1 >>> 57L ^ l2 << 7L) & 0x1FFFFFFFFFFFFFFL;
    l1 &= 0x1FFFFFFFFFFFFFFL;
    long l3 = paramArrayOflong2[0];
    long l4 = paramArrayOflong2[1];
    l4 = (l3 >>> 57L ^ l4 << 7L) & 0x1FFFFFFFFFFFFFFL;
    l3 &= 0x1FFFFFFFFFFFFFFL;
    long[] arrayOfLong = new long[6];
    implMulw(l1, l3, arrayOfLong, 0);
    implMulw(l2, l4, arrayOfLong, 2);
    implMulw(l1 ^ l2, l3 ^ l4, arrayOfLong, 4);
    long l5 = arrayOfLong[1] ^ arrayOfLong[2];
    long l6 = arrayOfLong[0];
    long l7 = arrayOfLong[3];
    long l8 = arrayOfLong[4] ^ l6 ^ l5;
    long l9 = arrayOfLong[5] ^ l7 ^ l5;
    paramArrayOflong3[0] = l6 ^ l8 << 57L;
    paramArrayOflong3[1] = l8 >>> 7L ^ l9 << 50L;
    paramArrayOflong3[2] = l9 >>> 14L ^ l7 << 43L;
    paramArrayOflong3[3] = l7 >>> 21L;
  }
  
  protected static void implMulw(long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt) {
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
    long l2 = arrayOfLong[i & 0x7];
    byte b = 48;
    while (true) {
      i = (int)(paramLong1 >>> b);
      long l = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L ^ arrayOfLong[i >>> 6 & 0x7] << 6L;
      l2 ^= l << b;
      l1 ^= l >>> -b;
      b -= 9;
      if (b <= 0) {
        l1 ^= (paramLong1 & 0x100804020100800L & paramLong2 << 7L >> 63L) >>> 8L;
        paramArrayOflong[paramInt] = l2 & 0x1FFFFFFFFFFFFFFL;
        paramArrayOflong[paramInt + 1] = l2 >>> 57L ^ l1 << 7L;
        return;
      } 
    } 
  }
  
  protected static void implSquare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    Interleave.expand64To128(paramArrayOflong1[0], paramArrayOflong2, 0);
    Interleave.expand64To128(paramArrayOflong1[1], paramArrayOflong2, 2);
  }
}
