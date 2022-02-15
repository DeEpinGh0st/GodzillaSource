package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Arrays;

public class SecP224K1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP224K1Curve.q;
  
  private static final int[] PRECOMP_POW2 = new int[] { 868209154, -587542221, 579297866, -1014948952, -1470801668, 514782679, -1897982644 };
  
  protected int[] x;
  
  public SecP224K1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP224K1FieldElement"); 
    this.x = SecP224K1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP224K1FieldElement() {
    this.x = Nat224.create();
  }
  
  protected SecP224K1FieldElement(int[] paramArrayOfint) {
    this.x = paramArrayOfint;
  }
  
  public boolean isZero() {
    return Nat224.isZero(this.x);
  }
  
  public boolean isOne() {
    return Nat224.isOne(this.x);
  }
  
  public boolean testBitZero() {
    return (Nat224.getBit(this.x, 0) == 1);
  }
  
  public BigInteger toBigInteger() {
    return Nat224.toBigInteger(this.x);
  }
  
  public String getFieldName() {
    return "SecP224K1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.add(this.x, ((SecP224K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.addOne(this.x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.subtract(this.x, ((SecP224K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.multiply(this.x, ((SecP224K1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    Mod.invert(SecP224K1Field.P, ((SecP224K1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP224K1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.negate(this.x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat224.create();
    SecP224K1Field.square(this.x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat224.create();
    Mod.invert(SecP224K1Field.P, this.x, arrayOfInt);
    return new SecP224K1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat224.isZero(arrayOfInt1) || Nat224.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat224.create();
    SecP224K1Field.square(arrayOfInt1, arrayOfInt2);
    SecP224K1Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = arrayOfInt2;
    SecP224K1Field.square(arrayOfInt2, arrayOfInt3);
    SecP224K1Field.multiply(arrayOfInt3, arrayOfInt1, arrayOfInt3);
    int[] arrayOfInt4 = Nat224.create();
    SecP224K1Field.square(arrayOfInt3, arrayOfInt4);
    SecP224K1Field.multiply(arrayOfInt4, arrayOfInt1, arrayOfInt4);
    int[] arrayOfInt5 = Nat224.create();
    SecP224K1Field.squareN(arrayOfInt4, 4, arrayOfInt5);
    SecP224K1Field.multiply(arrayOfInt5, arrayOfInt4, arrayOfInt5);
    int[] arrayOfInt6 = Nat224.create();
    SecP224K1Field.squareN(arrayOfInt5, 3, arrayOfInt6);
    SecP224K1Field.multiply(arrayOfInt6, arrayOfInt3, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt6;
    SecP224K1Field.squareN(arrayOfInt6, 8, arrayOfInt7);
    SecP224K1Field.multiply(arrayOfInt7, arrayOfInt5, arrayOfInt7);
    int[] arrayOfInt8 = arrayOfInt5;
    SecP224K1Field.squareN(arrayOfInt7, 4, arrayOfInt8);
    SecP224K1Field.multiply(arrayOfInt8, arrayOfInt4, arrayOfInt8);
    int[] arrayOfInt9 = arrayOfInt4;
    SecP224K1Field.squareN(arrayOfInt8, 19, arrayOfInt9);
    SecP224K1Field.multiply(arrayOfInt9, arrayOfInt7, arrayOfInt9);
    int[] arrayOfInt10 = Nat224.create();
    SecP224K1Field.squareN(arrayOfInt9, 42, arrayOfInt10);
    SecP224K1Field.multiply(arrayOfInt10, arrayOfInt9, arrayOfInt10);
    int[] arrayOfInt11 = arrayOfInt9;
    SecP224K1Field.squareN(arrayOfInt10, 23, arrayOfInt11);
    SecP224K1Field.multiply(arrayOfInt11, arrayOfInt8, arrayOfInt11);
    int[] arrayOfInt12 = arrayOfInt8;
    SecP224K1Field.squareN(arrayOfInt11, 84, arrayOfInt12);
    SecP224K1Field.multiply(arrayOfInt12, arrayOfInt10, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt12;
    SecP224K1Field.squareN(arrayOfInt13, 20, arrayOfInt13);
    SecP224K1Field.multiply(arrayOfInt13, arrayOfInt7, arrayOfInt13);
    SecP224K1Field.squareN(arrayOfInt13, 3, arrayOfInt13);
    SecP224K1Field.multiply(arrayOfInt13, arrayOfInt1, arrayOfInt13);
    SecP224K1Field.squareN(arrayOfInt13, 2, arrayOfInt13);
    SecP224K1Field.multiply(arrayOfInt13, arrayOfInt1, arrayOfInt13);
    SecP224K1Field.squareN(arrayOfInt13, 4, arrayOfInt13);
    SecP224K1Field.multiply(arrayOfInt13, arrayOfInt3, arrayOfInt13);
    SecP224K1Field.square(arrayOfInt13, arrayOfInt13);
    int[] arrayOfInt14 = arrayOfInt10;
    SecP224K1Field.square(arrayOfInt13, arrayOfInt14);
    if (Nat224.eq(arrayOfInt1, arrayOfInt14))
      return new SecP224K1FieldElement(arrayOfInt13); 
    SecP224K1Field.multiply(arrayOfInt13, PRECOMP_POW2, arrayOfInt13);
    SecP224K1Field.square(arrayOfInt13, arrayOfInt14);
    return Nat224.eq(arrayOfInt1, arrayOfInt14) ? new SecP224K1FieldElement(arrayOfInt13) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP224K1FieldElement))
      return false; 
    SecP224K1FieldElement secP224K1FieldElement = (SecP224K1FieldElement)paramObject;
    return Nat224.eq(this.x, secP224K1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
  }
}
