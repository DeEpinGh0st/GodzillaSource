package org.bouncycastle.pqc.math.linearalgebra;

public final class PolynomialRingGF2 {
  public static int add(int paramInt1, int paramInt2) {
    return paramInt1 ^ paramInt2;
  }
  
  public static long multiply(int paramInt1, int paramInt2) {
    long l = 0L;
    if (paramInt2 != 0) {
      long l1;
      for (l1 = paramInt2 & 0xFFFFFFFFL; paramInt1 != 0; l1 <<= 1L) {
        byte b = (byte)(paramInt1 & 0x1);
        if (b == 1)
          l ^= l1; 
        paramInt1 >>>= 1;
      } 
    } 
    return l;
  }
  
  public static int modMultiply(int paramInt1, int paramInt2, int paramInt3) {
    int i = 0;
    int j = remainder(paramInt1, paramInt3);
    int k = remainder(paramInt2, paramInt3);
    if (k != 0) {
      int m = 1 << degree(paramInt3);
      while (j != 0) {
        byte b = (byte)(j & 0x1);
        if (b == 1)
          i ^= k; 
        j >>>= 1;
        k <<= 1;
        if (k >= m)
          k ^= paramInt3; 
      } 
    } 
    return i;
  }
  
  public static int degree(int paramInt) {
    byte b = -1;
    while (paramInt != 0) {
      b++;
      paramInt >>>= 1;
    } 
    return b;
  }
  
  public static int degree(long paramLong) {
    byte b = 0;
    while (paramLong != 0L) {
      b++;
      paramLong >>>= 1L;
    } 
    return b - 1;
  }
  
  public static int remainder(int paramInt1, int paramInt2) {
    int i = paramInt1;
    if (paramInt2 == 0) {
      System.err.println("Error: to be divided by 0");
      return 0;
    } 
    while (degree(i) >= degree(paramInt2))
      i ^= paramInt2 << degree(i) - degree(paramInt2); 
    return i;
  }
  
  public static int rest(long paramLong, int paramInt) {
    long l1 = paramLong;
    if (paramInt == 0) {
      System.err.println("Error: to be divided by 0");
      return 0;
    } 
    long l2 = paramInt & 0xFFFFFFFFL;
    while (l1 >>> 32L != 0L)
      l1 ^= l2 << degree(l1) - degree(l2); 
    int i;
    for (i = (int)(l1 & 0xFFFFFFFFFFFFFFFFL); degree(i) >= degree(paramInt); i ^= paramInt << degree(i) - degree(paramInt));
    return i;
  }
  
  public static int gcd(int paramInt1, int paramInt2) {
    int i = paramInt1;
    for (int j = paramInt2; j != 0; j = k) {
      int k = remainder(i, j);
      i = j;
    } 
    return i;
  }
  
  public static boolean isIrreducible(int paramInt) {
    if (paramInt == 0)
      return false; 
    int i = degree(paramInt) >>> 1;
    int j = 2;
    for (byte b = 0; b < i; b++) {
      j = modMultiply(j, j, paramInt);
      if (gcd(j ^ 0x2, paramInt) != 1)
        return false; 
    } 
    return true;
  }
  
  public static int getIrreduciblePolynomial(int paramInt) {
    if (paramInt < 0) {
      System.err.println("The Degree is negative");
      return 0;
    } 
    if (paramInt > 31) {
      System.err.println("The Degree is more then 31");
      return 0;
    } 
    if (paramInt == 0)
      return 1; 
    int i = 1 << paramInt;
    i++;
    int j = 1 << paramInt + 1;
    for (int k = i; k < j; k += 2) {
      if (isIrreducible(k))
        return k; 
    } 
    return 0;
  }
}
