package org.bouncycastle.math.ec;

public class WTauNafPreCompInfo implements PreCompInfo {
  protected ECPoint.AbstractF2m[] preComp = null;
  
  public ECPoint.AbstractF2m[] getPreComp() {
    return this.preComp;
  }
  
  public void setPreComp(ECPoint.AbstractF2m[] paramArrayOfAbstractF2m) {
    this.preComp = paramArrayOfAbstractF2m;
  }
}
