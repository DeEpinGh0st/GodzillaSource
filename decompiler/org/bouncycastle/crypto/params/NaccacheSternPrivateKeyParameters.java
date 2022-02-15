package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.util.Vector;

public class NaccacheSternPrivateKeyParameters extends NaccacheSternKeyParameters {
  private BigInteger phi_n;
  
  private Vector smallPrimes;
  
  public NaccacheSternPrivateKeyParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt, Vector paramVector, BigInteger paramBigInteger3) {
    super(true, paramBigInteger1, paramBigInteger2, paramInt);
    this.smallPrimes = paramVector;
    this.phi_n = paramBigInteger3;
  }
  
  public BigInteger getPhi_n() {
    return this.phi_n;
  }
  
  public Vector getSmallPrimes() {
    return this.smallPrimes;
  }
}
