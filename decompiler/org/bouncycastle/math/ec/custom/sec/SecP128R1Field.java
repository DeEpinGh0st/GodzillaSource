package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.math.raw.Nat256;

public class SecP128R1Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { -1, -1, -1, -3 };
  
  static final int[] PExt = new int[] { 1, 0, 0, 4, -2, -1, 3, -4 };
  
  private static final int[] PExtInv = new int[] { -1, -1, -1, -5, 1, 0, -4, 3 };
  
  private static final int P3s1 = 2147483646;
  
  private static final int PExt7s1 = 2147483646;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat128.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[3] >>> 1 >= 2147483646 && Nat128.gte(paramArrayOfint3, P)))
      addPInvTo(paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat256.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[7] >>> 1 >= 2147483646 && Nat256.gte(paramArrayOfint3, PExt)))
      Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(4, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[3] >>> 1 >= 2147483646 && Nat128.gte(paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat128.fromBigInteger(paramBigInteger);
    if (arrayOfInt[3] >>> 1 >= 2147483646 && Nat128.gte(arrayOfInt, P))
      Nat128.subFrom(P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(4, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      int i = Nat128.add(paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(4, paramArrayOfint2, i);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat128.createExt();
    Nat128.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void multiplyAddToExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat128.mulAddTo(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[7] >>> 1 >= 2147483646 && Nat256.gte(paramArrayOfint3, PExt)))
      Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat128.isZero(paramArrayOfint1)) {
      Nat128.zero(paramArrayOfint2);
    } else {
      Nat128.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = paramArrayOfint1[0] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint1[1] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[2] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint1[3] & 0xFFFFFFFFL;
    long l5 = paramArrayOfint1[4] & 0xFFFFFFFFL;
    long l6 = paramArrayOfint1[5] & 0xFFFFFFFFL;
    long l7 = paramArrayOfint1[6] & 0xFFFFFFFFL;
    long l8 = paramArrayOfint1[7] & 0xFFFFFFFFL;
    l4 += l8;
    l7 += l8 << 1L;
    l3 += l7;
    l6 += l7 << 1L;
    l2 += l6;
    l5 += l6 << 1L;
    l1 += l5;
    l4 += l5 << 1L;
    paramArrayOfint2[0] = (int)l1;
    l2 += l1 >>> 32L;
    paramArrayOfint2[1] = (int)l2;
    l3 += l2 >>> 32L;
    paramArrayOfint2[2] = (int)l3;
    l4 += l3 >>> 32L;
    paramArrayOfint2[3] = (int)l4;
    reduce32((int)(l4 >>> 32L), paramArrayOfint2);
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    while (paramInt != 0) {
      long l2 = paramInt & 0xFFFFFFFFL;
      long l1 = (paramArrayOfint[0] & 0xFFFFFFFFL) + l2;
      paramArrayOfint[0] = (int)l1;
      l1 >>= 32L;
      if (l1 != 0L) {
        l1 += paramArrayOfint[1] & 0xFFFFFFFFL;
        paramArrayOfint[1] = (int)l1;
        l1 >>= 32L;
        l1 += paramArrayOfint[2] & 0xFFFFFFFFL;
        paramArrayOfint[2] = (int)l1;
        l1 >>= 32L;
      } 
      l1 += (paramArrayOfint[3] & 0xFFFFFFFFL) + (l2 << 1L);
      paramArrayOfint[3] = (int)l1;
      l1 >>= 32L;
      paramInt = (int)l1;
    } 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat128.createExt();
    Nat128.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat128.createExt();
    Nat128.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      Nat128.square(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat128.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      subPInvFrom(paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(10, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(4, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[3] >>> 1 >= 2147483646 && Nat128.gte(paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  private static void addPInvTo(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[1] & 0xFFFFFFFFL;
      paramArrayOfint[1] = (int)l;
      l >>= 32L;
      l += paramArrayOfint[2] & 0xFFFFFFFFL;
      paramArrayOfint[2] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) + 2L;
    paramArrayOfint[3] = (int)l;
  }
  
  private static void subPInvFrom(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[1] & 0xFFFFFFFFL;
      paramArrayOfint[1] = (int)l;
      l >>= 32L;
      l += paramArrayOfint[2] & 0xFFFFFFFFL;
      paramArrayOfint[2] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) - 2L;
    paramArrayOfint[3] = (int)l;
  }
}
