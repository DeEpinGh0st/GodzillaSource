package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;

public class SecP160R1Point extends ECPoint.AbstractFp {
  public SecP160R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecP160R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecP160R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecP160R1Point(null, getAffineXCoord(), getAffineYCoord());
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
    SecP160R1FieldElement secP160R1FieldElement1 = (SecP160R1FieldElement)this.x;
    SecP160R1FieldElement secP160R1FieldElement2 = (SecP160R1FieldElement)this.y;
    SecP160R1FieldElement secP160R1FieldElement3 = (SecP160R1FieldElement)paramECPoint.getXCoord();
    SecP160R1FieldElement secP160R1FieldElement4 = (SecP160R1FieldElement)paramECPoint.getYCoord();
    SecP160R1FieldElement secP160R1FieldElement5 = (SecP160R1FieldElement)this.zs[0];
    SecP160R1FieldElement secP160R1FieldElement6 = (SecP160R1FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat160.createExt();
    int[] arrayOfInt2 = Nat160.create();
    int[] arrayOfInt3 = Nat160.create();
    int[] arrayOfInt4 = Nat160.create();
    boolean bool1 = secP160R1FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = secP160R1FieldElement3.x;
      arrayOfInt6 = secP160R1FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SecP160R1Field.square(secP160R1FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SecP160R1Field.multiply(arrayOfInt6, secP160R1FieldElement3.x, arrayOfInt5);
      SecP160R1Field.multiply(arrayOfInt6, secP160R1FieldElement5.x, arrayOfInt6);
      SecP160R1Field.multiply(arrayOfInt6, secP160R1FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = secP160R1FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = secP160R1FieldElement1.x;
      arrayOfInt8 = secP160R1FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SecP160R1Field.square(secP160R1FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SecP160R1Field.multiply(arrayOfInt8, secP160R1FieldElement1.x, arrayOfInt7);
      SecP160R1Field.multiply(arrayOfInt8, secP160R1FieldElement6.x, arrayOfInt8);
      SecP160R1Field.multiply(arrayOfInt8, secP160R1FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat160.create();
    SecP160R1Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    SecP160R1Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat160.isZero(arrayOfInt9))
      return Nat160.isZero(arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SecP160R1Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat160.create();
    SecP160R1Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP160R1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SecP160R1Field.negate(arrayOfInt12, arrayOfInt12);
    Nat160.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat160.addBothTo(arrayOfInt13, arrayOfInt13, arrayOfInt12);
    SecP160R1Field.reduce32(i, arrayOfInt12);
    SecP160R1FieldElement secP160R1FieldElement7 = new SecP160R1FieldElement(arrayOfInt4);
    SecP160R1Field.square(arrayOfInt10, secP160R1FieldElement7.x);
    SecP160R1Field.subtract(secP160R1FieldElement7.x, arrayOfInt12, secP160R1FieldElement7.x);
    SecP160R1FieldElement secP160R1FieldElement8 = new SecP160R1FieldElement(arrayOfInt12);
    SecP160R1Field.subtract(arrayOfInt13, secP160R1FieldElement7.x, secP160R1FieldElement8.x);
    SecP160R1Field.multiplyAddToExt(secP160R1FieldElement8.x, arrayOfInt10, arrayOfInt1);
    SecP160R1Field.reduce(arrayOfInt1, secP160R1FieldElement8.x);
    SecP160R1FieldElement secP160R1FieldElement9 = new SecP160R1FieldElement(arrayOfInt9);
    if (!bool1)
      SecP160R1Field.multiply(secP160R1FieldElement9.x, secP160R1FieldElement5.x, secP160R1FieldElement9.x); 
    if (!bool2)
      SecP160R1Field.multiply(secP160R1FieldElement9.x, secP160R1FieldElement6.x, secP160R1FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { secP160R1FieldElement9 };
    return (ECPoint)new SecP160R1Point(eCCurve, secP160R1FieldElement7, secP160R1FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecP160R1FieldElement secP160R1FieldElement1 = (SecP160R1FieldElement)this.y;
    if (secP160R1FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecP160R1FieldElement secP160R1FieldElement2 = (SecP160R1FieldElement)this.x;
    SecP160R1FieldElement secP160R1FieldElement3 = (SecP160R1FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat160.create();
    int[] arrayOfInt2 = Nat160.create();
    int[] arrayOfInt3 = Nat160.create();
    SecP160R1Field.square(secP160R1FieldElement1.x, arrayOfInt3);
    int[] arrayOfInt4 = Nat160.create();
    SecP160R1Field.square(arrayOfInt3, arrayOfInt4);
    boolean bool = secP160R1FieldElement3.isOne();
    int[] arrayOfInt5 = secP160R1FieldElement3.x;
    if (!bool) {
      arrayOfInt5 = arrayOfInt2;
      SecP160R1Field.square(secP160R1FieldElement3.x, arrayOfInt5);
    } 
    SecP160R1Field.subtract(secP160R1FieldElement2.x, arrayOfInt5, arrayOfInt1);
    int[] arrayOfInt6 = arrayOfInt2;
    SecP160R1Field.add(secP160R1FieldElement2.x, arrayOfInt5, arrayOfInt6);
    SecP160R1Field.multiply(arrayOfInt6, arrayOfInt1, arrayOfInt6);
    int i = Nat160.addBothTo(arrayOfInt6, arrayOfInt6, arrayOfInt6);
    SecP160R1Field.reduce32(i, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt3;
    SecP160R1Field.multiply(arrayOfInt3, secP160R1FieldElement2.x, arrayOfInt7);
    i = Nat.shiftUpBits(5, arrayOfInt7, 2, 0);
    SecP160R1Field.reduce32(i, arrayOfInt7);
    i = Nat.shiftUpBits(5, arrayOfInt4, 3, 0, arrayOfInt1);
    SecP160R1Field.reduce32(i, arrayOfInt1);
    SecP160R1FieldElement secP160R1FieldElement4 = new SecP160R1FieldElement(arrayOfInt4);
    SecP160R1Field.square(arrayOfInt6, secP160R1FieldElement4.x);
    SecP160R1Field.subtract(secP160R1FieldElement4.x, arrayOfInt7, secP160R1FieldElement4.x);
    SecP160R1Field.subtract(secP160R1FieldElement4.x, arrayOfInt7, secP160R1FieldElement4.x);
    SecP160R1FieldElement secP160R1FieldElement5 = new SecP160R1FieldElement(arrayOfInt7);
    SecP160R1Field.subtract(arrayOfInt7, secP160R1FieldElement4.x, secP160R1FieldElement5.x);
    SecP160R1Field.multiply(secP160R1FieldElement5.x, arrayOfInt6, secP160R1FieldElement5.x);
    SecP160R1Field.subtract(secP160R1FieldElement5.x, arrayOfInt1, secP160R1FieldElement5.x);
    SecP160R1FieldElement secP160R1FieldElement6 = new SecP160R1FieldElement(arrayOfInt6);
    SecP160R1Field.twice(secP160R1FieldElement1.x, secP160R1FieldElement6.x);
    if (!bool)
      SecP160R1Field.multiply(secP160R1FieldElement6.x, secP160R1FieldElement3.x, secP160R1FieldElement6.x); 
    return (ECPoint)new SecP160R1Point(eCCurve, secP160R1FieldElement4, secP160R1FieldElement5, new ECFieldElement[] { secP160R1FieldElement6 }, this.withCompression);
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
    return (ECPoint)(isInfinity() ? this : new SecP160R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
