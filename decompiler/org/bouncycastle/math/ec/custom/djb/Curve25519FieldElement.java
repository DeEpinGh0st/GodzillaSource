package org.bouncycastle.math.ec.custom.djb;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class Curve25519FieldElement extends ECFieldElement {
  public static final BigInteger Q = Curve25519.q;
  
  private static final int[] PRECOMP_POW2 = new int[] { 1242472624, -991028441, -1389370248, 792926214, 1039914919, 726466713, 1338105611, 730014848 };
  
  protected int[] x;
  
  public Curve25519FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for Curve25519FieldElement"); 
    this.x = Curve25519Field.fromBigInteger(paramBigInteger);
  }
  
  public Curve25519FieldElement() {
    this.x = Nat256.create();
  }
  
  protected Curve25519FieldElement(int[] paramArrayOfint) {
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
    return "Curve25519Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.add(this.x, ((Curve25519FieldElement)paramECFieldElement).x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.addOne(this.x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.subtract(this.x, ((Curve25519FieldElement)paramECFieldElement).x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.multiply(this.x, ((Curve25519FieldElement)paramECFieldElement).x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(Curve25519Field.P, ((Curve25519FieldElement)paramECFieldElement).x, arrayOfInt);
    Curve25519Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.negate(this.x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat256.create();
    Curve25519Field.square(this.x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat256.create();
    Mod.invert(Curve25519Field.P, this.x, arrayOfInt);
    return new Curve25519FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat256.isZero(arrayOfInt1) || Nat256.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat256.create();
    Curve25519Field.square(arrayOfInt1, arrayOfInt2);
    Curve25519Field.multiply(arrayOfInt2, arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = arrayOfInt2;
    Curve25519Field.square(arrayOfInt2, arrayOfInt3);
    Curve25519Field.multiply(arrayOfInt3, arrayOfInt1, arrayOfInt3);
    int[] arrayOfInt4 = Nat256.create();
    Curve25519Field.square(arrayOfInt3, arrayOfInt4);
    Curve25519Field.multiply(arrayOfInt4, arrayOfInt1, arrayOfInt4);
    int[] arrayOfInt5 = Nat256.create();
    Curve25519Field.squareN(arrayOfInt4, 3, arrayOfInt5);
    Curve25519Field.multiply(arrayOfInt5, arrayOfInt3, arrayOfInt5);
    int[] arrayOfInt6 = arrayOfInt3;
    Curve25519Field.squareN(arrayOfInt5, 4, arrayOfInt6);
    Curve25519Field.multiply(arrayOfInt6, arrayOfInt4, arrayOfInt6);
    int[] arrayOfInt7 = arrayOfInt5;
    Curve25519Field.squareN(arrayOfInt6, 4, arrayOfInt7);
    Curve25519Field.multiply(arrayOfInt7, arrayOfInt4, arrayOfInt7);
    int[] arrayOfInt8 = arrayOfInt4;
    Curve25519Field.squareN(arrayOfInt7, 15, arrayOfInt8);
    Curve25519Field.multiply(arrayOfInt8, arrayOfInt7, arrayOfInt8);
    int[] arrayOfInt9 = arrayOfInt7;
    Curve25519Field.squareN(arrayOfInt8, 30, arrayOfInt9);
    Curve25519Field.multiply(arrayOfInt9, arrayOfInt8, arrayOfInt9);
    int[] arrayOfInt10 = arrayOfInt8;
    Curve25519Field.squareN(arrayOfInt9, 60, arrayOfInt10);
    Curve25519Field.multiply(arrayOfInt10, arrayOfInt9, arrayOfInt10);
    int[] arrayOfInt11 = arrayOfInt9;
    Curve25519Field.squareN(arrayOfInt10, 11, arrayOfInt11);
    Curve25519Field.multiply(arrayOfInt11, arrayOfInt6, arrayOfInt11);
    int[] arrayOfInt12 = arrayOfInt6;
    Curve25519Field.squareN(arrayOfInt11, 120, arrayOfInt12);
    Curve25519Field.multiply(arrayOfInt12, arrayOfInt10, arrayOfInt12);
    int[] arrayOfInt13 = arrayOfInt12;
    Curve25519Field.square(arrayOfInt13, arrayOfInt13);
    int[] arrayOfInt14 = arrayOfInt10;
    Curve25519Field.square(arrayOfInt13, arrayOfInt14);
    if (Nat256.eq(arrayOfInt1, arrayOfInt14))
      return new Curve25519FieldElement(arrayOfInt13); 
    Curve25519Field.multiply(arrayOfInt13, PRECOMP_POW2, arrayOfInt13);
    Curve25519Field.square(arrayOfInt13, arrayOfInt14);
    return Nat256.eq(arrayOfInt1, arrayOfInt14) ? new Curve25519FieldElement(arrayOfInt13) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Curve25519FieldElement))
      return false; 
    Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement)paramObject;
    return Nat256.eq(this.x, curve25519FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
  }
}
