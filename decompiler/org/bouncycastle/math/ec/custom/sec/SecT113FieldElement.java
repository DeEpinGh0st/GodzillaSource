package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.Arrays;

public class SecT113FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT113FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 113)
      throw new IllegalArgumentException("x value invalid for SecT113FieldElement"); 
    this.x = SecT113Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT113FieldElement() {
    this.x = Nat128.create64();
  }
  
  protected SecT113FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat128.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat128.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat128.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT113Field";
  }
  
  public int getFieldSize() {
    return 113;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.add(this.x, ((SecT113FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.addOne(this.x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.multiply(this.x, ((SecT113FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT113FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT113FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT113FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat128.createExt64();
    SecT113Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT113Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat128.create64();
    SecT113Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT113FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.square(this.x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT113FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT113FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat128.createExt64();
    SecT113Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT113Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat128.create64();
    SecT113Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT113FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.invert(this.x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat128.create64();
    SecT113Field.sqrt(this.x, arrayOfLong);
    return new SecT113FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 2;
  }
  
  public int getM() {
    return 113;
  }
  
  public int getK1() {
    return 9;
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
    if (!(paramObject instanceof SecT113FieldElement))
      return false; 
    SecT113FieldElement secT113FieldElement = (SecT113FieldElement)paramObject;
    return Nat128.eq64(this.x, secT113FieldElement.x);
  }
  
  public int hashCode() {
    return 0x1B971 ^ Arrays.hashCode(this.x, 0, 2);
  }
}
