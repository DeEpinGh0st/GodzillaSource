package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class GOST3410PublicKeyParameters extends GOST3410KeyParameters {
  private BigInteger y;
  
  public GOST3410PublicKeyParameters(BigInteger paramBigInteger, GOST3410Parameters paramGOST3410Parameters) {
    super(false, paramGOST3410Parameters);
    this.y = paramBigInteger;
  }
  
  public BigInteger getY() {
    return this.y;
  }
}
