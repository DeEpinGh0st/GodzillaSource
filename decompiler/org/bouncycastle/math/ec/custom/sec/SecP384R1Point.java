package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat384;

public class SecP384R1Point extends ECPoint.AbstractFp {
  public SecP384R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecP384R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecP384R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecP384R1Point(null, getAffineXCoord(), getAffineYCoord());
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
    SecP384R1FieldElement secP384R1FieldElement1 = (SecP384R1FieldElement)this.x;
    SecP384R1FieldElement secP384R1FieldElement2 = (SecP384R1FieldElement)this.y;
    SecP384R1FieldElement secP384R1FieldElement3 = (SecP384R1FieldElement)paramECPoint.getXCoord();
    SecP384R1FieldElement secP384R1FieldElement4 = (SecP384R1FieldElement)paramECPoint.getYCoord();
    SecP384R1FieldElement secP384R1FieldElement5 = (SecP384R1FieldElement)this.zs[0];
    SecP384R1FieldElement secP384R1FieldElement6 = (SecP384R1FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat.create(24);
    int[] arrayOfInt2 = Nat.create(24);
    int[] arrayOfInt3 = Nat.create(12);
    int[] arrayOfInt4 = Nat.create(12);
    boolean bool1 = secP384R1FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = secP384R1FieldElement3.x;
      arrayOfInt6 = secP384R1FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SecP384R1Field.square(secP384R1FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SecP384R1Field.multiply(arrayOfInt6, secP384R1FieldElement3.x, arrayOfInt5);
      SecP384R1Field.multiply(arrayOfInt6, secP384R1FieldElement5.x, arrayOfInt6);
      SecP384R1Field.multiply(arrayOfInt6, secP384R1FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = secP384R1FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = secP384R1FieldElement1.x;
      arrayOfInt8 = secP384R1FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SecP384R1Field.square(secP384R1FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SecP384R1Field.multiply(arrayOfInt8, secP384R1FieldElement1.x, arrayOfInt7);
      SecP384R1Field.multiply(arrayOfInt8, secP384R1FieldElement6.x, arrayOfInt8);
      SecP384R1Field.multiply(arrayOfInt8, secP384R1FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat.create(12);
    SecP384R1Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = Nat.create(12);
    SecP384R1Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat.isZero(12, arrayOfInt9))
      return Nat.isZero(12, arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SecP384R1Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat.create(12);
    SecP384R1Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP384R1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SecP384R1Field.negate(arrayOfInt12, arrayOfInt12);
    Nat384.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat.addBothTo(12, arrayOfInt13, arrayOfInt13, arrayOfInt12);
    SecP384R1Field.reduce32(i, arrayOfInt12);
    SecP384R1FieldElement secP384R1FieldElement7 = new SecP384R1FieldElement(arrayOfInt4);
    SecP384R1Field.square(arrayOfInt10, secP384R1FieldElement7.x);
    SecP384R1Field.subtract(secP384R1FieldElement7.x, arrayOfInt12, secP384R1FieldElement7.x);
    SecP384R1FieldElement secP384R1FieldElement8 = new SecP384R1FieldElement(arrayOfInt12);
    SecP384R1Field.subtract(arrayOfInt13, secP384R1FieldElement7.x, secP384R1FieldElement8.x);
    Nat384.mul(secP384R1FieldElement8.x, arrayOfInt10, arrayOfInt2);
    SecP384R1Field.addExt(arrayOfInt1, arrayOfInt2, arrayOfInt1);
    SecP384R1Field.reduce(arrayOfInt1, secP384R1FieldElement8.x);
    SecP384R1FieldElement secP384R1FieldElement9 = new SecP384R1FieldElement(arrayOfInt9);
    if (!bool1)
      SecP384R1Field.multiply(secP384R1FieldElement9.x, secP384R1FieldElement5.x, secP384R1FieldElement9.x); 
    if (!bool2)
      SecP384R1Field.multiply(secP384R1FieldElement9.x, secP384R1FieldElement6.x, secP384R1FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { secP384R1FieldElement9 };
    return (ECPoint)new SecP384R1Point(eCCurve, secP384R1FieldElement7, secP384R1FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecP384R1FieldElement secP384R1FieldElement1 = (SecP384R1FieldElement)this.y;
    if (secP384R1FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecP384R1FieldElement secP384R1FieldElement2 = (SecP384R1FieldElement)this.x;
    SecP384R1FieldElement secP384R1FieldElement3 = (SecP384R1FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat.create(12);
    int[] arrayOfInt2 = Nat.create(12);
    int[] arrayOfInt3 = Nat.create(12);
    SecP384R1Field.square(secP384R1FieldElement1.x, arrayOfInt3);
    int[] arrayOfInt4 = Nat.create(12);
    SecP384R1Field.square(arrayOfInt3, arrayOfInt4);
    boolean bool = secP384R1FieldElement3.isOne();
    int[] arrayOfInt5 = secP384R1FieldElement3.x;
    if (!bool) {
      arrayOfInt5 = arrayOfInt2;
      SecP384R1Field.square(secP384R1FieldElement3.x, arrayOfInt5);
    } 
    SecP384R1Field.subtract(secP384R1FieldElement2.x, arrayOfInt5, arrayOfInt1);
    int[] arrayOfInt6 = arrayOfInt2;
    SecP384R1Field.add(secP384R1FieldElement2.x, arrayOfInt5, arrayOfInt6);
    SecP384R1Field.multiply(arrayOfInt6, arrayOfInt1, arrayOfInt6);
    int i = Nat.addBothTo(12, arrayOfInt6, arrayOfInt6, arrayOfInt6);
    SecP384R1Field.reduce32(i, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt3;
    SecP384R1Field.multiply(arrayOfInt3, secP384R1FieldElement2.x, arrayOfInt7);
    i = Nat.shiftUpBits(12, arrayOfInt7, 2, 0);
    SecP384R1Field.reduce32(i, arrayOfInt7);
    i = Nat.shiftUpBits(12, arrayOfInt4, 3, 0, arrayOfInt1);
    SecP384R1Field.reduce32(i, arrayOfInt1);
    SecP384R1FieldElement secP384R1FieldElement4 = new SecP384R1FieldElement(arrayOfInt4);
    SecP384R1Field.square(arrayOfInt6, secP384R1FieldElement4.x);
    SecP384R1Field.subtract(secP384R1FieldElement4.x, arrayOfInt7, secP384R1FieldElement4.x);
    SecP384R1Field.subtract(secP384R1FieldElement4.x, arrayOfInt7, secP384R1FieldElement4.x);
    SecP384R1FieldElement secP384R1FieldElement5 = new SecP384R1FieldElement(arrayOfInt7);
    SecP384R1Field.subtract(arrayOfInt7, secP384R1FieldElement4.x, secP384R1FieldElement5.x);
    SecP384R1Field.multiply(secP384R1FieldElement5.x, arrayOfInt6, secP384R1FieldElement5.x);
    SecP384R1Field.subtract(secP384R1FieldElement5.x, arrayOfInt1, secP384R1FieldElement5.x);
    SecP384R1FieldElement secP384R1FieldElement6 = new SecP384R1FieldElement(arrayOfInt6);
    SecP384R1Field.twice(secP384R1FieldElement1.x, secP384R1FieldElement6.x);
    if (!bool)
      SecP384R1Field.multiply(secP384R1FieldElement6.x, secP384R1FieldElement3.x, secP384R1FieldElement6.x); 
    return (ECPoint)new SecP384R1Point(eCCurve, secP384R1FieldElement4, secP384R1FieldElement5, new ECFieldElement[] { secP384R1FieldElement6 }, this.withCompression);
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
    return (ECPoint)(isInfinity() ? this : new SecP384R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
