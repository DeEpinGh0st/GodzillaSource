package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat512;

public class SecP521R1Field {
  static final int[] P = new int[] { 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, 511 };
  
  private static final int P16 = 511;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3) + paramArrayOfint1[16] + paramArrayOfint2[16];
    if (i > 511 || (i == 511 && Nat.eq(16, paramArrayOfint3, P))) {
      i += Nat.inc(16, paramArrayOfint3);
      i &= 0x1FF;
    } 
    paramArrayOfint3[16] = i;
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(16, paramArrayOfint1, paramArrayOfint2) + paramArrayOfint1[16];
    if (i > 511 || (i == 511 && Nat.eq(16, paramArrayOfint2, P))) {
      i += Nat.inc(16, paramArrayOfint2);
      i &= 0x1FF;
    } 
    paramArrayOfint2[16] = i;
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat.fromBigInteger(521, paramBigInteger);
    if (Nat.eq(17, arrayOfInt, P))
      Nat.zero(17, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[16];
    int j = Nat.shiftDownBit(16, paramArrayOfint1, i, paramArrayOfint2);
    paramArrayOfint2[16] = i >>> 1 | j >>> 23;
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat.create(33);
    implMultiply(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat.isZero(17, paramArrayOfint1)) {
      Nat.zero(17, paramArrayOfint2);
    } else {
      Nat.sub(17, P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[32];
    int j = Nat.shiftDownBits(16, paramArrayOfint1, 16, 9, i, paramArrayOfint2, 0) >>> 23;
    j += i >>> 9;
    j += Nat.addTo(16, paramArrayOfint1, paramArrayOfint2);
    if (j > 511 || (j == 511 && Nat.eq(16, paramArrayOfint2, P))) {
      j += Nat.inc(16, paramArrayOfint2);
      j &= 0x1FF;
    } 
    paramArrayOfint2[16] = j;
  }
  
  public static void reduce23(int[] paramArrayOfint) {
    int i = paramArrayOfint[16];
    int j = Nat.addWordTo(16, i >>> 9, paramArrayOfint) + (i & 0x1FF);
    if (j > 511 || (j == 511 && Nat.eq(16, paramArrayOfint, P))) {
      j += Nat.inc(16, paramArrayOfint);
      j &= 0x1FF;
    } 
    paramArrayOfint[16] = j;
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat.create(33);
    implSquare(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat.create(33);
    implSquare(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      implSquare(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3) + paramArrayOfint1[16] - paramArrayOfint2[16];
    if (i < 0) {
      i += Nat.dec(16, paramArrayOfint3);
      i &= 0x1FF;
    } 
    paramArrayOfint3[16] = i;
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[16];
    int j = Nat.shiftUpBit(16, paramArrayOfint1, i << 23, paramArrayOfint2) | i << 1;
    paramArrayOfint2[16] = j & 0x1FF;
  }
  
  protected static void implMultiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat512.mul(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    int i = paramArrayOfint1[16];
    int j = paramArrayOfint2[16];
    paramArrayOfint3[32] = Nat.mul31BothAdd(16, i, paramArrayOfint2, j, paramArrayOfint1, paramArrayOfint3, 16) + i * j;
  }
  
  protected static void implSquare(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Nat512.square(paramArrayOfint1, paramArrayOfint2);
    int i = paramArrayOfint1[16];
    paramArrayOfint2[32] = Nat.mulWordAddTo(16, i << 1, paramArrayOfint1, 0, paramArrayOfint2, 16) + i * i;
  }
}
