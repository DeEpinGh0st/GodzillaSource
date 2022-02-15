package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat320;
import org.bouncycastle.util.Arrays;

public class SecT283FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT283FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 283)
      throw new IllegalArgumentException("x value invalid for SecT283FieldElement"); 
    this.x = SecT283Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT283FieldElement() {
    this.x = Nat320.create64();
  }
  
  protected SecT283FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat320.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat320.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat320.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT283Field";
  }
  
  public int getFieldSize() {
    return 283;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.add(this.x, ((SecT283FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.addOne(this.x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.multiply(this.x, ((SecT283FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT283FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT283FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT283FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat.create64(9);
    SecT283Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT283Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat320.create64();
    SecT283Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT283FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.square(this.x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT283FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT283FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat.create64(9);
    SecT283Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT283Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat320.create64();
    SecT283Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT283FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.invert(this.x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat320.create64();
    SecT283Field.sqrt(this.x, arrayOfLong);
    return new SecT283FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 3;
  }
  
  public int getM() {
    return 283;
  }
  
  public int getK1() {
    return 5;
  }
  
  public int getK2() {
    return 7;
  }
  
  public int getK3() {
    return 12;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecT283FieldElement))
      return false; 
    SecT283FieldElement secT283FieldElement = (SecT283FieldElement)paramObject;
    return Nat320.eq64(this.x, secT283FieldElement.x);
  }
  
  public int hashCode() {
    return 0x2B33AB ^ Arrays.hashCode(this.x, 0, 5);
  }
}
