package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;

public class SecP160K1Point extends ECPoint.AbstractFp {
  public SecP160K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecP160K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecP160K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecP160K1Point(null, getAffineXCoord(), getAffineYCoord());
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
    SecP160R2FieldElement secP160R2FieldElement1 = (SecP160R2FieldElement)this.x;
    SecP160R2FieldElement secP160R2FieldElement2 = (SecP160R2FieldElement)this.y;
    SecP160R2FieldElement secP160R2FieldElement3 = (SecP160R2FieldElement)paramECPoint.getXCoord();
    SecP160R2FieldElement secP160R2FieldElement4 = (SecP160R2FieldElement)paramECPoint.getYCoord();
    SecP160R2FieldElement secP160R2FieldElement5 = (SecP160R2FieldElement)this.zs[0];
    SecP160R2FieldElement secP160R2FieldElement6 = (SecP160R2FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat160.createExt();
    int[] arrayOfInt2 = Nat160.create();
    int[] arrayOfInt3 = Nat160.create();
    int[] arrayOfInt4 = Nat160.create();
    boolean bool1 = secP160R2FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = secP160R2FieldElement3.x;
      arrayOfInt6 = secP160R2FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SecP160R2Field.square(secP160R2FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SecP160R2Field.multiply(arrayOfInt6, secP160R2FieldElement3.x, arrayOfInt5);
      SecP160R2Field.multiply(arrayOfInt6, secP160R2FieldElement5.x, arrayOfInt6);
      SecP160R2Field.multiply(arrayOfInt6, secP160R2FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = secP160R2FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = secP160R2FieldElement1.x;
      arrayOfInt8 = secP160R2FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SecP160R2Field.square(secP160R2FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SecP160R2Field.multiply(arrayOfInt8, secP160R2FieldElement1.x, arrayOfInt7);
      SecP160R2Field.multiply(arrayOfInt8, secP160R2FieldElement6.x, arrayOfInt8);
      SecP160R2Field.multiply(arrayOfInt8, secP160R2FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat160.create();
    SecP160R2Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    SecP160R2Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat160.isZero(arrayOfInt9))
      return Nat160.isZero(arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SecP160R2Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat160.create();
    SecP160R2Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP160R2Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SecP160R2Field.negate(arrayOfInt12, arrayOfInt12);
    Nat160.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat160.addBothTo(arrayOfInt13, arrayOfInt13, arrayOfInt12);
    SecP160R2Field.reduce32(i, arrayOfInt12);
    SecP160R2FieldElement secP160R2FieldElement7 = new SecP160R2FieldElement(arrayOfInt4);
    SecP160R2Field.square(arrayOfInt10, secP160R2FieldElement7.x);
    SecP160R2Field.subtract(secP160R2FieldElement7.x, arrayOfInt12, secP160R2FieldElement7.x);
    SecP160R2FieldElement secP160R2FieldElement8 = new SecP160R2FieldElement(arrayOfInt12);
    SecP160R2Field.subtract(arrayOfInt13, secP160R2FieldElement7.x, secP160R2FieldElement8.x);
    SecP160R2Field.multiplyAddToExt(secP160R2FieldElement8.x, arrayOfInt10, arrayOfInt1);
    SecP160R2Field.reduce(arrayOfInt1, secP160R2FieldElement8.x);
    SecP160R2FieldElement secP160R2FieldElement9 = new SecP160R2FieldElement(arrayOfInt9);
    if (!bool1)
      SecP160R2Field.multiply(secP160R2FieldElement9.x, secP160R2FieldElement5.x, secP160R2FieldElement9.x); 
    if (!bool2)
      SecP160R2Field.multiply(secP160R2FieldElement9.x, secP160R2FieldElement6.x, secP160R2FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { secP160R2FieldElement9 };
    return (ECPoint)new SecP160K1Point(eCCurve, secP160R2FieldElement7, secP160R2FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecP160R2FieldElement secP160R2FieldElement1 = (SecP160R2FieldElement)this.y;
    if (secP160R2FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecP160R2FieldElement secP160R2FieldElement2 = (SecP160R2FieldElement)this.x;
    SecP160R2FieldElement secP160R2FieldElement3 = (SecP160R2FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat160.create();
    SecP160R2Field.square(secP160R2FieldElement1.x, arrayOfInt1);
    int[] arrayOfInt2 = Nat160.create();
    SecP160R2Field.square(arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat160.create();
    SecP160R2Field.square(secP160R2FieldElement2.x, arrayOfInt3);
    int i = Nat160.addBothTo(arrayOfInt3, arrayOfInt3, arrayOfInt3);
    SecP160R2Field.reduce32(i, arrayOfInt3);
    int[] arrayOfInt4 = arrayOfInt1;
    SecP160R2Field.multiply(arrayOfInt1, secP160R2FieldElement2.x, arrayOfInt4);
    i = Nat.shiftUpBits(5, arrayOfInt4, 2, 0);
    SecP160R2Field.reduce32(i, arrayOfInt4);
    int[] arrayOfInt5 = Nat160.create();
    i = Nat.shiftUpBits(5, arrayOfInt2, 3, 0, arrayOfInt5);
    SecP160R2Field.reduce32(i, arrayOfInt5);
    SecP160R2FieldElement secP160R2FieldElement4 = new SecP160R2FieldElement(arrayOfInt2);
    SecP160R2Field.square(arrayOfInt3, secP160R2FieldElement4.x);
    SecP160R2Field.subtract(secP160R2FieldElement4.x, arrayOfInt4, secP160R2FieldElement4.x);
    SecP160R2Field.subtract(secP160R2FieldElement4.x, arrayOfInt4, secP160R2FieldElement4.x);
    SecP160R2FieldElement secP160R2FieldElement5 = new SecP160R2FieldElement(arrayOfInt4);
    SecP160R2Field.subtract(arrayOfInt4, secP160R2FieldElement4.x, secP160R2FieldElement5.x);
    SecP160R2Field.multiply(secP160R2FieldElement5.x, arrayOfInt3, secP160R2FieldElement5.x);
    SecP160R2Field.subtract(secP160R2FieldElement5.x, arrayOfInt5, secP160R2FieldElement5.x);
    SecP160R2FieldElement secP160R2FieldElement6 = new SecP160R2FieldElement(arrayOfInt3);
    SecP160R2Field.twice(secP160R2FieldElement1.x, secP160R2FieldElement6.x);
    if (!secP160R2FieldElement3.isOne())
      SecP160R2Field.multiply(secP160R2FieldElement6.x, secP160R2FieldElement3.x, secP160R2FieldElement6.x); 
    return (ECPoint)new SecP160K1Point(eCCurve, secP160R2FieldElement4, secP160R2FieldElement5, new ECFieldElement[] { secP160R2FieldElement6 }, this.withCompression);
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
    return (ECPoint)(isInfinity() ? this : new SecP160K1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
