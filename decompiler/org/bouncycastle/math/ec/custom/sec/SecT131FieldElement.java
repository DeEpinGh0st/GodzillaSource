package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

public class SecT131FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT131FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 131)
      throw new IllegalArgumentException("x value invalid for SecT131FieldElement"); 
    this.x = SecT131Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT131FieldElement() {
    this.x = Nat192.create64();
  }
  
  protected SecT131FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat192.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat192.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat192.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT131Field";
  }
  
  public int getFieldSize() {
    return 131;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.add(this.x, ((SecT131FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.addOne(this.x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.multiply(this.x, ((SecT131FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT131FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT131FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT131FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat.create64(5);
    SecT131Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT131Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat192.create64();
    SecT131Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT131FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.square(this.x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT131FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT131FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat.create64(5);
    SecT131Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT131Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat192.create64();
    SecT131Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT131FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.invert(this.x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat192.create64();
    SecT131Field.sqrt(this.x, arrayOfLong);
    return new SecT131FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 3;
  }
  
  public int getM() {
    return 131;
  }
  
  public int getK1() {
    return 2;
  }
  
  public int getK2() {
    return 3;
  }
  
  public int getK3() {
    return 8;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecT131FieldElement))
      return false; 
    SecT131FieldElement secT131FieldElement = (SecT131FieldElement)paramObject;
    return Nat192.eq64(this.x, secT131FieldElement.x);
  }
  
  public int hashCode() {
    return 0x202F8 ^ Arrays.hashCode(this.x, 0, 3);
  }
}
