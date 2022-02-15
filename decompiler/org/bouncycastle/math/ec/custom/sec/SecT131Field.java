package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecT131Field {
  private static final long M03 = 7L;
  
  private static final long M44 = 17592186044415L;
  
  private static final long[] ROOT_Z = new long[] { 2791191049453778211L, 2791191049453778402L, 6L };
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
  }
  
  public static void addExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
    paramArrayOflong3[4] = paramArrayOflong1[4] ^ paramArrayOflong2[4];
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    paramArrayOflong2[1] = paramArrayOflong1[1];
    paramArrayOflong2[2] = paramArrayOflong1[2];
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat192.fromBigInteger64(paramBigInteger);
    reduce61(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat192.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat192.create64();
    long[] arrayOfLong2 = Nat192.create64();
    square(paramArrayOflong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 2, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    squareN(arrayOfLong2, 4, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 8, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    squareN(arrayOfLong2, 16, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 32, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, arrayOfLong2);
    multiply(arrayOfLong2, paramArrayOflong1, arrayOfLong2);
    squareN(arrayOfLong2, 65, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    square(arrayOfLong1, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat192.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat192.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    addExt(paramArrayOflong3, arrayOfLong, paramArrayOflong3);
  }
  
  public static void reduce(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    long l5 = paramArrayOflong1[4];
    l2 ^= l5 << 61L ^ l5 << 63L;
    l3 ^= l5 >>> 3L ^ l5 >>> 1L ^ l5 ^ l5 << 5L;
    l4 ^= l5 >>> 59L;
    l1 ^= l4 << 61L ^ l4 << 63L;
    l2 ^= l4 >>> 3L ^ l4 >>> 1L ^ l4 ^ l4 << 5L;
    l3 ^= l4 >>> 59L;
    long l6 = l3 >>> 3L;
    paramArrayOflong2[0] = l1 ^ l6 ^ l6 << 2L ^ l6 << 3L ^ l6 << 8L;
    paramArrayOflong2[1] = l2 ^ l6 >>> 56L;
    paramArrayOflong2[2] = l3 & 0x7L;
  }
  
  public static void reduce61(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 2];
    long l2 = l1 >>> 3L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2 ^ l2 << 2L ^ l2 << 3L ^ l2 << 8L;
    paramArrayOflong[paramInt + 1] = paramArrayOflong[paramInt + 1] ^ l2 >>> 56L;
    paramArrayOflong[paramInt + 2] = l1 & 0x7L;
  }
  
  public static void sqrt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat192.create64();
    long l1 = Interleave.unshuffle(paramArrayOflong1[0]);
    long l2 = Interleave.unshuffle(paramArrayOflong1[1]);
    long l3 = l1 & 0xFFFFFFFFL | l2 << 32L;
    arrayOfLong[0] = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    l1 = Interleave.unshuffle(paramArrayOflong1[2]);
    long l4 = l1 & 0xFFFFFFFFL;
    arrayOfLong[1] = l1 >>> 32L;
    multiply(arrayOfLong, ROOT_Z, paramArrayOflong2);
    paramArrayOflong2[0] = paramArrayOflong2[0] ^ l3;
    paramArrayOflong2[1] = paramArrayOflong2[1] ^ l4;
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(5);
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(5);
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(5);
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
    while (--paramInt > 0) {
      implSquare(paramArrayOflong2, arrayOfLong);
      reduce(arrayOfLong, paramArrayOflong2);
    } 
  }
  
  public static int trace(long[] paramArrayOflong) {
    return (int)(paramArrayOflong[0] ^ paramArrayOflong[1] >>> 59L ^ paramArrayOflong[2] >>> 1L) & 0x1;
  }
  
  protected static void implCompactExt(long[] paramArrayOflong) {
    long l1 = paramArrayOflong[0];
    long l2 = paramArrayOflong[1];
    long l3 = paramArrayOflong[2];
    long l4 = paramArrayOflong[3];
    long l5 = paramArrayOflong[4];
    long l6 = paramArrayOflong[5];
    paramArrayOflong[0] = l1 ^ l2 << 44L;
    paramArrayOflong[1] = l2 >>> 20L ^ l3 << 24L;
    paramArrayOflong[2] = l3 >>> 40L ^ l4 << 4L ^ l5 << 48L;
    paramArrayOflong[3] = l4 >>> 60L ^ l6 << 28L ^ l5 >>> 16L;
    paramArrayOflong[4] = l6 >>> 36L;
    paramArrayOflong[5] = 0L;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    l3 = (l2 >>> 24L ^ l3 << 40L) & 0xFFFFFFFFFFFL;
    l2 = (l1 >>> 44L ^ l2 << 20L) & 0xFFFFFFFFFFFL;
    l1 &= 0xFFFFFFFFFFFL;
    long l4 = paramArrayOflong2[0];
    long l5 = paramArrayOflong2[1];
    long l6 = paramArrayOflong2[2];
    l6 = (l5 >>> 24L ^ l6 << 40L) & 0xFFFFFFFFFFFL;
    l5 = (l4 >>> 44L ^ l5 << 20L) & 0xFFFFFFFFFFFL;
    l4 &= 0xFFFFFFFFFFFL;
    long[] arrayOfLong = new long[10];
    implMulw(l1, l4, arrayOfLong, 0);
    implMulw(l3, l6, arrayOfLong, 2);
    long l7 = l1 ^ l2 ^ l3;
    long l8 = l4 ^ l5 ^ l6;
    implMulw(l7, l8, arrayOfLong, 4);
    long l9 = l2 << 1L ^ l3 << 2L;
    long l10 = l5 << 1L ^ l6 << 2L;
    implMulw(l1 ^ l9, l4 ^ l10, arrayOfLong, 6);
    implMulw(l7 ^ l9, l8 ^ l10, arrayOfLong, 8);
    long l11 = arrayOfLong[6] ^ arrayOfLong[8];
    long l12 = arrayOfLong[7] ^ arrayOfLong[9];
    long l13 = l11 << 1L ^ arrayOfLong[6];
    long l14 = l11 ^ l12 << 1L ^ arrayOfLong[7];
    long l15 = l12;
    long l16 = arrayOfLong[0];
    long l17 = arrayOfLong[1] ^ arrayOfLong[0] ^ arrayOfLong[4];
    long l18 = arrayOfLong[1] ^ arrayOfLong[5];
    long l19 = l16 ^ l13 ^ arrayOfLong[2] << 4L ^ arrayOfLong[2] << 1L;
    long l20 = l17 ^ l14 ^ arrayOfLong[3] << 4L ^ arrayOfLong[3] << 1L;
    long l21 = l18 ^ l15;
    l20 ^= l19 >>> 44L;
    l19 &= 0xFFFFFFFFFFFL;
    l21 ^= l20 >>> 44L;
    l20 &= 0xFFFFFFFFFFFL;
    l19 = l19 >>> 1L ^ (l20 & 0x1L) << 43L;
    l20 = l20 >>> 1L ^ (l21 & 0x1L) << 43L;
    l21 >>>= 1L;
    l19 ^= l19 << 1L;
    l19 ^= l19 << 2L;
    l19 ^= l19 << 4L;
    l19 ^= l19 << 8L;
    l19 ^= l19 << 16L;
    l19 ^= l19 << 32L;
    l19 &= 0xFFFFFFFFFFFL;
    l20 ^= l19 >>> 43L;
    l20 ^= l20 << 1L;
    l20 ^= l20 << 2L;
    l20 ^= l20 << 4L;
    l20 ^= l20 << 8L;
    l20 ^= l20 << 16L;
    l20 ^= l20 << 32L;
    l20 &= 0xFFFFFFFFFFFL;
    l21 ^= l20 >>> 43L;
    l21 ^= l21 << 1L;
    l21 ^= l21 << 2L;
    l21 ^= l21 << 4L;
    l21 ^= l21 << 8L;
    l21 ^= l21 << 16L;
    l21 ^= l21 << 32L;
    paramArrayOflong3[0] = l16;
    paramArrayOflong3[1] = l17 ^ l19 ^ arrayOfLong[2];
    paramArrayOflong3[2] = l18 ^ l20 ^ l19 ^ arrayOfLong[3];
    paramArrayOflong3[3] = l21 ^ l20;
    paramArrayOflong3[4] = l21 ^ arrayOfLong[2];
    paramArrayOflong3[5] = arrayOfLong[3];
    implCompactExt(paramArrayOflong3);
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
    long l2 = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L ^ arrayOfLong[i >>> 6 & 0x7] << 6L;
    byte b = 33;
    while (true) {
      i = (int)(paramLong1 >>> b);
      long l = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L ^ arrayOfLong[i >>> 6 & 0x7] << 6L ^ arrayOfLong[i >>> 9 & 0x7] << 9L;
      l2 ^= l << b;
      l1 ^= l >>> -b;
      b -= 12;
      if (b <= 0) {
        paramArrayOflong[paramInt] = l2 & 0xFFFFFFFFFFFL;
        paramArrayOflong[paramInt + 1] = l2 >>> 44L ^ l1 << 20L;
        return;
      } 
    } 
  }
  
  protected static void implSquare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    Interleave.expand64To128(paramArrayOflong1[0], paramArrayOflong2, 0);
    Interleave.expand64To128(paramArrayOflong1[1], paramArrayOflong2, 2);
    paramArrayOflong2[4] = Interleave.expand8to16((int)paramArrayOflong1[2]) & 0xFFFFFFFFL;
  }
}
