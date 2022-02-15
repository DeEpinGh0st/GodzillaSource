package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat128 {
  private static final long M = 4294967295L;
  
  public static int add(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    l += (paramArrayOfint1[0] & 0xFFFFFFFFL) + (paramArrayOfint2[0] & 0xFFFFFFFFL);
    paramArrayOfint3[0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[1] & 0xFFFFFFFFL) + (paramArrayOfint2[1] & 0xFFFFFFFFL);
    paramArrayOfint3[1] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[2] & 0xFFFFFFFFL) + (paramArrayOfint2[2] & 0xFFFFFFFFL);
    paramArrayOfint3[2] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[3] & 0xFFFFFFFFL) + (paramArrayOfint2[3] & 0xFFFFFFFFL);
    paramArrayOfint3[3] = (int)l;
    l >>>= 32L;
    return (int)l;
  }
  
  public static int addBothTo(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    l += (paramArrayOfint1[0] & 0xFFFFFFFFL) + (paramArrayOfint2[0] & 0xFFFFFFFFL) + (paramArrayOfint3[0] & 0xFFFFFFFFL);
    paramArrayOfint3[0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[1] & 0xFFFFFFFFL) + (paramArrayOfint2[1] & 0xFFFFFFFFL) + (paramArrayOfint3[1] & 0xFFFFFFFFL);
    paramArrayOfint3[1] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[2] & 0xFFFFFFFFL) + (paramArrayOfint2[2] & 0xFFFFFFFFL) + (paramArrayOfint3[2] & 0xFFFFFFFFL);
    paramArrayOfint3[2] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[3] & 0xFFFFFFFFL) + (paramArrayOfint2[3] & 0xFFFFFFFFL) + (paramArrayOfint3[3] & 0xFFFFFFFFL);
    paramArrayOfint3[3] = (int)l;
    l >>>= 32L;
    return (int)l;
  }
  
  public static int addTo(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = 0L;
    l += (paramArrayOfint1[0] & 0xFFFFFFFFL) + (paramArrayOfint2[0] & 0xFFFFFFFFL);
    paramArrayOfint2[0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[1] & 0xFFFFFFFFL) + (paramArrayOfint2[1] & 0xFFFFFFFFL);
    paramArrayOfint2[1] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[2] & 0xFFFFFFFFL) + (paramArrayOfint2[2] & 0xFFFFFFFFL);
    paramArrayOfint2[2] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[3] & 0xFFFFFFFFL) + (paramArrayOfint2[3] & 0xFFFFFFFFL);
    paramArrayOfint2[3] = (int)l;
    l >>>= 32L;
    return (int)l;
  }
  
  public static int addTo(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2, int paramInt3) {
    long l = paramInt3 & 0xFFFFFFFFL;
    l += (paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 1] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 1] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 2] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 2] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 3] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 3] = (int)l;
    l >>>= 32L;
    return (int)l;
  }
  
  public static int addToEachOther(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2) {
    long l = 0L;
    l += (paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL);
    paramArrayOfint1[paramInt1 + 0] = (int)l;
    paramArrayOfint2[paramInt2 + 0] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 1] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL);
    paramArrayOfint1[paramInt1 + 1] = (int)l;
    paramArrayOfint2[paramInt2 + 1] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 2] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL);
    paramArrayOfint1[paramInt1 + 2] = (int)l;
    paramArrayOfint2[paramInt2 + 2] = (int)l;
    l >>>= 32L;
    l += (paramArrayOfint1[paramInt1 + 3] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL);
    paramArrayOfint1[paramInt1 + 3] = (int)l;
    paramArrayOfint2[paramInt2 + 3] = (int)l;
    l >>>= 32L;
    return (int)l;
  }
  
  public static void copy(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    paramArrayOfint2[0] = paramArrayOfint1[0];
    paramArrayOfint2[1] = paramArrayOfint1[1];
    paramArrayOfint2[2] = paramArrayOfint1[2];
    paramArrayOfint2[3] = paramArrayOfint1[3];
  }
  
  public static void copy64(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    paramArrayOflong2[0] = paramArrayOflong1[0];
    paramArrayOflong2[1] = paramArrayOflong1[1];
  }
  
  public static int[] create() {
    return new int[4];
  }
  
  public static long[] create64() {
    return new long[2];
  }
  
  public static int[] createExt() {
    return new int[8];
  }
  
  public static long[] createExt64() {
    return new long[4];
  }
  
  public static boolean diff(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2, int[] paramArrayOfint3, int paramInt3) {
    boolean bool = gte(paramArrayOfint1, paramInt1, paramArrayOfint2, paramInt2);
    if (bool) {
      sub(paramArrayOfint1, paramInt1, paramArrayOfint2, paramInt2, paramArrayOfint3, paramInt3);
    } else {
      sub(paramArrayOfint2, paramInt2, paramArrayOfint1, paramInt1, paramArrayOfint3, paramInt3);
    } 
    return bool;
  }
  
  public static boolean eq(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (byte b = 3; b >= 0; b--) {
      if (paramArrayOfint1[b] != paramArrayOfint2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean eq64(long[] paramArrayOflong1, long[] paramArrayOflong2) {
    for (byte b = 1; b; b--) {
      if (paramArrayOflong1[b] != paramArrayOflong2[b])
        return false; 
    } 
    return true;
  }
  
  public static int[] fromBigInteger(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 128)
      throw new IllegalArgumentException(); 
    int[] arrayOfInt = create();
    byte b = 0;
    while (paramBigInteger.signum() != 0) {
      arrayOfInt[b++] = paramBigInteger.intValue();
      paramBigInteger = paramBigInteger.shiftRight(32);
    } 
    return arrayOfInt;
  }
  
  public static long[] fromBigInteger64(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 128)
      throw new IllegalArgumentException(); 
    long[] arrayOfLong = create64();
    byte b = 0;
    while (paramBigInteger.signum() != 0) {
      arrayOfLong[b++] = paramBigInteger.longValue();
      paramBigInteger = paramBigInteger.shiftRight(64);
    } 
    return arrayOfLong;
  }
  
  public static int getBit(int[] paramArrayOfint, int paramInt) {
    if (paramInt == 0)
      return paramArrayOfint[0] & 0x1; 
    int i = paramInt >> 5;
    if (i < 0 || i >= 4)
      return 0; 
    int j = paramInt & 0x1F;
    return paramArrayOfint[i] >>> j & 0x1;
  }
  
  public static boolean gte(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (byte b = 3; b >= 0; b--) {
      int i = paramArrayOfint1[b] ^ Integer.MIN_VALUE;
      int j = paramArrayOfint2[b] ^ Integer.MIN_VALUE;
      if (i < j)
        return false; 
      if (i > j)
        return true; 
    } 
    return true;
  }
  
  public static boolean gte(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2) {
    for (byte b = 3; b >= 0; b--) {
      int i = paramArrayOfint1[paramInt1 + b] ^ Integer.MIN_VALUE;
      int j = paramArrayOfint2[paramInt2 + b] ^ Integer.MIN_VALUE;
      if (i < j)
        return false; 
      if (i > j)
        return true; 
    } 
    return true;
  }
  
  public static boolean isOne(int[] paramArrayOfint) {
    if (paramArrayOfint[0] != 1)
      return false; 
    for (byte b = 1; b < 4; b++) {
      if (paramArrayOfint[b] != 0)
        return false; 
    } 
    return true;
  }
  
  public static boolean isOne64(long[] paramArrayOflong) {
    if (paramArrayOflong[0] != 1L)
      return false; 
    for (byte b = 1; b < 2; b++) {
      if (paramArrayOflong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public static boolean isZero(int[] paramArrayOfint) {
    for (byte b = 0; b < 4; b++) {
      if (paramArrayOfint[b] != 0)
        return false; 
    } 
    return true;
  }
  
  public static boolean isZero64(long[] paramArrayOflong) {
    for (byte b = 0; b < 2; b++) {
      if (paramArrayOflong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public static void mul(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l1 = paramArrayOfint2[0] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint2[1] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint2[2] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint2[3] & 0xFFFFFFFFL;
    long l5 = 0L;
    long l6 = paramArrayOfint1[0] & 0xFFFFFFFFL;
    l5 += l6 * l1;
    paramArrayOfint3[0] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l2;
    paramArrayOfint3[1] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l3;
    paramArrayOfint3[2] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l4;
    paramArrayOfint3[3] = (int)l5;
    l5 >>>= 32L;
    paramArrayOfint3[4] = (int)l5;
    for (byte b = 1; b < 4; b++) {
      long l7 = 0L;
      long l8 = paramArrayOfint1[b] & 0xFFFFFFFFL;
      l7 += l8 * l1 + (paramArrayOfint3[b + 0] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 0] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l2 + (paramArrayOfint3[b + 1] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 1] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l3 + (paramArrayOfint3[b + 2] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 2] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l4 + (paramArrayOfint3[b + 3] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 3] = (int)l7;
      l7 >>>= 32L;
      paramArrayOfint3[b + 4] = (int)l7;
    } 
  }
  
  public static void mul(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2, int[] paramArrayOfint3, int paramInt3) {
    long l1 = paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL;
    long l5 = 0L;
    long l6 = paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL;
    l5 += l6 * l1;
    paramArrayOfint3[paramInt3 + 0] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l2;
    paramArrayOfint3[paramInt3 + 1] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l3;
    paramArrayOfint3[paramInt3 + 2] = (int)l5;
    l5 >>>= 32L;
    l5 += l6 * l4;
    paramArrayOfint3[paramInt3 + 3] = (int)l5;
    l5 >>>= 32L;
    paramArrayOfint3[paramInt3 + 4] = (int)l5;
    for (byte b = 1; b < 4; b++) {
      paramInt3++;
      long l7 = 0L;
      long l8 = paramArrayOfint1[paramInt1 + b] & 0xFFFFFFFFL;
      l7 += l8 * l1 + (paramArrayOfint3[paramInt3 + 0] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 0] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l2 + (paramArrayOfint3[paramInt3 + 1] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 1] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l3 + (paramArrayOfint3[paramInt3 + 2] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 2] = (int)l7;
      l7 >>>= 32L;
      l7 += l8 * l4 + (paramArrayOfint3[paramInt3 + 3] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 3] = (int)l7;
      l7 >>>= 32L;
      paramArrayOfint3[paramInt3 + 4] = (int)l7;
    } 
  }
  
  public static int mulAddTo(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l1 = paramArrayOfint2[0] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint2[1] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint2[2] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint2[3] & 0xFFFFFFFFL;
    long l5 = 0L;
    for (byte b = 0; b < 4; b++) {
      long l6 = 0L;
      long l7 = paramArrayOfint1[b] & 0xFFFFFFFFL;
      l6 += l7 * l1 + (paramArrayOfint3[b + 0] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 0] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l2 + (paramArrayOfint3[b + 1] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 1] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l3 + (paramArrayOfint3[b + 2] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 2] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l4 + (paramArrayOfint3[b + 3] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 3] = (int)l6;
      l6 >>>= 32L;
      l6 += l5 + (paramArrayOfint3[b + 4] & 0xFFFFFFFFL);
      paramArrayOfint3[b + 4] = (int)l6;
      l5 = l6 >>> 32L;
    } 
    return (int)l5;
  }
  
  public static int mulAddTo(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2, int[] paramArrayOfint3, int paramInt3) {
    long l1 = paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL;
    long l2 = paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL;
    long l3 = paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL;
    long l4 = paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL;
    long l5 = 0L;
    for (byte b = 0; b < 4; b++) {
      long l6 = 0L;
      long l7 = paramArrayOfint1[paramInt1 + b] & 0xFFFFFFFFL;
      l6 += l7 * l1 + (paramArrayOfint3[paramInt3 + 0] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 0] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l2 + (paramArrayOfint3[paramInt3 + 1] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 1] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l3 + (paramArrayOfint3[paramInt3 + 2] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 2] = (int)l6;
      l6 >>>= 32L;
      l6 += l7 * l4 + (paramArrayOfint3[paramInt3 + 3] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 3] = (int)l6;
      l6 >>>= 32L;
      l6 += l5 + (paramArrayOfint3[paramInt3 + 4] & 0xFFFFFFFFL);
      paramArrayOfint3[paramInt3 + 4] = (int)l6;
      l5 = l6 >>> 32L;
      paramInt3++;
    } 
    return (int)l5;
  }
  
  public static long mul33Add(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int[] paramArrayOfint3, int paramInt4) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    long l3 = paramArrayOfint1[paramInt2 + 0] & 0xFFFFFFFFL;
    l1 += l2 * l3 + (paramArrayOfint2[paramInt3 + 0] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt4 + 0] = (int)l1;
    l1 >>>= 32L;
    long l4 = paramArrayOfint1[paramInt2 + 1] & 0xFFFFFFFFL;
    l1 += l2 * l4 + l3 + (paramArrayOfint2[paramInt3 + 1] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt4 + 1] = (int)l1;
    l1 >>>= 32L;
    long l5 = paramArrayOfint1[paramInt2 + 2] & 0xFFFFFFFFL;
    l1 += l2 * l5 + l4 + (paramArrayOfint2[paramInt3 + 2] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt4 + 2] = (int)l1;
    l1 >>>= 32L;
    long l6 = paramArrayOfint1[paramInt2 + 3] & 0xFFFFFFFFL;
    l1 += l2 * l6 + l5 + (paramArrayOfint2[paramInt3 + 3] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt4 + 3] = (int)l1;
    l1 >>>= 32L;
    l1 += l6;
    return l1;
  }
  
  public static int mulWordAddExt(int paramInt1, int[] paramArrayOfint1, int paramInt2, int[] paramArrayOfint2, int paramInt3) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    l1 += l2 * (paramArrayOfint1[paramInt2 + 0] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + 0] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt3 + 0] = (int)l1;
    l1 >>>= 32L;
    l1 += l2 * (paramArrayOfint1[paramInt2 + 1] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + 1] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt3 + 1] = (int)l1;
    l1 >>>= 32L;
    l1 += l2 * (paramArrayOfint1[paramInt2 + 2] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + 2] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt3 + 2] = (int)l1;
    l1 >>>= 32L;
    l1 += l2 * (paramArrayOfint1[paramInt2 + 3] & 0xFFFFFFFFL) + (paramArrayOfint2[paramInt3 + 3] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt3 + 3] = (int)l1;
    l1 >>>= 32L;
    return (int)l1;
  }
  
  public static int mul33DWordAdd(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    long l3 = paramLong & 0xFFFFFFFFL;
    l1 += l2 * l3 + (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l1;
    l1 >>>= 32L;
    long l4 = paramLong >>> 32L;
    l1 += l2 * l4 + l3 + (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 1] = (int)l1;
    l1 >>>= 32L;
    l1 += l4 + (paramArrayOfint[paramInt2 + 2] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 2] = (int)l1;
    l1 >>>= 32L;
    l1 += paramArrayOfint[paramInt2 + 3] & 0xFFFFFFFFL;
    paramArrayOfint[paramInt2 + 3] = (int)l1;
    l1 >>>= 32L;
    return (int)l1;
  }
  
  public static int mul33WordAdd(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    long l3 = paramInt2 & 0xFFFFFFFFL;
    l1 += l3 * l2 + (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l1;
    l1 >>>= 32L;
    l1 += l3 + (paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 1] = (int)l1;
    l1 >>>= 32L;
    l1 += paramArrayOfint[paramInt3 + 2] & 0xFFFFFFFFL;
    paramArrayOfint[paramInt3 + 2] = (int)l1;
    l1 >>>= 32L;
    return (l1 == 0L) ? 0 : Nat.incAt(4, paramArrayOfint, paramInt3, 3);
  }
  
  public static int mulWordDwordAdd(int paramInt1, long paramLong, int[] paramArrayOfint, int paramInt2) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    l1 += l2 * (paramLong & 0xFFFFFFFFL) + (paramArrayOfint[paramInt2 + 0] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 0] = (int)l1;
    l1 >>>= 32L;
    l1 += l2 * (paramLong >>> 32L) + (paramArrayOfint[paramInt2 + 1] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt2 + 1] = (int)l1;
    l1 >>>= 32L;
    l1 += paramArrayOfint[paramInt2 + 2] & 0xFFFFFFFFL;
    paramArrayOfint[paramInt2 + 2] = (int)l1;
    l1 >>>= 32L;
    return (l1 == 0L) ? 0 : Nat.incAt(4, paramArrayOfint, paramInt2, 3);
  }
  
  public static int mulWordsAdd(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    long l3 = paramInt2 & 0xFFFFFFFFL;
    l1 += l3 * l2 + (paramArrayOfint[paramInt3 + 0] & 0xFFFFFFFFL);
    paramArrayOfint[paramInt3 + 0] = (int)l1;
    l1 >>>= 32L;
    l1 += paramArrayOfint[paramInt3 + 1] & 0xFFFFFFFFL;
    paramArrayOfint[paramInt3 + 1] = (int)l1;
    l1 >>>= 32L;
    return (l1 == 0L) ? 0 : Nat.incAt(4, paramArrayOfint, paramInt3, 2);
  }
  
  public static int mulWord(int paramInt1, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt2) {
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    byte b = 0;
    while (true) {
      l1 += l2 * (paramArrayOfint1[b] & 0xFFFFFFFFL);
      paramArrayOfint2[paramInt2 + b] = (int)l1;
      l1 >>>= 32L;
      if (++b >= 4)
        return (int)l1; 
    } 
  }
  
  public static void square(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = paramArrayOfint1[0] & 0xFFFFFFFFL;
    int i = 0;
    byte b1 = 3;
    byte b2 = 8;
    while (true) {
      long l1 = paramArrayOfint1[b1--] & 0xFFFFFFFFL;
      long l2 = l1 * l1;
      paramArrayOfint2[--b2] = i << 31 | (int)(l2 >>> 33L);
      paramArrayOfint2[--b2] = (int)(l2 >>> 1L);
      i = (int)l2;
      if (b1 <= 0) {
        l1 = l * l;
        long l3 = (i << 31) & 0xFFFFFFFFL | l1 >>> 33L;
        paramArrayOfint2[0] = (int)l1;
        i = (int)(l1 >>> 32L) & 0x1;
        long l4 = paramArrayOfint1[1] & 0xFFFFFFFFL;
        l1 = paramArrayOfint2[2] & 0xFFFFFFFFL;
        l3 += l4 * l;
        int j = (int)l3;
        paramArrayOfint2[1] = j << 1 | i;
        i = j >>> 31;
        l1 += l3 >>> 32L;
        l2 = paramArrayOfint1[2] & 0xFFFFFFFFL;
        long l5 = paramArrayOfint2[3] & 0xFFFFFFFFL;
        long l6 = paramArrayOfint2[4] & 0xFFFFFFFFL;
        l1 += l2 * l;
        j = (int)l1;
        paramArrayOfint2[2] = j << 1 | i;
        i = j >>> 31;
        l5 += (l1 >>> 32L) + l2 * l4;
        l6 += l5 >>> 32L;
        l5 &= 0xFFFFFFFFL;
        long l7 = paramArrayOfint1[3] & 0xFFFFFFFFL;
        long l8 = (paramArrayOfint2[5] & 0xFFFFFFFFL) + (l6 >>> 32L);
        l6 &= 0xFFFFFFFFL;
        long l9 = (paramArrayOfint2[6] & 0xFFFFFFFFL) + (l8 >>> 32L);
        l8 &= 0xFFFFFFFFL;
        l5 += l7 * l;
        j = (int)l5;
        paramArrayOfint2[3] = j << 1 | i;
        i = j >>> 31;
        l6 += (l5 >>> 32L) + l7 * l4;
        l8 += (l6 >>> 32L) + l7 * l2;
        l9 += l8 >>> 32L;
        l8 &= 0xFFFFFFFFL;
        j = (int)l6;
        paramArrayOfint2[4] = j << 1 | i;
        i = j >>> 31;
        j = (int)l8;
        paramArrayOfint2[5] = j << 1 | i;
        i = j >>> 31;
        j = (int)l9;
        paramArrayOfint2[6] = j << 1 | i;
        i = j >>> 31;
        j = paramArrayOfint2[7] + (int)(l9 >>> 32L);
        paramArrayOfint2[7] = j << 1 | i;
        return;
      } 
    } 
  }
  
  public static void square(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2) {
    long l = paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL;
    int i = 0;
    byte b1 = 3;
    byte b2 = 8;
    while (true) {
      long l1 = paramArrayOfint1[paramInt1 + b1--] & 0xFFFFFFFFL;
      long l2 = l1 * l1;
      paramArrayOfint2[paramInt2 + --b2] = i << 31 | (int)(l2 >>> 33L);
      paramArrayOfint2[paramInt2 + --b2] = (int)(l2 >>> 1L);
      i = (int)l2;
      if (b1 <= 0) {
        l1 = l * l;
        long l3 = (i << 31) & 0xFFFFFFFFL | l1 >>> 33L;
        paramArrayOfint2[paramInt2 + 0] = (int)l1;
        i = (int)(l1 >>> 32L) & 0x1;
        long l4 = paramArrayOfint1[paramInt1 + 1] & 0xFFFFFFFFL;
        l1 = paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL;
        l3 += l4 * l;
        int j = (int)l3;
        paramArrayOfint2[paramInt2 + 1] = j << 1 | i;
        i = j >>> 31;
        l1 += l3 >>> 32L;
        l2 = paramArrayOfint1[paramInt1 + 2] & 0xFFFFFFFFL;
        long l5 = paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL;
        long l6 = paramArrayOfint2[paramInt2 + 4] & 0xFFFFFFFFL;
        l1 += l2 * l;
        j = (int)l1;
        paramArrayOfint2[paramInt2 + 2] = j << 1 | i;
        i = j >>> 31;
        l5 += (l1 >>> 32L) + l2 * l4;
        l6 += l5 >>> 32L;
        l5 &= 0xFFFFFFFFL;
        long l7 = paramArrayOfint1[paramInt1 + 3] & 0xFFFFFFFFL;
        long l8 = (paramArrayOfint2[paramInt2 + 5] & 0xFFFFFFFFL) + (l6 >>> 32L);
        l6 &= 0xFFFFFFFFL;
        long l9 = (paramArrayOfint2[paramInt2 + 6] & 0xFFFFFFFFL) + (l8 >>> 32L);
        l8 &= 0xFFFFFFFFL;
        l5 += l7 * l;
        j = (int)l5;
        paramArrayOfint2[paramInt2 + 3] = j << 1 | i;
        i = j >>> 31;
        l6 += (l5 >>> 32L) + l7 * l4;
        l8 += (l6 >>> 32L) + l7 * l2;
        l9 += l8 >>> 32L;
        j = (int)l6;
        paramArrayOfint2[paramInt2 + 4] = j << 1 | i;
        i = j >>> 31;
        j = (int)l8;
        paramArrayOfint2[paramInt2 + 5] = j << 1 | i;
        i = j >>> 31;
        j = (int)l9;
        paramArrayOfint2[paramInt2 + 6] = j << 1 | i;
        i = j >>> 31;
        j = paramArrayOfint2[paramInt2 + 7] + (int)(l9 >>> 32L);
        paramArrayOfint2[paramInt2 + 7] = j << 1 | i;
        return;
      } 
    } 
  }
  
  public static int sub(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    l += (paramArrayOfint1[0] & 0xFFFFFFFFL) - (paramArrayOfint2[0] & 0xFFFFFFFFL);
    paramArrayOfint3[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[1] & 0xFFFFFFFFL) - (paramArrayOfint2[1] & 0xFFFFFFFFL);
    paramArrayOfint3[1] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[2] & 0xFFFFFFFFL) - (paramArrayOfint2[2] & 0xFFFFFFFFL);
    paramArrayOfint3[2] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[3] & 0xFFFFFFFFL) - (paramArrayOfint2[3] & 0xFFFFFFFFL);
    paramArrayOfint3[3] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  public static int sub(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2, int[] paramArrayOfint3, int paramInt3) {
    long l = 0L;
    l += (paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt3 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[paramInt1 + 1] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt3 + 1] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[paramInt1 + 2] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt3 + 2] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint1[paramInt1 + 3] & 0xFFFFFFFFL) - (paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL);
    paramArrayOfint3[paramInt3 + 3] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  public static int subBothFrom(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    long l = 0L;
    l += (paramArrayOfint3[0] & 0xFFFFFFFFL) - (paramArrayOfint1[0] & 0xFFFFFFFFL) - (paramArrayOfint2[0] & 0xFFFFFFFFL);
    paramArrayOfint3[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint3[1] & 0xFFFFFFFFL) - (paramArrayOfint1[1] & 0xFFFFFFFFL) - (paramArrayOfint2[1] & 0xFFFFFFFFL);
    paramArrayOfint3[1] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint3[2] & 0xFFFFFFFFL) - (paramArrayOfint1[2] & 0xFFFFFFFFL) - (paramArrayOfint2[2] & 0xFFFFFFFFL);
    paramArrayOfint3[2] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint3[3] & 0xFFFFFFFFL) - (paramArrayOfint1[3] & 0xFFFFFFFFL) - (paramArrayOfint2[3] & 0xFFFFFFFFL);
    paramArrayOfint3[3] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  public static int subFrom(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    long l = 0L;
    l += (paramArrayOfint2[0] & 0xFFFFFFFFL) - (paramArrayOfint1[0] & 0xFFFFFFFFL);
    paramArrayOfint2[0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[1] & 0xFFFFFFFFL) - (paramArrayOfint1[1] & 0xFFFFFFFFL);
    paramArrayOfint2[1] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[2] & 0xFFFFFFFFL) - (paramArrayOfint1[2] & 0xFFFFFFFFL);
    paramArrayOfint2[2] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[3] & 0xFFFFFFFFL) - (paramArrayOfint1[3] & 0xFFFFFFFFL);
    paramArrayOfint2[3] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  public static int subFrom(int[] paramArrayOfint1, int paramInt1, int[] paramArrayOfint2, int paramInt2) {
    long l = 0L;
    l += (paramArrayOfint2[paramInt2 + 0] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt1 + 0] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 0] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[paramInt2 + 1] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt1 + 1] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 1] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[paramInt2 + 2] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt1 + 2] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 2] = (int)l;
    l >>= 32L;
    l += (paramArrayOfint2[paramInt2 + 3] & 0xFFFFFFFFL) - (paramArrayOfint1[paramInt1 + 3] & 0xFFFFFFFFL);
    paramArrayOfint2[paramInt2 + 3] = (int)l;
    l >>= 32L;
    return (int)l;
  }
  
  public static BigInteger toBigInteger(int[] paramArrayOfint) {
    byte[] arrayOfByte = new byte[16];
    for (byte b = 0; b < 4; b++) {
      int i = paramArrayOfint[b];
      if (i != 0)
        Pack.intToBigEndian(i, arrayOfByte, 3 - b << 2); 
    } 
    return new BigInteger(1, arrayOfByte);
  }
  
  public static BigInteger toBigInteger64(long[] paramArrayOflong) {
    byte[] arrayOfByte = new byte[16];
    for (byte b = 0; b < 2; b++) {
      long l = paramArrayOflong[b];
      if (l != 0L)
        Pack.longToBigEndian(l, arrayOfByte, 1 - b << 3); 
    } 
    return new BigInteger(1, arrayOfByte);
  }
  
  public static void zero(int[] paramArrayOfint) {
    paramArrayOfint[0] = 0;
    paramArrayOfint[1] = 0;
    paramArrayOfint[2] = 0;
    paramArrayOfint[3] = 0;
  }
}
