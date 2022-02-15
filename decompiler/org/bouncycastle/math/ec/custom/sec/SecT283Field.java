package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat320;

public class SecT283Field {
  private static final long M27 = 134217727L;
  
  private static final long M57 = 144115188075855871L;
  
  private static final long[] ROOT_Z = new long[] { 878416384462358536L, 3513665537849438403L, -9076969306111048948L, 585610922974906400L, 34087042L };
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    paramArrayOflong3[0] = paramArrayOflong1[0] ^ paramArrayOflong2[0];
    paramArrayOflong3[1] = paramArrayOflong1[1] ^ paramArrayOflong2[1];
    paramArrayOflong3[2] = paramArrayOflong1[2] ^ paramArrayOflong2[2];
    paramArrayOflong3[3] = paramArrayOflong1[3] ^ paramArrayOflong2[3];
    paramArrayOflong3[4] = paramArrayOflong1[4] ^ paramArrayOflong2[4];
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
    paramArrayOflong3[8] = paramArrayOflong1[8] ^ paramArrayOflong2[8];
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    paramArrayOflong2[1] = paramArrayOflong1[1];
    paramArrayOflong2[2] = paramArrayOflong1[2];
    paramArrayOflong2[3] = paramArrayOflong1[3];
    paramArrayOflong2[4] = paramArrayOflong1[4];
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat320.fromBigInteger64(paramBigInteger);
    reduce37(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat320.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat320.create64();
    long[] arrayOfLong2 = Nat320.create64();
    square(paramArrayOflong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 2, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    squareN(arrayOfLong2, 4, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 8, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, arrayOfLong2);
    multiply(arrayOfLong2, paramArrayOflong1, arrayOfLong2);
    squareN(arrayOfLong2, 17, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    square(arrayOfLong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 35, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    squareN(arrayOfLong2, 70, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    square(arrayOfLong1, arrayOfLong1);
    multiply(arrayOfLong1, paramArrayOflong1, arrayOfLong1);
    squareN(arrayOfLong1, 141, arrayOfLong2);
    multiply(arrayOfLong2, arrayOfLong1, arrayOfLong2);
    square(arrayOfLong2, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat320.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat320.createExt64();
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
    long l9 = paramArrayOflong1[8];
    l4 ^= l9 << 37L ^ l9 << 42L ^ l9 << 44L ^ l9 << 49L;
    l5 ^= l9 >>> 27L ^ l9 >>> 22L ^ l9 >>> 20L ^ l9 >>> 15L;
    l3 ^= l8 << 37L ^ l8 << 42L ^ l8 << 44L ^ l8 << 49L;
    l4 ^= l8 >>> 27L ^ l8 >>> 22L ^ l8 >>> 20L ^ l8 >>> 15L;
    l2 ^= l7 << 37L ^ l7 << 42L ^ l7 << 44L ^ l7 << 49L;
    l3 ^= l7 >>> 27L ^ l7 >>> 22L ^ l7 >>> 20L ^ l7 >>> 15L;
    l1 ^= l6 << 37L ^ l6 << 42L ^ l6 << 44L ^ l6 << 49L;
    l2 ^= l6 >>> 27L ^ l6 >>> 22L ^ l6 >>> 20L ^ l6 >>> 15L;
    long l10 = l5 >>> 27L;
    paramArrayOflong2[0] = l1 ^ l10 ^ l10 << 5L ^ l10 << 7L ^ l10 << 12L;
    paramArrayOflong2[1] = l2;
    paramArrayOflong2[2] = l3;
    paramArrayOflong2[3] = l4;
    paramArrayOflong2[4] = l5 & 0x7FFFFFFL;
  }
  
  public static void reduce37(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 4];
    long l2 = l1 >>> 27L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2 ^ l2 << 5L ^ l2 << 7L ^ l2 << 12L;
    paramArrayOflong[paramInt + 4] = l1 & 0x7FFFFFFL;
  }
  
  public static void sqrt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat320.create64();
    long l1 = Interleave.unshuffle(paramArrayOflong1[0]);
    long l2 = Interleave.unshuffle(paramArrayOflong1[1]);
    long l3 = l1 & 0xFFFFFFFFL | l2 << 32L;
    arrayOfLong[0] = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    l1 = Interleave.unshuffle(paramArrayOflong1[2]);
    l2 = Interleave.unshuffle(paramArrayOflong1[3]);
    long l4 = l1 & 0xFFFFFFFFL | l2 << 32L;
    arrayOfLong[1] = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    l1 = Interleave.unshuffle(paramArrayOflong1[4]);
    long l5 = l1 & 0xFFFFFFFFL;
    arrayOfLong[2] = l1 >>> 32L;
    multiply(arrayOfLong, ROOT_Z, paramArrayOflong2);
    paramArrayOflong2[0] = paramArrayOflong2[0] ^ l3;
    paramArrayOflong2[1] = paramArrayOflong2[1] ^ l4;
    paramArrayOflong2[2] = paramArrayOflong2[2] ^ l5;
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(9);
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(9);
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat.create64(9);
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
    while (--paramInt > 0) {
      implSquare(paramArrayOflong2, arrayOfLong);
      reduce(arrayOfLong, paramArrayOflong2);
    } 
  }
  
  public static int trace(long[] paramArrayOflong) {
    return (int)(paramArrayOflong[0] ^ paramArrayOflong[4] >>> 15L) & 0x1;
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
    paramArrayOflong[0] = l1 ^ l2 << 57L;
    paramArrayOflong[1] = l2 >>> 7L ^ l3 << 50L;
    paramArrayOflong[2] = l3 >>> 14L ^ l4 << 43L;
    paramArrayOflong[3] = l4 >>> 21L ^ l5 << 36L;
    paramArrayOflong[4] = l5 >>> 28L ^ l6 << 29L;
    paramArrayOflong[5] = l6 >>> 35L ^ l7 << 22L;
    paramArrayOflong[6] = l7 >>> 42L ^ l8 << 15L;
    paramArrayOflong[7] = l8 >>> 49L ^ l9 << 8L;
    paramArrayOflong[8] = l9 >>> 56L ^ l10 << 1L;
    paramArrayOflong[9] = l10 >>> 63L;
  }
  
  protected static void implExpand(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[0];
    long l2 = paramArrayOflong1[1];
    long l3 = paramArrayOflong1[2];
    long l4 = paramArrayOflong1[3];
    long l5 = paramArrayOflong1[4];
    paramArrayOflong2[0] = l1 & 0x1FFFFFFFFFFFFFFL;
    paramArrayOflong2[1] = (l1 >>> 57L ^ l2 << 7L) & 0x1FFFFFFFFFFFFFFL;
    paramArrayOflong2[2] = (l2 >>> 50L ^ l3 << 14L) & 0x1FFFFFFFFFFFFFFL;
    paramArrayOflong2[3] = (l3 >>> 43L ^ l4 << 21L) & 0x1FFFFFFFFFFFFFFL;
    paramArrayOflong2[4] = l4 >>> 36L ^ l5 << 28L;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong1 = new long[5];
    long[] arrayOfLong2 = new long[5];
    implExpand(paramArrayOflong1, arrayOfLong1);
    implExpand(paramArrayOflong2, arrayOfLong2);
    long[] arrayOfLong3 = new long[26];
    implMulw(arrayOfLong1[0], arrayOfLong2[0], arrayOfLong3, 0);
    implMulw(arrayOfLong1[1], arrayOfLong2[1], arrayOfLong3, 2);
    implMulw(arrayOfLong1[2], arrayOfLong2[2], arrayOfLong3, 4);
    implMulw(arrayOfLong1[3], arrayOfLong2[3], arrayOfLong3, 6);
    implMulw(arrayOfLong1[4], arrayOfLong2[4], arrayOfLong3, 8);
    long l1 = arrayOfLong1[0] ^ arrayOfLong1[1];
    long l2 = arrayOfLong2[0] ^ arrayOfLong2[1];
    long l3 = arrayOfLong1[0] ^ arrayOfLong1[2];
    long l4 = arrayOfLong2[0] ^ arrayOfLong2[2];
    long l5 = arrayOfLong1[2] ^ arrayOfLong1[4];
    long l6 = arrayOfLong2[2] ^ arrayOfLong2[4];
    long l7 = arrayOfLong1[3] ^ arrayOfLong1[4];
    long l8 = arrayOfLong2[3] ^ arrayOfLong2[4];
    implMulw(l3 ^ arrayOfLong1[3], l4 ^ arrayOfLong2[3], arrayOfLong3, 18);
    implMulw(l5 ^ arrayOfLong1[1], l6 ^ arrayOfLong2[1], arrayOfLong3, 20);
    long l9 = l1 ^ l7;
    long l10 = l2 ^ l8;
    long l11 = l9 ^ arrayOfLong1[2];
    long l12 = l10 ^ arrayOfLong2[2];
    implMulw(l9, l10, arrayOfLong3, 22);
    implMulw(l11, l12, arrayOfLong3, 24);
    implMulw(l1, l2, arrayOfLong3, 10);
    implMulw(l3, l4, arrayOfLong3, 12);
    implMulw(l5, l6, arrayOfLong3, 14);
    implMulw(l7, l8, arrayOfLong3, 16);
    paramArrayOflong3[0] = arrayOfLong3[0];
    paramArrayOflong3[9] = arrayOfLong3[9];
    long l13 = arrayOfLong3[0] ^ arrayOfLong3[1];
    long l14 = l13 ^ arrayOfLong3[2];
    long l15 = l14 ^ arrayOfLong3[10];
    paramArrayOflong3[1] = l15;
    long l16 = arrayOfLong3[3] ^ arrayOfLong3[4];
    long l17 = arrayOfLong3[11] ^ arrayOfLong3[12];
    long l18 = l16 ^ l17;
    long l19 = l14 ^ l18;
    paramArrayOflong3[2] = l19;
    long l20 = l13 ^ l16;
    long l21 = arrayOfLong3[5] ^ arrayOfLong3[6];
    long l22 = l20 ^ l21;
    long l23 = l22 ^ arrayOfLong3[8];
    long l24 = arrayOfLong3[13] ^ arrayOfLong3[14];
    long l25 = l23 ^ l24;
    long l26 = arrayOfLong3[18] ^ arrayOfLong3[22];
    long l27 = l26 ^ arrayOfLong3[24];
    long l28 = l25 ^ l27;
    paramArrayOflong3[3] = l28;
    long l29 = arrayOfLong3[7] ^ arrayOfLong3[8];
    long l30 = l29 ^ arrayOfLong3[9];
    long l31 = l30 ^ arrayOfLong3[17];
    paramArrayOflong3[8] = l31;
    long l32 = l30 ^ l21;
    long l33 = arrayOfLong3[15] ^ arrayOfLong3[16];
    long l34 = l32 ^ l33;
    paramArrayOflong3[7] = l34;
    long l35 = l34 ^ l15;
    long l36 = arrayOfLong3[19] ^ arrayOfLong3[20];
    long l37 = arrayOfLong3[25] ^ arrayOfLong3[24];
    long l38 = arrayOfLong3[18] ^ arrayOfLong3[23];
    long l39 = l36 ^ l37;
    long l40 = l39 ^ l38;
    long l41 = l40 ^ l35;
    paramArrayOflong3[4] = l41;
    long l42 = l19 ^ l31;
    long l43 = l39 ^ l42;
    long l44 = arrayOfLong3[21] ^ arrayOfLong3[22];
    long l45 = l43 ^ l44;
    paramArrayOflong3[5] = l45;
    long l46 = l23 ^ arrayOfLong3[0];
    long l47 = l46 ^ arrayOfLong3[9];
    long l48 = l47 ^ l24;
    long l49 = l48 ^ arrayOfLong3[21];
    long l50 = l49 ^ arrayOfLong3[23];
    long l51 = l50 ^ arrayOfLong3[25];
    paramArrayOflong3[6] = l51;
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
    for (byte b = 0; b < 4; b++)
      Interleave.expand64To128(paramArrayOflong1[b], paramArrayOflong2, b << 1); 
    paramArrayOflong2[8] = Interleave.expand32to64((int)paramArrayOflong1[4]);
  }
}
