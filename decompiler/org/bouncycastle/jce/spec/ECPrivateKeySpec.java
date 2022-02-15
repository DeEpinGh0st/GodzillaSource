package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class ECPrivateKeySpec extends ECKeySpec {
  private BigInteger d;
  
  public ECPrivateKeySpec(BigInteger paramBigInteger, ECParameterSpec paramECParameterSpec) {
    super(paramECParameterSpec);
    this.d = paramBigInteger;
  }
  
  public BigInteger getD() {
    return this.d;
  }
}
