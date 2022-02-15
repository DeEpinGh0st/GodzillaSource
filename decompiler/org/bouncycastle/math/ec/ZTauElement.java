package org.bouncycastle.math.ec;

import java.math.BigInteger;

class ZTauElement {
  public final BigInteger u;
  
  public final BigInteger v;
  
  public ZTauElement(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.u = paramBigInteger1;
    this.v = paramBigInteger2;
  }
}
