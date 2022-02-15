package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ECPrivateKeyParameters extends ECKeyParameters {
  BigInteger d;
  
  public ECPrivateKeyParameters(BigInteger paramBigInteger, ECDomainParameters paramECDomainParameters) {
    super(true, paramECDomainParameters);
    this.d = paramBigInteger;
  }
  
  public BigInteger getD() {
    return this.d;
  }
}
