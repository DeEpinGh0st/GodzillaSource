package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.math.RoundingMode;















































@GwtCompatible(emulated = true)
public final class IntMath
{
  @VisibleForTesting
  static final int MAX_SIGNED_POWER_OF_TWO = 1073741824;
  @VisibleForTesting
  static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
  
  @Beta
  public static int ceilingPowerOfTwo(int x) {
    MathPreconditions.checkPositive("x", x);
    if (x > 1073741824) {
      throw new ArithmeticException("ceilingPowerOfTwo(" + x + ") not representable as an int");
    }
    return 1 << -Integer.numberOfLeadingZeros(x - 1);
  }







  
  @Beta
  public static int floorPowerOfTwo(int x) {
    MathPreconditions.checkPositive("x", x);
    return Integer.highestOneBit(x);
  }






  
  public static boolean isPowerOfTwo(int x) {
    return ((x > 0)) & (((x & x - 1) == 0));
  }







  
  @VisibleForTesting
  static int lessThanBranchFree(int x, int y) {
    return (x - y ^ 0xFFFFFFFF ^ 0xFFFFFFFF) >>> 31;
  }








  
  public static int log2(int x, RoundingMode mode) {
    int leadingZeros, cmp, logFloor;
    MathPreconditions.checkPositive("x", x);
    switch (mode) {
      case UNNECESSARY:
        MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
      
      case DOWN:
      case FLOOR:
        return 31 - Integer.numberOfLeadingZeros(x);
      
      case UP:
      case CEILING:
        return 32 - Integer.numberOfLeadingZeros(x - 1);

      
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
        leadingZeros = Integer.numberOfLeadingZeros(x);
        cmp = -1257966797 >>> leadingZeros;
        
        logFloor = 31 - leadingZeros;
        return logFloor + lessThanBranchFree(cmp, x);
    } 
    
    throw new AssertionError();
  }












  
  @GwtIncompatible
  public static int log10(int x, RoundingMode mode) {
    MathPreconditions.checkPositive("x", x);
    int logFloor = log10Floor(x);
    int floorPow = powersOf10[logFloor];
    switch (mode) {
      case UNNECESSARY:
        MathPreconditions.checkRoundingUnnecessary((x == floorPow));
      
      case DOWN:
      case FLOOR:
        return logFloor;
      case UP:
      case CEILING:
        return logFloor + lessThanBranchFree(floorPow, x);
      
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
        return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
    } 
    throw new AssertionError();
  }








  
  private static int log10Floor(int x) {
    int y = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(x)];



    
    return y - lessThanBranchFree(x, powersOf10[y]);
  }

  
  @VisibleForTesting
  static final byte[] maxLog10ForLeadingZeros = new byte[] { 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0 };



  
  @VisibleForTesting
  static final int[] powersOf10 = new int[] { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };



  
  @VisibleForTesting
  static final int[] halfPowersOf10 = new int[] { 3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE };




  
  @VisibleForTesting
  static final int FLOOR_SQRT_MAX_INT = 46340;




  
  @GwtIncompatible
  public static int pow(int b, int k) {
    MathPreconditions.checkNonNegative("exponent", k);
    switch (b) {
      case 0:
        return (k == 0) ? 1 : 0;
      case 1:
        return 1;
      case -1:
        return ((k & 0x1) == 0) ? 1 : -1;
      case 2:
        return (k < 32) ? (1 << k) : 0;
      case -2:
        if (k < 32) {
          return ((k & 0x1) == 0) ? (1 << k) : -(1 << k);
        }
        return 0;
    } 


    
    for (int accum = 1;; k >>= 1) {
      switch (k) {
        case 0:
          return accum;
        case 1:
          return b * accum;
      } 
      accum *= ((k & 0x1) == 0) ? 1 : b;
      b *= b;
    } 
  }








  
  @GwtIncompatible
  public static int sqrt(int x, RoundingMode mode) {
    int halfSquare;
    MathPreconditions.checkNonNegative("x", x);
    int sqrtFloor = sqrtFloor(x);
    switch (mode) {
      case UNNECESSARY:
        MathPreconditions.checkRoundingUnnecessary((sqrtFloor * sqrtFloor == x));
      case DOWN:
      case FLOOR:
        return sqrtFloor;
      case UP:
      case CEILING:
        return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
        halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;










        
        return sqrtFloor + lessThanBranchFree(halfSquare, x);
    } 
    throw new AssertionError();
  }



  
  private static int sqrtFloor(int x) {
    return (int)Math.sqrt(x);
  }







































  
  public static int divide(int p, int q, RoundingMode mode) {
    // Byte code:
    //   0: aload_2
    //   1: invokestatic checkNotNull : (Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: iload_1
    //   6: ifne -> 19
    //   9: new java/lang/ArithmeticException
    //   12: dup
    //   13: ldc '/ by zero'
    //   15: invokespecial <init> : (Ljava/lang/String;)V
    //   18: athrow
    //   19: iload_0
    //   20: iload_1
    //   21: idiv
    //   22: istore_3
    //   23: iload_0
    //   24: iload_1
    //   25: iload_3
    //   26: imul
    //   27: isub
    //   28: istore #4
    //   30: iload #4
    //   32: ifne -> 37
    //   35: iload_3
    //   36: ireturn
    //   37: iconst_1
    //   38: iload_0
    //   39: iload_1
    //   40: ixor
    //   41: bipush #31
    //   43: ishr
    //   44: ior
    //   45: istore #5
    //   47: getstatic com/google/common/math/IntMath$1.$SwitchMap$java$math$RoundingMode : [I
    //   50: aload_2
    //   51: invokevirtual ordinal : ()I
    //   54: iaload
    //   55: tableswitch default -> 238, 1 -> 100, 2 -> 113, 3 -> 140, 4 -> 119, 5 -> 125, 6 -> 155, 7 -> 155, 8 -> 155
    //   100: iload #4
    //   102: ifne -> 109
    //   105: iconst_1
    //   106: goto -> 110
    //   109: iconst_0
    //   110: invokestatic checkRoundingUnnecessary : (Z)V
    //   113: iconst_0
    //   114: istore #6
    //   116: goto -> 246
    //   119: iconst_1
    //   120: istore #6
    //   122: goto -> 246
    //   125: iload #5
    //   127: ifle -> 134
    //   130: iconst_1
    //   131: goto -> 135
    //   134: iconst_0
    //   135: istore #6
    //   137: goto -> 246
    //   140: iload #5
    //   142: ifge -> 149
    //   145: iconst_1
    //   146: goto -> 150
    //   149: iconst_0
    //   150: istore #6
    //   152: goto -> 246
    //   155: iload #4
    //   157: invokestatic abs : (I)I
    //   160: istore #7
    //   162: iload #7
    //   164: iload_1
    //   165: invokestatic abs : (I)I
    //   168: iload #7
    //   170: isub
    //   171: isub
    //   172: istore #8
    //   174: iload #8
    //   176: ifne -> 223
    //   179: aload_2
    //   180: getstatic java/math/RoundingMode.HALF_UP : Ljava/math/RoundingMode;
    //   183: if_acmpeq -> 213
    //   186: aload_2
    //   187: getstatic java/math/RoundingMode.HALF_EVEN : Ljava/math/RoundingMode;
    //   190: if_acmpne -> 197
    //   193: iconst_1
    //   194: goto -> 198
    //   197: iconst_0
    //   198: iload_3
    //   199: iconst_1
    //   200: iand
    //   201: ifeq -> 208
    //   204: iconst_1
    //   205: goto -> 209
    //   208: iconst_0
    //   209: iand
    //   210: ifeq -> 217
    //   213: iconst_1
    //   214: goto -> 218
    //   217: iconst_0
    //   218: istore #6
    //   220: goto -> 246
    //   223: iload #8
    //   225: ifle -> 232
    //   228: iconst_1
    //   229: goto -> 233
    //   232: iconst_0
    //   233: istore #6
    //   235: goto -> 246
    //   238: new java/lang/AssertionError
    //   241: dup
    //   242: invokespecial <init> : ()V
    //   245: athrow
    //   246: iload #6
    //   248: ifeq -> 258
    //   251: iload_3
    //   252: iload #5
    //   254: iadd
    //   255: goto -> 259
    //   258: iload_3
    //   259: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #317	-> 0
    //   #318	-> 5
    //   #319	-> 9
    //   #321	-> 19
    //   #322	-> 23
    //   #324	-> 30
    //   #325	-> 35
    //   #335	-> 37
    //   #337	-> 47
    //   #339	-> 100
    //   #342	-> 113
    //   #343	-> 116
    //   #345	-> 119
    //   #346	-> 122
    //   #348	-> 125
    //   #349	-> 137
    //   #351	-> 140
    //   #352	-> 152
    //   #356	-> 155
    //   #357	-> 162
    //   #360	-> 174
    //   #361	-> 179
    //   #363	-> 223
    //   #365	-> 235
    //   #367	-> 238
    //   #369	-> 246
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   116	3	6	increment	Z
    //   122	3	6	increment	Z
    //   137	3	6	increment	Z
    //   152	3	6	increment	Z
    //   220	3	6	increment	Z
    //   235	3	6	increment	Z
    //   162	76	7	absRem	I
    //   174	64	8	cmpRemToHalfDivisor	I
    //   0	260	0	p	I
    //   0	260	1	q	I
    //   0	260	2	mode	Ljava/math/RoundingMode;
    //   23	237	3	div	I
    //   30	230	4	rem	I
    //   47	213	5	signum	I
    //   246	14	6	increment	Z
  }







































  
  public static int mod(int x, int m) {
    if (m <= 0) {
      throw new ArithmeticException("Modulus " + m + " must be > 0");
    }
    int result = x % m;
    return (result >= 0) ? result : (result + m);
  }











  
  public static int gcd(int a, int b) {
    MathPreconditions.checkNonNegative("a", a);
    MathPreconditions.checkNonNegative("b", b);
    if (a == 0)
    {
      
      return b; } 
    if (b == 0) {
      return a;
    }



    
    int aTwos = Integer.numberOfTrailingZeros(a);
    a >>= aTwos;
    int bTwos = Integer.numberOfTrailingZeros(b);
    b >>= bTwos;
    while (a != b) {






      
      int delta = a - b;
      
      int minDeltaOrZero = delta & delta >> 31;

      
      a = delta - minDeltaOrZero - minDeltaOrZero;

      
      b += minDeltaOrZero;
      a >>= Integer.numberOfTrailingZeros(a);
    } 
    return a << Math.min(aTwos, bTwos);
  }





  
  public static int checkedAdd(int a, int b) {
    long result = a + b;
    MathPreconditions.checkNoOverflow((result == (int)result), "checkedAdd", a, b);
    return (int)result;
  }





  
  public static int checkedSubtract(int a, int b) {
    long result = a - b;
    MathPreconditions.checkNoOverflow((result == (int)result), "checkedSubtract", a, b);
    return (int)result;
  }





  
  public static int checkedMultiply(int a, int b) {
    long result = a * b;
    MathPreconditions.checkNoOverflow((result == (int)result), "checkedMultiply", a, b);
    return (int)result;
  }








  
  public static int checkedPow(int b, int k) {
    MathPreconditions.checkNonNegative("exponent", k);
    switch (b) {
      case 0:
        return (k == 0) ? 1 : 0;
      case 1:
        return 1;
      case -1:
        return ((k & 0x1) == 0) ? 1 : -1;
      case 2:
        MathPreconditions.checkNoOverflow((k < 31), "checkedPow", b, k);
        return 1 << k;
      case -2:
        MathPreconditions.checkNoOverflow((k < 32), "checkedPow", b, k);
        return ((k & 0x1) == 0) ? (1 << k) : (-1 << k);
    } 

    
    int accum = 1;
    while (true) {
      switch (k) {
        case 0:
          return accum;
        case 1:
          return checkedMultiply(accum, b);
      } 
      if ((k & 0x1) != 0) {
        accum = checkedMultiply(accum, b);
      }
      k >>= 1;
      if (k > 0) {
        MathPreconditions.checkNoOverflow(((-46340 <= b)) & ((b <= 46340)), "checkedPow", b, k);
        b *= b;
      } 
    } 
  }







  
  @Beta
  public static int saturatedAdd(int a, int b) {
    return Ints.saturatedCast(a + b);
  }






  
  @Beta
  public static int saturatedSubtract(int a, int b) {
    return Ints.saturatedCast(a - b);
  }






  
  @Beta
  public static int saturatedMultiply(int a, int b) {
    return Ints.saturatedCast(a * b);
  }






  
  @Beta
  public static int saturatedPow(int b, int k) {
    MathPreconditions.checkNonNegative("exponent", k);
    switch (b) {
      case 0:
        return (k == 0) ? 1 : 0;
      case 1:
        return 1;
      case -1:
        return ((k & 0x1) == 0) ? 1 : -1;
      case 2:
        if (k >= 31) {
          return Integer.MAX_VALUE;
        }
        return 1 << k;
      case -2:
        if (k >= 32) {
          return Integer.MAX_VALUE + (k & 0x1);
        }
        return ((k & 0x1) == 0) ? (1 << k) : (-1 << k);
    } 

    
    int accum = 1;
    
    int limit = Integer.MAX_VALUE + (b >>> 31 & k & 0x1);
    while (true) {
      switch (k) {
        case 0:
          return accum;
        case 1:
          return saturatedMultiply(accum, b);
      } 
      if ((k & 0x1) != 0) {
        accum = saturatedMultiply(accum, b);
      }
      k >>= 1;
      if (k > 0) {
        if ((((-46340 > b) ? 1 : 0) | ((b > 46340) ? 1 : 0)) != 0) {
          return limit;
        }
        b *= b;
      } 
    } 
  }









  
  public static int factorial(int n) {
    MathPreconditions.checkNonNegative("n", n);
    return (n < factorials.length) ? factorials[n] : Integer.MAX_VALUE;
  }
  
  private static final int[] factorials = new int[] { 1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600 };




















  
  public static int binomial(int n, int k) {
    MathPreconditions.checkNonNegative("n", n);
    MathPreconditions.checkNonNegative("k", k);
    Preconditions.checkArgument((k <= n), "k (%s) > n (%s)", k, n);
    if (k > n >> 1) {
      k = n - k;
    }
    if (k >= biggestBinomials.length || n > biggestBinomials[k]) {
      return Integer.MAX_VALUE;
    }
    switch (k) {
      case 0:
        return 1;
      case 1:
        return n;
    } 
    long result = 1L;
    for (int i = 0; i < k; i++) {
      result *= (n - i);
      result /= (i + 1);
    } 
    return (int)result;
  }


  
  @VisibleForTesting
  static int[] biggestBinomials = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33 };



























  
  public static int mean(int x, int y) {
    return (x & y) + ((x ^ y) >> 1);
  }












  
  @GwtIncompatible
  @Beta
  public static boolean isPrime(int n) {
    return LongMath.isPrime(n);
  }
}
