package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class SecP256K1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP256K1Curve.q;
  
  protected int[] x;
  
  public SecP256K1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement"); 
    this.x = SecP256K1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP256K1FieldElement() {
    this.x = Nat256.create();
  }
  
  protected SecP256K1FieldElement(int[] paramArrayOfint) {
    this.x = paramArrayOfint;
  }
  
  public boolean isZero() {
    return Nat256.isZero(this.x);
  }
  
  public boolean isOne() {
    return Nat256.isOne(this.x);
  }
  
  public boolean testBitZero() {
    return (Nat256.getBit(this.x, 0) == 1);
  }
  
  public BigInteger toBigInteger() {
    return Nat256.toBigInteger(this.x);
  }
  
  public String getFieldName() {
    return "SecP256K1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.add(this.x, ((SecP256K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.addOne(this.x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.subtract(this.x, ((SecP256K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.multiply(this.x, ((SecP256K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(SecP256K1Field.P, ((SecP256K1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP256K1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.negate(this.x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat256.create();
    SecP256K1Field.square(this.x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(SecP256K1Field.P, this.x, arrayOfInt);
    return new SecP256K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat256.isZero(arrayOfInt1) || Nat256.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat256.create();
    SecP256K1Field.square(arrayOfInt1, arrayOfInt2);
    SecP256K1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat256.create();
    SecP256K1Field.square(arrayOfInt2, arrayOfInt3);
    SecP256K1Field.multiply(arrayOfInt3, arrayOfInt1, arrayOfInt3);
    int[] arrayOfInt4 = Nat256.create();
    SecP256K1Field.squareN(arrayOfInt3, 3, arrayOfInt4);
    SecP256K1Field.multiply(arrayOfInt4, arrayOfInt3, arrayOfInt4);
    int[] arrayOfInt5 = arrayOfInt4;
    SecP256K1Field.squareN(arrayOfInt4, 3, arrayOfInt5);
    SecP256K1Field.multiply(arrayOfInt5, arrayOfInt3, arrayOfInt5);
    int[] arrayOfInt6 = arrayOfInt5;
    SecP256K1Field.squareN(arrayOfInt5, 2, arrayOfInt6);
    SecP256K1Field.multiply(arrayOfInt6, arrayOfInt2, arrayOfInt6);
    int[] arrayOfInt7 = Nat256.create();
    SecP256K1Field.squareN(arrayOfInt6, 11, arrayOfInt7);
    SecP256K1Field.multiply(arrayOfInt7, arrayOfInt6, arrayOfInt7);
    int[] arrayOfInt8 = arrayOfInt6;
    SecP256K1Field.squareN(arrayOfInt7, 22, arrayOfInt8);
    SecP256K1Field.multiply(arrayOfInt8, arrayOfInt7, arrayOfInt8);
    int[] arrayOfInt9 = Nat256.create();
    SecP256K1Field.squareN(arrayOfInt8, 44, arrayOfInt9);
    SecP256K1Field.multiply(arrayOfInt9, arrayOfInt8, arrayOfInt9);
    int[] arrayOfInt10 = Nat256.create();
    SecP256K1Field.squareN(arrayOfInt9, 88, arrayOfInt10);
    SecP256K1Field.multiply(arrayOfInt10, arrayOfInt9, arrayOfInt10);
    int[] arrayOfInt11 = arrayOfInt9;
    SecP256K1Field.squareN(arrayOfInt10, 44, arrayOfInt11);
    SecP256K1Field.multiply(arrayOfInt11, arrayOfInt8, arrayOfInt11);
    int[] arrayOfInt12 = arrayOfInt8;
    SecP256K1Field.squareN(arrayOfInt11, 3, arrayOfInt12);
    SecP256K1Field.multiply(arrayOfInt12, arrayOfInt3, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt12;
    SecP256K1Field.squareN(arrayOfInt13, 23, arrayOfInt13);
    SecP256K1Field.multiply(arrayOfInt13, arrayOfInt7, arrayOfInt13);
    SecP256K1Field.squareN(arrayOfInt13, 6, arrayOfInt13);
    SecP256K1Field.multiply(arrayOfInt13, arrayOfInt2, arrayOfInt13);
    SecP256K1Field.squareN(arrayOfInt13, 2, arrayOfInt13);
    int[] arrayOfInt14 = arrayOfInt2;
    SecP256K1Field.square(arrayOfInt13, arrayOfInt14);
    return Nat256.eq(arrayOfInt1, arrayOfInt14) ? new SecP256K1FieldElement(arrayOfInt13) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP256K1FieldElement))
      return false; 
    SecP256K1FieldElement secP256K1FieldElement = (SecP256K1FieldElement)paramObject;
    return Nat256.eq(this.x, secP256K1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
  }
}
