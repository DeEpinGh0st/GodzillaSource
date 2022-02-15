package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192R1Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { -1, -1, -2, -1, -1, -1 };
  
  static final int[] PExt = new int[] { 
      1, 0, 2, 0, 1, 0, -2, -1, -3, -1, 
      -1, -1 };
  
  private static final int[] PExtInv = new int[] { -1, -1, -3, -1, -2, -1, 1, 0, 2 };
  
  private static final int P5 = -1;
  
  private static final int PExt11 = -1;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat192.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[5] == -1 && Nat192.gte(paramArrayOfint3, P)))
      addPInvTo(paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[11] == -1 && Nat.gte(12, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(12, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(6, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat192.fromBigInteger(paramBigInteger);
    if (arrayOfInt[5] == -1 && Nat192.gte(arrayOfInt, P))
      Nat192.subFrom(P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(6, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      int i = Nat192.add(paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(6, paramArrayOfint2, i);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat192.createExt();
    Nat192.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void multiplyAddToExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat192.mulAddTo(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[11] == -1 && Nat.gte(12, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(12, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat192.isZero(paramArrayOfint1)) {
      Nat192.zero(paramArrayOfint2);
    } else {
      Nat192.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = paramArrayOfint1[6] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint1[7] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[8] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint1[9] & 0xFFFFFFFFL;
    long l5 = paramArrayOfint1[10] & 0xFFFFFFFFL;
    long l6 = paramArrayOfint1[11] & 0xFFFFFFFFL;
    long l7 = l1 + l5;
    long l8 = l2 + l6;
    long l9 = 0L;
    l9 += (paramArrayOfint1[0] & 0xFFFFFFFFL) + l7;
    int i = (int)l9;
    l9 >>= 32L;
    l9 += (paramArrayOfint1[1] & 0xFFFFFFFFL) + l8;
    paramArrayOfint2[1] = (int)l9;
    l9 >>= 32L;
    l7 += l3;
    l8 += l4;
    l9 += (paramArrayOfint1[2] & 0xFFFFFFFFL) + l7;
    long l10 = l9 & 0xFFFFFFFFL;
    l9 >>= 32L;
    l9 += (paramArrayOfint1[3] & 0xFFFFFFFFL) + l8;
    paramArrayOfint2[3] = (int)l9;
    l9 >>= 32L;
    l7 -= l1;
    l8 -= l2;
    l9 += (paramArrayOfint1[4] & 0xFFFFFFFFL) + l7;
    paramArrayOfint2[4] = (int)l9;
    l9 >>= 32L;
    l9 += (paramArrayOfint1[5] & 0xFFFFFFFFL) + l8;
    paramArrayOfint2[5] = (int)l9;
    l9 >>= 32L;
    l10 += l9;
    l9 += i & 0xFFFFFFFFL;
    paramArrayOfint2[0] = (int)l9;
    l9 >>= 32L;
    if (l9 != 0L) {
      l9 += paramArrayOfint2[1] & 0xFFFFFFFFL;
      paramArrayOfint2[1] = (int)l9;
      l10 += l9 >> 32L;
    } 
    paramArrayOfint2[2] = (int)l10;
    l9 = l10 >> 32L;
    if ((l9 != 0L && Nat.incAt(6, paramArrayOfint2, 3) != 0) || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    long l = 0L;
    if (paramInt != 0) {
      long l1 = paramInt & 0xFFFFFFFFL;
      l += (paramArrayOfint[0] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[0] = (int)l;
      l >>= 32L;
      if (l != 0L) {
        l += paramArrayOfint[1] & 0xFFFFFFFFL;
        paramArrayOfint[1] = (int)l;
        l >>= 32L;
      } 
      l += (paramArrayOfint[2] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[2] = (int)l;
      l >>= 32L;
    } 
    if ((l != 0L && Nat.incAt(6, paramArrayOfint, 3) != 0) || (paramArrayOfint[5] == -1 && Nat192.gte(paramArrayOfint, P)))
      addPInvTo(paramArrayOfint); 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat192.createExt();
    Nat192.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat192.createExt();
    Nat192.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      Nat192.square(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat192.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      subPInvFrom(paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 && Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.decAt(12, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(6, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
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
    } 
    l += (paramArrayOfint[2] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[2] = (int)l;
    l >>= 32L;
    if (l != 0L)
      Nat.incAt(6, paramArrayOfint, 3); 
  }
  
  private static void subPInvFrom(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[1] & 0xFFFFFFFFL;
      paramArrayOfint[1] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[2] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[2] = (int)l;
    l >>= 32L;
    if (l != 0L)
      Nat.decAt(6, paramArrayOfint, 3); 
  }
}
