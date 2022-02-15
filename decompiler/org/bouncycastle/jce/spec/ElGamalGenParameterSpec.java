package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class ElGamalGenParameterSpec implements AlgorithmParameterSpec {
  private int primeSize;
  
  public ElGamalGenParameterSpec(int paramInt) {
    this.primeSize = paramInt;
  }
  
  public int getPrimeSize() {
    return this.primeSize;
  }
}
