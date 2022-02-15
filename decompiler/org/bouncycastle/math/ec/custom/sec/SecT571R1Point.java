package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

public class SecT571R1Point extends ECPoint.AbstractF2m {
  public SecT571R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecT571R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecT571R1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecT571R1Point(null, getAffineXCoord(), getAffineYCoord());
  }
  
  public ECFieldElement getYCoord() {
    ECFieldElement eCFieldElement1 = this.x;
    ECFieldElement eCFieldElement2 = this.y;
    if (isInfinity() || eCFieldElement1.isZero())
      return eCFieldElement2; 
    ECFieldElement eCFieldElement3 = eCFieldElement2.add(eCFieldElement1).multiply(eCFieldElement1);
    ECFieldElement eCFieldElement4 = this.zs[0];
    if (!eCFieldElement4.isOne())
      eCFieldElement3 = eCFieldElement3.divide(eCFieldElement4); 
    return eCFieldElement3;
  }
  
  protected boolean getCompressionYTilde() {
    ECFieldElement eCFieldElement1 = getRawXCoord();
    if (eCFieldElement1.isZero())
      return false; 
    ECFieldElement eCFieldElement2 = getRawYCoord();
    return (eCFieldElement2.testBitZero() != eCFieldElement1.testBitZero());
  }
  
  public ECPoint add(ECPoint paramECPoint) {
    long[] arrayOfLong6;
    long[] arrayOfLong7;
    long[] arrayOfLong9;
    long[] arrayOfLong10;
    SecT571FieldElement secT571FieldElement7;
    SecT571FieldElement secT571FieldElement8;
    SecT571FieldElement secT571FieldElement9;
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecT571FieldElement secT571FieldElement1 = (SecT571FieldElement)this.x;
    SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)paramECPoint.getRawXCoord();
    if (secT571FieldElement1.isZero())
      return secT571FieldElement2.isZero() ? eCCurve.getInfinity() : paramECPoint.add((ECPoint)this); 
    SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)this.y;
    SecT571FieldElement secT571FieldElement4 = (SecT571FieldElement)this.zs[0];
    SecT571FieldElement secT571FieldElement5 = (SecT571FieldElement)paramECPoint.getRawYCoord();
    SecT571FieldElement secT571FieldElement6 = (SecT571FieldElement)paramECPoint.getZCoord(0);
    long[] arrayOfLong1 = Nat576.create64();
    long[] arrayOfLong2 = Nat576.create64();
    long[] arrayOfLong3 = Nat576.create64();
    long[] arrayOfLong4 = Nat576.create64();
    long[] arrayOfLong5 = secT571FieldElement4.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement4.x);
    if (arrayOfLong5 == null) {
      arrayOfLong6 = secT571FieldElement2.x;
      arrayOfLong7 = secT571FieldElement5.x;
    } else {
      SecT571Field.multiplyPrecomp(secT571FieldElement2.x, arrayOfLong5, arrayOfLong6 = arrayOfLong2);
      SecT571Field.multiplyPrecomp(secT571FieldElement5.x, arrayOfLong5, arrayOfLong7 = arrayOfLong4);
    } 
    long[] arrayOfLong8 = secT571FieldElement6.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement6.x);
    if (arrayOfLong8 == null) {
      arrayOfLong9 = secT571FieldElement1.x;
      arrayOfLong10 = secT571FieldElement3.x;
    } else {
      SecT571Field.multiplyPrecomp(secT571FieldElement1.x, arrayOfLong8, arrayOfLong9 = arrayOfLong1);
      SecT571Field.multiplyPrecomp(secT571FieldElement3.x, arrayOfLong8, arrayOfLong10 = arrayOfLong3);
    } 
    long[] arrayOfLong11 = arrayOfLong3;
    SecT571Field.add(arrayOfLong10, arrayOfLong7, arrayOfLong11);
    long[] arrayOfLong12 = arrayOfLong4;
    SecT571Field.add(arrayOfLong9, arrayOfLong6, arrayOfLong12);
    if (Nat576.isZero64(arrayOfLong12))
      return Nat576.isZero64(arrayOfLong11) ? twice() : eCCurve.getInfinity(); 
    if (secT571FieldElement2.isZero()) {
      ECPoint eCPoint = normalize();
      secT571FieldElement1 = (SecT571FieldElement)eCPoint.getXCoord();
      ECFieldElement eCFieldElement1 = eCPoint.getYCoord();
      SecT571FieldElement secT571FieldElement = secT571FieldElement5;
      ECFieldElement eCFieldElement2 = eCFieldElement1.add(secT571FieldElement).divide(secT571FieldElement1);
      secT571FieldElement7 = (SecT571FieldElement)eCFieldElement2.square().add(eCFieldElement2).add(secT571FieldElement1).addOne();
      if (secT571FieldElement7.isZero())
        return (ECPoint)new SecT571R1Point(eCCurve, secT571FieldElement7, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression); 
      ECFieldElement eCFieldElement3 = eCFieldElement2.multiply(secT571FieldElement1.add(secT571FieldElement7)).add(secT571FieldElement7).add(eCFieldElement1);
      secT571FieldElement8 = (SecT571FieldElement)eCFieldElement3.divide(secT571FieldElement7).add(secT571FieldElement7);
      secT571FieldElement9 = (SecT571FieldElement)eCCurve.fromBigInteger(ECConstants.ONE);
    } else {
      SecT571Field.square(arrayOfLong12, arrayOfLong12);
      long[] arrayOfLong13 = SecT571Field.precompMultiplicand(arrayOfLong11);
      long[] arrayOfLong14 = arrayOfLong1;
      long[] arrayOfLong15 = arrayOfLong2;
      SecT571Field.multiplyPrecomp(arrayOfLong9, arrayOfLong13, arrayOfLong14);
      SecT571Field.multiplyPrecomp(arrayOfLong6, arrayOfLong13, arrayOfLong15);
      secT571FieldElement7 = new SecT571FieldElement(arrayOfLong1);
      SecT571Field.multiply(arrayOfLong14, arrayOfLong15, secT571FieldElement7.x);
      if (secT571FieldElement7.isZero())
        return (ECPoint)new SecT571R1Point(eCCurve, secT571FieldElement7, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression); 
      secT571FieldElement9 = new SecT571FieldElement(arrayOfLong3);
      SecT571Field.multiplyPrecomp(arrayOfLong12, arrayOfLong13, secT571FieldElement9.x);
      if (arrayOfLong8 != null)
        SecT571Field.multiplyPrecomp(secT571FieldElement9.x, arrayOfLong8, secT571FieldElement9.x); 
      long[] arrayOfLong16 = Nat576.createExt64();
      SecT571Field.add(arrayOfLong15, arrayOfLong12, arrayOfLong4);
      SecT571Field.squareAddToExt(arrayOfLong4, arrayOfLong16);
      SecT571Field.add(secT571FieldElement3.x, secT571FieldElement4.x, arrayOfLong4);
      SecT571Field.multiplyAddToExt(arrayOfLong4, secT571FieldElement9.x, arrayOfLong16);
      secT571FieldElement8 = new SecT571FieldElement(arrayOfLong4);
      SecT571Field.reduce(arrayOfLong16, secT571FieldElement8.x);
      if (arrayOfLong5 != null)
        SecT571Field.multiplyPrecomp(secT571FieldElement9.x, arrayOfLong5, secT571FieldElement9.x); 
    } 
    return (ECPoint)new SecT571R1Point(eCCurve, secT571FieldElement7, secT571FieldElement8, new ECFieldElement[] { secT571FieldElement9 }, this.withCompression);
  }
  
  public ECPoint twice() {
    long[] arrayOfLong4;
    long[] arrayOfLong5;
    long[] arrayOfLong8;
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    SecT571FieldElement secT571FieldElement1 = (SecT571FieldElement)this.x;
    if (secT571FieldElement1.isZero())
      return eCCurve.getInfinity(); 
    SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)this.y;
    SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)this.zs[0];
    long[] arrayOfLong1 = Nat576.create64();
    long[] arrayOfLong2 = Nat576.create64();
    long[] arrayOfLong3 = secT571FieldElement3.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement3.x);
    if (arrayOfLong3 == null) {
      arrayOfLong4 = secT571FieldElement2.x;
      arrayOfLong5 = secT571FieldElement3.x;
    } else {
      SecT571Field.multiplyPrecomp(secT571FieldElement2.x, arrayOfLong3, arrayOfLong4 = arrayOfLong1);
      SecT571Field.square(secT571FieldElement3.x, arrayOfLong5 = arrayOfLong2);
    } 
    long[] arrayOfLong6 = Nat576.create64();
    SecT571Field.square(secT571FieldElement2.x, arrayOfLong6);
    SecT571Field.addBothTo(arrayOfLong4, arrayOfLong5, arrayOfLong6);
    if (Nat576.isZero64(arrayOfLong6))
      return (ECPoint)new SecT571R1Point(eCCurve, new SecT571FieldElement(arrayOfLong6), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression); 
    long[] arrayOfLong7 = Nat576.createExt64();
    SecT571Field.multiplyAddToExt(arrayOfLong6, arrayOfLong4, arrayOfLong7);
    SecT571FieldElement secT571FieldElement4 = new SecT571FieldElement(arrayOfLong1);
    SecT571Field.square(arrayOfLong6, secT571FieldElement4.x);
    SecT571FieldElement secT571FieldElement5 = new SecT571FieldElement(arrayOfLong6);
    if (arrayOfLong3 != null)
      SecT571Field.multiply(secT571FieldElement5.x, arrayOfLong5, secT571FieldElement5.x); 
    if (arrayOfLong3 == null) {
      arrayOfLong8 = secT571FieldElement1.x;
    } else {
      SecT571Field.multiplyPrecomp(secT571FieldElement1.x, arrayOfLong3, arrayOfLong8 = arrayOfLong2);
    } 
    SecT571Field.squareAddToExt(arrayOfLong8, arrayOfLong7);
    SecT571Field.reduce(arrayOfLong7, arrayOfLong2);
    SecT571Field.addBothTo(secT571FieldElement4.x, secT571FieldElement5.x, arrayOfLong2);
    SecT571FieldElement secT571FieldElement6 = new SecT571FieldElement(arrayOfLong2);
    return (ECPoint)new SecT571R1Point(eCCurve, secT571FieldElement4, secT571FieldElement6, new ECFieldElement[] { secT571FieldElement5 }, this.withCompression);
  }
  
  public ECPoint twicePlus(ECPoint paramECPoint) {
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return twice(); 
    ECCurve eCCurve = getCurve();
    SecT571FieldElement secT571FieldElement1 = (SecT571FieldElement)this.x;
    if (secT571FieldElement1.isZero())
      return paramECPoint; 
    SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)paramECPoint.getRawXCoord();
    SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)paramECPoint.getZCoord(0);
    if (secT571FieldElement2.isZero() || !secT571FieldElement3.isOne())
      return twice().add(paramECPoint); 
    SecT571FieldElement secT571FieldElement4 = (SecT571FieldElement)this.y;
    SecT571FieldElement secT571FieldElement5 = (SecT571FieldElement)this.zs[0];
    SecT571FieldElement secT571FieldElement6 = (SecT571FieldElement)paramECPoint.getRawYCoord();
    long[] arrayOfLong1 = Nat576.create64();
    long[] arrayOfLong2 = Nat576.create64();
    long[] arrayOfLong3 = Nat576.create64();
    long[] arrayOfLong4 = Nat576.create64();
    long[] arrayOfLong5 = arrayOfLong1;
    SecT571Field.square(secT571FieldElement1.x, arrayOfLong5);
    long[] arrayOfLong6 = arrayOfLong2;
    SecT571Field.square(secT571FieldElement4.x, arrayOfLong6);
    long[] arrayOfLong7 = arrayOfLong3;
    SecT571Field.square(secT571FieldElement5.x, arrayOfLong7);
    long[] arrayOfLong8 = arrayOfLong4;
    SecT571Field.multiply(secT571FieldElement4.x, secT571FieldElement5.x, arrayOfLong8);
    long[] arrayOfLong9 = arrayOfLong8;
    SecT571Field.addBothTo(arrayOfLong7, arrayOfLong6, arrayOfLong9);
    long[] arrayOfLong10 = SecT571Field.precompMultiplicand(arrayOfLong7);
    long[] arrayOfLong11 = arrayOfLong3;
    SecT571Field.multiplyPrecomp(secT571FieldElement6.x, arrayOfLong10, arrayOfLong11);
    SecT571Field.add(arrayOfLong11, arrayOfLong6, arrayOfLong11);
    long[] arrayOfLong12 = Nat576.createExt64();
    SecT571Field.multiplyAddToExt(arrayOfLong11, arrayOfLong9, arrayOfLong12);
    SecT571Field.multiplyPrecompAddToExt(arrayOfLong5, arrayOfLong10, arrayOfLong12);
    SecT571Field.reduce(arrayOfLong12, arrayOfLong11);
    long[] arrayOfLong13 = arrayOfLong1;
    SecT571Field.multiplyPrecomp(secT571FieldElement2.x, arrayOfLong10, arrayOfLong13);
    long[] arrayOfLong14 = arrayOfLong2;
    SecT571Field.add(arrayOfLong13, arrayOfLong9, arrayOfLong14);
    SecT571Field.square(arrayOfLong14, arrayOfLong14);
    if (Nat576.isZero64(arrayOfLong14))
      return Nat576.isZero64(arrayOfLong11) ? paramECPoint.twice() : eCCurve.getInfinity(); 
    if (Nat576.isZero64(arrayOfLong11))
      return (ECPoint)new SecT571R1Point(eCCurve, new SecT571FieldElement(arrayOfLong11), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression); 
    SecT571FieldElement secT571FieldElement7 = new SecT571FieldElement();
    SecT571Field.square(arrayOfLong11, secT571FieldElement7.x);
    SecT571Field.multiply(secT571FieldElement7.x, arrayOfLong13, secT571FieldElement7.x);
    SecT571FieldElement secT571FieldElement8 = new SecT571FieldElement(arrayOfLong1);
    SecT571Field.multiply(arrayOfLong11, arrayOfLong14, secT571FieldElement8.x);
    SecT571Field.multiplyPrecomp(secT571FieldElement8.x, arrayOfLong10, secT571FieldElement8.x);
    SecT571FieldElement secT571FieldElement9 = new SecT571FieldElement(arrayOfLong2);
    SecT571Field.add(arrayOfLong11, arrayOfLong14, secT571FieldElement9.x);
    SecT571Field.square(secT571FieldElement9.x, secT571FieldElement9.x);
    Nat.zero64(18, arrayOfLong12);
    SecT571Field.multiplyAddToExt(secT571FieldElement9.x, arrayOfLong9, arrayOfLong12);
    SecT571Field.addOne(secT571FieldElement6.x, arrayOfLong4);
    SecT571Field.multiplyAddToExt(arrayOfLong4, secT571FieldElement8.x, arrayOfLong12);
    SecT571Field.reduce(arrayOfLong12, secT571FieldElement9.x);
    return (ECPoint)new SecT571R1Point(eCCurve, secT571FieldElement7, secT571FieldElement9, new ECFieldElement[] { secT571FieldElement8 }, this.withCompression);
  }
  
  public ECPoint negate() {
    if (isInfinity())
      return (ECPoint)this; 
    ECFieldElement eCFieldElement1 = this.x;
    if (eCFieldElement1.isZero())
      return (ECPoint)this; 
    ECFieldElement eCFieldElement2 = this.y;
    ECFieldElement eCFieldElement3 = this.zs[0];
    return (ECPoint)new SecT571R1Point(this.curve, eCFieldElement1, eCFieldElement2.add(eCFieldElement3), new ECFieldElement[] { eCFieldElement3 }, this.withCompression);
  }
}
