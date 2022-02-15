package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class SecT193FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT193FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 193)
      throw new IllegalArgumentException("x value invalid for SecT193FieldElement"); 
    this.x = SecT193Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT193FieldElement() {
    this.x = Nat256.create64();
  }
  
  protected SecT193FieldElement(long[] paramArrayOflong) {
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
    return "SecT193Field";
  }
  
  public int getFieldSize() {
    return 193;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.add(this.x, ((SecT193FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.addOne(this.x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.multiply(this.x, ((SecT193FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT193FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT193FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT193FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat256.createExt64();
    SecT193Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT193Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat256.create64();
    SecT193Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT193FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.square(this.x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT193FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT193FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat256.createExt64();
    SecT193Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT193Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat256.create64();
    SecT193Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT193FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.invert(this.x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat256.create64();
    SecT193Field.sqrt(this.x, arrayOfLong);
    return new SecT193FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 2;
  }
  
  public int getM() {
    return 193;
  }
  
  public int getK1() {
    return 15;
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
    if (!(paramObject instanceof SecT193FieldElement))
      return false; 
    SecT193FieldElement secT193FieldElement = (SecT193FieldElement)paramObject;
    return Nat256.eq64(this.x, secT193FieldElement.x);
  }
  
  public int hashCode() {
    return 0x1D731F ^ Arrays.hashCode(this.x, 0, 4);
  }
}
