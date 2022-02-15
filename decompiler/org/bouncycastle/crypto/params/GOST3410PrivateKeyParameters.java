package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class GOST3410PrivateKeyParameters extends GOST3410KeyParameters {
  private BigInteger x;
  
  public GOST3410PrivateKeyParameters(BigInteger paramBigInteger, GOST3410Parameters paramGOST3410Parameters) {
    super(true, paramGOST3410Parameters);
    this.x = paramBigInteger;
  }
  
  public BigInteger getX() {
    return this.x;
  }
}
