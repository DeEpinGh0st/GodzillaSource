package org.bouncycastle.math.ec.custom.djb;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class Curve25519Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { -19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE };
  
  private static final int P7 = 2147483647;
  
  private static final int[] PExt = new int[] { 
      361, 0, 0, 0, 0, 0, 0, 0, -19, -1, 
      -1, -1, -1, -1, -1, 1073741823 };
  
  private static final int PInv = 19;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat256.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (Nat256.gte(paramArrayOfint3, P))
      subPFrom(paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat.add(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (Nat.gte(16, paramArrayOfint3, PExt))
      subPExtFrom(paramArrayOfint3); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Nat.inc(8, paramArrayOfint1, paramArrayOfint2);
    if (Nat256.gte(paramArrayOfint2, P))
      subPFrom(paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat256.fromBigInteger(paramBigInteger);
    while (Nat256.gte(arrayOfInt, P))
      Nat256.subFrom(P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(8, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      Nat256.add(paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(8, paramArrayOfint2, 0);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat256.createExt();
    Nat256.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void multiplyAddToExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat256.mulAddTo(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (Nat.gte(16, paramArrayOfint3, PExt))
      subPExtFrom(paramArrayOfint3); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat256.isZero(paramArrayOfint1)) {
      Nat256.zero(paramArrayOfint2);
    } else {
      Nat256.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[7];
    Nat.shiftUpBit(8, paramArrayOfint1, 8, i, paramArrayOfint2, 0);
    int j = Nat256.mulByWordAddTo(19, paramArrayOfint1, paramArrayOfint2) << 1;
    int k = paramArrayOfint2[7];
    j += (k >>> 31) - (i >>> 31);
    k &= Integer.MAX_VALUE;
    k += Nat.addWordTo(7, j * 19, paramArrayOfint2);
    paramArrayOfint2[7] = k;
    if (Nat256.gte(paramArrayOfint2, P))
      subPFrom(paramArrayOfint2); 
  }
  
  public static void reduce27(int paramInt, int[] paramArrayOfint) {
    int i = paramArrayOfint[7];
    int j = paramInt << 1 | i >>> 31;
    i &= Integer.MAX_VALUE;
    i += Nat.addWordTo(7, j * 19, paramArrayOfint);
    paramArrayOfint[7] = i;
    if (Nat256.gte(paramArrayOfint, P))
      subPFrom(paramArrayOfint); 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat256.createExt();
    Nat256.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat256.createExt();
    Nat256.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      Nat256.square(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat256.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      addPTo(paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      addPExtTo(paramArrayOfint3); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Nat.shiftUpBit(8, paramArrayOfint1, 0, paramArrayOfint2);
    if (Nat256.gte(paramArrayOfint2, P))
      subPFrom(paramArrayOfint2); 
  }
  
  private static int addPTo(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - 19L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.decAt(7, paramArrayOfint, 1); 
    l += (paramArrayOfint[7] & 0xFFFFFFFFL) + 2147483648L;
    paramArrayOfint[7] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  private static int addPExtTo(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + (PExt[0] & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.incAt(8, paramArrayOfint, 1); 
    l += (paramArrayOfint[8] & 0xFFFFFFFFL) - 19L;
    paramArrayOfint[8] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.decAt(15, paramArrayOfint, 9); 
    l += (paramArrayOfint[15] & 0xFFFFFFFFL) + ((PExt[15] + 1) & 0xFFFFFFFFL);
    paramArrayOfint[15] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  private static int subPFrom(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + 19L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.incAt(7, paramArrayOfint, 1); 
    l += (paramArrayOfint[7] & 0xFFFFFFFFL) - 2147483648L;
    paramArrayOfint[7] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  private static int subPExtFrom(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - (PExt[0] & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.decAt(8, paramArrayOfint, 1); 
    l += (paramArrayOfint[8] & 0xFFFFFFFFL) + 19L;
    paramArrayOfint[8] = (int)l;
    l >>= 32L;
    if (l != 0L)
      l = Nat.incAt(15, paramArrayOfint, 9); 
    l += (paramArrayOfint[15] & 0xFFFFFFFFL) - ((PExt[15] + 1) & 0xFFFFFFFFL);
    paramArrayOfint[15] = (int)l;
    l >>= 32L;
    return (int)l;
  }
}
