package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public class SecT131R2Curve extends ECCurve.AbstractF2m {
  private static final int SecT131R2_DEFAULT_COORDS = 6;
  
  protected SecT131R2Point infinity = new SecT131R2Point((ECCurve)this, null, null);
  
  public SecT131R2Curve() {
    super(131, 2, 3, 8);
  }
  
  protected ECCurve cloneCurve() {
    return (ECCurve)new SecT131R2Curve();
  }
  
  public boolean supportsCoordinateSystem(int paramInt) {
    switch (paramInt) {
      case 6:
        return true;
    } 
    return false;
  }
  
  public int getFieldSize() {
    return 131;
  }
  
  public ECFieldElement fromBigInteger(BigInteger paramBigInteger) {
    return new SecT131FieldElement(paramBigInteger);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    return (ECPoint)new SecT131R2Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramBoolean);
  }
  
  protected ECPoint createRawPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    return (ECPoint)new SecT131R2Point((ECCurve)this, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement, paramBoolean);
  }
  
  public ECPoint getInfinity() {
    return (ECPoint)this.infinity;
  }
  
  public boolean isKoblitz() {
    return false;
  }
  
  public int getM() {
    return 131;
  }
  
  public boolean isTrinomial() {
    return false;
  }
  
  public int getK1() {
    return 2;
  }
  
  public int getK2() {
    return 3;
  }
  
  public int getK3() {
    return 8;
  }
}
