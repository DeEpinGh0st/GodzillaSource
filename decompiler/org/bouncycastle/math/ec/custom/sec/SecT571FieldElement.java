package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.util.Arrays;

public class SecT571FieldElement extends ECFieldElement {
  protected long[] x;
  
  public SecT571FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.bitLength() > 571)
      throw new IllegalArgumentException("x value invalid for SecT571FieldElement"); 
    this.x = SecT571Field.fromBigInteger(paramBigInteger);
  }
  
  public SecT571FieldElement() {
    this.x = Nat576.create64();
  }
  
  protected SecT571FieldElement(long[] paramArrayOflong) {
    this.x = paramArrayOflong;
  }
  
  public boolean isOne() {
    return Nat576.isOne64(this.x);
  }
  
  public boolean isZero() {
    return Nat576.isZero64(this.x);
  }
  
  public boolean testBitZero() {
    return ((this.x[0] & 0x1L) != 0L);
  }
  
  public BigInteger toBigInteger() {
    return Nat576.toBigInteger64(this.x);
  }
  
  public String getFieldName() {
    return "SecT571Field";
  }
  
  public int getFieldSize() {
    return 571;
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.add(this.x, ((SecT571FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement addOne() {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.addOne(this.x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    return add(paramECFieldElement);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.multiply(this.x, ((SecT571FieldElement)paramECFieldElement).x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiplyPlusProduct(paramECFieldElement1, paramECFieldElement2, paramECFieldElement3);
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT571FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT571FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = ((SecT571FieldElement)paramECFieldElement3).x;
    long[] arrayOfLong5 = Nat576.createExt64();
    SecT571Field.multiplyAddToExt(arrayOfLong1, arrayOfLong2, arrayOfLong5);
    SecT571Field.multiplyAddToExt(arrayOfLong3, arrayOfLong4, arrayOfLong5);
    long[] arrayOfLong6 = Nat576.create64();
    SecT571Field.reduce(arrayOfLong5, arrayOfLong6);
    return new SecT571FieldElement(arrayOfLong6);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    return multiply(paramECFieldElement.invert());
  }
  
  public ECFieldElement negate() {
    return this;
  }
  
  public ECFieldElement square() {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.square(this.x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return squarePlusProduct(paramECFieldElement1, paramECFieldElement2);
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    long[] arrayOfLong1 = this.x;
    long[] arrayOfLong2 = ((SecT571FieldElement)paramECFieldElement1).x;
    long[] arrayOfLong3 = ((SecT571FieldElement)paramECFieldElement2).x;
    long[] arrayOfLong4 = Nat576.createExt64();
    SecT571Field.squareAddToExt(arrayOfLong1, arrayOfLong4);
    SecT571Field.multiplyAddToExt(arrayOfLong2, arrayOfLong3, arrayOfLong4);
    long[] arrayOfLong5 = Nat576.create64();
    SecT571Field.reduce(arrayOfLong4, arrayOfLong5);
    return new SecT571FieldElement(arrayOfLong5);
  }
  
  public ECFieldElement squarePow(int paramInt) {
    if (paramInt < 1)
      return this; 
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.squareN(this.x, paramInt, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement invert() {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.invert(this.x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public ECFieldElement sqrt() {
    long[] arrayOfLong = Nat576.create64();
    SecT571Field.sqrt(this.x, arrayOfLong);
    return new SecT571FieldElement(arrayOfLong);
  }
  
  public int getRepresentation() {
    return 3;
  }
  
  public int getM() {
    return 571;
  }
  
  public int getK1() {
    return 2;
  }
  
  public int getK2() {
    return 5;
  }
  
  public int getK3() {
    return 10;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecT571FieldElement))
      return false; 
    SecT571FieldElement secT571FieldElement = (SecT571FieldElement)paramObject;
    return Nat576.eq64(this.x, secT571FieldElement.x);
  }
  
  public int hashCode() {
    return 0x5724CC ^ Arrays.hashCode(this.x, 0, 9);
  }
}
