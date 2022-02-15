package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

public class ElGamalParameterSpec implements AlgorithmParameterSpec {
  private BigInteger p;
  
  private BigInteger g;
  
  public ElGamalParameterSpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.p = paramBigInteger1;
    this.g = paramBigInteger2;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getG() {
    return this.g;
  }
}
