package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;

public class Curve25519Point extends ECPoint.AbstractFp {
  public Curve25519Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public Curve25519Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  Curve25519Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new Curve25519Point(null, getAffineXCoord(), getAffineYCoord());
  }
  
  public ECFieldElement getZCoord(int paramInt) {
    return (paramInt == 1) ? getJacobianModifiedW() : super.getZCoord(paramInt);
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
    Curve25519FieldElement curve25519FieldElement1 = (Curve25519FieldElement)this.x;
    Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement)this.y;
    Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement)this.zs[0];
    Curve25519FieldElement curve25519FieldElement4 = (Curve25519FieldElement)paramECPoint.getXCoord();
    Curve25519FieldElement curve25519FieldElement5 = (Curve25519FieldElement)paramECPoint.getYCoord();
    Curve25519FieldElement curve25519FieldElement6 = (Curve25519FieldElement)paramECPoint.getZCoord(0);
    int[] arrayOfInt1 = Nat256.createExt();
    int[] arrayOfInt2 = Nat256.create();
    int[] arrayOfInt3 = Nat256.create();
    int[] arrayOfInt4 = Nat256.create();
    boolean bool1 = curve25519FieldElement3.isOne();
    if (bool1) {
      arrayOfInt5 = curve25519FieldElement4.x;
      arrayOfInt6 = curve25519FieldElement5.x;
    } else {
      arrayOfInt6 = arrayOfInt3;
      Curve25519Field.square(curve25519FieldElement3.x, arrayOfInt6);
      arrayOfInt5 = arrayOfInt2;
      Curve25519Field.multiply(arrayOfInt6, curve25519FieldElement4.x, arrayOfInt5);
      Curve25519Field.multiply(arrayOfInt6, curve25519FieldElement3.x, arrayOfInt6);
      Curve25519Field.multiply(arrayOfInt6, curve25519FieldElement5.x, arrayOfInt6);
    } 
    boolean bool2 = curve25519FieldElement6.isOne();
    if (bool2) {
      arrayOfInt7 = curve25519FieldElement1.x;
      arrayOfInt8 = curve25519FieldElement2.x;
    } else {
      arrayOfInt8 = arrayOfInt4;
      Curve25519Field.square(curve25519FieldElement6.x, arrayOfInt8);
      arrayOfInt7 = arrayOfInt1;
      Curve25519Field.multiply(arrayOfInt8, curve25519FieldElement1.x, arrayOfInt7);
      Curve25519Field.multiply(arrayOfInt8, curve25519FieldElement6.x, arrayOfInt8);
      Curve25519Field.multiply(arrayOfInt8, curve25519FieldElement2.x, arrayOfInt8);
    } 
    int[] arrayOfInt9 = Nat256.create();
    Curve25519Field.subtract(arrayOfInt7, arrayOfInt5, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt2;
    Curve25519Field.subtract(arrayOfInt8, arrayOfInt6, arrayOfInt10);
    if (Nat256.isZero(arrayOfInt9))
      return Nat256.isZero(arrayOfInt10) ? twice() : eCCurve.getInfinity(); 
    int[] arrayOfInt11 = Nat256.create();
    Curve25519Field.square(arrayOfInt9, arrayOfInt11);
    int[] arrayOfInt12 = Nat256.create();
    Curve25519Field.multiply(arrayOfInt11, arrayOfInt9, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    Curve25519Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt13);
    Curve25519Field.negate(arrayOfInt12, arrayOfInt12);
    Nat256.mul(arrayOfInt8, arrayOfInt12, arrayOfInt1);
    int i = Nat256.addBothTo(arrayOfInt13, arrayOfInt13, arrayOfInt12);
    Curve25519Field.reduce27(i, arrayOfInt12);
    Curve25519FieldElement curve25519FieldElement7 = new Curve25519FieldElement(arrayOfInt4);
    Curve25519Field.square(arrayOfInt10, curve25519FieldElement7.x);
    Curve25519Field.subtract(curve25519FieldElement7.x, arrayOfInt12, curve25519FieldElement7.x);
    Curve25519FieldElement curve25519FieldElement8 = new Curve25519FieldElement(arrayOfInt12);
    Curve25519Field.subtract(arrayOfInt13, curve25519FieldElement7.x, curve25519FieldElement8.x);
    Curve25519Field.multiplyAddToExt(curve25519FieldElement8.x, arrayOfInt10, arrayOfInt1);
    Curve25519Field.reduce(arrayOfInt1, curve25519FieldElement8.x);
    Curve25519FieldElement curve25519FieldElement9 = new Curve25519FieldElement(arrayOfInt9);
    if (!bool1)
      Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement3.x, curve25519FieldElement9.x); 
    if (!bool2)
      Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement6.x, curve25519FieldElement9.x); 
    int[] arrayOfInt14 = (bool1 && bool2) ? arrayOfInt11 : null;
    Curve25519FieldElement curve25519FieldElement10 = calculateJacobianModifiedW(curve25519FieldElement9, arrayOfInt14);
    ECFieldElement[] arrayOfECFieldElement = { curve25519FieldElement9, curve25519FieldElement10 };
    return (ECPoint)new Curve25519Point(eCCurve, curve25519FieldElement7, curve25519FieldElement8, arrayOfECFieldElement, this.withCompression);
  }
  
  public ECPoint twice() {
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    ECFieldElement eCFieldElement = this.y;
    return (ECPoint)(eCFieldElement.isZero() ? eCCurve.getInfinity() : twiceJacobianModified(true));
  }
  
  public ECPoint twicePlus(ECPoint paramECPoint) {
    if (this == paramECPoint)
      return threeTimes(); 
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return twice(); 
    ECFieldElement eCFieldElement = this.y;
    return eCFieldElement.isZero() ? paramECPoint : twiceJacobianModified(false).add(paramECPoint);
  }
  
  public ECPoint threeTimes() {
    if (isInfinity())
      return (ECPoint)this; 
    ECFieldElement eCFieldElement = this.y;
    return (ECPoint)(eCFieldElement.isZero() ? this : twiceJacobianModified(false).add((ECPoint)this));
  }
  
  public ECPoint negate() {
    return (ECPoint)(isInfinity() ? this : new Curve25519Point(getCurve(), this.x, this.y.negate(), this.zs, this.withCompression));
  }
  
  protected Curve25519FieldElement calculateJacobianModifiedW(Curve25519FieldElement paramCurve25519FieldElement, int[] paramArrayOfint) {
    Curve25519FieldElement curve25519FieldElement1 = (Curve25519FieldElement)getCurve().getA();
    if (paramCurve25519FieldElement.isOne())
      return curve25519FieldElement1; 
    Curve25519FieldElement curve25519FieldElement2 = new Curve25519FieldElement();
    if (paramArrayOfint == null) {
      paramArrayOfint = curve25519FieldElement2.x;
      Curve25519Field.square(paramCurve25519FieldElement.x, paramArrayOfint);
    } 
    Curve25519Field.square(paramArrayOfint, curve25519FieldElement2.x);
    Curve25519Field.multiply(curve25519FieldElement2.x, curve25519FieldElement1.x, curve25519FieldElement2.x);
    return curve25519FieldElement2;
  }
  
  protected Curve25519FieldElement getJacobianModifiedW() {
    Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement)this.zs[1];
    if (curve25519FieldElement == null)
      this.zs[1] = curve25519FieldElement = calculateJacobianModifiedW((Curve25519FieldElement)this.zs[0], (int[])null); 
    return curve25519FieldElement;
  }
  
  protected Curve25519Point twiceJacobianModified(boolean paramBoolean) {
    Curve25519FieldElement curve25519FieldElement1 = (Curve25519FieldElement)this.x;
    Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement)this.y;
    Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement)this.zs[0];
    Curve25519FieldElement curve25519FieldElement4 = getJacobianModifiedW();
    int[] arrayOfInt1 = Nat256.create();
    Curve25519Field.square(curve25519FieldElement1.x, arrayOfInt1);
    int i = Nat256.addBothTo(arrayOfInt1, arrayOfInt1, arrayOfInt1);
    i += Nat256.addTo(curve25519FieldElement4.x, arrayOfInt1);
    Curve25519Field.reduce27(i, arrayOfInt1);
    int[] arrayOfInt2 = Nat256.create();
    Curve25519Field.twice(curve25519FieldElement2.x, arrayOfInt2);
    int[] arrayOfInt3 = Nat256.create();
    Curve25519Field.multiply(arrayOfInt2, curve25519FieldElement2.x, arrayOfInt3);
    int[] arrayOfInt4 = Nat256.create();
    Curve25519Field.multiply(arrayOfInt3, curve25519FieldElement1.x, arrayOfInt4);
    Curve25519Field.twice(arrayOfInt4, arrayOfInt4);
    int[] arrayOfInt5 = Nat256.create();
    Curve25519Field.square(arrayOfInt3, arrayOfInt5);
    Curve25519Field.twice(arrayOfInt5, arrayOfInt5);
    Curve25519FieldElement curve25519FieldElement5 = new Curve25519FieldElement(arrayOfInt3);
    Curve25519Field.square(arrayOfInt1, curve25519FieldElement5.x);
    Curve25519Field.subtract(curve25519FieldElement5.x, arrayOfInt4, curve25519FieldElement5.x);
    Curve25519Field.subtract(curve25519FieldElement5.x, arrayOfInt4, curve25519FieldElement5.x);
    Curve25519FieldElement curve25519FieldElement6 = new Curve25519FieldElement(arrayOfInt4);
    Curve25519Field.subtract(arrayOfInt4, curve25519FieldElement5.x, curve25519FieldElement6.x);
    Curve25519Field.multiply(curve25519FieldElement6.x, arrayOfInt1, curve25519FieldElement6.x);
    Curve25519Field.subtract(curve25519FieldElement6.x, arrayOfInt5, curve25519FieldElement6.x);
    Curve25519FieldElement curve25519FieldElement7 = new Curve25519FieldElement(arrayOfInt2);
    if (!Nat256.isOne(curve25519FieldElement3.x))
      Curve25519Field.multiply(curve25519FieldElement7.x, curve25519FieldElement3.x, curve25519FieldElement7.x); 
    Curve25519FieldElement curve25519FieldElement8 = null;
    if (paramBoolean) {
      curve25519FieldElement8 = new Curve25519FieldElement(arrayOfInt5);
      Curve25519Field.multiply(curve25519FieldElement8.x, curve25519FieldElement4.x, curve25519FieldElement8.x);
      Curve25519Field.twice(curve25519FieldElement8.x, curve25519FieldElement8.x);
    } 
    return new Curve25519Point(getCurve(), curve25519FieldElement5, curve25519FieldElement6, new ECFieldElement[] { curve25519FieldElement7, curve25519FieldElement8 }, this.withCompression);
  }
}
