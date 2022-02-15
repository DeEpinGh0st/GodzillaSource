package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

class DHParametersHelper {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  static BigInteger[] generateSafePrimes(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    int i = paramInt1 - 1;
    int j = paramInt1 >>> 2;
    while (true) {
      BigInteger bigInteger2 = new BigInteger(i, 2, paramSecureRandom);
      BigInteger bigInteger1 = bigInteger2.shiftLeft(1).add(ONE);
      if (!bigInteger1.isProbablePrime(paramInt2) || (paramInt2 > 2 && !bigInteger2.isProbablePrime(paramInt2 - 2)) || WNafUtil.getNafWeight(bigInteger1) < j)
        continue; 
      return new BigInteger[] { bigInteger1, bigInteger2 };
    } 
  }
  
  static BigInteger selectGenerator(BigInteger paramBigInteger1, BigInteger paramBigInteger2, SecureRandom paramSecureRandom) {
    BigInteger bigInteger = paramBigInteger1.subtract(TWO);
    while (true) {
      BigInteger bigInteger2 = BigIntegers.createRandomInRange(TWO, bigInteger, paramSecureRandom);
      BigInteger bigInteger1 = bigInteger2.modPow(TWO, paramBigInteger1);
      if (!bigInteger1.equals(ONE))
        return bigInteger1; 
    } 
  }
}
