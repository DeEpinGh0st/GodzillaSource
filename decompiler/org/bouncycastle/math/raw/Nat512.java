package org.bouncycastle.math.raw;

public abstract class Nat512 {
  public static void mul(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    Nat256.mul(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3);
    Nat256.mul(paramArrayOfint1, 8, paramArrayOfint2, 8, paramArrayOfint3, 16);
    int i = Nat256.addToEachOther(paramArrayOfint3, 8, paramArrayOfint3, 16);
    int j = i + Nat256.addTo(paramArrayOfint3, 0, paramArrayOfint3, 8, 0);
    i += Nat256.addTo(paramArrayOfint3, 24, paramArrayOfint3, 16, j);
    int[] arrayOfInt1 = Nat256.create();
    int[] arrayOfInt2 = Nat256.create();
    boolean bool = (Nat256.diff(paramArrayOfint1, 8, paramArrayOfint1, 0, arrayOfInt1, 0) != Nat256.diff(paramArrayOfint2, 8, paramArrayOfint2, 0, arrayOfInt2, 0)) ? true : false;
    int[] arrayOfInt3 = Nat256.createExt();
    Nat256.mul(arrayOfInt1, arrayOfInt2, arrayOfInt3);
    i += bool ? Nat.addTo(16, arrayOfInt3, 0, paramArrayOfint3, 8) : Nat.subFrom(16, arrayOfInt3, 0, paramArrayOfint3, 8);
    Nat.addWordAt(32, i, paramArrayOfint3, 24);
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Nat256.square(paramArrayOfint1, paramArrayOfint2);
    Nat256.square(paramArrayOfint1, 8, paramArrayOfint2, 16);
    int i = Nat256.addToEachOther(paramArrayOfint2, 8, paramArrayOfint2, 16);
    int j = i + Nat256.addTo(paramArrayOfint2, 0, paramArrayOfint2, 8, 0);
    i += Nat256.addTo(paramArrayOfint2, 24, paramArrayOfint2, 16, j);
    int[] arrayOfInt1 = Nat256.create();
    Nat256.diff(paramArrayOfint1, 8, paramArrayOfint1, 0, arrayOfInt1, 0);
    int[] arrayOfInt2 = Nat256.createExt();
    Nat256.square(arrayOfInt1, arrayOfInt2);
    i += Nat.subFrom(16, arrayOfInt2, 0, paramArrayOfint2, 8);
    Nat.addWordAt(32, i, paramArrayOfint2, 24);
  }
}
