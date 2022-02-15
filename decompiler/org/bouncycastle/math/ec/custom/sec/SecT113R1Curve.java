package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public class SecT113R1Curve extends ECCurve.AbstractF2m {
  private static final int SecT113R1_DEFAULT_COORDS = 6;
  
  protected SecT113R1Point infinity = new SecT113R1Point((ECCurve)this, null, null);
  
  public SecT113R1Curve() {
    super(113, 9, 0, 0);
  }
  
  protected ECCurve cloneCurve() {
    return (ECCurve)new SecT113R1Curve();
  }
  
  public boolean supportsCoordinateSystem(int paramInt) {
    switch (paramInt) {
      case 6:
        return true;
    } 
    return false;
  }
  
  public int getFieldSize() {
    return 113;
  }
  
  public ECFieldElement fromBigInteger(BigInteger paramBigInteger) {
    return new SecT113FieldElement(paramBigInteger);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    return (ECPoint)new SecT113R1Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramBoolean);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    return (ECPoint)new SecT113R1Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement, paramBoolean);
  }
  
  public ECPoint getInfinity() {
    return (ECPoint)this.infinity;
  }
  
  public boolean isKoblitz() {
    return false;
  }
  
  public int getM() {
    return 113;
  }
  
  public boolean isTrinomial() {
    return true;
  }
  
  public int getK1() {
    return 9;
  }
  
  public int getK2() {
    return 0;
  }
  
  public int getK3() {
    return 0;
  }
}
