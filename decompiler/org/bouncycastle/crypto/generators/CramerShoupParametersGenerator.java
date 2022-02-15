package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupParametersGenerator {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private int size;
  
  private int certainty;
  
  private SecureRandom random;
  
  public void init(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    this.size = paramInt1;
    this.certainty = paramInt2;
    this.random = paramSecureRandom;
  }
  
  public CramerShoupParameters generateParameters() {
    BigInteger[] arrayOfBigInteger = ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
    BigInteger bigInteger1 = arrayOfBigInteger[1];
    BigInteger bigInteger2 = ParametersHelper.selectGenerator(bigInteger1, this.random);
    BigInteger bigInteger3;
    for (bigInteger3 = ParametersHelper.selectGenerator(bigInteger1, this.random); bigInteger2.equals(bigInteger3); bigInteger3 = ParametersHelper.selectGenerator(bigInteger1, this.random));
    return new CramerShoupParameters(bigInteger1, bigInteger2, bigInteger3, (Digest)new SHA256Digest());
  }
  
  public CramerShoupParameters generateParameters(DHParameters paramDHParameters) {
    BigInteger bigInteger1 = paramDHParameters.getP();
    BigInteger bigInteger2 = paramDHParameters.getG();
    BigInteger bigInteger3;
    for (bigInteger3 = ParametersHelper.selectGenerator(bigInteger1, this.random); bigInteger2.equals(bigInteger3); bigInteger3 = ParametersHelper.selectGenerator(bigInteger1, this.random));
    return new CramerShoupParameters(bigInteger1, bigInteger2, bigInteger3, (Digest)new SHA256Digest());
  }
  
  private static class ParametersHelper {
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    
    static BigInteger[] generateSafePrimes(int param1Int1, int param1Int2, SecureRandom param1SecureRandom) {
      BigInteger bigInteger1;
      BigInteger bigInteger2;
      int i = param1Int1 - 1;
      do {
        bigInteger2 = new BigInteger(i, 2, param1SecureRandom);
        bigInteger1 = bigInteger2.shiftLeft(1).add(CramerShoupParametersGenerator.ONE);
      } while (!bigInteger1.isProbablePrime(param1Int2) || (param1Int2 > 2 && !bigInteger2.isProbablePrime(param1Int2)));
      return new BigInteger[] { bigInteger1, bigInteger2 };
    }
    
    static BigInteger selectGenerator(BigInteger param1BigInteger, SecureRandom param1SecureRandom) {
      BigInteger bigInteger = param1BigInteger.subtract(TWO);
      while (true) {
        BigInteger bigInteger2 = BigIntegers.createRandomInRange(TWO, bigInteger, param1SecureRandom);
        BigInteger bigInteger1 = bigInteger2.modPow(TWO, param1BigInteger);
        if (!bigInteger1.equals(CramerShoupParametersGenerator.ONE))
          return bigInteger1; 
      } 
    }
  }
}
