package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.math.ec.ECPoint;

public class DualECPoints {
  private final ECPoint p;
  
  private final ECPoint q;
  
  private final int securityStrength;
  
  private final int cofactor;
  
  public DualECPoints(int paramInt1, ECPoint paramECPoint1, ECPoint paramECPoint2, int paramInt2) {
    if (!paramECPoint1.getCurve().equals(paramECPoint2.getCurve()))
      throw new IllegalArgumentException("points need to be on the same curve"); 
    this.securityStrength = paramInt1;
    this.p = paramECPoint1;
    this.q = paramECPoint2;
    this.cofactor = paramInt2;
  }
  
  public int getSeedLen() {
    return this.p.getCurve().getFieldSize();
  }
  
  public int getMaxOutlen() {
    return (this.p.getCurve().getFieldSize() - 13 + log2(this.cofactor)) / 8 * 8;
  }
  
  public ECPoint getP() {
    return this.p;
  }
  
  public ECPoint getQ() {
    return this.q;
  }
  
  public int getSecurityStrength() {
    return this.securityStrength;
  }
  
  public int getCofactor() {
    return this.cofactor;
  }
  
  private static int log2(int paramInt) {
    byte b;
    for (b = 0; (paramInt >>= 1) != 0; b++);
    return b;
  }
}
