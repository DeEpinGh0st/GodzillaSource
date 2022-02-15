package org.bouncycastle.math.ec.custom.djb;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;

public class Curve25519 extends ECCurve.AbstractFp {
  public static final BigInteger q = Nat256.toBigInteger(Curve25519Field.P);
  
  private static final int Curve25519_DEFAULT_COORDS = 4;
  
  protected Curve25519Point infinity = new Curve25519Point((ECCurve)this, null, null);
  
  public Curve25519() {
    super(q);
  }
  
  protected ECCurve cloneCurve() {
    return (ECCurve)new Curve25519();
  }
  
  public boolean supportsCoordinateSystem(int paramInt) {
    switch (paramInt) {
      case 4:
        return true;
    } 
    return false;
  }
  
  public BigInteger getQ() {
    return q;
  }
  
  public int getFieldSize() {
    return q.bitLength();
  }
  
  public ECFieldElement fromBigInteger(BigInteger paramBigInteger) {
    return new Curve25519FieldElement(paramBigInteger);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    return (ECPoint)new Curve25519Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramBoolean);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    return (ECPoint)new Curve25519Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement, paramBoolean);
  }
  
  public ECPoint getInfinity() {
    return (ECPoint)this.infinity;
  }
}
