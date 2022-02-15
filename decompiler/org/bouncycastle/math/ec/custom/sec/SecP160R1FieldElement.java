package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;

public class SecP160R1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP160R1Curve.q;
  
  protected int[] x;
  
  public SecP160R1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP160R1FieldElement"); 
    this.x = SecP160R1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP160R1FieldElement() {
    this.x = Nat160.create();
  }
  
  protected SecP160R1FieldElement(int[] paramArrayOfint) {
    this.x = paramArrayOfint;
  }
  
  public boolean isZero() {
    return Nat160.isZero(this.x);
  }
  
  public boolean isOne() {
    return Nat160.isOne(this.x);
  }
  
  public boolean testBitZero() {
    return (Nat160.getBit(this.x, 0) == 1);
  }
  
  public BigInteger toBigInteger() {
    return Nat160.toBigInteger(this.x);
  }
  
  public String getFieldName() {
    return "SecP160R1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.add(this.x, ((SecP160R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.addOne(this.x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.subtract(this.x, ((SecP160R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.multiply(this.x, ((SecP160R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat160.create();
    Mod.invert(SecP160R1Field.P, ((SecP160R1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP160R1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.negate(this.x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat160.create();
    SecP160R1Field.square(this.x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat160.create();
    Mod.invert(SecP160R1Field.P, this.x, arrayOfInt);
    return new SecP160R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat160.isZero(arrayOfInt1) || Nat160.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat160.create();
    SecP160R1Field.square(arrayOfInt1, arrayOfInt2);
    SecP160R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat160.create();
    SecP160R1Field.squareN(arrayOfInt2, 2, arrayOfInt3);
    SecP160R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    int[] arrayOfInt4 = arrayOfInt2;
    SecP160R1Field.squareN(arrayOfInt3, 4, arrayOfInt4);
    SecP160R1Field.multiply(arrayOfInt4, arrayOfInt3, arrayOfInt4);
    int[] arrayOfInt5 = arrayOfInt3;
    SecP160R1Field.squareN(arrayOfInt4, 8, arrayOfInt5);
    SecP160R1Field.multiply(arrayOfInt5, arrayOfInt4, arrayOfInt5);
    int[] arrayOfInt6 = arrayOfInt4;
    SecP160R1Field.squareN(arrayOfInt5, 16, arrayOfInt6);
    SecP160R1Field.multiply(arrayOfInt6, arrayOfInt5, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt5;
    SecP160R1Field.squareN(arrayOfInt6, 32, arrayOfInt7);
    SecP160R1Field.multiply(arrayOfInt7, arrayOfInt6, arrayOfInt7);
    int[] arrayOfInt8 = arrayOfInt6;
    SecP160R1Field.squareN(arrayOfInt7, 64, arrayOfInt8);
    SecP160R1Field.multiply(arrayOfInt8, arrayOfInt7, arrayOfInt8);
    int[] arrayOfInt9 = arrayOfInt7;
    SecP160R1Field.square(arrayOfInt8, arrayOfInt9);
    SecP160R1Field.multiply(arrayOfInt9, arrayOfInt1, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt9;
    SecP160R1Field.squareN(arrayOfInt10, 29, arrayOfInt10);
    int[] arrayOfInt11 = arrayOfInt8;
    SecP160R1Field.square(arrayOfInt10, arrayOfInt11);
    return Nat160.eq(arrayOfInt1, arrayOfInt11) ? new SecP160R1FieldElement(arrayOfInt10) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP160R1FieldElement))
      return false; 
    SecP160R1FieldElement secP160R1FieldElement = (SecP160R1FieldElement)paramObject;
    return Nat160.eq(this.x, secP160R1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
  }
}
