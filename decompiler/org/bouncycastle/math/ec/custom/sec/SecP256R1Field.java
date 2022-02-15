package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecP256R1Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { -1, -1, -1, 0, 0, 0, 1, -1 };
  
  static final int[] PExt = new int[] { 
      1, 0, 0, -2, -1, -1, -2, 1, -2, 1, 
      -2, 1, 1, -2, 2, -2 };
  
  private static final int P7 = -1;
  
  private static final int PExt15s1 = 2147483647;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat256.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[7] == -1 && Nat256.gte(paramArrayOfint3, P)))
      addPInvTo(paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, paramArrayOfint3, PExt)))
      Nat.subFrom(16, PExt, paramArrayOfint3); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(8, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[7] == -1 && Nat256.gte(paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat256.fromBigInteger(paramBigInteger);
    if (arrayOfInt[7] == -1 && Nat256.gte(arrayOfInt, P))
      Nat256.subFrom(P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(8, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      int i = Nat256.add(paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(8, paramArrayOfint2, i);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat256.createExt();
    Nat256.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void multiplyAddToExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat256.mulAddTo(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, paramArrayOfint3, PExt)))
      Nat.subFrom(16, PExt, paramArrayOfint3); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat256.isZero(paramArrayOfint1)) {
      Nat256.zero(paramArrayOfint2);
    } else {
      Nat256.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = paramArrayOfint1[8] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint1[9] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[10] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint1[11] & 0xFFFFFFFFL;
    long l5 = paramArrayOfint1[12] & 0xFFFFFFFFL;
    long l6 = paramArrayOfint1[13] & 0xFFFFFFFFL;
    long l7 = paramArrayOfint1[14] & 0xFFFFFFFFL;
    long l8 = paramArrayOfint1[15] & 0xFFFFFFFFL;
    l1 -= 6L;
    long l9 = l1 + l2;
    long l10 = l2 + l3;
    long l11 = l3 + l4 - l8;
    long l12 = l4 + l5;
    long l13 = l5 + l6;
    long l14 = l6 + l7;
    long l15 = l7 + l8;
    long l16 = l14 - l9;
    long l17 = 0L;
    l17 += (paramArrayOfint1[0] & 0xFFFFFFFFL) - l12 - l16;
    paramArrayOfint2[0] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[1] & 0xFFFFFFFFL) + l10 - l13 - l15;
    paramArrayOfint2[1] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[2] & 0xFFFFFFFFL) + l11 - l14;
    paramArrayOfint2[2] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[3] & 0xFFFFFFFFL) + (l12 << 1L) + l16 - l15;
    paramArrayOfint2[3] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[4] & 0xFFFFFFFFL) + (l13 << 1L) + l7 - l10;
    paramArrayOfint2[4] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[5] & 0xFFFFFFFFL) + (l14 << 1L) - l11;
    paramArrayOfint2[5] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[6] & 0xFFFFFFFFL) + (l15 << 1L) + l16;
    paramArrayOfint2[6] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[7] & 0xFFFFFFFFL) + (l8 << 1L) + l1 - l11 - l13;
    paramArrayOfint2[7] = (int)l17;
    l17 >>= 32L;
    l17 += 6L;
    reduce32((int)l17, paramArrayOfint2);
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
        l += paramArrayOfint[2] & 0xFFFFFFFFL;
        paramArrayOfint[2] = (int)l;
        l >>= 32L;
      } 
      l += (paramArrayOfint[3] & 0xFFFFFFFFL) - l1;
      paramArrayOfint[3] = (int)l;
      l >>= 32L;
      if (l != 0L) {
        l += paramArrayOfint[4] & 0xFFFFFFFFL;
        paramArrayOfint[4] = (int)l;
        l >>= 32L;
        l += paramArrayOfint[5] & 0xFFFFFFFFL;
        paramArrayOfint[5] = (int)l;
        l >>= 32L;
      } 
      l += (paramArrayOfint[6] & 0xFFFFFFFFL) - l1;
      paramArrayOfint[6] = (int)l;
      l >>= 32L;
      l += (paramArrayOfint[7] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[7] = (int)l;
      l >>= 32L;
    } 
    if (l != 0L || (paramArrayOfint[7] == -1 && Nat256.gte(paramArrayOfint, P)))
      addPInvTo(paramArrayOfint); 
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
      subPInvFrom(paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      Nat.addTo(16, PExt, paramArrayOfint3); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(8, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[7] == -1 && Nat256.gte(paramArrayOfint2, P)))
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
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[3] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[4] & 0xFFFFFFFFL;
      paramArrayOfint[4] = (int)l;
      l >>= 32L;
      l += paramArrayOfint[5] & 0xFFFFFFFFL;
      paramArrayOfint[5] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[6] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[6] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[7] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[7] = (int)l;
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
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[3] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[4] & 0xFFFFFFFFL;
      paramArrayOfint[4] = (int)l;
      l >>= 32L;
      l += paramArrayOfint[5] & 0xFFFFFFFFL;
      paramArrayOfint[5] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[6] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[6] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[7] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[7] = (int)l;
  }
}
