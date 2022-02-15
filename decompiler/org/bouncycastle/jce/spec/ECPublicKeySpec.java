package org.bouncycastle.jce.spec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeySpec extends ECKeySpec {
  private ECPoint q;
  
  public ECPublicKeySpec(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec) {
    super(paramECParameterSpec);
    if (paramECPoint.getCurve() != null) {
      this.q = paramECPoint.normalize();
    } else {
      this.q = paramECPoint;
    } 
  }
  
  public ECPoint getQ() {
    return this.q;
  }
}
