package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;

public class SecP521R1Point extends ECPoint.AbstractFp {
  public SecP521R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecP521R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecP521R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecP521R1Point(null, getAffineXCoord(), getAffineYCoord());
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
    SecP521R1FieldElement secP521R1FieldElement1 = (SecP521R1FieldElement)this.x;
    SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.y;
    SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)paramECPoint.getXCoord();
    SecP521R1FieldElement secP521R1FieldElement4 = (SecP521R1FieldElement)paramECPoint.getYCoord();
    SecP521R1FieldElement secP521R1FieldElement5 = (SecP521R1FieldElement)this.zs[0];
    SecP521R1FieldElement secP521R1FieldElement6 = (SecP521R1FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat.create(17);
    int[] arrayOfInt2 = Nat.create(17);
    int[] arrayOfInt3 = Nat.create(17);
    int[] arrayOfInt4 = Nat.create(17);
    boolean bool1 = secP521R1FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = secP521R1FieldElement3.x;
      arrayOfInt6 = secP521R1FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SecP521R1Field.square(secP521R1FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SecP521R1Field.multiply(arrayOfInt6, secP521R1FieldElement3.x, arrayOfInt5);
      SecP521R1Field.multiply(arrayOfInt6, secP521R1FieldElement5.x, arrayOfInt6);
      SecP521R1Field.multiply(arrayOfInt6, secP521R1FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = secP521R1FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = secP521R1FieldElement1.x;
      arrayOfInt8 = secP521R1FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SecP521R1Field.square(secP521R1FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SecP521R1Field.multiply(arrayOfInt8, secP521R1FieldElement1.x, arrayOfInt7);
      SecP521R1Field.multiply(arrayOfInt8, secP521R1FieldElement6.x, arrayOfInt8);
      SecP521R1Field.multiply(arrayOfInt8, secP521R1FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat.create(17);
    SecP521R1Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    SecP521R1Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat.isZero(17, arrayOfInt9))
      return Nat.isZero(17, arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SecP521R1Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat.create(17);
    SecP521R1Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP521R1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SecP521R1Field.multiply(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    SecP521R1FieldElement secP521R1FieldElement7 = new SecP521R1FieldElement(arrayOfInt4);
    SecP521R1Field.square(arrayOfInt10, secP521R1FieldElement7.x);
    SecP521R1Field.add(secP521R1FieldElement7.x, arrayOfInt12, secP521R1FieldElement7.x);
    SecP521R1Field.subtract(secP521R1FieldElement7.x, arrayOfInt13, secP521R1FieldElement7.x);
    SecP521R1Field.subtract(secP521R1FieldElement7.x, arrayOfInt13, secP521R1FieldElement7.x);
    SecP521R1FieldElement secP521R1FieldElement8 = new SecP521R1FieldElement(arrayOfInt12);
    SecP521R1Field.subtract(arrayOfInt13, secP521R1FieldElement7.x, secP521R1FieldElement8.x);
    SecP521R1Field.multiply(secP521R1FieldElement8.x, arrayOfInt10, arrayOfInt2);
    SecP521R1Field.subtract(arrayOfInt2, arrayOfInt1, secP521R1FieldElement8.x);
    SecP521R1FieldElement secP521R1FieldElement9 = new SecP521R1FieldElement(arrayOfInt9);
    if (!bool1)
      SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement5.x, secP521R1FieldElement9.x); 
    if (!bool2)
      SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement6.x, secP521R1FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { secP521R1FieldElement9 };
    return (ECPoint)new SecP521R1Point(eCCurve, secP521R1FieldElement7, secP521R1FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecP521R1FieldElement secP521R1FieldElement1 = (SecP521R1FieldElement)this.y;
    if (secP521R1FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.x;
    SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat.create(17);
    int[] arrayOfInt2 = Nat.create(17);
    int[] arrayOfInt3 = Nat.create(17);
    SecP521R1Field.square(secP521R1FieldElement1.x, arrayOfInt3);
    int[] arrayOfInt4 = Nat.create(17);
    SecP521R1Field.square(arrayOfInt3, arrayOfInt4);
    boolean bool = secP521R1FieldElement3.isOne();
    int[] arrayOfInt5 = secP521R1FieldElement3.x;
    if (!bool) {
      arrayOfInt5 = arrayOfInt2;
      SecP521R1Field.square(secP521R1FieldElement3.x, arrayOfInt5);
    } 
    SecP521R1Field.subtract(secP521R1FieldElement2.x, arrayOfInt5, arrayOfInt1);
    int[] arrayOfInt6 = arrayOfInt2;
    SecP521R1Field.add(secP521R1FieldElement2.x, arrayOfInt5, arrayOfInt6);
    SecP521R1Field.multiply(arrayOfInt6, arrayOfInt1, arrayOfInt6);
    Nat.addBothTo(17, arrayOfInt6, arrayOfInt6, arrayOfInt6);
    SecP521R1Field.reduce23(arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt3;
    SecP521R1Field.multiply(arrayOfInt3, secP521R1FieldElement2.x, arrayOfInt7);
    Nat.shiftUpBits(17, arrayOfInt7, 2, 0);
    SecP521R1Field.reduce23(arrayOfInt7);
    Nat.shiftUpBits(17, arrayOfInt4, 3, 0, arrayOfInt1);
    SecP521R1Field.reduce23(arrayOfInt1);
    SecP521R1FieldElement secP521R1FieldElement4 = new SecP521R1FieldElement(arrayOfInt4);
    SecP521R1Field.square(arrayOfInt6, secP521R1FieldElement4.x);
    SecP521R1Field.subtract(secP521R1FieldElement4.x, arrayOfInt7, secP521R1FieldElement4.x);
    SecP521R1Field.subtract(secP521R1FieldElement4.x, arrayOfInt7, secP521R1FieldElement4.x);
    SecP521R1FieldElement secP521R1FieldElement5 = new SecP521R1FieldElement(arrayOfInt7);
    SecP521R1Field.subtract(arrayOfInt7, secP521R1FieldElement4.x, secP521R1FieldElement5.x);
    SecP521R1Field.multiply(secP521R1FieldElement5.x, arrayOfInt6, secP521R1FieldElement5.x);
    SecP521R1Field.subtract(secP521R1FieldElement5.x, arrayOfInt1, secP521R1FieldElement5.x);
    SecP521R1FieldElement secP521R1FieldElement6 = new SecP521R1FieldElement(arrayOfInt6);
    SecP521R1Field.twice(secP521R1FieldElement1.x, secP521R1FieldElement6.x);
    if (!bool)
      SecP521R1Field.multiply(secP521R1FieldElement6.x, secP521R1FieldElement3.x, secP521R1FieldElement6.x); 
    return (ECPoint)new SecP521R1Point(eCCurve, secP521R1FieldElement4, secP521R1FieldElement5, new ECFieldElement[] { secP521R1FieldElement6 }, this.withCompression);
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
  
  protected ECFieldElement two(ECFieldElement paramECFieldElement) {
    return paramECFieldElement.add(paramECFieldElement);
  }
  
  protected ECFieldElement three(ECFieldElement paramECFieldElement) {
    return two(paramECFieldElement).add(paramECFieldElement);
  }
  
  protected ECFieldElement four(ECFieldElement paramECFieldElement) {
    return two(two(paramECFieldElement));
  }
  
  protected ECFieldElement eight(ECFieldElement paramECFieldElement) {
    return four(two(paramECFieldElement));
  }
  
  protected ECFieldElement doubleProductFromSquares(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3, ECFieldElement paramECFieldElement4) {
    return paramECFieldElement1.add(paramECFieldElement2).square().subtract(paramECFieldElement3).subtract(paramECFieldElement4);
  }
  
  public ECPoint negate() {
    return (ECPoint)(isInfinity() ? this : new SecP521R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
