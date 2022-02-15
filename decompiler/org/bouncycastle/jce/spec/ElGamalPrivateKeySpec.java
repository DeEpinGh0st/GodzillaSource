package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class ElGamalPrivateKeySpec extends ElGamalKeySpec {
  private BigInteger x;
  
  public ElGamalPrivateKeySpec(BigInteger paramBigInteger, ElGamalParameterSpec paramElGamalParameterSpec) {
    super(paramElGamalParameterSpec);
    this.x = paramBigInteger;
  }
  
  public BigInteger getX() {
    return this.x;
  }
}
