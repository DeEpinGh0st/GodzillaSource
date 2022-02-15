package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class ElGamalPublicKeySpec extends ElGamalKeySpec {
  private BigInteger y;
  
  public ElGamalPublicKeySpec(BigInteger paramBigInteger, ElGamalParameterSpec paramElGamalParameterSpec) {
    super(paramElGamalParameterSpec);
    this.y = paramBigInteger;
  }
  
  public BigInteger getY() {
    return this.y;
  }
}
