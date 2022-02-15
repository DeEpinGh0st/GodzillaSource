package org.bouncycastle.math.raw;

import java.util.Random;

public abstract class Mod {
  public static int inverse32(int paramInt) {
    int i = paramInt;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    i *= 2 - paramInt * i;
    return i;
  }
  
  public static void invert(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int i = paramArrayOfint1.length;
    if (Nat.isZero(i, paramArrayOfint2))
      throw new IllegalArgumentException("'x' cannot be 0"); 
    if (Nat.isOne(i, paramArrayOfint2)) {
      System.arraycopy(paramArrayOfint2, 0, paramArrayOfint3, 0, i);
      return;
    } 
    int[] arrayOfInt1 = Nat.copy(i, paramArrayOfint2);
    int[] arrayOfInt2 = Nat.create(i);
    arrayOfInt2[0] = 1;
    int j = 0;
    if ((arrayOfInt1[0] & 0x1) == 0)
      j = inversionStep(paramArrayOfint1, arrayOfInt1, i, arrayOfInt2, j); 
    if (Nat.isOne(i, arrayOfInt1)) {
      inversionResult(paramArrayOfint1, j, arrayOfInt2, paramArrayOfint3);
      return;
    } 
    int[] arrayOfInt3 = Nat.copy(i, paramArrayOfint1);
    int[] arrayOfInt4 = Nat.create(i);
    int k = 0;
    int m = i;
    while (true) {
      if (arrayOfInt1[m - 1] == 0 && arrayOfInt3[m - 1] == 0) {
        m--;
        continue;
      } 
      if (Nat.gte(m, arrayOfInt1, arrayOfInt3)) {
        Nat.subFrom(m, arrayOfInt3, arrayOfInt1);
        j += Nat.subFrom(i, arrayOfInt4, arrayOfInt2) - k;
        j = inversionStep(paramArrayOfint1, arrayOfInt1, m, arrayOfInt2, j);
        if (Nat.isOne(m, arrayOfInt1)) {
          inversionResult(paramArrayOfint1, j, arrayOfInt2, paramArrayOfint3);
          return;
        } 
        continue;
      } 
      Nat.subFrom(m, arrayOfInt1, arrayOfInt3);
      k += Nat.subFrom(i, arrayOfInt2, arrayOfInt4) - j;
      k = inversionStep(paramArrayOfint1, arrayOfInt3, m, arrayOfInt4, k);
      if (Nat.isOne(m, arrayOfInt3)) {
        inversionResult(paramArrayOfint1, k, arrayOfInt4, paramArrayOfint3);
        return;
      } 
    } 
  }
  
  public static int[] random(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    Random random = new Random();
    int[] arrayOfInt = Nat.create(i);
    int j = paramArrayOfint[i - 1];
    j |= j >>> 1;
    j |= j >>> 2;
    j |= j >>> 4;
    j |= j >>> 8;
    j |= j >>> 16;
    while (true) {
      for (int k = 0; k != i; k++)
        arrayOfInt[k] = random.nextInt(); 
      arrayOfInt[i - 1] = arrayOfInt[i - 1] & j;
      if (!Nat.gte(i, arrayOfInt, paramArrayOfint))
        return arrayOfInt; 
    } 
  }
  
  public static void add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4) {
    int i = paramArrayOfint1.length;
    int j = Nat.add(i, paramArrayOfint2, paramArrayOfint3, paramArrayOfint4);
    if (j != 0)
      Nat.subFrom(i, paramArrayOfint1, paramArrayOfint4); 
  }
  
  public static void subtract(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4) {
    int i = paramArrayOfint1.length;
    int j = Nat.sub(i, paramArrayOfint2, paramArrayOfint3, paramArrayOfint4);
    if (j != 0)
      Nat.addTo(i, paramArrayOfint1, paramArrayOfint4); 
  }
  
  private static void inversionResult(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    if (paramInt < 0) {
      Nat.add(paramArrayOfint1.length, paramArrayOfint2, paramArrayOfint1, paramArrayOfint3);
    } else {
      System.arraycopy(paramArrayOfint2, 0, paramArrayOfint3, 0, paramArrayOfint1.length);
    } 
  }
  
  private static int inversionStep(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int[] paramArrayOfint3, int paramInt2) {
    int i = paramArrayOfint1.length;
    int j;
    for (j = 0; paramArrayOfint2[0] == 0; j += true)
      Nat.shiftDownWord(paramInt1, paramArrayOfint2, 0); 
    int k = getTrailingZeroes(paramArrayOfint2[0]);
    if (k > 0) {
      Nat.shiftDownBits(paramInt1, paramArrayOfint2, k, 0);
      j += k;
    } 
    for (k = 0; k < j; k++) {
      if ((paramArrayOfint3[0] & 0x1) != 0)
        if (paramInt2 < 0) {
          paramInt2 += Nat.addTo(i, paramArrayOfint1, paramArrayOfint3);
        } else {
          paramInt2 += Nat.subFrom(i, paramArrayOfint1, paramArrayOfint3);
        }  
      Nat.shiftDownBit(i, paramArrayOfint3, paramInt2);
    } 
    return paramInt2;
  }
  
  private static int getTrailingZeroes(int paramInt) {
    byte b;
    for (b = 0; (paramInt & 0x1) == 0; b++)
      paramInt >>>= 1; 
    return b;
  }
}
