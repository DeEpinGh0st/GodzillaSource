package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192K1Field {
  static final int[] P = new int[] { -4553, -2, -1, -1, -1, -1 };
  
  static final int[] PExt = new int[] { 
      20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, 
      -1, -1 };
  
  private static final int[] PExtInv = new int[] { -20729809, -9107, -2, -1, -1, -1, 9105, 2 };
  
  private static final int P5 = -1;
  
  private static final int PExt11 = -1;
  
  private static final int PInv33 = 4553;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat192.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[5] == -1 && Nat192.gte(paramArrayOfint3, P)))
      Nat.add33To(6, 4553, paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[11] == -1 && Nat.gte(12, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(12, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(6, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
      Nat.add33To(6, 4553, paramArrayOfint2); 
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
    long l = Nat192.mul33Add(4553, paramArrayOfint1, 6, paramArrayOfint1, 0, paramArrayOfint2, 0);
    int i = Nat192.mul33DWordAdd(4553, l, paramArrayOfint2, 0);
    if (i != 0 || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
      Nat.add33To(6, 4553, paramArrayOfint2); 
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    if ((paramInt != 0 && Nat192.mul33WordAdd(4553, paramInt, paramArrayOfint, 0) != 0) || (paramArrayOfint[5] == -1 && Nat192.gte(paramArrayOfint, P)))
      Nat.add33To(6, 4553, paramArrayOfint); 
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
      Nat.sub33From(6, 4553, paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(12, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 && Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.decAt(12, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(6, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[5] == -1 && Nat192.gte(paramArrayOfint2, P)))
      Nat.add33To(6, 4553, paramArrayOfint2); 
  }
}
