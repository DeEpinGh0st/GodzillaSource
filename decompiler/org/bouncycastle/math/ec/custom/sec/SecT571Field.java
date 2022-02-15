package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

public class SecT571Field {
  private static final long M59 = 576460752303423487L;
  
  private static final long RM = -1190112520884487202L;
  
  private static final long[] ROOT_Z = new long[] { 3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L };
  
  public static void add(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    for (byte b = 0; b < 9; b++)
      paramArrayOflong3[b] = paramArrayOflong1[b] ^ paramArrayOflong2[b]; 
  }
  
  private static void add(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, long[] paramArrayOflong3, int paramInt3) {
    for (byte b = 0; b < 9; b++)
      paramArrayOflong3[paramInt3 + b] = paramArrayOflong1[paramInt1 + b] ^ paramArrayOflong2[paramInt2 + b]; 
  }
  
  public static void addBothTo(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    for (byte b = 0; b < 9; b++)
      paramArrayOflong3[b] = paramArrayOflong3[b] ^ paramArrayOflong1[b] ^ paramArrayOflong2[b]; 
  }
  
  private static void addBothTo(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, long[] paramArrayOflong3, int paramInt3) {
    for (byte b = 0; b < 9; b++)
      paramArrayOflong3[paramInt3 + b] = paramArrayOflong3[paramInt3 + b] ^ paramArrayOflong1[paramInt1 + b] ^ paramArrayOflong2[paramInt2 + b]; 
  }
  
  public static void addExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    for (byte b = 0; b < 18; b++)
      paramArrayOflong3[b] = paramArrayOflong1[b] ^ paramArrayOflong2[b]; 
  }
  
  public static void addOne(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0] ^ 0x1L;
    for (byte b = 1; b < 9; b++)
      paramArrayOflong2[b] = paramArrayOflong1[b]; 
  }
  
  public static long[] fromBigInteger(BigInteger paramBigInteger) {
    long[] arrayOfLong = Nat576.fromBigInteger64(paramBigInteger);
    reduce5(arrayOfLong, 0);
    return arrayOfLong;
  }
  
  public static void invert(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    if (Nat576.isZero64(paramArrayOflong1))
      throw new IllegalStateException(); 
    long[] arrayOfLong1 = Nat576.create64();
    long[] arrayOfLong2 = Nat576.create64();
    long[] arrayOfLong3 = Nat576.create64();
    square(paramArrayOflong1, arrayOfLong3);
    square(arrayOfLong3, arrayOfLong1);
    square(arrayOfLong1, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 2, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong3, arrayOfLong1);
    squareN(arrayOfLong1, 5, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong2, 5, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 15, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong3);
    squareN(arrayOfLong3, 30, arrayOfLong1);
    squareN(arrayOfLong1, 30, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 60, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong2, 60, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong1, 180, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    squareN(arrayOfLong2, 180, arrayOfLong2);
    multiply(arrayOfLong1, arrayOfLong2, arrayOfLong1);
    multiply(arrayOfLong1, arrayOfLong3, paramArrayOflong2);
  }
  
  public static void multiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat576.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat576.createExt64();
    implMultiply(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    addExt(paramArrayOflong3, arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyPrecomp(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat576.createExt64();
    implMultiplyPrecomp(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong3);
  }
  
  public static void multiplyPrecompAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = Nat576.createExt64();
    implMultiplyPrecomp(paramArrayOflong1, paramArrayOflong2, arrayOfLong);
    addExt(paramArrayOflong3, arrayOfLong, paramArrayOflong3);
  }
  
  public static long[] precompMultiplicand(long[] paramArrayOflong) {
    char c = '';
    long[] arrayOfLong = new long[c << 1];
    System.arraycopy(paramArrayOflong, 0, arrayOfLong, 9, 9);
    byte b1 = 0;
    for (byte b2 = 7; b2 > 0; b2--) {
      b1 += true;
      Nat.shiftUpBit64(9, arrayOfLong, b1 >>> 1, 0L, arrayOfLong, b1);
      reduce5(arrayOfLong, b1);
      add(arrayOfLong, 9, arrayOfLong, b1, arrayOfLong, b1 + 9);
    } 
    Nat.shiftUpBits64(c, arrayOfLong, 0, 4, 0L, arrayOfLong, c);
    return arrayOfLong;
  }
  
  public static void reduce(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long l1 = paramArrayOflong1[9];
    long l2 = paramArrayOflong1[17];
    long l3 = l1;
    l1 = l3 ^ l2 >>> 59L ^ l2 >>> 57L ^ l2 >>> 54L ^ l2 >>> 49L;
    l3 = paramArrayOflong1[8] ^ l2 << 5L ^ l2 << 7L ^ l2 << 10L ^ l2 << 15L;
    for (byte b = 16; b >= 10; b--) {
      l2 = paramArrayOflong1[b];
      paramArrayOflong2[b - 8] = l3 ^ l2 >>> 59L ^ l2 >>> 57L ^ l2 >>> 54L ^ l2 >>> 49L;
      l3 = paramArrayOflong1[b - 9] ^ l2 << 5L ^ l2 << 7L ^ l2 << 10L ^ l2 << 15L;
    } 
    l2 = l1;
    paramArrayOflong2[1] = l3 ^ l2 >>> 59L ^ l2 >>> 57L ^ l2 >>> 54L ^ l2 >>> 49L;
    l3 = paramArrayOflong1[0] ^ l2 << 5L ^ l2 << 7L ^ l2 << 10L ^ l2 << 15L;
    long l4 = paramArrayOflong2[8];
    long l5 = l4 >>> 59L;
    paramArrayOflong2[0] = l3 ^ l5 ^ l5 << 2L ^ l5 << 5L ^ l5 << 10L;
    paramArrayOflong2[8] = l4 & 0x7FFFFFFFFFFFFFFL;
  }
  
  public static void reduce5(long[] paramArrayOflong, int paramInt) {
    long l1 = paramArrayOflong[paramInt + 8];
    long l2 = l1 >>> 59L;
    paramArrayOflong[paramInt] = paramArrayOflong[paramInt] ^ l2 ^ l2 << 2L ^ l2 << 5L ^ l2 << 10L;
    paramArrayOflong[paramInt + 8] = l1 & 0x7FFFFFFFFFFFFFFL;
  }
  
  public static void sqrt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong1 = Nat576.create64();
    long[] arrayOfLong2 = Nat576.create64();
    byte b1 = 0;
    for (byte b2 = 0; b2 < 4; b2++) {
      long l1 = Interleave.unshuffle(paramArrayOflong1[b1++]);
      long l2 = Interleave.unshuffle(paramArrayOflong1[b1++]);
      arrayOfLong1[b2] = l1 & 0xFFFFFFFFL | l2 << 32L;
      arrayOfLong2[b2] = l1 >>> 32L | l2 & 0xFFFFFFFF00000000L;
    } 
    long l = Interleave.unshuffle(paramArrayOflong1[b1]);
    arrayOfLong1[4] = l & 0xFFFFFFFFL;
    arrayOfLong2[4] = l >>> 32L;
    multiply(arrayOfLong2, ROOT_Z, paramArrayOflong2);
    add(paramArrayOflong2, arrayOfLong1, paramArrayOflong2);
  }
  
  public static void square(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat576.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareAddToExt(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat576.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    addExt(paramArrayOflong2, arrayOfLong, paramArrayOflong2);
  }
  
  public static void squareN(long[] paramArrayOflong1, int paramInt, long[] paramArrayOflong2) {
    long[] arrayOfLong = Nat576.createExt64();
    implSquare(paramArrayOflong1, arrayOfLong);
    reduce(arrayOfLong, paramArrayOflong2);
    while (--paramInt > 0) {
      implSquare(paramArrayOflong2, arrayOfLong);
      reduce(arrayOfLong, paramArrayOflong2);
    } 
  }
  
  public static int trace(long[] paramArrayOflong) {
    return (int)(paramArrayOflong[0] ^ paramArrayOflong[8] >>> 49L ^ paramArrayOflong[8] >>> 57L) & 0x1;
  }
  
  protected static void implMultiply(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    long[] arrayOfLong = precompMultiplicand(paramArrayOflong2);
    implMultiplyPrecomp(paramArrayOflong1, arrayOfLong, paramArrayOflong3);
  }
  
  protected static void implMultiplyPrecomp(long[] paramArrayOflong1, long[] paramArrayOflong2, long[] paramArrayOflong3) {
    byte b1 = 15;
    byte b2;
    for (b2 = 56; b2 >= 0; b2 -= 8) {
      for (byte b = 1; b < 9; b += 2) {
        int i = (int)(paramArrayOflong1[b] >>> b2);
        int j = i & b1;
        int k = i >>> 4 & b1;
        addBothTo(paramArrayOflong2, 9 * j, paramArrayOflong2, 9 * (k + 16), paramArrayOflong3, b - 1);
      } 
      Nat.shiftUpBits64(16, paramArrayOflong3, 0, 8, 0L);
    } 
    for (b2 = 56; b2 >= 0; b2 -= 8) {
      for (byte b = 0; b < 9; b += 2) {
        int i = (int)(paramArrayOflong1[b] >>> b2);
        int j = i & b1;
        int k = i >>> 4 & b1;
        addBothTo(paramArrayOflong2, 9 * j, paramArrayOflong2, 9 * (k + 16), paramArrayOflong3, b);
      } 
      if (b2 > 0)
        Nat.shiftUpBits64(18, paramArrayOflong3, 0, 8, 0L); 
    } 
  }
  
  protected static void implMulwAcc(long[] paramArrayOflong1, long paramLong, long[] paramArrayOflong2, int paramInt) {
    long[] arrayOfLong = new long[32];
    arrayOfLong[1] = paramLong;
    for (byte b1 = 2; b1 < 32; b1 += 2) {
      arrayOfLong[b1] = arrayOfLong[b1 >>> 1] << 1L;
      arrayOfLong[b1 + 1] = arrayOfLong[b1] ^ paramLong;
    } 
    long l = 0L;
    for (byte b2 = 0; b2 < 9; b2++) {
      long l1 = paramArrayOflong1[b2];
      int i = (int)l1;
      l ^= arrayOfLong[i & 0x1F];
      long l2 = 0L;
      byte b = 60;
      while (true) {
        i = (int)(l1 >>> b);
        long l3 = arrayOfLong[i & 0x1F];
        l ^= l3 << b;
        l2 ^= l3 >>> -b;
        b -= 5;
        if (b <= 0) {
          for (byte b3 = 0; b3 < 4; b3++) {
            l1 = (l1 & 0xEF7BDEF7BDEF7BDEL) >>> 1L;
            l2 ^= l1 & paramLong << b3 >> 63L;
          } 
          break;
        } 
      } 
      paramArrayOflong2[paramInt + b2] = paramArrayOflong2[paramInt + b2] ^ l;
      l = l2;
    } 
    paramArrayOflong2[paramInt + 9] = paramArrayOflong2[paramInt + 9] ^ l;
  }
  
  protected static void implSquare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    for (byte b = 0; b < 9; b++)
      Interleave.expand64To128(paramArrayOflong1[b], paramArrayOflong2, b << 1); 
  }
}
