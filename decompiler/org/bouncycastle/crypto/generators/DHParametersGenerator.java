package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;

public class DHParametersGenerator {
  private int size;
  
  private int certainty;
  
  private SecureRandom random;
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  public void init(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    this.size = paramInt1;
    this.certainty = paramInt2;
    this.random = paramSecureRandom;
  }
  
  public DHParameters generateParameters() {
    BigInteger[] arrayOfBigInteger = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
    BigInteger bigInteger1 = arrayOfBigInteger[0];
    BigInteger bigInteger2 = arrayOfBigInteger[1];
    BigInteger bigInteger3 = DHParametersHelper.selectGenerator(bigInteger1, bigInteger2, this.random);
    return new DHParameters(bigInteger1, bigInteger3, bigInteger2, TWO, null);
  }
}
