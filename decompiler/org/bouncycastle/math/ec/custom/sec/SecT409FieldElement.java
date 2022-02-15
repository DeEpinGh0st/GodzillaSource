package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;
import org.bouncycastle.util.Arrays;

public class SecT409FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT409FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 409)
      throw new IllegalArgumentException("x value invalid for SecT409FieldElement"); 
    this.x = SecT409Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT409FieldElement() {
    this.x = Nat448.create64();
  }
  
  protected SecT409FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat448.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat448.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat448.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT409Field";
  }
  
  public int getFieldSize() {
    return 409;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.add(this.x, ((SecT409FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.addOne(this.x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.multiply(this.x, ((SecT409FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT409FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT409FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT409FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat.create64(13);
    SecT409Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT409Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat448.create64();
    SecT409Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT409FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.square(this.x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT409FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT409FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat.create64(13);
    SecT409Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT409Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat448.create64();
    SecT409Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT409FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.invert(this.x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat448.create64();
    SecT409Field.sqrt(this.x, arrayOfLong);
    return new SecT409FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 2;
  }
  
  public int getM() {
    return 409;
  }
  
  public int getK1() {
    return 87;
  }
  
  public int getK2() {
    return 0;
  }
  
  public int getK3() {
    return 0;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecT409FieldElement))
      return false; 
    SecT409FieldElement secT409FieldElement = (SecT409FieldElement)paramObject;
    return Nat448.eq64(this.x, secT409FieldElement.x);
  }
  
  public int hashCode() {
    return 0x3E68E7 ^ Arrays.hashCode(this.x, 0, 7);
  }
}
