package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class SecT233FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT233FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 233)
      throw new IllegalArgumentException("x value invalid for SecT233FieldElement"); 
    this.x = SecT233Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT233FieldElement() {
    this.x = Nat256.create64();
  }
  
  protected SecT233FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat256.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat256.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat256.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT233Field";
  }
  
  public int getFieldSize() {
    return 233;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.add(this.x, ((SecT233FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.addOne(this.x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.multiply(this.x, ((SecT233FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT233FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT233FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT233FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat256.createExt64();
    SecT233Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT233Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat256.create64();
    SecT233Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT233FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.square(this.x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT233FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT233FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat256.createExt64();
    SecT233Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT233Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat256.create64();
    SecT233Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT233FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.invert(this.x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat256.create64();
    SecT233Field.sqrt(this.x, arrayOfLong);
    return new SecT233FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 2;
  }
  
  public int getM() {
    return 233;
  }
  
  public int getK1() {
    return 74;
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
    if (!(paramObject instanceof SecT233FieldElement))
      return false; 
    SecT233FieldElement secT233FieldElement = (SecT233FieldElement)paramObject;
    return Nat256.eq64(this.x, secT233FieldElement.x);
  }
  
  public int hashCode() {
    return 0x238DDA ^ Arrays.hashCode(this.x, 0, 4);
  }
}
