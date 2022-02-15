package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;

public class SecP160R1Field {
  private static final long M = 4294967295L;
  
  static final int[] P = new int[] { Integer.MAX_VALUE, -1, -1, -1, -1 };
  
  static final int[] PExt = new int[] { 1, 1073741825, 0, 0, 0, -2, -2, -1, -1, -1 };
  
  private static final int[] PExtInv = new int[] { -1, -1073741826, -1, -1, -1, 1, 1 };
  
  private static final int P4 = -1;
  
  private static final int PExt9 = -1;
  
  private static final int PInv = -2147483647;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat160.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[4] == -1 && Nat160.gte(paramArrayOfint3, P)))
      Nat.addWordTo(5, -2147483647, paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(10, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[9] == -1 && Nat.gte(10, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(10, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(5, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[4] == -1 && Nat160.gte(paramArrayOfint2, P)))
      Nat.addWordTo(5, -2147483647, paramArrayOfint2); 
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    int[] arrayOfInt = Nat160.fromBigInteger(paramBigInteger);
    if (arrayOfInt[4] == -1 && Nat160.gte(arrayOfInt, P))
      Nat160.subFrom(P, arrayOfInt); 
    return arrayOfInt;
  }
  
  public static void half(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if ((paramArrayOfint1[0] & 0x1) == 0) {
      Nat.shiftDownBit(5, paramArrayOfint1, 0, paramArrayOfint2);
    } else {
      int i = Nat160.add(paramArrayOfint1, P, paramArrayOfint2);
      Nat.shiftDownBit(5, paramArrayOfint2, i);
    } 
  }
  
  public static void multiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt = Nat160.createExt();
    Nat160.mul(paramArrayOfint1, paramArrayOfint2, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint3);
  }
  
  public static void multiplyAddToExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat160.mulAddTo(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[9] == -1 && Nat.gte(10, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(10, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat160.isZero(paramArrayOfint1)) {
      Nat160.zero(paramArrayOfint2);
    } else {
      Nat160.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = paramArrayOfint1[5] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint1[6] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[7] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint1[8] & 0xFFFFFFFFL;
    long l5 = paramArrayOfint1[9] & 0xFFFFFFFFL;
    long l6 = 0L;
    l6 += (paramArrayOfint1[0] & 0xFFFFFFFFL) + l1 + (l1 << 31L);
    paramArrayOfint2[0] = (int)l6;
    l6 >>>= 32L;
    l6 += (paramArrayOfint1[1] & 0xFFFFFFFFL) + l2 + (l2 << 31L);
    paramArrayOfint2[1] = (int)l6;
    l6 >>>= 32L;
    l6 += (paramArrayOfint1[2] & 0xFFFFFFFFL) + l3 + (l3 << 31L);
    paramArrayOfint2[2] = (int)l6;
    l6 >>>= 32L;
    l6 += (paramArrayOfint1[3] & 0xFFFFFFFFL) + l4 + (l4 << 31L);
    paramArrayOfint2[3] = (int)l6;
    l6 >>>= 32L;
    l6 += (paramArrayOfint1[4] & 0xFFFFFFFFL) + l5 + (l5 << 31L);
    paramArrayOfint2[4] = (int)l6;
    l6 >>>= 32L;
    reduce32((int)l6, paramArrayOfint2);
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    if ((paramInt != 0 && Nat160.mulWordsAdd(-2147483647, paramInt, paramArrayOfint, 0) != 0) || (paramArrayOfint[4] == -1 && Nat160.gte(paramArrayOfint, P)))
      Nat.addWordTo(5, -2147483647, paramArrayOfint); 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat160.createExt();
    Nat160.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
  }
  
  public static void squareN(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    int[] arrayOfInt = Nat160.createExt();
    Nat160.square(paramArrayOfint1, arrayOfInt);
    reduce(arrayOfInt, paramArrayOfint2);
    while (--paramInt > 0) {
      Nat160.square(paramArrayOfint2, arrayOfInt);
      reduce(arrayOfInt, paramArrayOfint2);
    } 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat160.sub(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0)
      Nat.subWordFrom(5, -2147483647, paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(10, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 && Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.decAt(10, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(5, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[4] == -1 && Nat160.gte(paramArrayOfint2, P)))
      Nat.addWordTo(5, -2147483647, paramArrayOfint2); 
  }
}
