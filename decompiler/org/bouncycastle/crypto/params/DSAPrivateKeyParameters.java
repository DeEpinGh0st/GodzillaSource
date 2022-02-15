package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DSAPrivateKeyParameters extends DSAKeyParameters {
  private BigInteger x;
  
  public DSAPrivateKeyParameters(BigInteger paramBigInteger, DSAParameters paramDSAParameters) {
    super(true, paramDSAParameters);
    this.x = paramBigInteger;
  }
  
  public BigInteger getX() {
    return this.x;
  }
}
