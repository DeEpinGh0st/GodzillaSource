package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Hashtable;

public abstract class ECPoint {
  protected static ECFieldElement[] EMPTY_ZS = new ECFieldElement[0];
  
  protected ECCurve curve;
  
  protected ECFieldElement x;
  
  protected ECFieldElement y;
  
  protected ECFieldElement[] zs;
  
  protected boolean withCompression;
  
  protected Hashtable preCompTable = null;
  
  protected static ECFieldElement[] getInitialZCoords(ECCurve paramECCurve) {
    boolean bool = (null == paramECCurve) ? false : paramECCurve.getCoordinateSystem();
    switch (bool) {
      case false:
      case true:
        return EMPTY_ZS;
    } 
    ECFieldElement eCFieldElement = paramECCurve.fromBigInteger(ECConstants.ONE);
    switch (bool) {
      case true:
      case true:
      case true:
        return new ECFieldElement[] { eCFieldElement };
      case true:
        return new ECFieldElement[] { eCFieldElement, eCFieldElement, eCFieldElement };
      case true:
        return new ECFieldElement[] { eCFieldElement, paramECCurve.getA() };
    } 
    throw new IllegalArgumentException("unknown coordinate system");
  }
  
  protected ECPoint(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    this(paramECCurve, paramECFieldElement1, paramECFieldElement2, getInitialZCoords(paramECCurve));
  }
  
  protected ECPoint(ECCurve paramECCurve, ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement[] paramArrayOfECFieldElement) {
    this.curve = paramECCurve;
    this.x = paramECFieldElement1;
    this.y = paramECFieldElement2;
    this.zs = paramArrayOfECFieldElement;
  }
  
  protected boolean satisfiesCofactor() {
    BigInteger bigInteger = this.curve.getCofactor();
    return (bigInteger == null || bigInteger.equals(ECConstants.ONE) || !ECAlgorithms.referenceMultiply(this, bigInteger).isInfinity());
  }
  
  protected abstract boolean satisfiesCurveEquation();
  
  public final ECPoint getDetachedPoint() {
    return normalize().detach();
  }
  
  public ECCurve getCurve() {
    return this.curve;
  }
  
  protected abstract ECPoint detach();
  
  protected int getCurveCoordinateSystem() {
    return (null == this.curve) ? 0 : this.curve.getCoordinateSystem();
  }
  
  public ECFieldElement getX() {
    return normalize().getXCoord();
  }
  
  public ECFieldElement getY() {
    return normalize().getYCoord();
  }
  
  public ECFieldElement getAffineXCoord() {
    checkNormalized();
    return getXCoord();
  }
  
  public ECFieldElement getAffineYCoord() {
    checkNormalized();
    return getYCoord();
  }
  
  public ECFieldElement getXCoord() {
    return this.x;
  }
  
  public ECFieldElement getYCoord() {
    return this.y;
  }
  
  public ECFieldElement getZCoord(int paramInt) {
    return (paramInt < 0 || paramInt >= this.zs.length) ? null : this.zs[paramInt];
  }
  
  public ECFieldElement[] getZCoords() {
    int i = this.zs.length;
    if (i == 0)
      return EMPTY_ZS; 
    ECFieldElement[] arrayOfECFieldElement = new ECFieldElement[i];
    System.arraycopy(this.zs, 0, arrayOfECFieldElement, 0, i);
    return arrayOfECFieldElement;
  }
  
  public final ECFieldElement getRawXCoord() {
    return this.x;
  }
  
  public final ECFieldElement getRawYCoord() {
    return this.y;
  }
  
  protected final ECFieldElement[] getRawZCoords() {
    return this.zs;
  }
  
  protected void checkNormalized() {
    if (!isNormalized())
      throw new IllegalStateException("point not in normal form"); 
  }
  
  public boolean isNormalized() {
    int i = getCurveCoordinateSystem();
    return (i == 0 || i == 5 || isInfinity() || this.zs[0].isOne());
  }
  
  public ECPoint normalize() {
    if (isInfinity())
      return this; 
    switch (getCurveCoordinateSystem()) {
      case 0:
      case 5:
        return this;
    } 
    ECFieldElement eCFieldElement = getZCoord(0);
    return eCFieldElement.isOne() ? this : normalize(eCFieldElement.invert());
  }
  
  ECPoint normalize(ECFieldElement paramECFieldElement) {
    ECFieldElement eCFieldElement1;
    ECFieldElement eCFieldElement2;
    switch (getCurveCoordinateSystem()) {
      case 1:
      case 6:
        return createScaledPoint(paramECFieldElement, paramECFieldElement);
      case 2:
      case 3:
      case 4:
        eCFieldElement1 = paramECFieldElement.square();
        eCFieldElement2 = eCFieldElement1.multiply(paramECFieldElement);
        return createScaledPoint(eCFieldElement1, eCFieldElement2);
    } 
    throw new IllegalStateException("not a projective coordinate system");
  }
  
  protected ECPoint createScaledPoint(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return getCurve().createRawPoint(getRawXCoord().multiply(paramECFieldElement1), getRawYCoord().multiply(paramECFieldElement2), this.withCompression);
  }
  
  public boolean isInfinity() {
    return (this.x == null || this.y == null || (this.zs.length > 0 && this.zs[0].isZero()));
  }
  
  public boolean isCompressed() {
    return this.withCompression;
  }
  
  public boolean isValid() {
    if (isInfinity())
      return true; 
    ECCurve eCCurve = getCurve();
    if (eCCurve != null) {
      if (!satisfiesCurveEquation())
        return false; 
      if (!satisfiesCofactor())
        return false; 
    } 
    return true;
  }
  
  public ECPoint scaleX(ECFieldElement paramECFieldElement) {
    return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord().multiply(paramECFieldElement), getRawYCoord(), getRawZCoords(), this.withCompression);
  }
  
  public ECPoint scaleY(ECFieldElement paramECFieldElement) {
    return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord(), getRawYCoord().multiply(paramECFieldElement), getRawZCoords(), this.withCompression);
  }
  
  public boolean equals(ECPoint paramECPoint) {
    if (null == paramECPoint)
      return false; 
    ECCurve eCCurve1 = getCurve();
    ECCurve eCCurve2 = paramECPoint.getCurve();
    boolean bool1 = (null == eCCurve1) ? true : false;
    boolean bool2 = (null == eCCurve2) ? true : false;
    boolean bool3 = isInfinity();
    boolean bool4 = paramECPoint.isInfinity();
    if (bool3 || bool4)
      return (bool3 && bool4 && (bool1 || bool2 || eCCurve1.equals(eCCurve2))); 
    ECPoint eCPoint1 = this;
    ECPoint eCPoint2 = paramECPoint;
    if (!bool1 || !bool2)
      if (bool1) {
        eCPoint2 = eCPoint2.normalize();
      } else if (bool2) {
        eCPoint1 = eCPoint1.normalize();
      } else {
        if (!eCCurve1.equals(eCCurve2))
          return false; 
        ECPoint[] arrayOfECPoint = { this, eCCurve1.importPoint(eCPoint2) };
        eCCurve1.normalizeAll(arrayOfECPoint);
        eCPoint1 = arrayOfECPoint[0];
        eCPoint2 = arrayOfECPoint[1];
      }  
    return (eCPoint1.getXCoord().equals(eCPoint2.getXCoord()) && eCPoint1.getYCoord().equals(eCPoint2.getYCoord()));
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == this) ? true : (!(paramObject instanceof ECPoint) ? false : equals((ECPoint)paramObject));
  }
  
  public int hashCode() {
    ECCurve eCCurve = getCurve();
    int i = (null == eCCurve) ? 0 : (eCCurve.hashCode() ^ 0xFFFFFFFF);
    if (!isInfinity()) {
      ECPoint eCPoint = normalize();
      i ^= eCPoint.getXCoord().hashCode() * 17;
      i ^= eCPoint.getYCoord().hashCode() * 257;
    } 
    return i;
  }
  
  public String toString() {
    if (isInfinity())
      return "INF"; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    stringBuffer.append(getRawXCoord());
    stringBuffer.append(',');
    stringBuffer.append(getRawYCoord());
    for (byte b = 0; b < this.zs.length; b++) {
      stringBuffer.append(',');
      stringBuffer.append(this.zs[b]);
    } 
    stringBuffer.append(')');
    return stringBuffer.toString();
  }
  
  public byte[] getEncoded() {
    return getEncoded(this.withCompression);
  }
  
  public byte[] getEncoded(boolean paramBoolean) {
    if (isInfinity())
      return new byte[1]; 
    ECPoint eCPoint = normalize();
    byte[] arrayOfByte1 = eCPoint.getXCoord().getEncoded();
    if (paramBoolean) {
      byte[] arrayOfByte = new byte[arrayOfByte1.length + 1];
      arrayOfByte[0] = (byte)(eCPoint.getCompressionYTilde() ? 3 : 2);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, 1, arrayOfByte1.length);
      return arrayOfByte;
    } 
    byte[] arrayOfByte2 = eCPoint.getYCoord().getEncoded();
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length + 1];
    arrayOfByte3[0] = 4;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 1, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length + 1, arrayOfByte2.length);
    return arrayOfByte3;
  }
  
  protected abstract boolean getCompressionYTilde();
  
  public abstract ECPoint add(ECPoint paramECPoint);
  
  public abstract ECPoint negate();
  
  public abstract ECPoint subtract(ECPoint paramECPoint);
  
  public ECPoint timesPow2(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("'e' cannot be negative"); 
    ECPoint eCPoint;
    for (eCPoint = this; --paramInt >= 0; eCPoint = eCPoint.twice());
    return eCPoint;
  }
  
  public abstract ECPoint twice();
  
  public ECPoint twicePlus(ECPoint paramECPoint) {
    return twice().add(paramECPoint);
  }
  
  public ECPoint threeTimes() {
    return twicePlus(this);
  }
  
  public ECPoint multiply(BigInteger paramBigInteger) {
    return getCurve().getMultiplier().multiply(this, paramBigInteger);
  }
  
  public static abstract class AbstractF2m extends ECPoint {
    protected AbstractF2m(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2);
    }
    
    protected AbstractF2m(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement[] param1ArrayOfECFieldElement) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, param1ArrayOfECFieldElement);
    }
    
    protected boolean satisfiesCurveEquation() {
      ECFieldElement eCFieldElement6;
      ECCurve eCCurve = getCurve();
      ECFieldElement eCFieldElement1 = this.x;
      ECFieldElement eCFieldElement2 = eCCurve.getA();
      ECFieldElement eCFieldElement3 = eCCurve.getB();
      int i = eCCurve.getCoordinateSystem();
      if (i == 6) {
        ECFieldElement eCFieldElement11;
        ECFieldElement eCFieldElement7 = this.zs[0];
        boolean bool = eCFieldElement7.isOne();
        if (eCFieldElement1.isZero()) {
          ECFieldElement eCFieldElement12 = this.y;
          ECFieldElement eCFieldElement13 = eCFieldElement12.square();
          eCFieldElement10 = eCFieldElement3;
          if (!bool)
            eCFieldElement10 = eCFieldElement10.multiply(eCFieldElement7.square()); 
          return eCFieldElement13.equals(eCFieldElement10);
        } 
        ECFieldElement eCFieldElement8 = this.y;
        ECFieldElement eCFieldElement9 = eCFieldElement1.square();
        if (bool) {
          eCFieldElement10 = eCFieldElement8.square().add(eCFieldElement8).add(eCFieldElement2);
          eCFieldElement11 = eCFieldElement9.square().add(eCFieldElement3);
        } else {
          ECFieldElement eCFieldElement12 = eCFieldElement7.square();
          ECFieldElement eCFieldElement13 = eCFieldElement12.square();
          eCFieldElement10 = eCFieldElement8.add(eCFieldElement7).multiplyPlusProduct(eCFieldElement8, eCFieldElement2, eCFieldElement12);
          eCFieldElement11 = eCFieldElement9.squarePlusProduct(eCFieldElement3, eCFieldElement13);
        } 
        ECFieldElement eCFieldElement10 = eCFieldElement10.multiply(eCFieldElement9);
        return eCFieldElement10.equals(eCFieldElement11);
      } 
      ECFieldElement eCFieldElement4 = this.y;
      ECFieldElement eCFieldElement5 = eCFieldElement4.add(eCFieldElement1).multiply(eCFieldElement4);
      switch (i) {
        case 0:
          eCFieldElement6 = eCFieldElement1.add(eCFieldElement2).multiply(eCFieldElement1.square()).add(eCFieldElement3);
          return eCFieldElement5.equals(eCFieldElement6);
        case 1:
          eCFieldElement6 = this.zs[0];
          if (!eCFieldElement6.isOne()) {
            ECFieldElement eCFieldElement7 = eCFieldElement6.square();
            ECFieldElement eCFieldElement8 = eCFieldElement6.multiply(eCFieldElement7);
            eCFieldElement5 = eCFieldElement5.multiply(eCFieldElement6);
            eCFieldElement2 = eCFieldElement2.multiply(eCFieldElement6);
            eCFieldElement3 = eCFieldElement3.multiply(eCFieldElement8);
          } 
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint scaleX(ECFieldElement param1ECFieldElement) {
      ECFieldElement eCFieldElement1;
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      if (isInfinity())
        return this; 
      int i = getCurveCoordinateSystem();
      switch (i) {
        case 5:
          eCFieldElement1 = getRawXCoord();
          eCFieldElement2 = getRawYCoord();
          eCFieldElement3 = eCFieldElement1.multiply(param1ECFieldElement);
          eCFieldElement4 = eCFieldElement2.add(eCFieldElement1).divide(param1ECFieldElement).add(eCFieldElement3);
          return getCurve().createRawPoint(eCFieldElement1, eCFieldElement4, getRawZCoords(), this.withCompression);
        case 6:
          eCFieldElement1 = getRawXCoord();
          eCFieldElement2 = getRawYCoord();
          eCFieldElement3 = getRawZCoords()[0];
          eCFieldElement4 = eCFieldElement1.multiply(param1ECFieldElement.square());
          eCFieldElement5 = eCFieldElement2.add(eCFieldElement1).add(eCFieldElement4);
          eCFieldElement6 = eCFieldElement3.multiply(param1ECFieldElement);
          return getCurve().createRawPoint(eCFieldElement4, eCFieldElement5, new ECFieldElement[] { eCFieldElement6 }, this.withCompression);
      } 
      return super.scaleX(param1ECFieldElement);
    }
    
    public ECPoint scaleY(ECFieldElement param1ECFieldElement) {
      ECFieldElement eCFieldElement1;
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      if (isInfinity())
        return this; 
      int i = getCurveCoordinateSystem();
      switch (i) {
        case 5:
        case 6:
          eCFieldElement1 = getRawXCoord();
          eCFieldElement2 = getRawYCoord();
          eCFieldElement3 = eCFieldElement2.add(eCFieldElement1).multiply(param1ECFieldElement).add(eCFieldElement1);
          return getCurve().createRawPoint(eCFieldElement1, eCFieldElement3, getRawZCoords(), this.withCompression);
      } 
      return super.scaleY(param1ECFieldElement);
    }
    
    public ECPoint subtract(ECPoint param1ECPoint) {
      return param1ECPoint.isInfinity() ? this : add(param1ECPoint.negate());
    }
    
    public AbstractF2m tau() {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      if (isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement1 = this.x;
      switch (i) {
        case 0:
        case 5:
          eCFieldElement2 = this.y;
          return (AbstractF2m)eCCurve.createRawPoint(eCFieldElement1.square(), eCFieldElement2.square(), this.withCompression);
        case 1:
        case 6:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          return (AbstractF2m)eCCurve.createRawPoint(eCFieldElement1.square(), eCFieldElement2.square(), new ECFieldElement[] { eCFieldElement3.square() }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public AbstractF2m tauPow(int param1Int) {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      if (isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement1 = this.x;
      switch (i) {
        case 0:
        case 5:
          eCFieldElement2 = this.y;
          return (AbstractF2m)eCCurve.createRawPoint(eCFieldElement1.squarePow(param1Int), eCFieldElement2.squarePow(param1Int), this.withCompression);
        case 1:
        case 6:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          return (AbstractF2m)eCCurve.createRawPoint(eCFieldElement1.squarePow(param1Int), eCFieldElement2.squarePow(param1Int), new ECFieldElement[] { eCFieldElement3.squarePow(param1Int) }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
  }
  
  public static abstract class AbstractFp extends ECPoint {
    protected AbstractFp(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2);
    }
    
    protected AbstractFp(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement[] param1ArrayOfECFieldElement) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, param1ArrayOfECFieldElement);
    }
    
    protected boolean getCompressionYTilde() {
      return getAffineYCoord().testBitZero();
    }
    
    protected boolean satisfiesCurveEquation() {
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement1 = this.x;
      ECFieldElement eCFieldElement2 = this.y;
      ECFieldElement eCFieldElement3 = this.curve.getA();
      ECFieldElement eCFieldElement4 = this.curve.getB();
      ECFieldElement eCFieldElement5 = eCFieldElement2.square();
      switch (getCurveCoordinateSystem()) {
        case 0:
          eCFieldElement6 = eCFieldElement1.square().add(eCFieldElement3).multiply(eCFieldElement1).add(eCFieldElement4);
          return eCFieldElement5.equals(eCFieldElement6);
        case 1:
          eCFieldElement6 = this.zs[0];
          if (!eCFieldElement6.isOne()) {
            ECFieldElement eCFieldElement7 = eCFieldElement6.square();
            ECFieldElement eCFieldElement8 = eCFieldElement6.multiply(eCFieldElement7);
            eCFieldElement5 = eCFieldElement5.multiply(eCFieldElement6);
            eCFieldElement3 = eCFieldElement3.multiply(eCFieldElement7);
            eCFieldElement4 = eCFieldElement4.multiply(eCFieldElement8);
          } 
        case 2:
        case 3:
        case 4:
          eCFieldElement6 = this.zs[0];
          if (!eCFieldElement6.isOne()) {
            ECFieldElement eCFieldElement7 = eCFieldElement6.square();
            ECFieldElement eCFieldElement8 = eCFieldElement7.square();
            ECFieldElement eCFieldElement9 = eCFieldElement7.multiply(eCFieldElement8);
            eCFieldElement3 = eCFieldElement3.multiply(eCFieldElement8);
            eCFieldElement4 = eCFieldElement4.multiply(eCFieldElement9);
          } 
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint subtract(ECPoint param1ECPoint) {
      return param1ECPoint.isInfinity() ? this : add(param1ECPoint.negate());
    }
  }
  
  public static class F2m extends AbstractF2m {
    public F2m(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      this(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, false);
    }
    
    public F2m(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, boolean param1Boolean) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2);
      if (((param1ECFieldElement1 == null) ? true : false) != ((param1ECFieldElement2 == null) ? true : false))
        throw new IllegalArgumentException("Exactly one of the field elements is null"); 
      if (param1ECFieldElement1 != null) {
        ECFieldElement.F2m.checkFieldElements(this.x, this.y);
        if (param1ECCurve != null)
          ECFieldElement.F2m.checkFieldElements(this.x, this.curve.getA()); 
      } 
      this.withCompression = param1Boolean;
    }
    
    F2m(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement[] param1ArrayOfECFieldElement, boolean param1Boolean) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, param1ArrayOfECFieldElement);
      this.withCompression = param1Boolean;
    }
    
    protected ECPoint detach() {
      return new F2m(null, getAffineXCoord(), getAffineYCoord());
    }
    
    public ECFieldElement getYCoord() {
      ECFieldElement eCFieldElement1;
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      int i = getCurveCoordinateSystem();
      switch (i) {
        case 5:
        case 6:
          eCFieldElement1 = this.x;
          eCFieldElement2 = this.y;
          if (isInfinity() || eCFieldElement1.isZero())
            return eCFieldElement2; 
          eCFieldElement3 = eCFieldElement2.add(eCFieldElement1).multiply(eCFieldElement1);
          if (6 == i) {
            ECFieldElement eCFieldElement = this.zs[0];
            if (!eCFieldElement.isOne())
              eCFieldElement3 = eCFieldElement3.divide(eCFieldElement); 
          } 
          return eCFieldElement3;
      } 
      return this.y;
    }
    
    protected boolean getCompressionYTilde() {
      ECFieldElement eCFieldElement1 = getRawXCoord();
      if (eCFieldElement1.isZero())
        return false; 
      ECFieldElement eCFieldElement2 = getRawYCoord();
      switch (getCurveCoordinateSystem()) {
        case 5:
        case 6:
          return (eCFieldElement2.testBitZero() != eCFieldElement1.testBitZero());
      } 
      return eCFieldElement2.divide(eCFieldElement1).testBitZero();
    }
    
    public ECPoint add(ECPoint param1ECPoint) {
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      boolean bool1;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      boolean bool2;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      ECFieldElement eCFieldElement16;
      ECFieldElement eCFieldElement17;
      ECFieldElement eCFieldElement18;
      ECFieldElement eCFieldElement19;
      ECFieldElement eCFieldElement20;
      ECFieldElement eCFieldElement21;
      ECFieldElement eCFieldElement22;
      if (isInfinity())
        return param1ECPoint; 
      if (param1ECPoint.isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement1 = this.x;
      ECFieldElement eCFieldElement2 = param1ECPoint.x;
      switch (i) {
        case 0:
          eCFieldElement3 = this.y;
          eCFieldElement4 = param1ECPoint.y;
          eCFieldElement5 = eCFieldElement1.add(eCFieldElement2);
          eCFieldElement6 = eCFieldElement3.add(eCFieldElement4);
          if (eCFieldElement5.isZero())
            return eCFieldElement6.isZero() ? twice() : eCCurve.getInfinity(); 
          eCFieldElement7 = eCFieldElement6.divide(eCFieldElement5);
          eCFieldElement8 = eCFieldElement7.square().add(eCFieldElement7).add(eCFieldElement5).add(eCCurve.getA());
          eCFieldElement9 = eCFieldElement7.multiply(eCFieldElement1.add(eCFieldElement8)).add(eCFieldElement8).add(eCFieldElement3);
          return new F2m(eCCurve, eCFieldElement8, eCFieldElement9, this.withCompression);
        case 1:
          eCFieldElement3 = this.y;
          eCFieldElement4 = this.zs[0];
          eCFieldElement5 = param1ECPoint.y;
          eCFieldElement6 = param1ECPoint.zs[0];
          bool1 = eCFieldElement6.isOne();
          eCFieldElement8 = eCFieldElement4.multiply(eCFieldElement5);
          eCFieldElement9 = bool1 ? eCFieldElement3 : eCFieldElement3.multiply(eCFieldElement6);
          eCFieldElement10 = eCFieldElement8.add(eCFieldElement9);
          eCFieldElement11 = eCFieldElement4.multiply(eCFieldElement2);
          eCFieldElement12 = bool1 ? eCFieldElement1 : eCFieldElement1.multiply(eCFieldElement6);
          eCFieldElement13 = eCFieldElement11.add(eCFieldElement12);
          if (eCFieldElement13.isZero())
            return eCFieldElement10.isZero() ? twice() : eCCurve.getInfinity(); 
          eCFieldElement14 = eCFieldElement13.square();
          eCFieldElement15 = eCFieldElement14.multiply(eCFieldElement13);
          eCFieldElement16 = bool1 ? eCFieldElement4 : eCFieldElement4.multiply(eCFieldElement6);
          eCFieldElement17 = eCFieldElement10.add(eCFieldElement13);
          eCFieldElement18 = eCFieldElement17.multiplyPlusProduct(eCFieldElement10, eCFieldElement14, eCCurve.getA()).multiply(eCFieldElement16).add(eCFieldElement15);
          eCFieldElement19 = eCFieldElement13.multiply(eCFieldElement18);
          eCFieldElement20 = bool1 ? eCFieldElement14 : eCFieldElement14.multiply(eCFieldElement6);
          eCFieldElement21 = eCFieldElement10.multiplyPlusProduct(eCFieldElement1, eCFieldElement13, eCFieldElement3).multiplyPlusProduct(eCFieldElement20, eCFieldElement17, eCFieldElement18);
          eCFieldElement22 = eCFieldElement15.multiply(eCFieldElement16);
          return new F2m(eCCurve, eCFieldElement19, eCFieldElement21, new ECFieldElement[] { eCFieldElement22 }, this.withCompression);
        case 6:
          if (eCFieldElement1.isZero())
            return eCFieldElement2.isZero() ? eCCurve.getInfinity() : param1ECPoint.add(this); 
          eCFieldElement3 = this.y;
          eCFieldElement4 = this.zs[0];
          eCFieldElement5 = param1ECPoint.y;
          eCFieldElement6 = param1ECPoint.zs[0];
          bool1 = eCFieldElement4.isOne();
          eCFieldElement8 = eCFieldElement2;
          eCFieldElement9 = eCFieldElement5;
          if (!bool1) {
            eCFieldElement8 = eCFieldElement8.multiply(eCFieldElement4);
            eCFieldElement9 = eCFieldElement9.multiply(eCFieldElement4);
          } 
          bool2 = eCFieldElement6.isOne();
          eCFieldElement11 = eCFieldElement1;
          eCFieldElement12 = eCFieldElement3;
          if (!bool2) {
            eCFieldElement11 = eCFieldElement11.multiply(eCFieldElement6);
            eCFieldElement12 = eCFieldElement12.multiply(eCFieldElement6);
          } 
          eCFieldElement13 = eCFieldElement12.add(eCFieldElement9);
          eCFieldElement14 = eCFieldElement11.add(eCFieldElement8);
          if (eCFieldElement14.isZero())
            return eCFieldElement13.isZero() ? twice() : eCCurve.getInfinity(); 
          if (eCFieldElement2.isZero()) {
            ECPoint eCPoint = normalize();
            eCFieldElement1 = eCPoint.getXCoord();
            eCFieldElement19 = eCPoint.getYCoord();
            eCFieldElement20 = eCFieldElement5;
            eCFieldElement21 = eCFieldElement19.add(eCFieldElement20).divide(eCFieldElement1);
            eCFieldElement15 = eCFieldElement21.square().add(eCFieldElement21).add(eCFieldElement1).add(eCCurve.getA());
            if (eCFieldElement15.isZero())
              return new F2m(eCCurve, eCFieldElement15, eCCurve.getB().sqrt(), this.withCompression); 
            eCFieldElement22 = eCFieldElement21.multiply(eCFieldElement1.add(eCFieldElement15)).add(eCFieldElement15).add(eCFieldElement19);
            eCFieldElement16 = eCFieldElement22.divide(eCFieldElement15).add(eCFieldElement15);
            eCFieldElement17 = eCCurve.fromBigInteger(ECConstants.ONE);
          } else {
            eCFieldElement14 = eCFieldElement14.square();
            eCFieldElement18 = eCFieldElement13.multiply(eCFieldElement11);
            eCFieldElement19 = eCFieldElement13.multiply(eCFieldElement8);
            eCFieldElement15 = eCFieldElement18.multiply(eCFieldElement19);
            if (eCFieldElement15.isZero())
              return new F2m(eCCurve, eCFieldElement15, eCCurve.getB().sqrt(), this.withCompression); 
            eCFieldElement20 = eCFieldElement13.multiply(eCFieldElement14);
            if (!bool2)
              eCFieldElement20 = eCFieldElement20.multiply(eCFieldElement6); 
            eCFieldElement16 = eCFieldElement19.add(eCFieldElement14).squarePlusProduct(eCFieldElement20, eCFieldElement3.add(eCFieldElement4));
            eCFieldElement17 = eCFieldElement20;
            if (!bool1)
              eCFieldElement17 = eCFieldElement17.multiply(eCFieldElement4); 
          } 
          return new F2m(eCCurve, eCFieldElement15, eCFieldElement16, new ECFieldElement[] { eCFieldElement17 }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint twice() {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      boolean bool;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      if (isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      ECFieldElement eCFieldElement1 = this.x;
      if (eCFieldElement1.isZero())
        return eCCurve.getInfinity(); 
      int i = eCCurve.getCoordinateSystem();
      switch (i) {
        case 0:
          eCFieldElement2 = this.y;
          eCFieldElement3 = eCFieldElement2.divide(eCFieldElement1).add(eCFieldElement1);
          eCFieldElement4 = eCFieldElement3.square().add(eCFieldElement3).add(eCCurve.getA());
          eCFieldElement5 = eCFieldElement1.squarePlusProduct(eCFieldElement4, eCFieldElement3.addOne());
          return new F2m(eCCurve, eCFieldElement4, eCFieldElement5, this.withCompression);
        case 1:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          bool = eCFieldElement3.isOne();
          eCFieldElement5 = bool ? eCFieldElement1 : eCFieldElement1.multiply(eCFieldElement3);
          eCFieldElement6 = bool ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement3);
          eCFieldElement7 = eCFieldElement1.square();
          eCFieldElement8 = eCFieldElement7.add(eCFieldElement6);
          eCFieldElement9 = eCFieldElement5;
          eCFieldElement10 = eCFieldElement9.square();
          eCFieldElement11 = eCFieldElement8.add(eCFieldElement9);
          eCFieldElement12 = eCFieldElement11.multiplyPlusProduct(eCFieldElement8, eCFieldElement10, eCCurve.getA());
          eCFieldElement13 = eCFieldElement9.multiply(eCFieldElement12);
          eCFieldElement14 = eCFieldElement7.square().multiplyPlusProduct(eCFieldElement9, eCFieldElement12, eCFieldElement11);
          eCFieldElement15 = eCFieldElement9.multiply(eCFieldElement10);
          return new F2m(eCCurve, eCFieldElement13, eCFieldElement14, new ECFieldElement[] { eCFieldElement15 }, this.withCompression);
        case 6:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          bool = eCFieldElement3.isOne();
          eCFieldElement5 = bool ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement3);
          eCFieldElement6 = bool ? eCFieldElement3 : eCFieldElement3.square();
          eCFieldElement7 = eCCurve.getA();
          eCFieldElement8 = bool ? eCFieldElement7 : eCFieldElement7.multiply(eCFieldElement6);
          eCFieldElement9 = eCFieldElement2.square().add(eCFieldElement5).add(eCFieldElement8);
          if (eCFieldElement9.isZero())
            return new F2m(eCCurve, eCFieldElement9, eCCurve.getB().sqrt(), this.withCompression); 
          eCFieldElement10 = eCFieldElement9.square();
          eCFieldElement11 = bool ? eCFieldElement9 : eCFieldElement9.multiply(eCFieldElement6);
          eCFieldElement12 = eCCurve.getB();
          if (eCFieldElement12.bitLength() < eCCurve.getFieldSize() >> 1) {
            eCFieldElement14 = eCFieldElement2.add(eCFieldElement1).square();
            if (eCFieldElement12.isOne()) {
              eCFieldElement15 = eCFieldElement8.add(eCFieldElement6).square();
            } else {
              eCFieldElement15 = eCFieldElement8.squarePlusProduct(eCFieldElement12, eCFieldElement6.square());
            } 
            eCFieldElement13 = eCFieldElement14.add(eCFieldElement9).add(eCFieldElement6).multiply(eCFieldElement14).add(eCFieldElement15).add(eCFieldElement10);
            if (eCFieldElement7.isZero()) {
              eCFieldElement13 = eCFieldElement13.add(eCFieldElement11);
            } else if (!eCFieldElement7.isOne()) {
              eCFieldElement13 = eCFieldElement13.add(eCFieldElement7.addOne().multiply(eCFieldElement11));
            } 
          } else {
            eCFieldElement14 = bool ? eCFieldElement1 : eCFieldElement1.multiply(eCFieldElement3);
            eCFieldElement13 = eCFieldElement14.squarePlusProduct(eCFieldElement9, eCFieldElement5).add(eCFieldElement10).add(eCFieldElement11);
          } 
          return new F2m(eCCurve, eCFieldElement10, eCFieldElement13, new ECFieldElement[] { eCFieldElement11 }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint twicePlus(ECPoint param1ECPoint) {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      ECFieldElement eCFieldElement16;
      ECFieldElement eCFieldElement17;
      ECFieldElement eCFieldElement18;
      if (isInfinity())
        return param1ECPoint; 
      if (param1ECPoint.isInfinity())
        return twice(); 
      ECCurve eCCurve = getCurve();
      ECFieldElement eCFieldElement1 = this.x;
      if (eCFieldElement1.isZero())
        return param1ECPoint; 
      int i = eCCurve.getCoordinateSystem();
      switch (i) {
        case 6:
          eCFieldElement2 = param1ECPoint.x;
          eCFieldElement3 = param1ECPoint.zs[0];
          if (eCFieldElement2.isZero() || !eCFieldElement3.isOne())
            return twice().add(param1ECPoint); 
          eCFieldElement4 = this.y;
          eCFieldElement5 = this.zs[0];
          eCFieldElement6 = param1ECPoint.y;
          eCFieldElement7 = eCFieldElement1.square();
          eCFieldElement8 = eCFieldElement4.square();
          eCFieldElement9 = eCFieldElement5.square();
          eCFieldElement10 = eCFieldElement4.multiply(eCFieldElement5);
          eCFieldElement11 = eCCurve.getA().multiply(eCFieldElement9).add(eCFieldElement8).add(eCFieldElement10);
          eCFieldElement12 = eCFieldElement6.addOne();
          eCFieldElement13 = eCCurve.getA().add(eCFieldElement12).multiply(eCFieldElement9).add(eCFieldElement8).multiplyPlusProduct(eCFieldElement11, eCFieldElement7, eCFieldElement9);
          eCFieldElement14 = eCFieldElement2.multiply(eCFieldElement9);
          eCFieldElement15 = eCFieldElement14.add(eCFieldElement11).square();
          if (eCFieldElement15.isZero())
            return eCFieldElement13.isZero() ? param1ECPoint.twice() : eCCurve.getInfinity(); 
          if (eCFieldElement13.isZero())
            return new F2m(eCCurve, eCFieldElement13, eCCurve.getB().sqrt(), this.withCompression); 
          eCFieldElement16 = eCFieldElement13.square().multiply(eCFieldElement14);
          eCFieldElement17 = eCFieldElement13.multiply(eCFieldElement15).multiply(eCFieldElement9);
          eCFieldElement18 = eCFieldElement13.add(eCFieldElement15).square().multiplyPlusProduct(eCFieldElement11, eCFieldElement12, eCFieldElement17);
          return new F2m(eCCurve, eCFieldElement16, eCFieldElement18, new ECFieldElement[] { eCFieldElement17 }, this.withCompression);
      } 
      return twice().add(param1ECPoint);
    }
    
    public ECPoint negate() {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      if (isInfinity())
        return this; 
      ECFieldElement eCFieldElement1 = this.x;
      if (eCFieldElement1.isZero())
        return this; 
      switch (getCurveCoordinateSystem()) {
        case 0:
          eCFieldElement2 = this.y;
          return new F2m(this.curve, eCFieldElement1, eCFieldElement2.add(eCFieldElement1), this.withCompression);
        case 1:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          return new F2m(this.curve, eCFieldElement1, eCFieldElement2.add(eCFieldElement1), new ECFieldElement[] { eCFieldElement3 }, this.withCompression);
        case 5:
          eCFieldElement2 = this.y;
          return new F2m(this.curve, eCFieldElement1, eCFieldElement2.addOne(), this.withCompression);
        case 6:
          eCFieldElement2 = this.y;
          eCFieldElement3 = this.zs[0];
          return new F2m(this.curve, eCFieldElement1, eCFieldElement2.add(eCFieldElement3), new ECFieldElement[] { eCFieldElement3 }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
  }
  
  public static class Fp extends AbstractFp {
    public Fp(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      this(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, false);
    }
    
    public Fp(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, boolean param1Boolean) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2);
      if (((param1ECFieldElement1 == null) ? true : false) != ((param1ECFieldElement2 == null) ? true : false))
        throw new IllegalArgumentException("Exactly one of the field elements is null"); 
      this.withCompression = param1Boolean;
    }
    
    Fp(ECCurve param1ECCurve, ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement[] param1ArrayOfECFieldElement, boolean param1Boolean) {
      super(param1ECCurve, param1ECFieldElement1, param1ECFieldElement2, param1ArrayOfECFieldElement);
      this.withCompression = param1Boolean;
    }
    
    protected ECPoint detach() {
      return new Fp(null, getAffineXCoord(), getAffineYCoord());
    }
    
    public ECFieldElement getZCoord(int param1Int) {
      return (param1Int == 1 && 4 == getCurveCoordinateSystem()) ? getJacobianModifiedW() : super.getZCoord(param1Int);
    }
    
    public ECPoint add(ECPoint param1ECPoint) {
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      boolean bool1;
      ECFieldElement eCFieldElement9;
      boolean bool2;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement[] arrayOfECFieldElement;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      ECFieldElement eCFieldElement16;
      ECFieldElement eCFieldElement17;
      ECFieldElement eCFieldElement18;
      ECFieldElement eCFieldElement19;
      ECFieldElement eCFieldElement20;
      ECFieldElement eCFieldElement21;
      ECFieldElement eCFieldElement22;
      ECFieldElement eCFieldElement23;
      if (isInfinity())
        return param1ECPoint; 
      if (param1ECPoint.isInfinity())
        return this; 
      if (this == param1ECPoint)
        return twice(); 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement1 = this.x;
      ECFieldElement eCFieldElement2 = this.y;
      ECFieldElement eCFieldElement3 = param1ECPoint.x;
      ECFieldElement eCFieldElement4 = param1ECPoint.y;
      switch (i) {
        case 0:
          eCFieldElement5 = eCFieldElement3.subtract(eCFieldElement1);
          eCFieldElement6 = eCFieldElement4.subtract(eCFieldElement2);
          if (eCFieldElement5.isZero())
            return eCFieldElement6.isZero() ? twice() : eCCurve.getInfinity(); 
          eCFieldElement7 = eCFieldElement6.divide(eCFieldElement5);
          eCFieldElement9 = eCFieldElement7.square().subtract(eCFieldElement1).subtract(eCFieldElement3);
          eCFieldElement10 = eCFieldElement7.multiply(eCFieldElement1.subtract(eCFieldElement9)).subtract(eCFieldElement2);
          return new Fp(eCCurve, eCFieldElement9, eCFieldElement10, this.withCompression);
        case 1:
          eCFieldElement5 = this.zs[0];
          eCFieldElement6 = param1ECPoint.zs[0];
          bool1 = eCFieldElement5.isOne();
          bool2 = eCFieldElement6.isOne();
          eCFieldElement10 = bool1 ? eCFieldElement4 : eCFieldElement4.multiply(eCFieldElement5);
          eCFieldElement11 = bool2 ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement6);
          eCFieldElement12 = eCFieldElement10.subtract(eCFieldElement11);
          eCFieldElement13 = bool1 ? eCFieldElement3 : eCFieldElement3.multiply(eCFieldElement5);
          eCFieldElement14 = bool2 ? eCFieldElement1 : eCFieldElement1.multiply(eCFieldElement6);
          eCFieldElement15 = eCFieldElement13.subtract(eCFieldElement14);
          if (eCFieldElement15.isZero())
            return eCFieldElement12.isZero() ? twice() : eCCurve.getInfinity(); 
          eCFieldElement16 = bool1 ? eCFieldElement6 : (bool2 ? eCFieldElement5 : eCFieldElement5.multiply(eCFieldElement6));
          eCFieldElement17 = eCFieldElement15.square();
          eCFieldElement18 = eCFieldElement17.multiply(eCFieldElement15);
          eCFieldElement19 = eCFieldElement17.multiply(eCFieldElement14);
          eCFieldElement20 = eCFieldElement12.square().multiply(eCFieldElement16).subtract(eCFieldElement18).subtract(two(eCFieldElement19));
          eCFieldElement21 = eCFieldElement15.multiply(eCFieldElement20);
          eCFieldElement22 = eCFieldElement19.subtract(eCFieldElement20).multiplyMinusProduct(eCFieldElement12, eCFieldElement11, eCFieldElement18);
          eCFieldElement23 = eCFieldElement18.multiply(eCFieldElement16);
          return new Fp(eCCurve, eCFieldElement21, eCFieldElement22, new ECFieldElement[] { eCFieldElement23 }, this.withCompression);
        case 2:
        case 4:
          eCFieldElement5 = this.zs[0];
          eCFieldElement6 = param1ECPoint.zs[0];
          bool1 = eCFieldElement5.isOne();
          eCFieldElement12 = null;
          if (!bool1 && eCFieldElement5.equals(eCFieldElement6)) {
            eCFieldElement13 = eCFieldElement1.subtract(eCFieldElement3);
            eCFieldElement14 = eCFieldElement2.subtract(eCFieldElement4);
            if (eCFieldElement13.isZero())
              return eCFieldElement14.isZero() ? twice() : eCCurve.getInfinity(); 
            eCFieldElement15 = eCFieldElement13.square();
            eCFieldElement16 = eCFieldElement1.multiply(eCFieldElement15);
            eCFieldElement17 = eCFieldElement3.multiply(eCFieldElement15);
            eCFieldElement18 = eCFieldElement16.subtract(eCFieldElement17).multiply(eCFieldElement2);
            eCFieldElement8 = eCFieldElement14.square().subtract(eCFieldElement16).subtract(eCFieldElement17);
            eCFieldElement10 = eCFieldElement16.subtract(eCFieldElement8).multiply(eCFieldElement14).subtract(eCFieldElement18);
            eCFieldElement11 = eCFieldElement13;
            eCFieldElement11 = eCFieldElement11.multiply(eCFieldElement5);
          } else {
            if (bool1) {
              eCFieldElement13 = eCFieldElement5;
              eCFieldElement14 = eCFieldElement3;
              eCFieldElement15 = eCFieldElement4;
            } else {
              eCFieldElement13 = eCFieldElement5.square();
              eCFieldElement14 = eCFieldElement13.multiply(eCFieldElement3);
              eCFieldElement16 = eCFieldElement13.multiply(eCFieldElement5);
              eCFieldElement15 = eCFieldElement16.multiply(eCFieldElement4);
            } 
            boolean bool = eCFieldElement6.isOne();
            if (bool) {
              eCFieldElement17 = eCFieldElement6;
              eCFieldElement18 = eCFieldElement1;
              eCFieldElement19 = eCFieldElement2;
            } else {
              eCFieldElement17 = eCFieldElement6.square();
              eCFieldElement18 = eCFieldElement17.multiply(eCFieldElement1);
              eCFieldElement20 = eCFieldElement17.multiply(eCFieldElement6);
              eCFieldElement19 = eCFieldElement20.multiply(eCFieldElement2);
            } 
            eCFieldElement20 = eCFieldElement18.subtract(eCFieldElement14);
            eCFieldElement21 = eCFieldElement19.subtract(eCFieldElement15);
            if (eCFieldElement20.isZero())
              return eCFieldElement21.isZero() ? twice() : eCCurve.getInfinity(); 
            eCFieldElement22 = eCFieldElement20.square();
            eCFieldElement23 = eCFieldElement22.multiply(eCFieldElement20);
            ECFieldElement eCFieldElement = eCFieldElement22.multiply(eCFieldElement18);
            eCFieldElement8 = eCFieldElement21.square().add(eCFieldElement23).subtract(two(eCFieldElement));
            eCFieldElement10 = eCFieldElement.subtract(eCFieldElement8).multiplyMinusProduct(eCFieldElement21, eCFieldElement23, eCFieldElement19);
            eCFieldElement11 = eCFieldElement20;
            if (!bool1)
              eCFieldElement11 = eCFieldElement11.multiply(eCFieldElement5); 
            if (!bool)
              eCFieldElement11 = eCFieldElement11.multiply(eCFieldElement6); 
            if (eCFieldElement11 == eCFieldElement20)
              eCFieldElement12 = eCFieldElement22; 
          } 
          if (i == 4) {
            eCFieldElement14 = calculateJacobianModifiedW(eCFieldElement11, eCFieldElement12);
            arrayOfECFieldElement = new ECFieldElement[] { eCFieldElement11, eCFieldElement14 };
          } else {
            arrayOfECFieldElement = new ECFieldElement[] { eCFieldElement11 };
          } 
          return new Fp(eCCurve, eCFieldElement8, eCFieldElement10, arrayOfECFieldElement, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint twice() {
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      boolean bool;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      ECFieldElement eCFieldElement16;
      if (isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      ECFieldElement eCFieldElement1 = this.y;
      if (eCFieldElement1.isZero())
        return eCCurve.getInfinity(); 
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement2 = this.x;
      switch (i) {
        case 0:
          eCFieldElement3 = eCFieldElement2.square();
          eCFieldElement4 = three(eCFieldElement3).add(getCurve().getA()).divide(two(eCFieldElement1));
          eCFieldElement5 = eCFieldElement4.square().subtract(two(eCFieldElement2));
          eCFieldElement6 = eCFieldElement4.multiply(eCFieldElement2.subtract(eCFieldElement5)).subtract(eCFieldElement1);
          return new Fp(eCCurve, eCFieldElement5, eCFieldElement6, this.withCompression);
        case 1:
          eCFieldElement3 = this.zs[0];
          bool = eCFieldElement3.isOne();
          eCFieldElement5 = eCCurve.getA();
          if (!eCFieldElement5.isZero() && !bool)
            eCFieldElement5 = eCFieldElement5.multiply(eCFieldElement3.square()); 
          eCFieldElement5 = eCFieldElement5.add(three(eCFieldElement2.square()));
          eCFieldElement6 = bool ? eCFieldElement1 : eCFieldElement1.multiply(eCFieldElement3);
          eCFieldElement7 = bool ? eCFieldElement1.square() : eCFieldElement6.multiply(eCFieldElement1);
          eCFieldElement8 = eCFieldElement2.multiply(eCFieldElement7);
          eCFieldElement9 = four(eCFieldElement8);
          eCFieldElement10 = eCFieldElement5.square().subtract(two(eCFieldElement9));
          eCFieldElement11 = two(eCFieldElement6);
          eCFieldElement12 = eCFieldElement10.multiply(eCFieldElement11);
          eCFieldElement13 = two(eCFieldElement7);
          eCFieldElement14 = eCFieldElement9.subtract(eCFieldElement10).multiply(eCFieldElement5).subtract(two(eCFieldElement13.square()));
          eCFieldElement15 = bool ? two(eCFieldElement13) : eCFieldElement11.square();
          eCFieldElement16 = two(eCFieldElement15).multiply(eCFieldElement6);
          return new Fp(eCCurve, eCFieldElement12, eCFieldElement14, new ECFieldElement[] { eCFieldElement16 }, this.withCompression);
        case 2:
          eCFieldElement3 = this.zs[0];
          bool = eCFieldElement3.isOne();
          eCFieldElement5 = eCFieldElement1.square();
          eCFieldElement6 = eCFieldElement5.square();
          eCFieldElement7 = eCCurve.getA();
          eCFieldElement8 = eCFieldElement7.negate();
          if (eCFieldElement8.toBigInteger().equals(BigInteger.valueOf(3L))) {
            eCFieldElement11 = bool ? eCFieldElement3 : eCFieldElement3.square();
            eCFieldElement9 = three(eCFieldElement2.add(eCFieldElement11).multiply(eCFieldElement2.subtract(eCFieldElement11)));
            eCFieldElement10 = four(eCFieldElement5.multiply(eCFieldElement2));
          } else {
            eCFieldElement11 = eCFieldElement2.square();
            eCFieldElement9 = three(eCFieldElement11);
            if (bool) {
              eCFieldElement9 = eCFieldElement9.add(eCFieldElement7);
            } else if (!eCFieldElement7.isZero()) {
              eCFieldElement12 = eCFieldElement3.square();
              eCFieldElement13 = eCFieldElement12.square();
              if (eCFieldElement8.bitLength() < eCFieldElement7.bitLength()) {
                eCFieldElement9 = eCFieldElement9.subtract(eCFieldElement13.multiply(eCFieldElement8));
              } else {
                eCFieldElement9 = eCFieldElement9.add(eCFieldElement13.multiply(eCFieldElement7));
              } 
            } 
            eCFieldElement10 = four(eCFieldElement2.multiply(eCFieldElement5));
          } 
          eCFieldElement11 = eCFieldElement9.square().subtract(two(eCFieldElement10));
          eCFieldElement12 = eCFieldElement10.subtract(eCFieldElement11).multiply(eCFieldElement9).subtract(eight(eCFieldElement6));
          eCFieldElement13 = two(eCFieldElement1);
          if (!bool)
            eCFieldElement13 = eCFieldElement13.multiply(eCFieldElement3); 
          return new Fp(eCCurve, eCFieldElement11, eCFieldElement12, new ECFieldElement[] { eCFieldElement13 }, this.withCompression);
        case 4:
          return twiceJacobianModified(true);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    public ECPoint twicePlus(ECPoint param1ECPoint) {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      ECFieldElement eCFieldElement14;
      ECFieldElement eCFieldElement15;
      if (this == param1ECPoint)
        return threeTimes(); 
      if (isInfinity())
        return param1ECPoint; 
      if (param1ECPoint.isInfinity())
        return twice(); 
      ECFieldElement eCFieldElement1 = this.y;
      if (eCFieldElement1.isZero())
        return param1ECPoint; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      switch (i) {
        case 0:
          eCFieldElement2 = this.x;
          eCFieldElement3 = param1ECPoint.x;
          eCFieldElement4 = param1ECPoint.y;
          eCFieldElement5 = eCFieldElement3.subtract(eCFieldElement2);
          eCFieldElement6 = eCFieldElement4.subtract(eCFieldElement1);
          if (eCFieldElement5.isZero())
            return eCFieldElement6.isZero() ? threeTimes() : this; 
          eCFieldElement7 = eCFieldElement5.square();
          eCFieldElement8 = eCFieldElement6.square();
          eCFieldElement9 = eCFieldElement7.multiply(two(eCFieldElement2).add(eCFieldElement3)).subtract(eCFieldElement8);
          if (eCFieldElement9.isZero())
            return eCCurve.getInfinity(); 
          eCFieldElement10 = eCFieldElement9.multiply(eCFieldElement5);
          eCFieldElement11 = eCFieldElement10.invert();
          eCFieldElement12 = eCFieldElement9.multiply(eCFieldElement11).multiply(eCFieldElement6);
          eCFieldElement13 = two(eCFieldElement1).multiply(eCFieldElement7).multiply(eCFieldElement5).multiply(eCFieldElement11).subtract(eCFieldElement12);
          eCFieldElement14 = eCFieldElement13.subtract(eCFieldElement12).multiply(eCFieldElement12.add(eCFieldElement13)).add(eCFieldElement3);
          eCFieldElement15 = eCFieldElement2.subtract(eCFieldElement14).multiply(eCFieldElement13).subtract(eCFieldElement1);
          return new Fp(eCCurve, eCFieldElement14, eCFieldElement15, this.withCompression);
        case 4:
          return twiceJacobianModified(false).add(param1ECPoint);
      } 
      return twice().add(param1ECPoint);
    }
    
    public ECPoint threeTimes() {
      ECFieldElement eCFieldElement2;
      ECFieldElement eCFieldElement3;
      ECFieldElement eCFieldElement4;
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      ECFieldElement eCFieldElement8;
      ECFieldElement eCFieldElement9;
      ECFieldElement eCFieldElement10;
      ECFieldElement eCFieldElement11;
      ECFieldElement eCFieldElement12;
      ECFieldElement eCFieldElement13;
      if (isInfinity())
        return this; 
      ECFieldElement eCFieldElement1 = this.y;
      if (eCFieldElement1.isZero())
        return this; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      switch (i) {
        case 0:
          eCFieldElement2 = this.x;
          eCFieldElement3 = two(eCFieldElement1);
          eCFieldElement4 = eCFieldElement3.square();
          eCFieldElement5 = three(eCFieldElement2.square()).add(getCurve().getA());
          eCFieldElement6 = eCFieldElement5.square();
          eCFieldElement7 = three(eCFieldElement2).multiply(eCFieldElement4).subtract(eCFieldElement6);
          if (eCFieldElement7.isZero())
            return getCurve().getInfinity(); 
          eCFieldElement8 = eCFieldElement7.multiply(eCFieldElement3);
          eCFieldElement9 = eCFieldElement8.invert();
          eCFieldElement10 = eCFieldElement7.multiply(eCFieldElement9).multiply(eCFieldElement5);
          eCFieldElement11 = eCFieldElement4.square().multiply(eCFieldElement9).subtract(eCFieldElement10);
          eCFieldElement12 = eCFieldElement11.subtract(eCFieldElement10).multiply(eCFieldElement10.add(eCFieldElement11)).add(eCFieldElement2);
          eCFieldElement13 = eCFieldElement2.subtract(eCFieldElement12).multiply(eCFieldElement11).subtract(eCFieldElement1);
          return new Fp(eCCurve, eCFieldElement12, eCFieldElement13, this.withCompression);
        case 4:
          return twiceJacobianModified(false).add(this);
      } 
      return twice().add(this);
    }
    
    public ECPoint timesPow2(int param1Int) {
      ECFieldElement eCFieldElement5;
      ECFieldElement eCFieldElement6;
      ECFieldElement eCFieldElement7;
      if (param1Int < 0)
        throw new IllegalArgumentException("'e' cannot be negative"); 
      if (param1Int == 0 || isInfinity())
        return this; 
      if (param1Int == 1)
        return twice(); 
      ECCurve eCCurve = getCurve();
      ECFieldElement eCFieldElement1 = this.y;
      if (eCFieldElement1.isZero())
        return eCCurve.getInfinity(); 
      int i = eCCurve.getCoordinateSystem();
      ECFieldElement eCFieldElement2 = eCCurve.getA();
      ECFieldElement eCFieldElement3 = this.x;
      ECFieldElement eCFieldElement4 = (this.zs.length < 1) ? eCCurve.fromBigInteger(ECConstants.ONE) : this.zs[0];
      if (!eCFieldElement4.isOne()) {
        ECFieldElement eCFieldElement;
        switch (i) {
          case 0:
            break;
          case 1:
            eCFieldElement = eCFieldElement4.square();
            eCFieldElement3 = eCFieldElement3.multiply(eCFieldElement4);
            eCFieldElement1 = eCFieldElement1.multiply(eCFieldElement);
            eCFieldElement2 = calculateJacobianModifiedW(eCFieldElement4, eCFieldElement);
            break;
          case 2:
            eCFieldElement2 = calculateJacobianModifiedW(eCFieldElement4, (ECFieldElement)null);
            break;
          case 4:
            eCFieldElement2 = getJacobianModifiedW();
            break;
          default:
            throw new IllegalStateException("unsupported coordinate system");
        } 
      } 
      for (byte b = 0; b < param1Int; b++) {
        if (eCFieldElement1.isZero())
          return eCCurve.getInfinity(); 
        ECFieldElement eCFieldElement8 = eCFieldElement3.square();
        ECFieldElement eCFieldElement9 = three(eCFieldElement8);
        ECFieldElement eCFieldElement10 = two(eCFieldElement1);
        ECFieldElement eCFieldElement11 = eCFieldElement10.multiply(eCFieldElement1);
        ECFieldElement eCFieldElement12 = two(eCFieldElement3.multiply(eCFieldElement11));
        ECFieldElement eCFieldElement13 = eCFieldElement11.square();
        ECFieldElement eCFieldElement14 = two(eCFieldElement13);
        if (!eCFieldElement2.isZero()) {
          eCFieldElement9 = eCFieldElement9.add(eCFieldElement2);
          eCFieldElement2 = two(eCFieldElement14.multiply(eCFieldElement2));
        } 
        eCFieldElement3 = eCFieldElement9.square().subtract(two(eCFieldElement12));
        eCFieldElement1 = eCFieldElement9.multiply(eCFieldElement12.subtract(eCFieldElement3)).subtract(eCFieldElement14);
        eCFieldElement4 = eCFieldElement4.isOne() ? eCFieldElement10 : eCFieldElement10.multiply(eCFieldElement4);
      } 
      switch (i) {
        case 0:
          eCFieldElement5 = eCFieldElement4.invert();
          eCFieldElement6 = eCFieldElement5.square();
          eCFieldElement7 = eCFieldElement6.multiply(eCFieldElement5);
          return new Fp(eCCurve, eCFieldElement3.multiply(eCFieldElement6), eCFieldElement1.multiply(eCFieldElement7), this.withCompression);
        case 1:
          eCFieldElement3 = eCFieldElement3.multiply(eCFieldElement4);
          eCFieldElement4 = eCFieldElement4.multiply(eCFieldElement4.square());
          return new Fp(eCCurve, eCFieldElement3, eCFieldElement1, new ECFieldElement[] { eCFieldElement4 }, this.withCompression);
        case 2:
          return new Fp(eCCurve, eCFieldElement3, eCFieldElement1, new ECFieldElement[] { eCFieldElement4 }, this.withCompression);
        case 4:
          return new Fp(eCCurve, eCFieldElement3, eCFieldElement1, new ECFieldElement[] { eCFieldElement4, eCFieldElement2 }, this.withCompression);
      } 
      throw new IllegalStateException("unsupported coordinate system");
    }
    
    protected ECFieldElement two(ECFieldElement param1ECFieldElement) {
      return param1ECFieldElement.add(param1ECFieldElement);
    }
    
    protected ECFieldElement three(ECFieldElement param1ECFieldElement) {
      return two(param1ECFieldElement).add(param1ECFieldElement);
    }
    
    protected ECFieldElement four(ECFieldElement param1ECFieldElement) {
      return two(two(param1ECFieldElement));
    }
    
    protected ECFieldElement eight(ECFieldElement param1ECFieldElement) {
      return four(two(param1ECFieldElement));
    }
    
    protected ECFieldElement doubleProductFromSquares(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement param1ECFieldElement3, ECFieldElement param1ECFieldElement4) {
      return param1ECFieldElement1.add(param1ECFieldElement2).square().subtract(param1ECFieldElement3).subtract(param1ECFieldElement4);
    }
    
    public ECPoint negate() {
      if (isInfinity())
        return this; 
      ECCurve eCCurve = getCurve();
      int i = eCCurve.getCoordinateSystem();
      return (0 != i) ? new Fp(eCCurve, this.x, this.y.negate(), this.zs, this.withCompression) : new Fp(eCCurve, this.x, this.y.negate(), this.withCompression);
    }
    
    protected ECFieldElement calculateJacobianModifiedW(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      ECFieldElement eCFieldElement1 = getCurve().getA();
      if (eCFieldElement1.isZero() || param1ECFieldElement1.isOne())
        return eCFieldElement1; 
      if (param1ECFieldElement2 == null)
        param1ECFieldElement2 = param1ECFieldElement1.square(); 
      ECFieldElement eCFieldElement2 = param1ECFieldElement2.square();
      ECFieldElement eCFieldElement3 = eCFieldElement1.negate();
      if (eCFieldElement3.bitLength() < eCFieldElement1.bitLength()) {
        eCFieldElement2 = eCFieldElement2.multiply(eCFieldElement3).negate();
      } else {
        eCFieldElement2 = eCFieldElement2.multiply(eCFieldElement1);
      } 
      return eCFieldElement2;
    }
    
    protected ECFieldElement getJacobianModifiedW() {
      ECFieldElement eCFieldElement = this.zs[1];
      if (eCFieldElement == null)
        this.zs[1] = eCFieldElement = calculateJacobianModifiedW(this.zs[0], (ECFieldElement)null); 
      return eCFieldElement;
    }
    
    protected Fp twiceJacobianModified(boolean param1Boolean) {
      ECFieldElement eCFieldElement1 = this.x;
      ECFieldElement eCFieldElement2 = this.y;
      ECFieldElement eCFieldElement3 = this.zs[0];
      ECFieldElement eCFieldElement4 = getJacobianModifiedW();
      ECFieldElement eCFieldElement5 = eCFieldElement1.square();
      ECFieldElement eCFieldElement6 = three(eCFieldElement5).add(eCFieldElement4);
      ECFieldElement eCFieldElement7 = two(eCFieldElement2);
      ECFieldElement eCFieldElement8 = eCFieldElement7.multiply(eCFieldElement2);
      ECFieldElement eCFieldElement9 = two(eCFieldElement1.multiply(eCFieldElement8));
      ECFieldElement eCFieldElement10 = eCFieldElement6.square().subtract(two(eCFieldElement9));
      ECFieldElement eCFieldElement11 = eCFieldElement8.square();
      ECFieldElement eCFieldElement12 = two(eCFieldElement11);
      ECFieldElement eCFieldElement13 = eCFieldElement6.multiply(eCFieldElement9.subtract(eCFieldElement10)).subtract(eCFieldElement12);
      ECFieldElement eCFieldElement14 = param1Boolean ? two(eCFieldElement12.multiply(eCFieldElement4)) : null;
      ECFieldElement eCFieldElement15 = eCFieldElement3.isOne() ? eCFieldElement7 : eCFieldElement7.multiply(eCFieldElement3);
      return new Fp(getCurve(), eCFieldElement10, eCFieldElement13, new ECFieldElement[] { eCFieldElement15, eCFieldElement14 }, this.withCompression);
    }
  }
}
