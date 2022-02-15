package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class SecP256R1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP256R1Curve.q;
  
  protected int[] x;
  
  public SecP256R1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement"); 
    this.x = SecP256R1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP256R1FieldElement() {
    this.x = Nat256.create();
  }
  
  protected SecP256R1FieldElement(int[] paramArrayOfint) {
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
    return "SecP256R1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.add(this.x, ((SecP256R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.addOne(this.x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.subtract(this.x, ((SecP256R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.multiply(this.x, ((SecP256R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(SecP256R1Field.P, ((SecP256R1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP256R1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.negate(this.x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat256.create();
    SecP256R1Field.square(this.x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(SecP256R1Field.P, this.x, arrayOfInt);
    return new SecP256R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat256.isZero(arrayOfInt1) || Nat256.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat256.create();
    int[] arrayOfInt3 = Nat256.create();
    SecP256R1Field.square(arrayOfInt1, arrayOfInt2);
    SecP256R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    SecP256R1Field.squareN(arrayOfInt2, 2, arrayOfInt3);
    SecP256R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    SecP256R1Field.squareN(arrayOfInt3, 4, arrayOfInt2);
    SecP256R1Field.multiply(arrayOfInt2, arrayOfInt3, arrayOfInt2);
    SecP256R1Field.squareN(arrayOfInt2, 8, arrayOfInt3);
    SecP256R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    SecP256R1Field.squareN(arrayOfInt3, 16, arrayOfInt2);
    SecP256R1Field.multiply(arrayOfInt2, arrayOfInt3, arrayOfInt2);
    SecP256R1Field.squareN(arrayOfInt2, 32, arrayOfInt2);
    SecP256R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    SecP256R1Field.squareN(arrayOfInt2, 96, arrayOfInt2);
    SecP256R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    SecP256R1Field.squareN(arrayOfInt2, 94, arrayOfInt2);
    SecP256R1Field.square(arrayOfInt2, arrayOfInt3);
    return Nat256.eq(arrayOfInt1, arrayOfInt3) ? new SecP256R1FieldElement(arrayOfInt2) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP256R1FieldElement))
      return false; 
    SecP256R1FieldElement secP256R1FieldElement = (SecP256R1FieldElement)paramObject;
    return Nat256.eq(this.x, secP256R1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
  }
}
