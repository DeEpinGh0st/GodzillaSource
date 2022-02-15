package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192K1Point extends ECPoint.AbstractFp {
  public SecP192K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecP192K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecP192K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecP192K1Point(null, getAffineXCoord(), getAffineYCoord());
  }
  
  public ECPoint add(ECPoint paramECPoint) {
    int[] arrayOfInt5;
    int[] arrayOfInt6;
    int[] arrayOfInt7;
    int[] arrayOfInt8;
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return (ECPoint)this; 
    if (this == paramECPoint)
      return twice(); 
    ECCurve eCCurve = getCurve();
    SecP192K1FieldElement secP192K1FieldElement1 = (SecP192K1FieldElement)this.x;
    SecP192K1FieldElement secP192K1FieldElement2 = (SecP192K1FieldElement)this.y;
    SecP192K1FieldElement secP192K1FieldElement3 = (SecP192K1FieldElement)paramECPoint.getXCoord();
    SecP192K1FieldElement secP192K1FieldElement4 = (SecP192K1FieldElement)paramECPoint.getYCoord();
    SecP192K1FieldElement secP192K1FieldElement5 = (SecP192K1FieldElement)this.zs[0];
    SecP192K1FieldElement secP192K1FieldElement6 = (SecP192K1FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat192.createExt();
    int[] arrayOfInt2 = Nat192.create();
    int[] arrayOfInt3 = Nat192.create();
    int[] arrayOfInt4 = Nat192.create();
    boolean bool1 = secP192K1FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = secP192K1FieldElement3.x;
      arrayOfInt6 = secP192K1FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SecP192K1Field.square(secP192K1FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SecP192K1Field.multiply(arrayOfInt6, secP192K1FieldElement3.x, arrayOfInt5);
      SecP192K1Field.multiply(arrayOfInt6, secP192K1FieldElement5.x, arrayOfInt6);
      SecP192K1Field.multiply(arrayOfInt6, secP192K1FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = secP192K1FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = secP192K1FieldElement1.x;
      arrayOfInt8 = secP192K1FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SecP192K1Field.square(secP192K1FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SecP192K1Field.multiply(arrayOfInt8, secP192K1FieldElement1.x, arrayOfInt7);
      SecP192K1Field.multiply(arrayOfInt8, secP192K1FieldElement6.x, arrayOfInt8);
      SecP192K1Field.multiply(arrayOfInt8, secP192K1FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat192.create();
    SecP192K1Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    SecP192K1Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat192.isZero(arrayOfInt9))
      return Nat192.isZero(arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SecP192K1Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat192.create();
    SecP192K1Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP192K1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SecP192K1Field.negate(arrayOfInt12, arrayOfInt12);
    Nat192.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat192.addBothTo(arrayOfInt13, arrayOfInt13, arrayOfInt12);
    SecP192K1Field.reduce32(i, arrayOfInt12);
    SecP192K1FieldElement secP192K1FieldElement7 = new SecP192K1FieldElement(arrayOfInt4);
    SecP192K1Field.square(arrayOfInt10, secP192K1FieldElement7.x);
    SecP192K1Field.subtract(secP192K1FieldElement7.x, arrayOfInt12, secP192K1FieldElement7.x);
    SecP192K1FieldElement secP192K1FieldElement8 = new SecP192K1FieldElement(arrayOfInt12);
    SecP192K1Field.subtract(arrayOfInt13, secP192K1FieldElement7.x, secP192K1FieldElement8.x);
    SecP192K1Field.multiplyAddToExt(secP192K1FieldElement8.x, arrayOfInt10, arrayOfInt1);
    SecP192K1Field.reduce(arrayOfInt1, secP192K1FieldElement8.x);
    SecP192K1FieldElement secP192K1FieldElement9 = new SecP192K1FieldElement(arrayOfInt9);
    if (!bool1)
      SecP192K1Field.multiply(secP192K1FieldElement9.x, secP192K1FieldElement5.x, secP192K1FieldElement9.x); 
    if (!bool2)
      SecP192K1Field.multiply(secP192K1FieldElement9.x, secP192K1FieldElement6.x, secP192K1FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { secP192K1FieldElement9 };
    return (ECPoint)new SecP192K1Point(eCCurve, secP192K1FieldElement7, secP192K1FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecP192K1FieldElement secP192K1FieldElement1 = (SecP192K1FieldElement)this.y;
    if (secP192K1FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecP192K1FieldElement secP192K1FieldElement2 = (SecP192K1FieldElement)this.x;
    SecP192K1FieldElement secP192K1FieldElement3 = (SecP192K1FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat192.create();
    SecP192K1Field.square(secP192K1FieldElement1.x, arrayOfInt1);
    int[] arrayOfInt2 = Nat192.create();
    SecP192K1Field.square(arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat192.create();
    SecP192K1Field.square(secP192K1FieldElement2.x, arrayOfInt3);
    int i = Nat192.addBothTo(arrayOfInt3, arrayOfInt3, arrayOfInt3);
    SecP192K1Field.reduce32(i, arrayOfInt3);
    int[] arrayOfInt4 = arrayOfInt1;
    SecP192K1Field.multiply(arrayOfInt1, secP192K1FieldElement2.x, arrayOfInt4);
    i = Nat.shiftUpBits(6, arrayOfInt4, 2, 0);
    SecP192K1Field.reduce32(i, arrayOfInt4);
    int[] arrayOfInt5 = Nat192.create();
    i = Nat.shiftUpBits(6, arrayOfInt2, 3, 0, arrayOfInt5);
    SecP192K1Field.reduce32(i, arrayOfInt5);
    SecP192K1FieldElement secP192K1FieldElement4 = new SecP192K1FieldElement(arrayOfInt2);
    SecP192K1Field.square(arrayOfInt3, secP192K1FieldElement4.x);
    SecP192K1Field.subtract(secP192K1FieldElement4.x, arrayOfInt4, secP192K1FieldElement4.x);
    SecP192K1Field.subtract(secP192K1FieldElement4.x, arrayOfInt4, secP192K1FieldElement4.x);
    SecP192K1FieldElement secP192K1FieldElement5 = new SecP192K1FieldElement(arrayOfInt4);
    SecP192K1Field.subtract(arrayOfInt4, secP192K1FieldElement4.x, secP192K1FieldElement5.x);
    SecP192K1Field.multiply(secP192K1FieldElement5.x, arrayOfInt3, secP192K1FieldElement5.x);
    SecP192K1Field.subtract(secP192K1FieldElement5.x, arrayOfInt5, secP192K1FieldElement5.x);
    SecP192K1FieldElement secP192K1FieldElement6 = new SecP192K1FieldElement(arrayOfInt3);
    SecP192K1Field.twice(secP192K1FieldElement1.x, secP192K1FieldElement6.x);
    if (!secP192K1FieldElement3.isOne())
      SecP192K1Field.multiply(secP192K1FieldElement6.x, secP192K1FieldElement3.x, secP192K1FieldElement6.x); 
    return (ECPoint)new SecP192K1Point(eCCurve, secP192K1FieldElement4, secP192K1FieldElement5, new ECFieldElement[] { secP192K1FieldElement6 }, this.withCompression);
  }
  
  public ECPoint twicePlus(ECPoint paramECPoint) {
    if (this == paramECPoint)
      return threeTimes(); 
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return twice(); 
    ECFieldElement eCFieldElement = this.y;
    return eCFieldElement.isZero() ? paramECPoint : twice().add(paramECPoint);
  }
  
  public ECPoint threeTimes() {
    return (ECPoint)((isInfinity() || this.y.isZero()) ? this : twice().add((ECPoint)this));
  }
  
  public ECPoint negate() {
    return (ECPoint)(isInfinity() ? this : new SecP192K1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
