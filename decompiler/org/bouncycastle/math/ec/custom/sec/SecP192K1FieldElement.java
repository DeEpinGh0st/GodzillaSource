package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

public class SecP192K1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP192K1Curve.q;
  
  protected int[] x;
  
  public SecP192K1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP192K1FieldElement"); 
    this.x = SecP192K1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP192K1FieldElement() {
    this.x = Nat192.create();
  }
  
  protected SecP192K1FieldElement(int[] paramArrayOfint) {
    this.x = paramArrayOfint;
  }
  
  public boolean isZero() {
    return Nat192.isZero(this.x);
  }
  
  public boolean isOne() {
    return Nat192.isOne(this.x);
  }
  
  public boolean testBitZero() {
    return (Nat192.getBit(this.x, 0) == 1);
  }
  
  public BigInteger toBigInteger() {
    return Nat192.toBigInteger(this.x);
  }
  
  public String getFieldName() {
    return "SecP192K1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.add(this.x, ((SecP192K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.addOne(this.x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.subtract(this.x, ((SecP192K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.multiply(this.x, ((SecP192K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    Mod.invert(SecP192K1Field.P, ((SecP192K1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP192K1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.negate(this.x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat192.create();
    SecP192K1Field.square(this.x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat192.create();
    Mod.invert(SecP192K1Field.P, this.x, arrayOfInt);
    return new SecP192K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat192.isZero(arrayOfInt1) || Nat192.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat192.create();
    SecP192K1Field.square(arrayOfInt1, arrayOfInt2);
    SecP192K1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat192.create();
    SecP192K1Field.square(arrayOfInt2, arrayOfInt3);
    SecP192K1Field.multiply(arrayOfInt3, arrayOfInt1, arrayOfInt3);
    int[] arrayOfInt4 = Nat192.create();
    SecP192K1Field.squareN(arrayOfInt3, 3, arrayOfInt4);
    SecP192K1Field.multiply(arrayOfInt4, arrayOfInt3, arrayOfInt4);
    int[] arrayOfInt5 = arrayOfInt4;
    SecP192K1Field.squareN(arrayOfInt4, 2, arrayOfInt5);
    SecP192K1Field.multiply(arrayOfInt5, arrayOfInt2, arrayOfInt5);
    int[] arrayOfInt6 = arrayOfInt2;
    SecP192K1Field.squareN(arrayOfInt5, 8, arrayOfInt6);
    SecP192K1Field.multiply(arrayOfInt6, arrayOfInt5, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt5;
    SecP192K1Field.squareN(arrayOfInt6, 3, arrayOfInt7);
    SecP192K1Field.multiply(arrayOfInt7, arrayOfInt3, arrayOfInt7);
    int[] arrayOfInt8 = Nat192.create();
    SecP192K1Field.squareN(arrayOfInt7, 16, arrayOfInt8);
    SecP192K1Field.multiply(arrayOfInt8, arrayOfInt6, arrayOfInt8);
    int[] arrayOfInt9 = arrayOfInt6;
    SecP192K1Field.squareN(arrayOfInt8, 35, arrayOfInt9);
    SecP192K1Field.multiply(arrayOfInt9, arrayOfInt8, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt8;
    SecP192K1Field.squareN(arrayOfInt9, 70, arrayOfInt10);
    SecP192K1Field.multiply(arrayOfInt10, arrayOfInt9, arrayOfInt10);
    int[] arrayOfInt11 = arrayOfInt9;
    SecP192K1Field.squareN(arrayOfInt10, 19, arrayOfInt11);
    SecP192K1Field.multiply(arrayOfInt11, arrayOfInt7, arrayOfInt11);
    int[] arrayOfInt12 = arrayOfInt11;
    SecP192K1Field.squareN(arrayOfInt12, 20, arrayOfInt12);
    SecP192K1Field.multiply(arrayOfInt12, arrayOfInt7, arrayOfInt12);
    SecP192K1Field.squareN(arrayOfInt12, 4, arrayOfInt12);
    SecP192K1Field.multiply(arrayOfInt12, arrayOfInt3, arrayOfInt12);
    SecP192K1Field.squareN(arrayOfInt12, 6, arrayOfInt12);
    SecP192K1Field.multiply(arrayOfInt12, arrayOfInt3, arrayOfInt12);
    SecP192K1Field.square(arrayOfInt12, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt3;
    SecP192K1Field.square(arrayOfInt12, arrayOfInt13);
    return Nat192.eq(arrayOfInt1, arrayOfInt13) ? new SecP192K1FieldElement(arrayOfInt12) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP192K1FieldElement))
      return false; 
    SecP192K1FieldElement secP192K1FieldElement = (SecP192K1FieldElement)paramObject;
    return Nat192.eq(this.x, secP192K1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 6);
  }
}
