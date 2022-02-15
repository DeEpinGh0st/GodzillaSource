package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomDSAKCalculator implements DSAKCalculator {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private BigInteger q;
  
  private SecureRandom random;
  
  public boolean isDeterministic() {
    return false;
  }
  
  public void init(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    this.q = paramBigInteger;
    this.random = paramSecureRandom;
  }
  
  public void init(BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
    throw new IllegalStateException("Operation not supported");
  }
  
  public BigInteger nextK() {
    int i = this.q.bitLength();
    while (true) {
      BigInteger bigInteger = new BigInteger(i, this.random);
      if (!bigInteger.equals(ZERO) && bigInteger.compareTo(this.q) < 0)
        return bigInteger; 
    } 
  }
}
