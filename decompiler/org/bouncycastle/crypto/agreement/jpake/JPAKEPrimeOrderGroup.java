package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

public class JPAKEPrimeOrderGroup {
  private final BigInteger p;
  
  private final BigInteger q;
  
  private final BigInteger g;
  
  public JPAKEPrimeOrderGroup(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this(paramBigInteger1, paramBigInteger2, paramBigInteger3, false);
  }
  
  JPAKEPrimeOrderGroup(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, boolean paramBoolean) {
    JPAKEUtil.validateNotNull(paramBigInteger1, "p");
    JPAKEUtil.validateNotNull(paramBigInteger2, "q");
    JPAKEUtil.validateNotNull(paramBigInteger3, "g");
    if (!paramBoolean) {
      if (!paramBigInteger1.subtract(JPAKEUtil.ONE).mod(paramBigInteger2).equals(JPAKEUtil.ZERO))
        throw new IllegalArgumentException("p-1 must be evenly divisible by q"); 
      if (paramBigInteger3.compareTo(BigInteger.valueOf(2L)) == -1 || paramBigInteger3.compareTo(paramBigInteger1.subtract(JPAKEUtil.ONE)) == 1)
        throw new IllegalArgumentException("g must be in [2, p-1]"); 
      if (!paramBigInteger3.modPow(paramBigInteger2, paramBigInteger1).equals(JPAKEUtil.ONE))
        throw new IllegalArgumentException("g^q mod p must equal 1"); 
      if (!paramBigInteger1.isProbablePrime(20))
        throw new IllegalArgumentException("p must be prime"); 
      if (!paramBigInteger2.isProbablePrime(20))
        throw new IllegalArgumentException("q must be prime"); 
    } 
    this.p = paramBigInteger1;
    this.q = paramBigInteger2;
    this.g = paramBigInteger3;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getQ() {
    return this.q;
  }
  
  public BigInteger getG() {
    return this.g;
  }
}
