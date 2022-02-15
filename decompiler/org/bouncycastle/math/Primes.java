package org.bouncycastle.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public abstract class Primes {
  public static final int SMALL_FACTOR_LIMIT = 211;
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  private static final BigInteger THREE = BigInteger.valueOf(3L);
  
  public static STOutput generateSTRandomPrime(Digest paramDigest, int paramInt, byte[] paramArrayOfbyte) {
    if (paramDigest == null)
      throw new IllegalArgumentException("'hash' cannot be null"); 
    if (paramInt < 2)
      throw new IllegalArgumentException("'length' must be >= 2"); 
    if (paramArrayOfbyte == null || paramArrayOfbyte.length == 0)
      throw new IllegalArgumentException("'inputSeed' cannot be null or empty"); 
    return implSTRandomPrime(paramDigest, paramInt, Arrays.clone(paramArrayOfbyte));
  }
  
  public static MROutput enhancedMRProbablePrimeTest(BigInteger paramBigInteger, SecureRandom paramSecureRandom, int paramInt) {
    checkCandidate(paramBigInteger, "candidate");
    if (paramSecureRandom == null)
      throw new IllegalArgumentException("'random' cannot be null"); 
    if (paramInt < 1)
      throw new IllegalArgumentException("'iterations' must be > 0"); 
    if (paramBigInteger.bitLength() == 2)
      return MROutput.probablyPrime(); 
    if (!paramBigInteger.testBit(0))
      return MROutput.provablyCompositeWithFactor(TWO); 
    BigInteger bigInteger1 = paramBigInteger;
    BigInteger bigInteger2 = paramBigInteger.subtract(ONE);
    BigInteger bigInteger3 = paramBigInteger.subtract(TWO);
    int i = bigInteger2.getLowestSetBit();
    BigInteger bigInteger4 = bigInteger2.shiftRight(i);
    for (byte b = 0; b < paramInt; b++) {
      BigInteger bigInteger5 = BigIntegers.createRandomInRange(TWO, bigInteger3, paramSecureRandom);
      BigInteger bigInteger6 = bigInteger5.gcd(bigInteger1);
      if (bigInteger6.compareTo(ONE) > 0)
        return MROutput.provablyCompositeWithFactor(bigInteger6); 
      BigInteger bigInteger7 = bigInteger5.modPow(bigInteger4, bigInteger1);
      if (!bigInteger7.equals(ONE) && !bigInteger7.equals(bigInteger2)) {
        boolean bool = false;
        BigInteger bigInteger = bigInteger7;
        for (byte b1 = 1; b1 < i; b1++) {
          bigInteger7 = bigInteger7.modPow(TWO, bigInteger1);
          if (bigInteger7.equals(bigInteger2)) {
            bool = true;
            break;
          } 
          if (bigInteger7.equals(ONE))
            break; 
          bigInteger = bigInteger7;
        } 
        if (!bool) {
          if (!bigInteger7.equals(ONE)) {
            bigInteger = bigInteger7;
            bigInteger7 = bigInteger7.modPow(TWO, bigInteger1);
            if (!bigInteger7.equals(ONE))
              bigInteger = bigInteger7; 
          } 
          bigInteger6 = bigInteger.subtract(ONE).gcd(bigInteger1);
          return (bigInteger6.compareTo(ONE) > 0) ? MROutput.provablyCompositeWithFactor(bigInteger6) : MROutput.provablyCompositeNotPrimePower();
        } 
      } 
    } 
    return MROutput.probablyPrime();
  }
  
  public static boolean hasAnySmallFactors(BigInteger paramBigInteger) {
    checkCandidate(paramBigInteger, "candidate");
    return implHasAnySmallFactors(paramBigInteger);
  }
  
  public static boolean isMRProbablePrime(BigInteger paramBigInteger, SecureRandom paramSecureRandom, int paramInt) {
    checkCandidate(paramBigInteger, "candidate");
    if (paramSecureRandom == null)
      throw new IllegalArgumentException("'random' cannot be null"); 
    if (paramInt < 1)
      throw new IllegalArgumentException("'iterations' must be > 0"); 
    if (paramBigInteger.bitLength() == 2)
      return true; 
    if (!paramBigInteger.testBit(0))
      return false; 
    BigInteger bigInteger1 = paramBigInteger;
    BigInteger bigInteger2 = paramBigInteger.subtract(ONE);
    BigInteger bigInteger3 = paramBigInteger.subtract(TWO);
    int i = bigInteger2.getLowestSetBit();
    BigInteger bigInteger4 = bigInteger2.shiftRight(i);
    for (byte b = 0; b < paramInt; b++) {
      BigInteger bigInteger = BigIntegers.createRandomInRange(TWO, bigInteger3, paramSecureRandom);
      if (!implMRProbablePrimeToBase(bigInteger1, bigInteger2, bigInteger4, i, bigInteger))
        return false; 
    } 
    return true;
  }
  
  public static boolean isMRProbablePrimeToBase(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    checkCandidate(paramBigInteger1, "candidate");
    checkCandidate(paramBigInteger2, "base");
    if (paramBigInteger2.compareTo(paramBigInteger1.subtract(ONE)) >= 0)
      throw new IllegalArgumentException("'base' must be < ('candidate' - 1)"); 
    if (paramBigInteger1.bitLength() == 2)
      return true; 
    BigInteger bigInteger1 = paramBigInteger1;
    BigInteger bigInteger2 = paramBigInteger1.subtract(ONE);
    int i = bigInteger2.getLowestSetBit();
    BigInteger bigInteger3 = bigInteger2.shiftRight(i);
    return implMRProbablePrimeToBase(bigInteger1, bigInteger2, bigInteger3, i, paramBigInteger2);
  }
  
  private static void checkCandidate(BigInteger paramBigInteger, String paramString) {
    if (paramBigInteger == null || paramBigInteger.signum() < 1 || paramBigInteger.bitLength() < 2)
      throw new IllegalArgumentException("'" + paramString + "' must be non-null and >= 2"); 
  }
  
  private static boolean implHasAnySmallFactors(BigInteger paramBigInteger) {
    int i = 223092870;
    int j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 2 == 0 || j % 3 == 0 || j % 5 == 0 || j % 7 == 0 || j % 11 == 0 || j % 13 == 0 || j % 17 == 0 || j % 19 == 0 || j % 23 == 0)
      return true; 
    i = 58642669;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 29 == 0 || j % 31 == 0 || j % 37 == 0 || j % 41 == 0 || j % 43 == 0)
      return true; 
    i = 600662303;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 47 == 0 || j % 53 == 0 || j % 59 == 0 || j % 61 == 0 || j % 67 == 0)
      return true; 
    i = 33984931;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 71 == 0 || j % 73 == 0 || j % 79 == 0 || j % 83 == 0)
      return true; 
    i = 89809099;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 89 == 0 || j % 97 == 0 || j % 101 == 0 || j % 103 == 0)
      return true; 
    i = 167375713;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 107 == 0 || j % 109 == 0 || j % 113 == 0 || j % 127 == 0)
      return true; 
    i = 371700317;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 131 == 0 || j % 137 == 0 || j % 139 == 0 || j % 149 == 0)
      return true; 
    i = 645328247;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 151 == 0 || j % 157 == 0 || j % 163 == 0 || j % 167 == 0)
      return true; 
    i = 1070560157;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    if (j % 173 == 0 || j % 179 == 0 || j % 181 == 0 || j % 191 == 0)
      return true; 
    i = 1596463769;
    j = paramBigInteger.mod(BigInteger.valueOf(i)).intValue();
    return (j % 193 == 0 || j % 197 == 0 || j % 199 == 0 || j % 211 == 0);
  }
  
  private static boolean implMRProbablePrimeToBase(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, int paramInt, BigInteger paramBigInteger4) {
    BigInteger bigInteger = paramBigInteger4.modPow(paramBigInteger3, paramBigInteger1);
    if (bigInteger.equals(ONE) || bigInteger.equals(paramBigInteger2))
      return true; 
    boolean bool = false;
    for (byte b = 1; b < paramInt; b++) {
      bigInteger = bigInteger.modPow(TWO, paramBigInteger1);
      if (bigInteger.equals(paramBigInteger2)) {
        bool = true;
        break;
      } 
      if (bigInteger.equals(ONE))
        return false; 
    } 
    return bool;
  }
  
  private static STOutput implSTRandomPrime(Digest paramDigest, int paramInt, byte[] paramArrayOfbyte) {
    int i = paramDigest.getDigestSize();
    if (paramInt < 33) {
      byte b = 0;
      byte[] arrayOfByte1 = new byte[i];
      byte[] arrayOfByte2 = new byte[i];
      while (true) {
        hash(paramDigest, paramArrayOfbyte, arrayOfByte1, 0);
        inc(paramArrayOfbyte, 1);
        hash(paramDigest, paramArrayOfbyte, arrayOfByte2, 0);
        inc(paramArrayOfbyte, 1);
        int i1 = extract32(arrayOfByte1) ^ extract32(arrayOfByte2);
        i1 &= -1 >>> 32 - paramInt;
        i1 |= 1 << paramInt - 1 | 0x1;
        b++;
        long l = i1 & 0xFFFFFFFFL;
        if (isPrime32(l))
          return new STOutput(BigInteger.valueOf(l), paramArrayOfbyte, b); 
        if (b > 4 * paramInt)
          throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine"); 
      } 
    } 
    STOutput sTOutput = implSTRandomPrime(paramDigest, (paramInt + 3) / 2, paramArrayOfbyte);
    BigInteger bigInteger1 = sTOutput.getPrime();
    paramArrayOfbyte = sTOutput.getPrimeSeed();
    int j = sTOutput.getPrimeGenCounter();
    int k = 8 * i;
    int m = (paramInt - 1) / k;
    int n = j;
    BigInteger bigInteger2 = hashGen(paramDigest, paramArrayOfbyte, m + 1);
    bigInteger2 = bigInteger2.mod(ONE.shiftLeft(paramInt - 1)).setBit(paramInt - 1);
    BigInteger bigInteger3 = bigInteger1.shiftLeft(1);
    BigInteger bigInteger4 = bigInteger2.subtract(ONE).divide(bigInteger3).add(ONE).shiftLeft(1);
    boolean bool = false;
    for (BigInteger bigInteger5 = bigInteger4.multiply(bigInteger1).add(ONE);; bigInteger5 = bigInteger5.add(bigInteger3)) {
      if (bigInteger5.bitLength() > paramInt) {
        bigInteger4 = ONE.shiftLeft(paramInt - 1).subtract(ONE).divide(bigInteger3).add(ONE).shiftLeft(1);
        bigInteger5 = bigInteger4.multiply(bigInteger1).add(ONE);
      } 
      j++;
      if (!implHasAnySmallFactors(bigInteger5)) {
        BigInteger bigInteger6 = hashGen(paramDigest, paramArrayOfbyte, m + 1);
        bigInteger6 = bigInteger6.mod(bigInteger5.subtract(THREE)).add(TWO);
        bigInteger4 = bigInteger4.add(BigInteger.valueOf(bool));
        bool = false;
        BigInteger bigInteger7 = bigInteger6.modPow(bigInteger4, bigInteger5);
        if (bigInteger5.gcd(bigInteger7.subtract(ONE)).equals(ONE) && bigInteger7.modPow(bigInteger1, bigInteger5).equals(ONE))
          return new STOutput(bigInteger5, paramArrayOfbyte, j); 
      } else {
        inc(paramArrayOfbyte, m + 1);
      } 
      if (j >= 4 * paramInt + n)
        throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine"); 
      bool += true;
    } 
  }
  
  private static int extract32(byte[] paramArrayOfbyte) {
    int i = 0;
    int j = Math.min(4, paramArrayOfbyte.length);
    for (byte b = 0; b < j; b++) {
      int k = paramArrayOfbyte[paramArrayOfbyte.length - b + 1] & 0xFF;
      i |= k << 8 * b;
    } 
    return i;
  }
  
  private static void hash(Digest paramDigest, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    paramDigest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    paramDigest.doFinal(paramArrayOfbyte2, paramInt);
  }
  
  private static BigInteger hashGen(Digest paramDigest, byte[] paramArrayOfbyte, int paramInt) {
    int i = paramDigest.getDigestSize();
    int j = paramInt * i;
    byte[] arrayOfByte = new byte[j];
    for (byte b = 0; b < paramInt; b++) {
      j -= i;
      hash(paramDigest, paramArrayOfbyte, arrayOfByte, j);
      inc(paramArrayOfbyte, 1);
    } 
    return new BigInteger(1, arrayOfByte);
  }
  
  private static void inc(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte.length;
    while (paramInt > 0 && --i >= 0) {
      paramInt += paramArrayOfbyte[i] & 0xFF;
      paramArrayOfbyte[i] = (byte)paramInt;
      paramInt >>>= 8;
    } 
  }
  
  private static boolean isPrime32(long paramLong) {
    if (paramLong >>> 32L != 0L)
      throw new IllegalArgumentException("Size limit exceeded"); 
    if (paramLong <= 5L)
      return (paramLong == 2L || paramLong == 3L || paramLong == 5L); 
    if ((paramLong & 0x1L) == 0L || paramLong % 3L == 0L || paramLong % 5L == 0L)
      return false; 
    long[] arrayOfLong = { 1L, 7L, 11L, 13L, 17L, 19L, 23L, 29L };
    long l = 0L;
    byte b;
    for (b = 1;; b = 0) {
      while (b < arrayOfLong.length) {
        long l1 = l + arrayOfLong[b];
        if (paramLong % l1 == 0L)
          return (paramLong < 30L); 
        b++;
      } 
      l += 30L;
      if (l * l >= paramLong)
        return true; 
    } 
  }
  
  public static class MROutput {
    private boolean provablyComposite;
    
    private BigInteger factor;
    
    private static MROutput probablyPrime() {
      return new MROutput(false, null);
    }
    
    private static MROutput provablyCompositeWithFactor(BigInteger param1BigInteger) {
      return new MROutput(true, param1BigInteger);
    }
    
    private static MROutput provablyCompositeNotPrimePower() {
      return new MROutput(true, null);
    }
    
    private MROutput(boolean param1Boolean, BigInteger param1BigInteger) {
      this.provablyComposite = param1Boolean;
      this.factor = param1BigInteger;
    }
    
    public BigInteger getFactor() {
      return this.factor;
    }
    
    public boolean isProvablyComposite() {
      return this.provablyComposite;
    }
    
    public boolean isNotPrimePower() {
      return (this.provablyComposite && this.factor == null);
    }
  }
  
  public static class STOutput {
    private BigInteger prime;
    
    private byte[] primeSeed;
    
    private int primeGenCounter;
    
    private STOutput(BigInteger param1BigInteger, byte[] param1ArrayOfbyte, int param1Int) {
      this.prime = param1BigInteger;
      this.primeSeed = param1ArrayOfbyte;
      this.primeGenCounter = param1Int;
    }
    
    public BigInteger getPrime() {
      return this.prime;
    }
    
    public byte[] getPrimeSeed() {
      return this.primeSeed;
    }
    
    public int getPrimeGenCounter() {
      return this.primeGenCounter;
    }
  }
}
