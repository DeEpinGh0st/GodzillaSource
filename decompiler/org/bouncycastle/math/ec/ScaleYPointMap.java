package org.bouncycastle.math.ec;

public class ScaleYPointMap implements ECPointMap {
  protected final ECFieldElement scale;
  
  public ScaleYPointMap(ECFieldElement paramECFieldElement) {
    this.scale = paramECFieldElement;
  }
  
  public ECPoint map(ECPoint paramECPoint) {
    return paramECPoint.scaleY(this.scale);
  }
}
