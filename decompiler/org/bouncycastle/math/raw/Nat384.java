package org.bouncycastle.math.raw;

public abstract class Nat384 {
  public static void mul(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat192.mul(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    Nat192.mul(paramArrayOfint1, 6, paramArrayOfint2, 6, paramArrayOfint3, 12);
    int i = Nat192.addToEachOther(paramArrayOfint3, 6, paramArrayOfint3, 12);
    int j = i + Nat192.addTo(paramArrayOfint3, 0, paramArrayOfint3, 6, 0);
    i += Nat192.addTo(paramArrayOfint3, 18, paramArrayOfint3, 12, j);
    int[] arrayOfInt1 = Nat192.create();
    int[] arrayOfInt2 = Nat192.create();
    boolean bool = (Nat192.diff(paramArrayOfint1, 6, paramArrayOfint1, 0, arrayOfInt1, 0) != Nat192.diff(paramArrayOfint2, 6, paramArrayOfint2, 0, arrayOfInt2, 0)) ? true : false;
    int[] arrayOfInt3 = Nat192.createExt();
    Nat192.mul(arrayOfInt1, arrayOfInt2, arrayOfInt3);
    i += bool ? Nat.addTo(12, arrayOfInt3, 0, paramArrayOfint3, 6) : Nat.subFrom(12, arrayOfInt3, 0, paramArrayOfint3, 6);
    Nat.addWordAt(24, i, paramArrayOfint3, 18);
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Nat192.square(paramArrayOfint1, paramArrayOfint2);
    Nat192.square(paramArrayOfint1, 6, paramArrayOfint2, 12);
    int i = Nat192.addToEachOther(paramArrayOfint2, 6, paramArrayOfint2, 12);
    int j = i + Nat192.addTo(paramArrayOfint2, 0, paramArrayOfint2, 6, 0);
    i += Nat192.addTo(paramArrayOfint2, 18, paramArrayOfint2, 12, j);
    int[] arrayOfInt1 = Nat192.create();
    Nat192.diff(paramArrayOfint1, 6, paramArrayOfint1, 0, arrayOfInt1, 0);
    int[] arrayOfInt2 = Nat192.createExt();
    Nat192.square(arrayOfInt1, arrayOfInt2);
    i += Nat.subFrom(12, arrayOfInt2, 0, paramArrayOfint2, 6);
    Nat.addWordAt(24, i, paramArrayOfint2, 18);
  }
}
