package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat {
  private static final long M = 4294967295L;
  
  public static int add(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint1[b] & 0xFFFFFFFFL) + (paramArrayOfint2[b] & 0xFFFFFFFFL);
      paramArrayOfint3[b] = (int)l;
      l >>>= 32L;
    } 
    return (int)l;
  }
  
  public static int add33At(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL) + (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[paramInt3 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3 + 2);
  }
  
  public static int add33At(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3, int paramInt4) {
    long l = (paramArrayOfint[paramInt3 + paramInt4] & 0xFFFFFFFFL) + (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + paramInt4] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt3 + paramInt4 + 1] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[paramInt3 + paramInt4 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3, paramInt4 + 2);
  }
  
  public static int add33To(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, 2);
  }
  
  public static int add33To(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL) + (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL) + 1L;
    paramArrayOfint[paramInt3 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3, 2);
  }
  
  public static int addBothTo(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint1[b] & 0xFFFFFFFFL) + (paramArrayOfint2[b] & 0xFFFFFFFFL) + (paramArrayOfint3[b] & 0xFFFFFFFFL);
      paramArrayOfint3[b] = (int)l;
      l >>>= 32L;
    } 
    return (int)l;
  }
  
  public static int addBothTo(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      l += (paramArrayOfint1[paramInt2 + b] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + b] & 0xFFFFFFFFL) + (paramArrayOfint3[paramInt4 + b] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt4 + b] = (int)l;
      l >>>= 32L;
    } 
    return (int)l;
  }
  
  public static int addDWordAt(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l = (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL) + (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL) + (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt2 + 2);
  }
  
  public static int addDWordAt(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    long l = (paramArrayOfint[paramInt2 + paramInt3] & 0xFFFFFFFFL) + (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + paramInt3] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt2 + paramInt3 + 1] & 0xFFFFFFFFL) + (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + paramInt3 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt2, paramInt3 + 2);
  }
  
  public static int addDWordTo(int paramInt, long paramLong, int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) + (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) + (paramLong >>> 32L);
    paramArrayOfint[1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt, paramArrayOfint, 2);
  }
  
  public static int addDWordTo(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l = (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL) + (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL) + (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + 1] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt2, 2);
  }
  
  public static int addTo(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint1[b] & 0xFFFFFFFFL) + (paramArrayOfint2[b] & 0xFFFFFFFFL);
      paramArrayOfint2[b] = (int)l;
      l >>>= 32L;
    } 
    return (int)l;
  }
  
  public static int addTo(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      l += (paramArrayOfint1[paramInt2 + b] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt3 + b] = (int)l;
      l >>>= 32L;
    } 
    return (int)l;
  }
  
  public static int addWordAt(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramInt2 & 0xFFFFFFFFL) + (paramArrayOfint[paramInt3] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3 + 1);
  }
  
  public static int addWordAt(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3, int paramInt4) {
    long l = (paramInt2 & 0xFFFFFFFFL) + (paramArrayOfint[paramInt3 + paramInt4] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + paramInt4] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3, paramInt4 + 1);
  }
  
  public static int addWordTo(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    long l = (paramInt2 & 0xFFFFFFFFL) + (paramArrayOfint[0] & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, 1);
  }
  
  public static int addWordTo(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramInt2 & 0xFFFFFFFFL) + (paramArrayOfint[paramInt3] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3] = (int)l;
    l >>>= 32L;
    return (l == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3, 1);
  }
  
  public static int[] copy(int paramInt, int[] paramArrayOfint) {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramInt);
    return arrayOfInt;
  }
  
  public static void copy(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    System.arraycopy(paramArrayOfint1, 0, paramArrayOfint2, 0, paramInt);
  }
  
  public static int[] create(int paramInt) {
    return new int[paramInt];
  }
  
  public static long[] create64(int paramInt) {
    return new long[paramInt];
  }
  
  public static int dec(int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++) {
      paramArrayOfint[b] = paramArrayOfint[b] - 1;
      if (paramArrayOfint[b] - 1 != -1)
        return 0; 
    } 
    return -1;
  }
  
  public static int dec(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    byte b = 0;
    while (b < paramInt) {
      int i = paramArrayOfint1[b] - 1;
      paramArrayOfint2[b] = i;
      b++;
      if (i != -1) {
        while (b < paramInt) {
          paramArrayOfint2[b] = paramArrayOfint1[b];
          b++;
        } 
        return 0;
      } 
    } 
    return -1;
  }
  
  public static int decAt(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    for (int i = paramInt2; i < paramInt1; i++) {
      paramArrayOfint[i] = paramArrayOfint[i] - 1;
      if (paramArrayOfint[i] - 1 != -1)
        return 0; 
    } 
    return -1;
  }
  
  public static int decAt(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    for (int i = paramInt3; i < paramInt1; i++) {
      paramArrayOfint[paramInt2 + i] = paramArrayOfint[paramInt2 + i] - 1;
      if (paramArrayOfint[paramInt2 + i] - 1 != -1)
        return 0; 
    } 
    return -1;
  }
  
  public static boolean eq(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (int i = paramInt - 1; i >= 0; i--) {
      if (paramArrayOfint1[i] != paramArrayOfint2[i])
        return false; 
    } 
    return true;
  }
  
  public static int[] fromBigInteger(int paramInt, BigInteger paramBigInteger) {
    if (paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > paramInt)
      throw new IllegalArgumentException(); 
    int i = paramInt + 31 >> 5;
    int[] arrayOfInt = create(i);
    byte b = 0;
    while (paramBigInteger.signum() != 0) {
      arrayOfInt[b++] = paramBigInteger.intValue();
      paramBigInteger = paramBigInteger.shiftRight(32);
    } 
    return arrayOfInt;
  }
  
  public static int getBit(int[] paramArrayOfint, int paramInt) {
    if (paramInt == 0)
      return paramArrayOfint[0] & 0x1; 
    int i = paramInt >> 5;
    if (i < 0 || i >= paramArrayOfint.length)
      return 0; 
    int j = paramInt & 0x1F;
    return paramArrayOfint[i] >>> j & 0x1;
  }
  
  public static boolean gte(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (int i = paramInt - 1; i >= 0; i--) {
      int j = paramArrayOfint1[i] ^ Integer.MIN_VALUE;
      int k = paramArrayOfint2[i] ^ Integer.MIN_VALUE;
      if (j < k)
        return false; 
      if (j > k)
        return true; 
    } 
    return true;
  }
  
  public static int inc(int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++) {
      paramArrayOfint[b] = paramArrayOfint[b] + 1;
      if (paramArrayOfint[b] + 1 != 0)
        return 0; 
    } 
    return 1;
  }
  
  public static int inc(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    byte b = 0;
    while (b < paramInt) {
      int i = paramArrayOfint1[b] + 1;
      paramArrayOfint2[b] = i;
      b++;
      if (i != 0) {
        while (b < paramInt) {
          paramArrayOfint2[b] = paramArrayOfint1[b];
          b++;
        } 
        return 0;
      } 
    } 
    return 1;
  }
  
  public static int incAt(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    for (int i = paramInt2; i < paramInt1; i++) {
      paramArrayOfint[i] = paramArrayOfint[i] + 1;
      if (paramArrayOfint[i] + 1 != 0)
        return 0; 
    } 
    return 1;
  }
  
  public static int incAt(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    for (int i = paramInt3; i < paramInt1; i++) {
      paramArrayOfint[paramInt2 + i] = paramArrayOfint[paramInt2 + i] + 1;
      if (paramArrayOfint[paramInt2 + i] + 1 != 0)
        return 0; 
    } 
    return 1;
  }
  
  public static boolean isOne(int paramInt, int[] paramArrayOfint) {
    if (paramArrayOfint[0] != 1)
      return false; 
    for (byte b = 1; b < paramInt; b++) {
      if (paramArrayOfint[b] != 0)
        return false; 
    } 
    return true;
  }
  
  public static boolean isZero(int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++) {
      if (paramArrayOfint[b] != 0)
        return false; 
    } 
    return true;
  }
  
  public static void mul(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    paramArrayOfint3[paramInt] = mulWord(paramInt, paramArrayOfint1[0], paramArrayOfint2, paramArrayOfint3);
    for (byte b = 1; b < paramInt; b++)
      paramArrayOfint3[b + paramInt] = mulWordAddTo(paramInt, paramArrayOfint1[b], paramArrayOfint2, 0, paramArrayOfint3, b); 
  }
  
  public static void mul(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    paramArrayOfint3[paramInt4 + paramInt1] = mulWord(paramInt1, paramArrayOfint1[paramInt2], paramArrayOfint2, paramInt3, paramArrayOfint3, paramInt4);
    for (byte b = 1; b < paramInt1; b++)
      paramArrayOfint3[paramInt4 + b + paramInt1] = mulWordAddTo(paramInt1, paramArrayOfint1[paramInt2 + b], paramArrayOfint2, paramInt3, paramArrayOfint3, paramInt4 + b); 
  }
  
  public static int mulAddTo(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      long l1 = mulWordAddTo(paramInt, paramArrayOfint1[b], paramArrayOfint2, 0, paramArrayOfint3, b) & 0xFFFFFFFFL;
      l1 += l + (paramArrayOfint3[b + paramInt] & 0xFFFFFFFFL);
      paramArrayOfint3[b + paramInt] = (int)l1;
      l = l1 >>> 32L;
    } 
    return (int)l;
  }
  
  public static int mulAddTo(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      long l1 = mulWordAddTo(paramInt1, paramArrayOfint1[paramInt2 + b], paramArrayOfint2, paramInt3, paramArrayOfint3, paramInt4) & 0xFFFFFFFFL;
      l1 += l + (paramArrayOfint3[paramInt4 + paramInt1] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt4 + paramInt1] = (int)l1;
      l = l1 >>> 32L;
      paramInt4++;
    } 
    return (int)l;
  }
  
  public static int mul31BothAdd(int paramInt1, int paramInt2, int[] paramArrayOfint1, int paramInt3, int[] paramArrayOfint2, int[] paramArrayOfint3, int paramInt4) {
    long l1 = 0L;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    long l3 = paramInt3 & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[b] & 0xFFFFFFFFL) + l3 * (paramArrayOfint2[b] & 0xFFFFFFFFL) + (paramArrayOfint3[paramInt4 + b] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt4 + b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= paramInt1)
        return (int)l1; 
    } 
  }
  
  public static int mulWord(int paramInt1, int paramInt2, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l1 = 0L;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[b] & 0xFFFFFFFFL);
      paramArrayOfint2[b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= paramInt1)
        return (int)l1; 
    } 
  }
  
  public static int mulWord(int paramInt1, int paramInt2, int[] paramArrayOfint1, int paramInt3, int[] paramArrayOfint2, int paramInt4) {
    long l1 = 0L;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[paramInt3 + b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt4 + b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= paramInt1)
        return (int)l1; 
    } 
  }
  
  public static int mulWordAddTo(int paramInt1, int paramInt2, int[] paramArrayOfint1, int paramInt3, int[] paramArrayOfint2, int paramInt4) {
    long l1 = 0L;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[paramInt3 + b] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt4 + b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt4 + b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= paramInt1)
        return (int)l1; 
    } 
  }
  
  public static int mulWordDwordAddAt(int paramInt1, int paramInt2, long paramLong, int[] paramArrayOfint, int paramInt3) {
    long l1 = 0L;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    l1 += l2 * (paramLong & 0xFFFFFFFFL) + (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l1;
    l1 >>>= 32L;
    l1 += l2 * (paramLong >>> 32L) + (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 1] = (int)l1;
    l1 >>>= 32L;
    l1 += paramArrayOfint[paramInt3 + 2] & 0xFFFFFFFFL;
    paramArrayOfint[paramInt3 + 2] = (int)l1;
    l1 >>>= 32L;
    return (l1 == 0L) ? 0 : incAt(paramInt1, paramArrayOfint, paramInt3 + 3);
  }
  
  public static int shiftDownBit(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint[i];
      paramArrayOfint[i] = j >>> 1 | paramInt2 << 31;
      paramInt2 = j;
    } 
    return paramInt2 << 31;
  }
  
  public static int shiftDownBit(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint[paramInt2 + i];
      paramArrayOfint[paramInt2 + i] = j >>> 1 | paramInt3 << 31;
      paramInt3 = j;
    } 
    return paramInt3 << 31;
  }
  
  public static int shiftDownBit(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint1[i];
      paramArrayOfint2[i] = j >>> 1 | paramInt2 << 31;
      paramInt2 = j;
    } 
    return paramInt2 << 31;
  }
  
  public static int shiftDownBit(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int[] paramArrayOfint2, int paramInt4) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint1[paramInt2 + i];
      paramArrayOfint2[paramInt4 + i] = j >>> 1 | paramInt3 << 31;
      paramInt3 = j;
    } 
    return paramInt3 << 31;
  }
  
  public static int shiftDownBits(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint[i];
      paramArrayOfint[i] = j >>> paramInt2 | paramInt3 << -paramInt2;
      paramInt3 = j;
    } 
    return paramInt3 << -paramInt2;
  }
  
  public static int shiftDownBits(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint[paramInt2 + i];
      paramArrayOfint[paramInt2 + i] = j >>> paramInt3 | paramInt4 << -paramInt3;
      paramInt4 = j;
    } 
    return paramInt4 << -paramInt3;
  }
  
  public static int shiftDownBits(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int[] paramArrayOfint2) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint1[i];
      paramArrayOfint2[i] = j >>> paramInt2 | paramInt3 << -paramInt2;
      paramInt3 = j;
    } 
    return paramInt3 << -paramInt2;
  }
  
  public static int shiftDownBits(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint2, int paramInt5) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint1[paramInt2 + i];
      paramArrayOfint2[paramInt5 + i] = j >>> paramInt3 | paramInt4 << -paramInt3;
      paramInt4 = j;
    } 
    return paramInt4 << -paramInt3;
  }
  
  public static int shiftDownWord(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    int i = paramInt1;
    while (--i >= 0) {
      int j = paramArrayOfint[i];
      paramArrayOfint[i] = paramInt2;
      paramInt2 = j;
    } 
    return paramInt2;
  }
  
  public static int shiftUpBit(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint[b];
      paramArrayOfint[b] = i << 1 | paramInt2 >>> 31;
      paramInt2 = i;
    } 
    return paramInt2 >>> 31;
  }
  
  public static int shiftUpBit(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint[paramInt2 + b];
      paramArrayOfint[paramInt2 + b] = i << 1 | paramInt3 >>> 31;
      paramInt3 = i;
    } 
    return paramInt3 >>> 31;
  }
  
  public static int shiftUpBit(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint1[b];
      paramArrayOfint2[b] = i << 1 | paramInt2 >>> 31;
      paramInt2 = i;
    } 
    return paramInt2 >>> 31;
  }
  
  public static int shiftUpBit(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int[] paramArrayOfint2, int paramInt4) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint1[paramInt2 + b];
      paramArrayOfint2[paramInt4 + b] = i << 1 | paramInt3 >>> 31;
      paramInt3 = i;
    } 
    return paramInt3 >>> 31;
  }
  
  public static long shiftUpBit64(int paramInt1, long[] paramArrayOflong1, int paramInt2, long paramLong, long[] paramArrayOflong2, int paramInt3) {
    for (byte b = 0; b < paramInt1; b++) {
      long l = paramArrayOflong1[paramInt2 + b];
      paramArrayOflong2[paramInt3 + b] = l << 1L | paramLong >>> 63L;
      paramLong = l;
    } 
    return paramLong >>> 63L;
  }
  
  public static int shiftUpBits(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint[b];
      paramArrayOfint[b] = i << paramInt2 | paramInt3 >>> -paramInt2;
      paramInt3 = i;
    } 
    return paramInt3 >>> -paramInt2;
  }
  
  public static int shiftUpBits(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3, int paramInt4) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint[paramInt2 + b];
      paramArrayOfint[paramInt2 + b] = i << paramInt3 | paramInt4 >>> -paramInt3;
      paramInt4 = i;
    } 
    return paramInt4 >>> -paramInt3;
  }
  
  public static long shiftUpBits64(int paramInt1, long[] paramArrayOflong, int paramInt2, int paramInt3, long paramLong) {
    for (byte b = 0; b < paramInt1; b++) {
      long l = paramArrayOflong[paramInt2 + b];
      paramArrayOflong[paramInt2 + b] = l << paramInt3 | paramLong >>> -paramInt3;
      paramLong = l;
    } 
    return paramLong >>> -paramInt3;
  }
  
  public static int shiftUpBits(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int[] paramArrayOfint2) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint1[b];
      paramArrayOfint2[b] = i << paramInt2 | paramInt3 >>> -paramInt2;
      paramInt3 = i;
    } 
    return paramInt3 >>> -paramInt2;
  }
  
  public static int shiftUpBits(int paramInt1, int[] paramArrayOfint1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint2, int paramInt5) {
    for (byte b = 0; b < paramInt1; b++) {
      int i = paramArrayOfint1[paramInt2 + b];
      paramArrayOfint2[paramInt5 + b] = i << paramInt3 | paramInt4 >>> -paramInt3;
      paramInt4 = i;
    } 
    return paramInt4 >>> -paramInt3;
  }
  
  public static long shiftUpBits64(int paramInt1, long[] paramArrayOflong1, int paramInt2, int paramInt3, long paramLong, long[] paramArrayOflong2, int paramInt4) {
    for (byte b = 0; b < paramInt1; b++) {
      long l = paramArrayOflong1[paramInt2 + b];
      paramArrayOflong2[paramInt4 + b] = l << paramInt3 | paramLong >>> -paramInt3;
      paramLong = l;
    } 
    return paramLong >>> -paramInt3;
  }
  
  public static void square(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramInt << 1;
    int j = 0;
    int k = paramInt;
    int m = i;
    while (true) {
      long l1 = paramArrayOfint1[--k] & 0xFFFFFFFFL;
      long l2 = l1 * l1;
      paramArrayOfint2[--m] = j << 31 | (int)(l2 >>> 33L);
      paramArrayOfint2[--m] = (int)(l2 >>> 1L);
      j = (int)l2;
      if (k <= 0) {
        for (byte b = 1; b < paramInt; b++) {
          j = squareWordAdd(paramArrayOfint1, b, paramArrayOfint2);
          addWordAt(i, j, paramArrayOfint2, b << 1);
        } 
        shiftUpBit(i, paramArrayOfint2, paramArrayOfint1[0] << 31);
        return;
      } 
    } 
  }
  
  public static void square(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    int i = paramInt1 << 1;
    int j = 0;
    int k = paramInt1;
    int m = i;
    while (true) {
      long l1 = paramArrayOfint1[paramInt2 + --k] & 0xFFFFFFFFL;
      long l2 = l1 * l1;
      paramArrayOfint2[paramInt3 + --m] = j << 31 | (int)(l2 >>> 33L);
      paramArrayOfint2[paramInt3 + --m] = (int)(l2 >>> 1L);
      j = (int)l2;
      if (k <= 0) {
        for (byte b = 1; b < paramInt1; b++) {
          j = squareWordAdd(paramArrayOfint1, paramInt2, b, paramArrayOfint2, paramInt3);
          addWordAt(i, j, paramArrayOfint2, paramInt3, b << 1);
        } 
        shiftUpBit(i, paramArrayOfint2, paramInt3, paramArrayOfint1[paramInt2] << 31);
        return;
      } 
    } 
  }
  
  public static int squareWordAdd(int[] paramArrayOfint1, int paramInt, int[] paramArrayOfint2) {
    long l1 = 0L;
    long l2 = paramArrayOfint1[paramInt] & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[b] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt + b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt + b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= paramInt)
        return (int)l1; 
    } 
  }
  
  public static int squareWordAdd(int[] paramArrayOfint1, int paramInt1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    long l1 = 0L;
    long l2 = paramArrayOfint1[paramInt1 + paramInt2] & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[paramInt1 + b] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + paramInt3] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt2 + paramInt3] = (int)l1;
      l1 >>>= 32L;
      paramInt3++;
      if (++b >= paramInt2)
        return (int)l1; 
    } 
  }
  
  public static int sub(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint1[b] & 0xFFFFFFFFL) - (paramArrayOfint2[b] & 0xFFFFFFFFL);
      paramArrayOfint3[b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int sub(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      l += (paramArrayOfint1[paramInt2 + b] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt3 + b] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt4 + b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int sub33At(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[paramInt3 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3 + 2);
  }
  
  public static int sub33At(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3, int paramInt4) {
    long l = (paramArrayOfint[paramInt3 + paramInt4] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + paramInt4] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt3 + paramInt4 + 1] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[paramInt3 + paramInt4 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3, paramInt4 + 2);
  }
  
  public static int sub33From(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, 2);
  }
  
  public static int sub33From(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL) - 1L;
    paramArrayOfint[paramInt3 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3, 2);
  }
  
  public static int subBothFrom(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint3[b] & 0xFFFFFFFFL) - (paramArrayOfint1[b] & 0xFFFFFFFFL) - (paramArrayOfint2[b] & 0xFFFFFFFFL);
      paramArrayOfint3[b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int subBothFrom(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      l += (paramArrayOfint3[paramInt4 + b] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt2 + b] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt3 + b] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt4 + b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int subDWordAt(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l = (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL) - (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL) - (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt2 + 2);
  }
  
  public static int subDWordAt(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    long l = (paramArrayOfint[paramInt2 + paramInt3] & 0xFFFFFFFFL) - (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + paramInt3] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt2 + paramInt3 + 1] & 0xFFFFFFFFL) - (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + paramInt3 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt2, paramInt3 + 2);
  }
  
  public static int subDWordFrom(int paramInt, long paramLong, int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[1] & 0xFFFFFFFFL) - (paramLong >>> 32L);
    paramArrayOfint[1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt, paramArrayOfint, 2);
  }
  
  public static int subDWordFrom(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l = (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL) - (paramLong & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL) - (paramLong >>> 32L);
    paramArrayOfint[paramInt2 + 1] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt2, 2);
  }
  
  public static int subFrom(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      l += (paramArrayOfint2[b] & 0xFFFFFFFFL) - (paramArrayOfint1[b] & 0xFFFFFFFFL);
      paramArrayOfint2[b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int subFrom(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    long l = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      l += (paramArrayOfint2[paramInt3 + b] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt2 + b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt3 + b] = (int)l;
      l >>= 32L;
    } 
    return (int)l;
  }
  
  public static int subWordAt(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3 + 1);
  }
  
  public static int subWordAt(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3, int paramInt4) {
    long l = (paramArrayOfint[paramInt3 + paramInt4] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + paramInt4] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3, paramInt4 + 1);
  }
  
  public static int subWordFrom(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    long l = (paramArrayOfint[0] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[0] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, 1);
  }
  
  public static int subWordFrom(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l = (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL) - (paramInt2 & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l;
    l >>= 32L;
    return (l == 0L) ? 0 : decAt(paramInt1, paramArrayOfint, paramInt3, 1);
  }
  
  public static BigInteger toBigInteger(int paramInt, int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[paramInt << 2];
    for (byte b = 0; b < paramInt; b++) {
      int i = paramArrayOfint[b];
      if (i != 0)
        Pack.intToBigEndian(i, arrayOfByte, paramInt - 1 - b << 2); 
    } 
    return new BigInteger(1, arrayOfByte);
  }
  
  public static void zero(int paramInt, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfint[b] = 0; 
  }
  
  public static void zero64(int paramInt, long[] paramArrayOflong) {
    for (byte b = 0; b < paramInt; b++)
      paramArrayOflong[b] = 0L; 
  }
}
