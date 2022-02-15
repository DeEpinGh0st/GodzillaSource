package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

public class SecP192R1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP192R1Curve.q;
  
  protected int[] x;
  
  public SecP192R1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP192R1FieldElement"); 
    this.x = SecP192R1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP192R1FieldElement() {
    this.x = Nat192.create();
  }
  
  protected SecP192R1FieldElement(int[] paramArrayOfint) {
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
    return "SecP192R1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.add(this.x, ((SecP192R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.addOne(this.x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.subtract(this.x, ((SecP192R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.multiply(this.x, ((SecP192R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat192.create();
    Mod.invert(SecP192R1Field.P, ((SecP192R1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP192R1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.negate(this.x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat192.create();
    SecP192R1Field.square(this.x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat192.create();
    Mod.invert(SecP192R1Field.P, this.x, arrayOfInt);
    return new SecP192R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat192.isZero(arrayOfInt1) || Nat192.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat192.create();
    int[] arrayOfInt3 = Nat192.create();
    SecP192R1Field.square(arrayOfInt1, arrayOfInt2);
    SecP192R1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    SecP192R1Field.squareN(arrayOfInt2, 2, arrayOfInt3);
    SecP192R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    SecP192R1Field.squareN(arrayOfInt3, 4, arrayOfInt2);
    SecP192R1Field.multiply(arrayOfInt2, arrayOfInt3, arrayOfInt2);
    SecP192R1Field.squareN(arrayOfInt2, 8, arrayOfInt3);
    SecP192R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    SecP192R1Field.squareN(arrayOfInt3, 16, arrayOfInt2);
    SecP192R1Field.multiply(arrayOfInt2, arrayOfInt3, arrayOfInt2);
    SecP192R1Field.squareN(arrayOfInt2, 32, arrayOfInt3);
    SecP192R1Field.multiply(arrayOfInt3, arrayOfInt2, arrayOfInt3);
    SecP192R1Field.squareN(arrayOfInt3, 64, arrayOfInt2);
    SecP192R1Field.multiply(arrayOfInt2, arrayOfInt3, arrayOfInt2);
    SecP192R1Field.squareN(arrayOfInt2, 62, arrayOfInt2);
    SecP192R1Field.square(arrayOfInt2, arrayOfInt3);
    return Nat192.eq(arrayOfInt1, arrayOfInt3) ? new SecP192R1FieldElement(arrayOfInt2) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP192R1FieldElement))
      return false; 
    SecP192R1FieldElement secP192R1FieldElement = (SecP192R1FieldElement)paramObject;
    return Nat192.eq(this.x, secP192R1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 6);
  }
}
