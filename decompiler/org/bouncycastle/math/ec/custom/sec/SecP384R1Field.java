package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat384;

public class SecP384R1Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { 
      -1, 0, 0, -1, -2, -1, -1, -1, -1, -1, 
      -1, -1 };
  
  static final int[] PExt = new int[] { 
      1, -2, 0, 2, 0, -2, 0, 2, 1, 0, 
      0, 0, -2, 1, 0, -2, -3, -1, -1, -1, 
      -1, -1, -1, -1 };
  
  private static final int[] PExtInv = new int[] { 
      -1, 1, -1, -3, -1, 1, -1, -3, -2, -1, 
      -1, -1, 1, -2, -1, 1, 2 };
  
  private static final int P11 = -1;
  
  private static final int PExt23 = -1;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[11] == -1 && Nat.gte(12, paramArrayOfint3, P)))
      addPInvTo(paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(24, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[23] == -1 && Nat.gte(24, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(24, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(12, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[11] == -1 && Nat.gte(12, paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat.fromBigInteger(384, paramBigInteger);
    if (arrayOfInt[11] == -1 && Nat.gte(12, arrayOfInt, P))
      Nat.subFrom(12, P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(12, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      int i = Nat.add(12, paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(12, paramArrayOfint2, i);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat.create(24);
    Nat384.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat.isZero(12, paramArrayOfint1)) {
      Nat.zero(12, paramArrayOfint2);
    } else {
      Nat.sub(12, P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = paramArrayOfint1[16] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint1[17] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[18] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint1[19] & 0xFFFFFFFFL;
    long l5 = paramArrayOfint1[20] & 0xFFFFFFFFL;
    long l6 = paramArrayOfint1[21] & 0xFFFFFFFFL;
    long l7 = paramArrayOfint1[22] & 0xFFFFFFFFL;
    long l8 = paramArrayOfint1[23] & 0xFFFFFFFFL;
    long l9 = (paramArrayOfint1[12] & 0xFFFFFFFFL) + l5 - 1L;
    long l10 = (paramArrayOfint1[13] & 0xFFFFFFFFL) + l7;
    long l11 = (paramArrayOfint1[14] & 0xFFFFFFFFL) + l7 + l8;
    long l12 = (paramArrayOfint1[15] & 0xFFFFFFFFL) + l8;
    long l13 = l2 + l6;
    long l14 = l6 - l8;
    long l15 = l7 - l8;
    long l16 = l9 + l14;
    long l17 = 0L;
    l17 += (paramArrayOfint1[0] & 0xFFFFFFFFL) + l16;
    paramArrayOfint2[0] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[1] & 0xFFFFFFFFL) + l8 - l9 + l10;
    paramArrayOfint2[1] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[2] & 0xFFFFFFFFL) - l6 - l10 + l11;
    paramArrayOfint2[2] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[3] & 0xFFFFFFFFL) - l11 + l12 + l16;
    paramArrayOfint2[3] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[4] & 0xFFFFFFFFL) + l1 + l6 + l10 - l12 + l16;
    paramArrayOfint2[4] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[5] & 0xFFFFFFFFL) - l1 + l10 + l11 + l13;
    paramArrayOfint2[5] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[6] & 0xFFFFFFFFL) + l3 - l2 + l11 + l12;
    paramArrayOfint2[6] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[7] & 0xFFFFFFFFL) + l1 + l4 - l3 + l12;
    paramArrayOfint2[7] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[8] & 0xFFFFFFFFL) + l1 + l2 + l5 - l4;
    paramArrayOfint2[8] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[9] & 0xFFFFFFFFL) + l3 - l5 + l13;
    paramArrayOfint2[9] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[10] & 0xFFFFFFFFL) + l3 + l4 - l14 + l15;
    paramArrayOfint2[10] = (int)l17;
    l17 >>= 32L;
    l17 += (paramArrayOfint1[11] & 0xFFFFFFFFL) + l4 + l5 - l15;
    paramArrayOfint2[11] = (int)l17;
    l17 >>= 32L;
    l17++;
    reduce32((int)l17, paramArrayOfint2);
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    long l = 0L;
    if (paramInt != 0) {
      long l1 = paramInt & 0xFFFFFFFFL;
      l += (paramArrayOfint[0] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[0] = (int)l;
      l >>= 32L;
      l += (paramArrayOfint[1] & 0xFFFFFFFFL) - l1;
      paramArrayOfint[1] = (int)l;
      l >>= 32L;
      if (l != 0L) {
        l += paramArrayOfint[2] & 0xFFFFFFFFL;
        paramArrayOfint[2] = (int)l;
        l >>= 32L;
      } 
      l += (paramArrayOfint[3] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[3] = (int)l;
      l >>= 32L;
      l += (paramArrayOfint[4] & 0xFFFFFFFFL) + l1;
      paramArrayOfint[4] = (int)l;
      l >>= 32L;
    } 
    if ((l != 0L && Nat.incAt(12, paramArrayOfint, 5) != 0) || (paramArrayOfint[11] == -1 && Nat.gte(12, paramArrayOfint, P)))
      addPInvTo(paramArrayOfint); 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat.create(24);
    Nat384.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat.create(24);
    Nat384.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      Nat384.square(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      subPInvFrom(paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(24, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 && Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.decAt(24, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(12, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[11] == -1 && Nat.gte(12, paramArrayOfint2, P)))
      addPInvTo(paramArrayOfint2); 
  }
  
  private static void addPInvTo(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[1] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[2] & 0xFFFFFFFFL;
      paramArrayOfint[2] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[3] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[4] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[4] = (int)l;
    l >>= 32L;
    if (l != 0L)
      Nat.incAt(12, paramArrayOfint, 5); 
  }
  
  private static void subPInvFrom(int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[1] = (int)l;
    l >>= 32L;
    if (l != 0L) {
      l += paramArrayOfint[2] & 0xFFFFFFFFL;
      paramArrayOfint[2] = (int)l;
      l >>= 32L;
    } 
    l += (paramArrayOfint[3] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[3] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[4] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[4] = (int)l;
    l >>= 32L;
    if (l != 0L)
      Nat.decAt(12, paramArrayOfint, 5); 
  }
}
