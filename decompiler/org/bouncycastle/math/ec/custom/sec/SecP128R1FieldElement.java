package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.Arrays;

public class SecP128R1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP128R1Curve.q;
  
  protected int[] x;
  
  public SecP128R1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP128R1FieldElement"); 
    this.x = SecP128R1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP128R1FieldElement() {
    this.x = Nat128.create();
  }
  
  protected SecP128R1FieldElement(int[] paramArrayOfint) {
    this.x = paramArrayOfint;
  }
  
  public boolean isZero() {
    return Nat128.isZero(this.x);
  }
  
  public boolean isOne() {
    return Nat128.isOne(this.x);
  }
  
  public boolean testBitZero() {
    return (Nat128.getBit(this.x, 0) == 1);
  }
  
  public BigInteger toBigInteger() {
    return Nat128.toBigInteger(this.x);
  }
  
  public String getFieldName() {
    return "SecP128R1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.add(this.x, ((SecP128R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.addOne(this.x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.subtract(this.x, ((SecP128R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.multiply(this.x, ((SecP128R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat128.create();
    Mod.invert(SecP128R1Field.P, ((SecP128R1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP128R1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.negate(this.x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat128.create();
    SecP128R1Field.square(this.x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat128.create();
    Mod.invert(SecP128R1Field.P, this.x, arrayOfInt);
    return new SecP128R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat128.isZero(arrayOfInt1) || Nat128.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat128.create();
    SecP128R1Field.square(arrayOfInt1, arrayOfInt2);
    SecP128R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Nat128.create();
    SecP128R1Field.squareN(arrayOfInt2, 2, arrayOfInt3);
    SecP128R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    int[] arrayOfInt4 = Nat128.create();
    SecP128R1Field.squareN(arrayOfInt3, 4, arrayOfInt4);
    SecP128R1Field.multiply(arrayOfInt4, arrayOfInt3, arrayOfInt4);
    int[] arrayOfInt5 = arrayOfInt3;
    SecP128R1Field.squareN(arrayOfInt4, 2, arrayOfInt5);
    SecP128R1Field.multiply(arrayOfInt5, arrayOfInt2, arrayOfInt5);
    int[] arrayOfInt6 = arrayOfInt2;
    SecP128R1Field.squareN(arrayOfInt5, 10, arrayOfInt6);
    SecP128R1Field.multiply(arrayOfInt6, arrayOfInt5, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt4;
    SecP128R1Field.squareN(arrayOfInt6, 10, arrayOfInt7);
    SecP128R1Field.multiply(arrayOfInt7, arrayOfInt5, arrayOfInt7);
    int[] arrayOfInt8 = arrayOfInt5;
    SecP128R1Field.square(arrayOfInt7, arrayOfInt8);
    SecP128R1Field.multiply(arrayOfInt8, arrayOfInt1, arrayOfInt8);
    int[] arrayOfInt9 = arrayOfInt8;
    SecP128R1Field.squareN(arrayOfInt9, 95, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt7;
    SecP128R1Field.square(arrayOfInt9, arrayOfInt10);
    return Nat128.eq(arrayOfInt1, arrayOfInt10) ? new SecP128R1FieldElement(arrayOfInt9) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP128R1FieldElement))
      return false; 
    SecP128R1FieldElement secP128R1FieldElement = (SecP128R1FieldElement)paramObject;
    return Nat128.eq(this.x, secP128R1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 4);
  }
}
