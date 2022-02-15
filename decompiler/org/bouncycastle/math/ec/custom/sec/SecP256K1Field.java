package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecP256K1Field {
  static final int[] P = new int[] { -977, -2, -1, -1, -1, -1, -1, -1 };
  
  static final int[] PExt = new int[] { 
      954529, 1954, 1, 0, 0, 0, 0, 0, -1954, -3, 
      -1, -1, -1, -1, -1, -1 };
  
  private static final int[] PExtInv = new int[] { -954529, -1955, -2, -1, -1, -1, -1, -1, 1953, 2 };
  
  private static final int P7 = -1;
  
  private static final int PExt15 = -1;
  
  private static final int PInv33 = 977;
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat256.add(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 || (paramArrayOfint3[7] == -1 && Nat256.gte(paramArrayOfint3, P)))
      Nat.add33To(8, 977, paramArrayOfint3); 
  }
  
  public static void addExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.add(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if ((i != 0 || (paramArrayOfint3[15] == -1 && Nat.gte(16, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(16, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void addOne(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.inc(8, paramArrayOfint1, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[7] == -1 && Nat256.gte(paramArrayOfint2, P)))
      Nat.add33To(8, 977, paramArrayOfint2); 
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
    if ((i != 0 || (paramArrayOfint3[15] == -1 && Nat.gte(16, paramArrayOfint3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.incAt(16, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void negate(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (Nat256.isZero(paramArrayOfint1)) {
      Nat256.zero(paramArrayOfint2);
    } else {
      Nat256.sub(P, paramArrayOfint1, paramArrayOfint2);
    } 
  }
  
  public static void reduce(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = Nat256.mul33Add(977, paramArrayOfint1, 8, paramArrayOfint1, 0, paramArrayOfint2, 0);
    int i = Nat256.mul33DWordAdd(977, l, paramArrayOfint2, 0);
    if (i != 0 || (paramArrayOfint2[7] == -1 && Nat256.gte(paramArrayOfint2, P)))
      Nat.add33To(8, 977, paramArrayOfint2); 
  }
  
  public static void reduce32(int paramInt, int[] paramArrayOfint) {
    if ((paramInt != 0 && Nat256.mul33WordAdd(977, paramInt, paramArrayOfint, 0) != 0) || (paramArrayOfint[7] == -1 && Nat256.gte(paramArrayOfint, P)))
      Nat.add33To(8, 977, paramArrayOfint); 
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
      Nat.sub33From(8, 977, paramArrayOfint3); 
  }
  
  public static void subtractExt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = Nat.sub(16, paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    if (i != 0 && Nat.subFrom(PExtInv.length, PExtInv, paramArrayOfint3) != 0)
      Nat.decAt(16, paramArrayOfint3, PExtInv.length); 
  }
  
  public static void twice(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = Nat.shiftUpBit(8, paramArrayOfint1, 0, paramArrayOfint2);
    if (i != 0 || (paramArrayOfint2[7] == -1 && Nat256.gte(paramArrayOfint2, P)))
      Nat.add33To(8, 977, paramArrayOfint2); 
  }
}
