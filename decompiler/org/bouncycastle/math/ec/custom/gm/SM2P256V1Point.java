package org.bouncycastle.math.ec.custom.gm;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SM2P256V1Point extends ECPoint.AbstractFp {
  public SM2P256V1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SM2P256V1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SM2P256V1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SM2P256V1Point(null, getAffineXCoord(), getAffineYCoord());
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
    SM2P256V1FieldElement sM2P256V1FieldElement1 = (SM2P256V1FieldElement)this.x;
    SM2P256V1FieldElement sM2P256V1FieldElement2 = (SM2P256V1FieldElement)this.y;
    SM2P256V1FieldElement sM2P256V1FieldElement3 = (SM2P256V1FieldElement)paramECPoint.getXCoord();
    SM2P256V1FieldElement sM2P256V1FieldElement4 = (SM2P256V1FieldElement)paramECPoint.getYCoord();
    SM2P256V1FieldElement sM2P256V1FieldElement5 = (SM2P256V1FieldElement)this.zs[0];
    SM2P256V1FieldElement sM2P256V1FieldElement6 = (SM2P256V1FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat256.createExt();
    int[] arrayOfInt2 = Nat256.create();
    int[] arrayOfInt3 = Nat256.create();
    int[] arrayOfInt4 = Nat256.create();
    boolean bool1 = sM2P256V1FieldElement5.isOne();
    if (bool1) {
      arrayOfInt5 = sM2P256V1FieldElement3.x;
      arrayOfInt6 = sM2P256V1FieldElement4.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      SM2P256V1Field.square(sM2P256V1FieldElement5.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      SM2P256V1Field.multiply(arrayOfInt6, sM2P256V1FieldElement3.x, arrayOfInt5);
      SM2P256V1Field.multiply(arrayOfInt6, sM2P256V1FieldElement5.x, arrayOfInt6);
      SM2P256V1Field.multiply(arrayOfInt6, sM2P256V1FieldElement4.x, arrayOfInt6);
    } 
    boolean bool2 = sM2P256V1FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = sM2P256V1FieldElement1.x;
      arrayOfInt8 = sM2P256V1FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      SM2P256V1Field.square(sM2P256V1FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      SM2P256V1Field.multiply(arrayOfInt8, sM2P256V1FieldElement1.x, arrayOfInt7);
      SM2P256V1Field.multiply(arrayOfInt8, sM2P256V1FieldElement6.x, arrayOfInt8);
      SM2P256V1Field.multiply(arrayOfInt8, sM2P256V1FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat256.create();
    SM2P256V1Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    SM2P256V1Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat256.isZero(arrayOfInt9))
      return Nat256.isZero(arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = arrayOfInt3;
    SM2P256V1Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat256.create();
    SM2P256V1Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SM2P256V1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    SM2P256V1Field.negate(arrayOfInt12, arrayOfInt12);
    Nat256.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat256.addBothTo(arrayOfInt13, arrayOfInt13, arrayOfInt12);
    SM2P256V1Field.reduce32(i, arrayOfInt12);
    SM2P256V1FieldElement sM2P256V1FieldElement7 = new SM2P256V1FieldElement(arrayOfInt4);
    SM2P256V1Field.square(arrayOfInt10, sM2P256V1FieldElement7.x);
    SM2P256V1Field.subtract(sM2P256V1FieldElement7.x, arrayOfInt12, sM2P256V1FieldElement7.x);
    SM2P256V1FieldElement sM2P256V1FieldElement8 = new SM2P256V1FieldElement(arrayOfInt12);
    SM2P256V1Field.subtract(arrayOfInt13, sM2P256V1FieldElement7.x, sM2P256V1FieldElement8.x);
    SM2P256V1Field.multiplyAddToExt(sM2P256V1FieldElement8.x, arrayOfInt10, arrayOfInt1);
    SM2P256V1Field.reduce(arrayOfInt1, sM2P256V1FieldElement8.x);
    SM2P256V1FieldElement sM2P256V1FieldElement9 = new SM2P256V1FieldElement(arrayOfInt9);
    if (!bool1)
      SM2P256V1Field.multiply(sM2P256V1FieldElement9.x, sM2P256V1FieldElement5.x, sM2P256V1FieldElement9.x); 
    if (!bool2)
      SM2P256V1Field.multiply(sM2P256V1FieldElement9.x, sM2P256V1FieldElement6.x, sM2P256V1FieldElement9.x); 
    ECFieldElement[] arrayOfECFieldElement = { sM2P256V1FieldElement9 };
    return (ECPoint)new SM2P256V1Point(eCCurve, sM2P256V1FieldElement7, sM2P256V1FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SM2P256V1FieldElement sM2P256V1FieldElement1 = (SM2P256V1FieldElement)this.y;
    if (sM2P256V1FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SM2P256V1FieldElement sM2P256V1FieldElement2 = (SM2P256V1FieldElement)this.x;
    SM2P256V1FieldElement sM2P256V1FieldElement3 = (SM2P256V1FieldElement)this.zs[0];
    int[] arrayOfInt1 = Nat256.create();
    int[] arrayOfInt2 = Nat256.create();
    int[] arrayOfInt3 = Nat256.create();
    SM2P256V1Field.square(sM2P256V1FieldElement1.x, arrayOfInt3);
    int[] arrayOfInt4 = Nat256.create();
    SM2P256V1Field.square(arrayOfInt3, arrayOfInt4);
    boolean bool = sM2P256V1FieldElement3.isOne();
    int[] arrayOfInt5 = sM2P256V1FieldElement3.x;
    if (!bool) {
      arrayOfInt5 = arrayOfInt2;
      SM2P256V1Field.square(sM2P256V1FieldElement3.x, arrayOfInt5);
    } 
    SM2P256V1Field.subtract(sM2P256V1FieldElement2.x, arrayOfInt5, arrayOfInt1);
    int[] arrayOfInt6 = arrayOfInt2;
    SM2P256V1Field.add(sM2P256V1FieldElement2.x, arrayOfInt5, arrayOfInt6);
    SM2P256V1Field.multiply(arrayOfInt6, arrayOfInt1, arrayOfInt6);
    int i = Nat256.addBothTo(arrayOfInt6, arrayOfInt6, arrayOfInt6);
    SM2P256V1Field.reduce32(i, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt3;
    SM2P256V1Field.multiply(arrayOfInt3, sM2P256V1FieldElement2.x, arrayOfInt7);
    i = Nat.shiftUpBits(8, arrayOfInt7, 2, 0);
    SM2P256V1Field.reduce32(i, arrayOfInt7);
    i = Nat.shiftUpBits(8, arrayOfInt4, 3, 0, arrayOfInt1);
    SM2P256V1Field.reduce32(i, arrayOfInt1);
    SM2P256V1FieldElement sM2P256V1FieldElement4 = new SM2P256V1FieldElement(arrayOfInt4);
    SM2P256V1Field.square(arrayOfInt6, sM2P256V1FieldElement4.x);
    SM2P256V1Field.subtract(sM2P256V1FieldElement4.x, arrayOfInt7, sM2P256V1FieldElement4.x);
    SM2P256V1Field.subtract(sM2P256V1FieldElement4.x, arrayOfInt7, sM2P256V1FieldElement4.x);
    SM2P256V1FieldElement sM2P256V1FieldElement5 = new SM2P256V1FieldElement(arrayOfInt7);
    SM2P256V1Field.subtract(arrayOfInt7, sM2P256V1FieldElement4.x, sM2P256V1FieldElement5.x);
    SM2P256V1Field.multiply(sM2P256V1FieldElement5.x, arrayOfInt6, sM2P256V1FieldElement5.x);
    SM2P256V1Field.subtract(sM2P256V1FieldElement5.x, arrayOfInt1, sM2P256V1FieldElement5.x);
    SM2P256V1FieldElement sM2P256V1FieldElement6 = new SM2P256V1FieldElement(arrayOfInt6);
    SM2P256V1Field.twice(sM2P256V1FieldElement1.x, sM2P256V1FieldElement6.x);
    if (!bool)
      SM2P256V1Field.multiply(sM2P256V1FieldElement6.x, sM2P256V1FieldElement3.x, sM2P256V1FieldElement6.x); 
    return (ECPoint)new SM2P256V1Point(eCCurve, sM2P256V1FieldElement4, sM2P256V1FieldElement5, new ECFieldElement[] { sM2P256V1FieldElement6 }, this.withCompression);
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
    return (ECPoint)(isInfinity() ? this : new SM2P256V1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression));
  }
}
