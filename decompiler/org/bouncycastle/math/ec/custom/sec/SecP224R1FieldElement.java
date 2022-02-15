package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Arrays;

public class SecP224R1FieldElement extends ECFieldElement {
  public static final BigInteger Q = SecP224R1Curve.q;
  
  protected int[] x;
  
  public SecP224R1FieldElement(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0 || paramBigInteger.compareTo(Q) >= 0)
      throw new IllegalArgumentException("x value invalid for SecP224R1FieldElement"); 
    this.x = SecP224R1Field.fromBigInteger(paramBigInteger);
  }
  
  public SecP224R1FieldElement() {
    this.x = Nat224.create();
  }
  
  protected SecP224R1FieldElement(int[] paramArrayOfint) {
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
    return "SecP224R1Field";
  }
  
  public int getFieldSize() {
    return Q.bitLength();
  }
  
  public ECFieldElement add(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.add(this.x, ((SecP224R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement addOne() {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.addOne(this.x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement subtract(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.subtract(this.x, ((SecP224R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement multiply(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.multiply(this.x, ((SecP224R1FieldElement)paramECFieldElement).x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement divide(ECFieldElement paramECFieldElement) {
    int[] arrayOfInt = Nat224.create();
    Mod.invert(SecP224R1Field.P, ((SecP224R1FieldElement)paramECFieldElement).x, arrayOfInt);
    SecP224R1Field.multiply(arrayOfInt, this.x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement negate() {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.negate(this.x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement square() {
    int[] arrayOfInt = Nat224.create();
    SecP224R1Field.square(this.x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement invert() {
    int[] arrayOfInt = Nat224.create();
    Mod.invert(SecP224R1Field.P, this.x, arrayOfInt);
    return new SecP224R1FieldElement(arrayOfInt);
  }
  
  public ECFieldElement sqrt() {
    int[] arrayOfInt1 = this.x;
    if (Nat224.isZero(arrayOfInt1) || Nat224.isOne(arrayOfInt1))
      return this; 
    int[] arrayOfInt2 = Nat224.create();
    SecP224R1Field.negate(arrayOfInt1, arrayOfInt2);
    int[] arrayOfInt3 = Mod.random(SecP224R1Field.P);
    int[] arrayOfInt4 = Nat224.create();
    if (!isSquare(arrayOfInt1))
      return null; 
    while (!trySqrt(arrayOfInt2, arrayOfInt3, arrayOfInt4))
      SecP224R1Field.addOne(arrayOfInt3, arrayOfInt3); 
    SecP224R1Field.square(arrayOfInt4, arrayOfInt3);
    return Nat224.eq(arrayOfInt1, arrayOfInt3) ? new SecP224R1FieldElement(arrayOfInt4) : null;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SecP224R1FieldElement))
      return false; 
    SecP224R1FieldElement secP224R1FieldElement = (SecP224R1FieldElement)paramObject;
    return Nat224.eq(this.x, secP224R1FieldElement.x);
  }
  
  public int hashCode() {
    return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
  }
  
  private static boolean isSquare(int[] paramArrayOfint) {
    int[] arrayOfInt1 = Nat224.create();
    int[] arrayOfInt2 = Nat224.create();
    Nat224.copy(paramArrayOfint, arrayOfInt1);
    for (byte b = 0; b < 7; b++) {
      Nat224.copy(arrayOfInt1, arrayOfInt2);
      SecP224R1Field.squareN(arrayOfInt1, 1 << b, arrayOfInt1);
      SecP224R1Field.multiply(arrayOfInt1, arrayOfInt2, arrayOfInt1);
    } 
    SecP224R1Field.squareN(arrayOfInt1, 95, arrayOfInt1);
    return Nat224.isOne(arrayOfInt1);
  }
  
  private static void RM(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4, int[] paramArrayOfint5, int[] paramArrayOfint6, int[] paramArrayOfint7) {
    SecP224R1Field.multiply(paramArrayOfint5, paramArrayOfint3, paramArrayOfint7);
    SecP224R1Field.multiply(paramArrayOfint7, paramArrayOfint1, paramArrayOfint7);
    SecP224R1Field.multiply(paramArrayOfint4, paramArrayOfint2, paramArrayOfint6);
    SecP224R1Field.add(paramArrayOfint6, paramArrayOfint7, paramArrayOfint6);
    SecP224R1Field.multiply(paramArrayOfint4, paramArrayOfint3, paramArrayOfint7);
    Nat224.copy(paramArrayOfint6, paramArrayOfint4);
    SecP224R1Field.multiply(paramArrayOfint5, paramArrayOfint2, paramArrayOfint5);
    SecP224R1Field.add(paramArrayOfint5, paramArrayOfint7, paramArrayOfint5);
    SecP224R1Field.square(paramArrayOfint5, paramArrayOfint6);
    SecP224R1Field.multiply(paramArrayOfint6, paramArrayOfint1, paramArrayOfint6);
  }
  
  private static void RP(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4, int[] paramArrayOfint5) {
    Nat224.copy(paramArrayOfint1, paramArrayOfint4);
    int[] arrayOfInt1 = Nat224.create();
    int[] arrayOfInt2 = Nat224.create();
    for (byte b = 0; b < 7; b++) {
      Nat224.copy(paramArrayOfint2, arrayOfInt1);
      Nat224.copy(paramArrayOfint3, arrayOfInt2);
      int i = 1 << b;
      while (--i >= 0)
        RS(paramArrayOfint2, paramArrayOfint3, paramArrayOfint4, paramArrayOfint5); 
      RM(paramArrayOfint1, arrayOfInt1, arrayOfInt2, paramArrayOfint2, paramArrayOfint3, paramArrayOfint4, paramArrayOfint5);
    } 
  }
  
  private static void RS(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4) {
    SecP224R1Field.multiply(paramArrayOfint2, paramArrayOfint1, paramArrayOfint2);
    SecP224R1Field.twice(paramArrayOfint2, paramArrayOfint2);
    SecP224R1Field.square(paramArrayOfint1, paramArrayOfint4);
    SecP224R1Field.add(paramArrayOfint3, paramArrayOfint4, paramArrayOfint1);
    SecP224R1Field.multiply(paramArrayOfint3, paramArrayOfint4, paramArrayOfint3);
    int i = Nat.shiftUpBits(7, paramArrayOfint3, 2, 0);
    SecP224R1Field.reduce32(i, paramArrayOfint3);
  }
  
  private static boolean trySqrt(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt1 = Nat224.create();
    Nat224.copy(paramArrayOfint2, arrayOfInt1);
    int[] arrayOfInt2 = Nat224.create();
    arrayOfInt2[0] = 1;
    int[] arrayOfInt3 = Nat224.create();
    RP(paramArrayOfint1, arrayOfInt1, arrayOfInt2, arrayOfInt3, paramArrayOfint3);
    int[] arrayOfInt4 = Nat224.create();
    int[] arrayOfInt5 = Nat224.create();
    for (byte b = 1; b < 96; b++) {
      Nat224.copy(arrayOfInt1, arrayOfInt4);
      Nat224.copy(arrayOfInt2, arrayOfInt5);
      RS(arrayOfInt1, arrayOfInt2, arrayOfInt3, paramArrayOfint3);
      if (Nat224.isZero(arrayOfInt1)) {
        Mod.invert(SecP224R1Field.P, arrayOfInt5, paramArrayOfint3);
        SecP224R1Field.multiply(paramArrayOfint3, arrayOfInt4, paramArrayOfint3);
        return true;
      } 
    } 
    return false;
  }
}
