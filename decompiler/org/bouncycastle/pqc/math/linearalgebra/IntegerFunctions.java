package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class IntegerFunctions {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  private static final BigInteger FOUR = BigInteger.valueOf(4L);
  
  private static final int[] SMALL_PRIMES = new int[] { 
      3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 
      37, 41 };
  
  private static final long SMALL_PRIME_PRODUCT = 152125131763605L;
  
  private static SecureRandom sr = null;
  
  private static final int[] jacobiTable = new int[] { 0, 1, 0, -1, 0, -1, 0, 1 };
  
  public static int jacobi(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    long l = 1L;
    l = 1L;
    if (paramBigInteger2.equals(ZERO)) {
      BigInteger bigInteger = paramBigInteger1.abs();
      return bigInteger.equals(ONE) ? 1 : 0;
    } 
    if (!paramBigInteger1.testBit(0) && !paramBigInteger2.testBit(0))
      return 0; 
    BigInteger bigInteger1 = paramBigInteger1;
    BigInteger bigInteger2 = paramBigInteger2;
    if (bigInteger2.signum() == -1) {
      bigInteger2 = bigInteger2.negate();
      if (bigInteger1.signum() == -1)
        l = -1L; 
    } 
    BigInteger bigInteger3 = ZERO;
    while (!bigInteger2.testBit(0)) {
      bigInteger3 = bigInteger3.add(ONE);
      bigInteger2 = bigInteger2.divide(TWO);
    } 
    if (bigInteger3.testBit(0))
      l *= jacobiTable[bigInteger1.intValue() & 0x7]; 
    if (bigInteger1.signum() < 0) {
      if (bigInteger2.testBit(1))
        l = -l; 
      bigInteger1 = bigInteger1.negate();
    } 
    while (bigInteger1.signum() != 0) {
      bigInteger3 = ZERO;
      while (!bigInteger1.testBit(0)) {
        bigInteger3 = bigInteger3.add(ONE);
        bigInteger1 = bigInteger1.divide(TWO);
      } 
      if (bigInteger3.testBit(0))
        l *= jacobiTable[bigInteger2.intValue() & 0x7]; 
      if (bigInteger1.compareTo(bigInteger2) < 0) {
        BigInteger bigInteger = bigInteger1;
        bigInteger1 = bigInteger2;
        bigInteger2 = bigInteger;
        if (bigInteger1.testBit(1) && bigInteger2.testBit(1))
          l = -l; 
      } 
      bigInteger1 = bigInteger1.subtract(bigInteger2);
    } 
    return bigInteger2.equals(ONE) ? (int)l : 0;
  }
  
  public static BigInteger ressol(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws IllegalArgumentException {
    BigInteger bigInteger1 = null;
    if (paramBigInteger1.compareTo(ZERO) < 0)
      paramBigInteger1 = paramBigInteger1.add(paramBigInteger2); 
    if (paramBigInteger1.equals(ZERO))
      return ZERO; 
    if (paramBigInteger2.equals(TWO))
      return paramBigInteger1; 
    if (paramBigInteger2.testBit(0) && paramBigInteger2.testBit(1)) {
      if (jacobi(paramBigInteger1, paramBigInteger2) == 1) {
        bigInteger1 = paramBigInteger2.add(ONE);
        bigInteger1 = bigInteger1.shiftRight(2);
        return paramBigInteger1.modPow(bigInteger1, paramBigInteger2);
      } 
      throw new IllegalArgumentException("No quadratic residue: " + paramBigInteger1 + ", " + paramBigInteger2);
    } 
    long l1 = 0L;
    BigInteger bigInteger2 = paramBigInteger2.subtract(ONE);
    long l2 = 0L;
    while (!bigInteger2.testBit(0)) {
      l2++;
      bigInteger2 = bigInteger2.shiftRight(1);
    } 
    bigInteger2 = bigInteger2.subtract(ONE);
    bigInteger2 = bigInteger2.shiftRight(1);
    BigInteger bigInteger3 = paramBigInteger1.modPow(bigInteger2, paramBigInteger2);
    BigInteger bigInteger4 = bigInteger3.multiply(bigInteger3).remainder(paramBigInteger2);
    bigInteger4 = bigInteger4.multiply(paramBigInteger1).remainder(paramBigInteger2);
    bigInteger3 = bigInteger3.multiply(paramBigInteger1).remainder(paramBigInteger2);
    if (bigInteger4.equals(ONE))
      return bigInteger3; 
    BigInteger bigInteger5;
    for (bigInteger5 = TWO; jacobi(bigInteger5, paramBigInteger2) == 1; bigInteger5 = bigInteger5.add(ONE));
    bigInteger1 = bigInteger2;
    bigInteger1 = bigInteger1.multiply(TWO);
    bigInteger1 = bigInteger1.add(ONE);
    BigInteger bigInteger6 = bigInteger5.modPow(bigInteger1, paramBigInteger2);
    while (bigInteger4.compareTo(ONE) == 1) {
      bigInteger2 = bigInteger4;
      l1 = l2;
      for (l2 = 0L; !bigInteger2.equals(ONE); l2++)
        bigInteger2 = bigInteger2.multiply(bigInteger2).mod(paramBigInteger2); 
      l1 -= l2;
      if (l1 == 0L)
        throw new IllegalArgumentException("No quadratic residue: " + paramBigInteger1 + ", " + paramBigInteger2); 
      bigInteger1 = ONE;
      long l;
      for (l = 0L; l < l1 - 1L; l++)
        bigInteger1 = bigInteger1.shiftLeft(1); 
      bigInteger6 = bigInteger6.modPow(bigInteger1, paramBigInteger2);
      bigInteger3 = bigInteger3.multiply(bigInteger6).remainder(paramBigInteger2);
      bigInteger6 = bigInteger6.multiply(bigInteger6).remainder(paramBigInteger2);
      bigInteger4 = bigInteger4.multiply(bigInteger6).mod(paramBigInteger2);
    } 
    return bigInteger3;
  }
  
  public static int gcd(int paramInt1, int paramInt2) {
    return BigInteger.valueOf(paramInt1).gcd(BigInteger.valueOf(paramInt2)).intValue();
  }
  
  public static int[] extGCD(int paramInt1, int paramInt2) {
    BigInteger bigInteger1 = BigInteger.valueOf(paramInt1);
    BigInteger bigInteger2 = BigInteger.valueOf(paramInt2);
    BigInteger[] arrayOfBigInteger = extgcd(bigInteger1, bigInteger2);
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = arrayOfBigInteger[0].intValue();
    arrayOfInt[1] = arrayOfBigInteger[1].intValue();
    arrayOfInt[2] = arrayOfBigInteger[2].intValue();
    return arrayOfInt;
  }
  
  public static BigInteger divideAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return (paramBigInteger1.signum() < 0) ? divideAndRound(paramBigInteger1.negate(), paramBigInteger2).negate() : ((paramBigInteger2.signum() < 0) ? divideAndRound(paramBigInteger1, paramBigInteger2.negate()).negate() : paramBigInteger1.shiftLeft(1).add(paramBigInteger2).divide(paramBigInteger2.shiftLeft(1)));
  }
  
  public static BigInteger[] divideAndRound(BigInteger[] paramArrayOfBigInteger, BigInteger paramBigInteger) {
    BigInteger[] arrayOfBigInteger = new BigInteger[paramArrayOfBigInteger.length];
    for (byte b = 0; b < paramArrayOfBigInteger.length; b++)
      arrayOfBigInteger[b] = divideAndRound(paramArrayOfBigInteger[b], paramBigInteger); 
    return arrayOfBigInteger;
  }
  
  public static int ceilLog(BigInteger paramBigInteger) {
    byte b = 0;
    for (BigInteger bigInteger = ONE; bigInteger.compareTo(paramBigInteger) < 0; bigInteger = bigInteger.shiftLeft(1))
      b++; 
    return b;
  }
  
  public static int ceilLog(int paramInt) {
    byte b = 0;
    int i = 1;
    while (i < paramInt) {
      i <<= 1;
      b++;
    } 
    return b;
  }
  
  public static int ceilLog256(int paramInt) {
    int i;
    if (paramInt == 0)
      return 1; 
    if (paramInt < 0) {
      i = -paramInt;
    } else {
      i = paramInt;
    } 
    byte b = 0;
    while (i > 0) {
      b++;
      i >>>= 8;
    } 
    return b;
  }
  
  public static int ceilLog256(long paramLong) {
    long l;
    if (paramLong == 0L)
      return 1; 
    if (paramLong < 0L) {
      l = -paramLong;
    } else {
      l = paramLong;
    } 
    byte b = 0;
    while (l > 0L) {
      b++;
      l >>>= 8L;
    } 
    return b;
  }
  
  public static int floorLog(BigInteger paramBigInteger) {
    byte b = -1;
    for (BigInteger bigInteger = ONE; bigInteger.compareTo(paramBigInteger) <= 0; bigInteger = bigInteger.shiftLeft(1))
      b++; 
    return b;
  }
  
  public static int floorLog(int paramInt) {
    byte b = 0;
    if (paramInt <= 0)
      return -1; 
    for (int i = paramInt >>> 1; i > 0; i >>>= 1)
      b++; 
    return b;
  }
  
  public static int maxPower(int paramInt) {
    byte b = 0;
    if (paramInt != 0)
      for (int i = 1; (paramInt & i) == 0; i <<= 1)
        b++;  
    return b;
  }
  
  public static int bitCount(int paramInt) {
    int i = 0;
    while (paramInt != 0) {
      i += paramInt & 0x1;
      paramInt >>>= 1;
    } 
    return i;
  }
  
  public static int order(int paramInt1, int paramInt2) {
    int i = paramInt1 % paramInt2;
    byte b = 1;
    if (i == 0)
      throw new IllegalArgumentException(paramInt1 + " is not an element of Z/(" + paramInt2 + "Z)^*; it is not meaningful to compute its order."); 
    while (i != 1) {
      i *= paramInt1;
      i %= paramInt2;
      if (i < 0)
        i += paramInt2; 
      b++;
    } 
    return b;
  }
  
  public static BigInteger reduceInto(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    return paramBigInteger1.subtract(paramBigInteger2).mod(paramBigInteger3.subtract(paramBigInteger2)).add(paramBigInteger2);
  }
  
  public static int pow(int paramInt1, int paramInt2) {
    int i = 1;
    while (paramInt2 > 0) {
      if ((paramInt2 & 0x1) == 1)
        i *= paramInt1; 
      paramInt1 *= paramInt1;
      paramInt2 >>>= 1;
    } 
    return i;
  }
  
  public static long pow(long paramLong, int paramInt) {
    long l = 1L;
    while (paramInt > 0) {
      if ((paramInt & 0x1) == 1)
        l *= paramLong; 
      paramLong *= paramLong;
      paramInt >>>= 1;
    } 
    return l;
  }
  
  public static int modPow(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 <= 0 || paramInt3 * paramInt3 > Integer.MAX_VALUE || paramInt2 < 0)
      return 0; 
    int i = 1;
    paramInt1 = (paramInt1 % paramInt3 + paramInt3) % paramInt3;
    while (paramInt2 > 0) {
      if ((paramInt2 & 0x1) == 1)
        i = i * paramInt1 % paramInt3; 
      paramInt1 = paramInt1 * paramInt1 % paramInt3;
      paramInt2 >>>= 1;
    } 
    return i;
  }
  
  public static BigInteger[] extgcd(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = ONE;
    BigInteger bigInteger2 = ZERO;
    BigInteger bigInteger3 = paramBigInteger1;
    if (paramBigInteger2.signum() != 0) {
      BigInteger bigInteger4 = ZERO;
      for (BigInteger bigInteger5 = paramBigInteger2; bigInteger5.signum() != 0; bigInteger5 = bigInteger7) {
        BigInteger[] arrayOfBigInteger = bigInteger3.divideAndRemainder(bigInteger5);
        BigInteger bigInteger6 = arrayOfBigInteger[0];
        BigInteger bigInteger7 = arrayOfBigInteger[1];
        BigInteger bigInteger8 = bigInteger1.subtract(bigInteger6.multiply(bigInteger4));
        bigInteger1 = bigInteger4;
        bigInteger3 = bigInteger5;
        bigInteger4 = bigInteger8;
      } 
      bigInteger2 = bigInteger3.subtract(paramBigInteger1.multiply(bigInteger1)).divide(paramBigInteger2);
    } 
    return new BigInteger[] { bigInteger3, bigInteger1, bigInteger2 };
  }
  
  public static BigInteger leastCommonMultiple(BigInteger[] paramArrayOfBigInteger) {
    int i = paramArrayOfBigInteger.length;
    BigInteger bigInteger = paramArrayOfBigInteger[0];
    for (byte b = 1; b < i; b++) {
      BigInteger bigInteger1 = bigInteger.gcd(paramArrayOfBigInteger[b]);
      bigInteger = bigInteger.multiply(paramArrayOfBigInteger[b]).divide(bigInteger1);
    } 
    return bigInteger;
  }
  
  public static long mod(long paramLong1, long paramLong2) {
    long l = paramLong1 % paramLong2;
    if (l < 0L)
      l += paramLong2; 
    return l;
  }
  
  public static int modInverse(int paramInt1, int paramInt2) {
    return BigInteger.valueOf(paramInt1).modInverse(BigInteger.valueOf(paramInt2)).intValue();
  }
  
  public static long modInverse(long paramLong1, long paramLong2) {
    return BigInteger.valueOf(paramLong1).modInverse(BigInteger.valueOf(paramLong2)).longValue();
  }
  
  public static int isPower(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0)
      return -1; 
    byte b = 0;
    int i = paramInt1;
    while (i > 1) {
      if (i % paramInt2 != 0)
        return -1; 
      i /= paramInt2;
      b++;
    } 
    return b;
  }
  
  public static int leastDiv(int paramInt) {
    if (paramInt < 0)
      paramInt = -paramInt; 
    if (paramInt == 0)
      return 1; 
    if ((paramInt & 0x1) == 0)
      return 2; 
    for (byte b = 3; b <= paramInt / b; b += 2) {
      if (paramInt % b == 0)
        return b; 
    } 
    return paramInt;
  }
  
  public static boolean isPrime(int paramInt) {
    if (paramInt < 2)
      return false; 
    if (paramInt == 2)
      return true; 
    if ((paramInt & 0x1) == 0)
      return false; 
    if (paramInt < 42)
      for (byte b = 0; b < SMALL_PRIMES.length; b++) {
        if (paramInt == SMALL_PRIMES[b])
          return true; 
      }  
    return (paramInt % 3 == 0 || paramInt % 5 == 0 || paramInt % 7 == 0 || paramInt % 11 == 0 || paramInt % 13 == 0 || paramInt % 17 == 0 || paramInt % 19 == 0 || paramInt % 23 == 0 || paramInt % 29 == 0 || paramInt % 31 == 0 || paramInt % 37 == 0 || paramInt % 41 == 0) ? false : BigInteger.valueOf(paramInt).isProbablePrime(20);
  }
  
  public static boolean passesSmallPrimeTest(BigInteger paramBigInteger) {
    int[] arrayOfInt = { 
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 
        73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 
        179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 
        233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 
        283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 
        353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 
        419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 
        467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 
        547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 
        607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 
        661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 
        739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 
        811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 
        877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 
        947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 
        1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 
        1087, 1091, 1093, 1097, 1103, 1109, 1117, 1123, 1129, 1151, 
        1153, 1163, 1171, 1181, 1187, 1193, 1201, 1213, 1217, 1223, 
        1229, 1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289, 1291, 
        1297, 1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367, 1373, 
        1381, 1399, 1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 
        1453, 1459, 1471, 1481, 1483, 1487, 1489, 1493, 1499 };
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (paramBigInteger.mod(BigInteger.valueOf(arrayOfInt[b])).equals(ZERO))
        return false; 
    } 
    return true;
  }
  
  public static int nextSmallerPrime(int paramInt) {
    if (paramInt <= 2)
      return 1; 
    if (paramInt == 3)
      return 2; 
    if ((paramInt & 0x1) == 0) {
      paramInt--;
    } else {
      paramInt -= 2;
    } 
    while (true) {
      if ((((paramInt > 3) ? 1 : 0) & (!isPrime(paramInt) ? 1 : 0)) != 0) {
        paramInt -= 2;
        continue;
      } 
      return paramInt;
    } 
  }
  
  public static BigInteger nextProbablePrime(BigInteger paramBigInteger, int paramInt) {
    if (paramBigInteger.signum() < 0 || paramBigInteger.signum() == 0 || paramBigInteger.equals(ONE))
      return TWO; 
    BigInteger bigInteger = paramBigInteger.add(ONE);
    if (!bigInteger.testBit(0))
      bigInteger = bigInteger.add(ONE); 
    while (true) {
      if (bigInteger.bitLength() > 6) {
        long l = bigInteger.remainder(BigInteger.valueOf(152125131763605L)).longValue();
        if (l % 3L == 0L || l % 5L == 0L || l % 7L == 0L || l % 11L == 0L || l % 13L == 0L || l % 17L == 0L || l % 19L == 0L || l % 23L == 0L || l % 29L == 0L || l % 31L == 0L || l % 37L == 0L || l % 41L == 0L) {
          bigInteger = bigInteger.add(TWO);
          continue;
        } 
      } 
      if (bigInteger.bitLength() < 4)
        return bigInteger; 
      if (bigInteger.isProbablePrime(paramInt))
        return bigInteger; 
      bigInteger = bigInteger.add(TWO);
    } 
  }
  
  public static BigInteger nextProbablePrime(BigInteger paramBigInteger) {
    return nextProbablePrime(paramBigInteger, 20);
  }
  
  public static BigInteger nextPrime(long paramLong) {
    boolean bool = false;
    long l2 = 0L;
    if (paramLong <= 1L)
      return BigInteger.valueOf(2L); 
    if (paramLong == 2L)
      return BigInteger.valueOf(3L); 
    for (long l1 = paramLong + 1L + (paramLong & 0x1L); l1 <= paramLong << 1L && !bool; l1 += 2L) {
      long l;
      for (l = 3L; l <= l1 >> 1L && !bool; l += 2L) {
        if (l1 % l == 0L)
          bool = true; 
      } 
      if (bool) {
        bool = false;
      } else {
        l2 = l1;
        bool = true;
      } 
    } 
    return BigInteger.valueOf(l2);
  }
  
  public static BigInteger binomial(int paramInt1, int paramInt2) {
    BigInteger bigInteger = ONE;
    if (paramInt1 == 0)
      return (paramInt2 == 0) ? bigInteger : ZERO; 
    if (paramInt2 > paramInt1 >>> 1)
      paramInt2 = paramInt1 - paramInt2; 
    for (byte b = 1; b <= paramInt2; b++)
      bigInteger = bigInteger.multiply(BigInteger.valueOf((paramInt1 - b - 1))).divide(BigInteger.valueOf(b)); 
    return bigInteger;
  }
  
  public static BigInteger randomize(BigInteger paramBigInteger) {
    if (sr == null)
      sr = new SecureRandom(); 
    return randomize(paramBigInteger, sr);
  }
  
  public static BigInteger randomize(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    int i = paramBigInteger.bitLength();
    BigInteger bigInteger = BigInteger.valueOf(0L);
    if (paramSecureRandom == null)
      paramSecureRandom = (sr != null) ? sr : new SecureRandom(); 
    for (byte b = 0; b < 20; b++) {
      bigInteger = new BigInteger(i, paramSecureRandom);
      if (bigInteger.compareTo(paramBigInteger) < 0)
        return bigInteger; 
    } 
    return bigInteger.mod(paramBigInteger);
  }
  
  public static BigInteger squareRoot(BigInteger paramBigInteger) {
    if (paramBigInteger.compareTo(ZERO) < 0)
      throw new ArithmeticException("cannot extract root of negative number" + paramBigInteger + "."); 
    int i = paramBigInteger.bitLength();
    BigInteger bigInteger1 = ZERO;
    BigInteger bigInteger2 = ZERO;
    if ((i & 0x1) != 0) {
      bigInteger1 = bigInteger1.add(ONE);
      i--;
    } 
    while (i > 0) {
      bigInteger2 = bigInteger2.multiply(FOUR);
      bigInteger2 = bigInteger2.add(BigInteger.valueOf(((paramBigInteger.testBit(--i) ? 2 : 0) + (paramBigInteger.testBit(--i) ? 1 : 0))));
      BigInteger bigInteger = bigInteger1.multiply(FOUR).add(ONE);
      bigInteger1 = bigInteger1.multiply(TWO);
      if (bigInteger2.compareTo(bigInteger) != -1) {
        bigInteger1 = bigInteger1.add(ONE);
        bigInteger2 = bigInteger2.subtract(bigInteger);
      } 
    } 
    return bigInteger1;
  }
  
  public static float intRoot(int paramInt1, int paramInt2) {
    float f1 = (paramInt1 / paramInt2);
    float f2 = 0.0F;
    byte b = 0;
    while (Math.abs(f2 - f1) > 1.0E-4D) {
      float f;
      for (f = floatPow(f1, paramInt2); Float.isInfinite(f); f = floatPow(f1, paramInt2))
        f1 = (f1 + f2) / 2.0F; 
      b++;
      f2 = f1;
      f1 = f2 - (f - paramInt1) / paramInt2 * floatPow(f2, paramInt2 - 1);
    } 
    return f1;
  }
  
  public static float floatPow(float paramFloat, int paramInt) {
    float f = 1.0F;
    while (paramInt > 0) {
      f *= paramFloat;
      paramInt--;
    } 
    return f;
  }
  
  public static double log(double paramDouble) {
    if (paramDouble > 0.0D && paramDouble < 1.0D) {
      double d = 1.0D / paramDouble;
      return -log(d);
    } 
    byte b = 0;
    double d1 = 1.0D;
    double d2 = paramDouble;
    while (d2 > 2.0D) {
      d2 /= 2.0D;
      b++;
      d1 *= 2.0D;
    } 
    double d3 = paramDouble / d1;
    d3 = logBKM(d3);
    return b + d3;
  }
  
  public static double log(long paramLong) {
    int i = floorLog(BigInteger.valueOf(paramLong));
    long l = (1 << i);
    double d = paramLong / l;
    d = logBKM(d);
    return i + d;
  }
  
  private static double logBKM(double paramDouble) {
    double[] arrayOfDouble = { 
        1.0D, 0.5849625007211562D, 0.32192809488736235D, 0.16992500144231237D, 0.0874628412503394D, 0.044394119358453436D, 0.02236781302845451D, 0.01122725542325412D, 0.005624549193878107D, 0.0028150156070540383D, 
        0.0014081943928083889D, 7.042690112466433E-4D, 3.5217748030102726E-4D, 1.7609948644250602E-4D, 8.80524301221769E-5D, 4.4026886827316716E-5D, 2.2013611360340496E-5D, 1.1006847667481442E-5D, 5.503434330648604E-6D, 2.751719789561283E-6D, 
        1.375860550841138E-6D, 6.879304394358497E-7D, 3.4396526072176454E-7D, 1.7198264061184464E-7D, 8.599132286866321E-8D, 4.299566207501687E-8D, 2.1497831197679756E-8D, 1.0748915638882709E-8D, 5.374457829452062E-9D, 2.687228917228708E-9D, 
        1.3436144592400231E-9D, 6.718072297764289E-10D, 3.3590361492731876E-10D, 1.6795180747343547E-10D, 8.397590373916176E-11D, 4.1987951870191886E-11D, 2.0993975935248694E-11D, 1.0496987967662534E-11D, 5.2484939838408146E-12D, 2.624246991922794E-12D, 
        1.3121234959619935E-12D, 6.56061747981146E-13D, 3.2803087399061026E-13D, 1.6401543699531447E-13D, 8.200771849765956E-14D, 4.1003859248830365E-14D, 2.0501929624415328E-14D, 1.02509648122077E-14D, 5.1254824061038595E-15D, 2.5627412030519317E-15D, 
        1.2813706015259665E-15D, 6.406853007629834E-16D, 3.203426503814917E-16D, 1.6017132519074588E-16D, 8.008566259537294E-17D, 4.004283129768647E-17D, 2.0021415648843235E-17D, 1.0010707824421618E-17D, 5.005353912210809E-18D, 2.5026769561054044E-18D, 
        1.2513384780527022E-18D, 6.256692390263511E-19D, 3.1283461951317555E-19D, 1.5641730975658778E-19D, 7.820865487829389E-20D, 3.9104327439146944E-20D, 1.9552163719573472E-20D, 9.776081859786736E-21D, 4.888040929893368E-21D, 2.444020464946684E-21D, 
        1.222010232473342E-21D, 6.11005116236671E-22D, 3.055025581183355E-22D, 1.5275127905916775E-22D, 7.637563952958387E-23D, 3.818781976479194E-23D, 1.909390988239597E-23D, 9.546954941197984E-24D, 4.773477470598992E-24D, 2.386738735299496E-24D, 
        1.193369367649748E-24D, 5.96684683824874E-25D, 2.98342341912437E-25D, 1.491711709562185E-25D, 7.458558547810925E-26D, 3.7292792739054626E-26D, 1.8646396369527313E-26D, 9.323198184763657E-27D, 4.661599092381828E-27D, 2.330799546190914E-27D, 
        1.165399773095457E-27D, 5.826998865477285E-28D, 2.9134994327386427E-28D, 1.4567497163693213E-28D, 7.283748581846607E-29D, 3.6418742909233034E-29D, 1.8209371454616517E-29D, 9.104685727308258E-30D, 4.552342863654129E-30D, 2.2761714318270646E-30D };
    byte b1 = 53;
    double d1 = 1.0D;
    double d2 = 0.0D;
    double d3 = 1.0D;
    for (byte b2 = 0; b2 < b1; b2++) {
      double d = d1 + d1 * d3;
      if (d <= paramDouble) {
        d1 = d;
        d2 += arrayOfDouble[b2];
      } 
      d3 *= 0.5D;
    } 
    return d2;
  }
  
  public static boolean isIncreasing(int[] paramArrayOfint) {
    for (byte b = 1; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b - 1] >= paramArrayOfint[b]) {
        System.out.println("a[" + (b - 1) + "] = " + paramArrayOfint[b - 1] + " >= " + paramArrayOfint[b] + " = a[" + b + "]");
        return false;
      } 
    } 
    return true;
  }
  
  public static byte[] integerToOctets(BigInteger paramBigInteger) {
    byte[] arrayOfByte1 = paramBigInteger.abs().toByteArray();
    if ((paramBigInteger.bitLength() & 0x7) != 0)
      return arrayOfByte1; 
    byte[] arrayOfByte2 = new byte[paramBigInteger.bitLength() >> 3];
    System.arraycopy(arrayOfByte1, 1, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
  
  public static BigInteger octetsToInteger(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt2 + 1];
    arrayOfByte[0] = 0;
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 1, paramInt2);
    return new BigInteger(arrayOfByte);
  }
  
  public static BigInteger octetsToInteger(byte[] paramArrayOfbyte) {
    return octetsToInteger(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
}
