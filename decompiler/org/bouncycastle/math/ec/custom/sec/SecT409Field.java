package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

public class SecT409Field {
  private static final long M25 = 33554431L;
  
  private static final long M59 = 576460752303423487L;
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
    paramArrayOflong3[4] = paramArrayOflong1[4] ^ paramArrayOflong2[4];
    paramArrayOflong3[5] = paramArrayOflong1[5] ^ paramArrayOflong2[5];
    paramArrayOflong3[6] = paramArrayOflong1[6] ^ paramArrayOflong2[6];
  }
  
  public static void addExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    for (byte b = 0; b < 13; b++)
      paramArrayOflong3[b] = paramArrayOflong1[b] ^ paramArrayOflong2[b]; 
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    paramArrayOflong2[1] = paramArrayOflong1[1];
    paramArrayOflong2[2] = paramArrayOflong1[2];
    paramArrayOflong2[3] = paramArrayOflong1[3];
    paramArrayOflong2[4] = paramArrayOflong1[4];
    paramArrayOflong2[5] = paramArrayOflong1[5];
    paramArrayOflong2[6] = paramArrayOflong1[6];
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat448.fromBigInteger64(paramBigInteger);
    reduce39(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat448.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat448.create64();
    long[] arrayOfLong2 = Nat448.create64();
    long[] arrayOfLong3 = Nat448.create64();
    square(paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 1, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong2, 1, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 3, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 6, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 12, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong3);
    squareN(arrayOfLong3, 24, arrayOfLong1);
    squareN(arrayOfLong1, 24, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 48, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 96, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 192, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong3, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat448.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat448.createExt64();
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
    long l9 = paramArrayOflong1[12];
    l6 ^= l9 << 39L;
    l7 ^= l9 >>> 25L ^ l9 << 62L;
    l8 ^= l9 >>> 2L;
    l9 = paramArrayOflong1[11];
    l5 ^= l9 << 39L;
    l6 ^= l9 >>> 25L ^ l9 << 62L;
    l7 ^= l9 >>> 2L;
    l9 = paramArrayOflong1[10];
    l4 ^= l9 << 39L;
    l5 ^= l9 >>> 25L ^ l9 << 62L;
    l6 ^= l9 >>> 2L;
    l9 = paramArrayOflong1[9];
    l3 ^= l9 << 39L;
    l4 ^= l9 >>> 25L ^ l9 << 62L;
    l5 ^= l9 >>> 2L;
    l9 = paramArrayOflong1[8];
    l2 ^= l9 << 39L;
    l3 ^= l9 >>> 25L ^ l9 << 62L;
    l4 ^= l9 >>> 2L;
    l9 = l8;
    l1 ^= l9 << 39L;
    l2 ^= l9 >>> 25L ^ l9 << 62L;
    l3 ^= l9 >>> 2L;
    long l10 = l7 >>> 25L;
    paramArrayOflong2[0] = l1 ^ l10;
    paramArrayOflong2[1] = l2 ^ l10 << 23L;
    paramArrayOflong2[2] = l3;
    paramArrayOflong2[3] = l4;
    paramArrayOflong2[4] = l5;
    paramArrayOflong2[5] = l6;
    paramArrayOflong2[6] = l7 & 0x1FFFFFFL;
  }
  
  public static void reduce39(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 6];
    long l2 = l1 >>> 25L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2;
    paramArrayOflong[paramInt + 1] = paramArrayOflong[paramInt + 1] ^ l2 << 23L;
    paramArrayOflong[paramInt + 6] = l1 & 0x1FFFFFFL;
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
    l1 = Interleave.unshuffle(paramArrayOflong1[4]);
    l2 = Interleave.unshuffle(paramArrayOflong1[5]);
    long l7 = l1 & 0xFFFFFFFFL | l2 << 32L;
    long l8 = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    l1 = Interleave.unshuffle(paramArrayOflong1[6]);
    long l9 = l1 & 0xFFFFFFFFL;
    long l10 = l1 >>> 32L;
    paramArrayOflong2[0] = l3 ^ l4 << 44L;
    paramArrayOflong2[1] = l5 ^ l6 << 44L ^ l4 >>> 20L;
    paramArrayOflong2[2] = l7 ^ l8 << 44L ^ l6 >>> 20L;
    paramArrayOflong2[3] = l9 ^ l10 << 44L ^ l8 >>> 20L ^ l4 << 13L;
    paramArrayOflong2[4] = l10 >>> 20L ^ l6 << 13L ^ l4 >>> 51L;
    paramArrayOflong2[5] = l8 << 13L ^ l6 >>> 51L;
    paramArrayOflong2[6] = l10 << 13L ^ l8 >>> 51L;
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(13);
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(13);
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(13);
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
  
  protected static void implCompactExt(long[] paramArrayOflong) {
    long l1 = paramArrayOflong[0];
    long l2 = paramArrayOflong[1];
    long l3 = paramArrayOflong[2];
    long l4 = paramArrayOflong[3];
    long l5 = paramArrayOflong[4];
    long l6 = paramArrayOflong[5];
    long l7 = paramArrayOflong[6];
    long l8 = paramArrayOflong[7];
    long l9 = paramArrayOflong[8];
    long l10 = paramArrayOflong[9];
    long l11 = paramArrayOflong[10];
    long l12 = paramArrayOflong[11];
    long l13 = paramArrayOflong[12];
    long l14 = paramArrayOflong[13];
    paramArrayOflong[0] = l1 ^ l2 << 59L;
    paramArrayOflong[1] = l2 >>> 5L ^ l3 << 54L;
    paramArrayOflong[2] = l3 >>> 10L ^ l4 << 49L;
    paramArrayOflong[3] = l4 >>> 15L ^ l5 << 44L;
    paramArrayOflong[4] = l5 >>> 20L ^ l6 << 39L;
    paramArrayOflong[5] = l6 >>> 25L ^ l7 << 34L;
    paramArrayOflong[6] = l7 >>> 30L ^ l8 << 29L;
    paramArrayOflong[7] = l8 >>> 35L ^ l9 << 24L;
    paramArrayOflong[8] = l9 >>> 40L ^ l10 << 19L;
    paramArrayOflong[9] = l10 >>> 45L ^ l11 << 14L;
    paramArrayOflong[10] = l11 >>> 50L ^ l12 << 9L;
    paramArrayOflong[11] = l12 >>> 55L ^ l13 << 4L ^ l14 << 63L;
    paramArrayOflong[12] = l13 >>> 60L ^ l14 >>> 1L;
    paramArrayOflong[13] = 0L;
  }
  
  protected static void implExpand(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    long l5 = paramArrayOflong1[4];
    long l6 = paramArrayOflong1[5];
    long l7 = paramArrayOflong1[6];
    paramArrayOflong2[0] = l1 & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[1] = (l1 >>> 59L ^ l2 << 5L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[2] = (l2 >>> 54L ^ l3 << 10L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[3] = (l3 >>> 49L ^ l4 << 15L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[4] = (l4 >>> 44L ^ l5 << 20L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[5] = (l5 >>> 39L ^ l6 << 25L) & 0x7FFFFFFFFFFFFFFL;
    paramArrayOflong2[6] = l6 >>> 34L ^ l7 << 30L;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong1 = new long[7];
    long[] arrayOfLong2 = new long[7];
    implExpand(paramArrayOflong1, arrayOfLong1);
    implExpand(paramArrayOflong2, arrayOfLong2);
    for (byte b = 0; b < 7; b++)
      implMulwAcc(arrayOfLong1, arrayOfLong2[b], paramArrayOflong3, b); 
    implCompactExt(paramArrayOflong3);
  }
  
  protected static void implMulwAcc(long[] paramArrayOflong1, long paramLong, long[] paramArrayOflong2, int paramInt) {
    long[] arrayOfLong = new long[8];
    arrayOfLong[1] = paramLong;
    arrayOfLong[2] = arrayOfLong[1] << 1L;
    arrayOfLong[3] = arrayOfLong[2] ^ paramLong;
    arrayOfLong[4] = arrayOfLong[2] << 1L;
    arrayOfLong[5] = arrayOfLong[4] ^ paramLong;
    arrayOfLong[6] = arrayOfLong[3] << 1L;
    arrayOfLong[7] = arrayOfLong[6] ^ paramLong;
    byte b = 0;
    while (b < 7) {
      long l1 = paramArrayOflong1[b];
      int i = (int)l1;
      long l2 = 0L;
      long l3 = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L;
      byte b1 = 54;
      while (true) {
        i = (int)(l1 >>> b1);
        long l = arrayOfLong[i & 0x7] ^ arrayOfLong[i >>> 3 & 0x7] << 3L;
        l3 ^= l << b1;
        l2 ^= l >>> -b1;
        b1 -= 6;
        if (b1 <= 0) {
          paramArrayOflong2[paramInt + b] = paramArrayOflong2[paramInt + b] ^ l3 & 0x7FFFFFFFFFFFFFFL;
          paramArrayOflong2[paramInt + b + 1] = paramArrayOflong2[paramInt + b + 1] ^ l3 >>> 59L ^ l2 << 5L;
          b++;
        } 
      } 
    } 
  }
  
  protected static void implSquare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    for (byte b = 0; b < 6; b++)
      Interleave.expand64To128(paramArrayOflong1[b], paramArrayOflong2, b << 1); 
    paramArrayOflong2[12] = Interleave.expand32to64((int)paramArrayOflong1[6]);
  }
}
