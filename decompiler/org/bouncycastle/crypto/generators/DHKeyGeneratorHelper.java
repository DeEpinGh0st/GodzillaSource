package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

class DHKeyGeneratorHelper {
  static final DHKeyGeneratorHelper INSTANCE = new DHKeyGeneratorHelper();
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  BigInteger calculatePrivate(DHParameters paramDHParameters, SecureRandom paramSecureRandom) {
    int i = paramDHParameters.getL();
    if (i != 0) {
      int m = i >>> 2;
      while (true) {
        BigInteger bigInteger = (new BigInteger(i, paramSecureRandom)).setBit(i - 1);
        if (WNafUtil.getNafWeight(bigInteger) >= m)
          return bigInteger; 
      } 
    } 
    BigInteger bigInteger1 = TWO;
    int j = paramDHParameters.getM();
    if (j != 0)
      bigInteger1 = ONE.shiftLeft(j - 1); 
    BigInteger bigInteger2 = paramDHParameters.getQ();
    if (bigInteger2 == null)
      bigInteger2 = paramDHParameters.getP(); 
    BigInteger bigInteger3 = bigInteger2.subtract(TWO);
    int k = bigInteger3.bitLength() >>> 2;
    while (true) {
      BigInteger bigInteger = BigIntegers.createRandomInRange(bigInteger1, bigInteger3, paramSecureRandom);
      if (WNafUtil.getNafWeight(bigInteger) >= k)
        return bigInteger; 
    } 
  }
  
  BigInteger calculatePublic(DHParameters paramDHParameters, BigInteger paramBigInteger) {
    return paramDHParameters.getG().modPow(paramBigInteger, paramDHParameters.getP());
  }
}
