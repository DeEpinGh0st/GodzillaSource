package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

public class SecT163FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT163FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 163)
      throw new IllegalArgumentException("x value invalid for SecT163FieldElement"); 
    this.x = SecT163Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT163FieldElement() {
    this.x = Nat192.create64();
  }
  
  protected SecT163FieldElement(long[] paramArrayOflong) {
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
    return "SecT163Field";
  }
  
  public int getFieldSize() {
    return 163;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.add(this.x, ((SecT163FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.addOne(this.x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.multiply(this.x, ((SecT163FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT163FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT163FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT163FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat192.createExt64();
    SecT163Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT163Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat192.create64();
    SecT163Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT163FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.square(this.x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT163FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT163FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat192.createExt64();
    SecT163Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT163Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat192.create64();
    SecT163Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT163FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.invert(this.x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat192.create64();
    SecT163Field.sqrt(this.x, arrayOfLong);
    return new SecT163FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 3;
  }
  
  public int getM() {
    return 163;
  }
  
  public int getK1() {
    return 3;
  }
  
  public int getK2() {
    return 6;
  }
  
  public int getK3() {
    return 7;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecT163FieldElement))
      return false; 
    SecT163FieldElement secT163FieldElement = (SecT163FieldElement)paramObject;
    return Nat192.eq64(this.x, secT163FieldElement.x);
  }
  
  public int hashCode() {
    return 0x27FB3 ^ Arrays.hashCode(this.x, 0, 3);
  }
}
