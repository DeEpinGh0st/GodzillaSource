package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat576;

public class SecT571K1Point extends ECPoint.AbstractF2m {
  public SecT571K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, false);
  }
  
  public SecT571K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2);
    if (((paramECFieldElement1 == null) ? true : false) != ((paramECFieldElement2 == null) ? true : false))
      throw new IllegalArgumentException("Exactly one of the field elements is null"); 
    this.withCompression = paramBoolean;
  }
  
  SecT571K1Point(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement, boolean paramBoolean) {
    super(paramECCurve, paramECFieldElement1, paramECFieldElement2, paramArrayOfECFieldElement);
    this.withCompression = paramBoolean;
  }
  
  protected ECPoint detach() {
    return (ECPoint)new SecT571K1Point(null, getAffineXCoord(), getAffineYCoord());
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
      secT571FieldElement7 = (SecT571FieldElement)eCFieldElement2.square().add(eCFieldElement2).add(secT571FieldElement1);
      if (secT571FieldElement7.isZero())
        return (ECPoint)new SecT571K1Point(eCCurve, secT571FieldElement7, eCCurve.getB(), this.withCompression); 
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
        return (ECPoint)new SecT571K1Point(eCCurve, secT571FieldElement7, eCCurve.getB(), this.withCompression); 
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
    return (ECPoint)new SecT571K1Point(eCCurve, secT571FieldElement7, secT571FieldElement8, new ECFieldElement[] { secT571FieldElement9 }, this.withCompression);
  }
  
  public ECPoint twice() {
    ECFieldElement eCFieldElement5;
    if (isInfinity())
      return (ECPoint)this; 
    ECCurve eCCurve = getCurve();
    ECFieldElement eCFieldElement1 = this.x;
    if (eCFieldElement1.isZero())
      return eCCurve.getInfinity(); 
    ECFieldElement eCFieldElement2 = this.y;
    ECFieldElement eCFieldElement3 = this.zs[0];
    boolean bool = eCFieldElement3.isOne();
    ECFieldElement eCFieldElement4 = bool ? eCFieldElement3 : eCFieldElement3.square();
    if (bool) {
      eCFieldElement5 = eCFieldElement2.square().add(eCFieldElement2);
    } else {
      eCFieldElement5 = eCFieldElement2.add(eCFieldElement3).multiply(eCFieldElement2);
    } 
    if (eCFieldElement5.isZero())
      return (ECPoint)new SecT571K1Point(eCCurve, eCFieldElement5, eCCurve.getB(), this.withCompression); 
    ECFieldElement eCFieldElement6 = eCFieldElement5.square();
    ECFieldElement eCFieldElement7 = bool ? eCFieldElement5 : eCFieldElement5.multiply(eCFieldElement4);
    ECFieldElement eCFieldElement8 = eCFieldElement2.add(eCFieldElement1).square();
    ECFieldElement eCFieldElement9 = bool ? eCFieldElement3 : eCFieldElement4.square();
    ECFieldElement eCFieldElement10 = eCFieldElement8.add(eCFieldElement5).add(eCFieldElement4).multiply(eCFieldElement8).add(eCFieldElement9).add(eCFieldElement6).add(eCFieldElement7);
    return (ECPoint)new SecT571K1Point(eCCurve, eCFieldElement6, eCFieldElement10, new ECFieldElement[] { eCFieldElement7 }, this.withCompression);
  }
  
  public ECPoint twicePlus(ECPoint paramECPoint) {
    if (isInfinity())
      return paramECPoint; 
    if (paramECPoint.isInfinity())
      return twice(); 
    ECCurve eCCurve = getCurve();
    ECFieldElement eCFieldElement1 = this.x;
    if (eCFieldElement1.isZero())
      return paramECPoint; 
    ECFieldElement eCFieldElement2 = paramECPoint.getRawXCoord();
    ECFieldElement eCFieldElement3 = paramECPoint.getZCoord(0);
    if (eCFieldElement2.isZero() || !eCFieldElement3.isOne())
      return twice().add(paramECPoint); 
    ECFieldElement eCFieldElement4 = this.y;
    ECFieldElement eCFieldElement5 = this.zs[0];
    ECFieldElement eCFieldElement6 = paramECPoint.getRawYCoord();
    ECFieldElement eCFieldElement7 = eCFieldElement1.square();
    ECFieldElement eCFieldElement8 = eCFieldElement4.square();
    ECFieldElement eCFieldElement9 = eCFieldElement5.square();
    ECFieldElement eCFieldElement10 = eCFieldElement4.multiply(eCFieldElement5);
    ECFieldElement eCFieldElement11 = eCFieldElement8.add(eCFieldElement10);
    ECFieldElement eCFieldElement12 = eCFieldElement6.addOne();
    ECFieldElement eCFieldElement13 = eCFieldElement12.multiply(eCFieldElement9).add(eCFieldElement8).multiplyPlusProduct(eCFieldElement11, eCFieldElement7, eCFieldElement9);
    ECFieldElement eCFieldElement14 = eCFieldElement2.multiply(eCFieldElement9);
    ECFieldElement eCFieldElement15 = eCFieldElement14.add(eCFieldElement11).square();
    if (eCFieldElement15.isZero())
      return eCFieldElement13.isZero() ? paramECPoint.twice() : eCCurve.getInfinity(); 
    if (eCFieldElement13.isZero())
      return (ECPoint)new SecT571K1Point(eCCurve, eCFieldElement13, eCCurve.getB(), this.withCompression); 
    ECFieldElement eCFieldElement16 = eCFieldElement13.square().multiply(eCFieldElement14);
    ECFieldElement eCFieldElement17 = eCFieldElement13.multiply(eCFieldElement15).multiply(eCFieldElement9);
    ECFieldElement eCFieldElement18 = eCFieldElement13.add(eCFieldElement15).square().multiplyPlusProduct(eCFieldElement11, eCFieldElement12, eCFieldElement17);
    return (ECPoint)new SecT571K1Point(eCCurve, eCFieldElement16, eCFieldElement18, new ECFieldElement[] { eCFieldElement17 }, this.withCompression);
  }
  
  public ECPoint negate() {
    if (isInfinity())
      return (ECPoint)this; 
    ECFieldElement eCFieldElement1 = this.x;
    if (eCFieldElement1.isZero())
      return (ECPoint)this; 
    ECFieldElement eCFieldElement2 = this.y;
    ECFieldElement eCFieldElement3 = this.zs[0];
    return (ECPoint)new SecT571K1Point(this.curve, eCFieldElement1, eCFieldElement2.add(eCFieldElement3), new ECFieldElement[] { eCFieldElement3 }, this.withCompression);
  }
}
