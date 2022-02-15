package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class PolynomialGF2mSmallM {
  private GF2mField field;
  
  private int degree;
  
  private int[] coefficients;
  
  public static final char RANDOM_IRREDUCIBLE_POLYNOMIAL = 'I';
  
  public PolynomialGF2mSmallM(GF2mField paramGF2mField) {
    this.field = paramGF2mField;
    this.degree = -1;
    this.coefficients = new int[1];
  }
  
  public PolynomialGF2mSmallM(GF2mField paramGF2mField, int paramInt, char paramChar, SecureRandom paramSecureRandom) {
    this.field = paramGF2mField;
    switch (paramChar) {
      case 'I':
        this.coefficients = createRandomIrreduciblePolynomial(paramInt, paramSecureRandom);
        break;
      default:
        throw new IllegalArgumentException(" Error: type " + paramChar + " is not defined for GF2smallmPolynomial");
    } 
    computeDegree();
  }
  
  private int[] createRandomIrreduciblePolynomial(int paramInt, SecureRandom paramSecureRandom) {
    int[] arrayOfInt = new int[paramInt + 1];
    arrayOfInt[paramInt] = 1;
    arrayOfInt[0] = this.field.getRandomNonZeroElement(paramSecureRandom);
    int i;
    for (i = 1; i < paramInt; i++)
      arrayOfInt[i] = this.field.getRandomElement(paramSecureRandom); 
    while (!isIrreducible(arrayOfInt)) {
      i = RandUtils.nextInt(paramSecureRandom, paramInt);
      if (i == 0) {
        arrayOfInt[0] = this.field.getRandomNonZeroElement(paramSecureRandom);
        continue;
      } 
      arrayOfInt[i] = this.field.getRandomElement(paramSecureRandom);
    } 
    return arrayOfInt;
  }
  
  public PolynomialGF2mSmallM(GF2mField paramGF2mField, int paramInt) {
    this.field = paramGF2mField;
    this.degree = paramInt;
    this.coefficients = new int[paramInt + 1];
    this.coefficients[paramInt] = 1;
  }
  
  public PolynomialGF2mSmallM(GF2mField paramGF2mField, int[] paramArrayOfint) {
    this.field = paramGF2mField;
    this.coefficients = normalForm(paramArrayOfint);
    computeDegree();
  }
  
  public PolynomialGF2mSmallM(GF2mField paramGF2mField, byte[] paramArrayOfbyte) {
    this.field = paramGF2mField;
    byte b1 = 8;
    byte b2 = 1;
    while (paramGF2mField.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    if (paramArrayOfbyte.length % b2 != 0)
      throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m"); 
    this.coefficients = new int[paramArrayOfbyte.length / b2];
    b2 = 0;
    for (byte b3 = 0; b3 < this.coefficients.length; b3++) {
      for (byte b = 0; b < b1; b += 8)
        this.coefficients[b3] = this.coefficients[b3] ^ (paramArrayOfbyte[b2++] & 0xFF) << b; 
      if (!this.field.isElementOfThisField(this.coefficients[b3]))
        throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m"); 
    } 
    if (this.coefficients.length != 1 && this.coefficients[this.coefficients.length - 1] == 0)
      throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m"); 
    computeDegree();
  }
  
  public PolynomialGF2mSmallM(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    this.field = paramPolynomialGF2mSmallM.field;
    this.degree = paramPolynomialGF2mSmallM.degree;
    this.coefficients = IntUtils.clone(paramPolynomialGF2mSmallM.coefficients);
  }
  
  public PolynomialGF2mSmallM(GF2mVector paramGF2mVector) {
    this(paramGF2mVector.getField(), paramGF2mVector.getIntArrayForm());
  }
  
  public int getDegree() {
    int i = this.coefficients.length - 1;
    return (this.coefficients[i] == 0) ? -1 : i;
  }
  
  public int getHeadCoefficient() {
    return (this.degree == -1) ? 0 : this.coefficients[this.degree];
  }
  
  private static int headCoefficient(int[] paramArrayOfint) {
    int i = computeDegree(paramArrayOfint);
    return (i == -1) ? 0 : paramArrayOfint[i];
  }
  
  public int getCoefficient(int paramInt) {
    return (paramInt < 0 || paramInt > this.degree) ? 0 : this.coefficients[paramInt];
  }
  
  public byte[] getEncoded() {
    byte b1 = 8;
    byte b2 = 1;
    while (this.field.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    byte[] arrayOfByte = new byte[this.coefficients.length * b2];
    b2 = 0;
    for (byte b3 = 0; b3 < this.coefficients.length; b3++) {
      for (byte b = 0; b < b1; b += 8)
        arrayOfByte[b2++] = (byte)(this.coefficients[b3] >>> b); 
    } 
    return arrayOfByte;
  }
  
  public int evaluateAt(int paramInt) {
    int i = this.coefficients[this.degree];
    for (int j = this.degree - 1; j >= 0; j--)
      i = this.field.mult(i, paramInt) ^ this.coefficients[j]; 
    return i;
  }
  
  public PolynomialGF2mSmallM add(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt = add(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  public void addToThis(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    this.coefficients = add(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    computeDegree();
  }
  
  private int[] add(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    if (paramArrayOfint1.length < paramArrayOfint2.length) {
      arrayOfInt1 = new int[paramArrayOfint2.length];
      System.arraycopy(paramArrayOfint2, 0, arrayOfInt1, 0, paramArrayOfint2.length);
      arrayOfInt2 = paramArrayOfint1;
    } else {
      arrayOfInt1 = new int[paramArrayOfint1.length];
      System.arraycopy(paramArrayOfint1, 0, arrayOfInt1, 0, paramArrayOfint1.length);
      arrayOfInt2 = paramArrayOfint2;
    } 
    for (int i = arrayOfInt2.length - 1; i >= 0; i--)
      arrayOfInt1[i] = this.field.add(arrayOfInt1[i], arrayOfInt2[i]); 
    return arrayOfInt1;
  }
  
  public PolynomialGF2mSmallM addMonomial(int paramInt) {
    int[] arrayOfInt1 = new int[paramInt + 1];
    arrayOfInt1[paramInt] = 1;
    int[] arrayOfInt2 = add(this.coefficients, arrayOfInt1);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt2);
  }
  
  public PolynomialGF2mSmallM multWithElement(int paramInt) {
    if (!this.field.isElementOfThisField(paramInt))
      throw new ArithmeticException("Not an element of the finite field this polynomial is defined over."); 
    int[] arrayOfInt = multWithElement(this.coefficients, paramInt);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  public void multThisWithElement(int paramInt) {
    if (!this.field.isElementOfThisField(paramInt))
      throw new ArithmeticException("Not an element of the finite field this polynomial is defined over."); 
    this.coefficients = multWithElement(this.coefficients, paramInt);
    computeDegree();
  }
  
  private int[] multWithElement(int[] paramArrayOfint, int paramInt) {
    int i = computeDegree(paramArrayOfint);
    if (i == -1 || paramInt == 0)
      return new int[1]; 
    if (paramInt == 1)
      return IntUtils.clone(paramArrayOfint); 
    int[] arrayOfInt = new int[i + 1];
    for (int j = i; j >= 0; j--)
      arrayOfInt[j] = this.field.mult(paramArrayOfint[j], paramInt); 
    return arrayOfInt;
  }
  
  public PolynomialGF2mSmallM multWithMonomial(int paramInt) {
    int[] arrayOfInt = multWithMonomial(this.coefficients, paramInt);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  private static int[] multWithMonomial(int[] paramArrayOfint, int paramInt) {
    int i = computeDegree(paramArrayOfint);
    if (i == -1)
      return new int[1]; 
    int[] arrayOfInt = new int[i + paramInt + 1];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, paramInt, i + 1);
    return arrayOfInt;
  }
  
  public PolynomialGF2mSmallM[] div(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[][] arrayOfInt = div(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM[] { new PolynomialGF2mSmallM(this.field, arrayOfInt[0]), new PolynomialGF2mSmallM(this.field, arrayOfInt[1]) };
  }
  
  private int[][] div(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = computeDegree(paramArrayOfint2);
    int j = computeDegree(paramArrayOfint1) + 1;
    if (i == -1)
      throw new ArithmeticException("Division by zero."); 
    int[][] arrayOfInt = new int[2][];
    arrayOfInt[0] = new int[1];
    arrayOfInt[1] = new int[j];
    int k = headCoefficient(paramArrayOfint2);
    k = this.field.inverse(k);
    arrayOfInt[0][0] = 0;
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt[1], 0, (arrayOfInt[1]).length);
    while (i <= computeDegree(arrayOfInt[1])) {
      int[] arrayOfInt2 = new int[1];
      arrayOfInt2[0] = this.field.mult(headCoefficient(arrayOfInt[1]), k);
      int[] arrayOfInt1 = multWithElement(paramArrayOfint2, arrayOfInt2[0]);
      int m = computeDegree(arrayOfInt[1]) - i;
      arrayOfInt1 = multWithMonomial(arrayOfInt1, m);
      arrayOfInt2 = multWithMonomial(arrayOfInt2, m);
      arrayOfInt[0] = add(arrayOfInt2, arrayOfInt[0]);
      arrayOfInt[1] = add(arrayOfInt1, arrayOfInt[1]);
    } 
    return arrayOfInt;
  }
  
  public PolynomialGF2mSmallM gcd(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt = gcd(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  private int[] gcd(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1 = paramArrayOfint1;
    int[] arrayOfInt2 = paramArrayOfint2;
    if (computeDegree(arrayOfInt1) == -1)
      return arrayOfInt2; 
    while (computeDegree(arrayOfInt2) != -1) {
      int[] arrayOfInt = mod(arrayOfInt1, arrayOfInt2);
      arrayOfInt1 = new int[arrayOfInt2.length];
      System.arraycopy(arrayOfInt2, 0, arrayOfInt1, 0, arrayOfInt1.length);
      arrayOfInt2 = new int[arrayOfInt.length];
      System.arraycopy(arrayOfInt, 0, arrayOfInt2, 0, arrayOfInt2.length);
    } 
    int i = this.field.inverse(headCoefficient(arrayOfInt1));
    return multWithElement(arrayOfInt1, i);
  }
  
  public PolynomialGF2mSmallM multiply(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt = multiply(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  private int[] multiply(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (computeDegree(paramArrayOfint1) < computeDegree(paramArrayOfint2)) {
      arrayOfInt1 = paramArrayOfint2;
      arrayOfInt2 = paramArrayOfint1;
    } else {
      arrayOfInt1 = paramArrayOfint1;
      arrayOfInt2 = paramArrayOfint2;
    } 
    int[] arrayOfInt1 = normalForm(arrayOfInt1);
    int[] arrayOfInt2 = normalForm(arrayOfInt2);
    if (arrayOfInt2.length == 1)
      return multWithElement(arrayOfInt1, arrayOfInt2[0]); 
    int i = arrayOfInt1.length;
    int j = arrayOfInt2.length;
    int[] arrayOfInt3 = new int[i + j - 1];
    if (j != i) {
      int[] arrayOfInt4 = new int[j];
      int[] arrayOfInt5 = new int[i - j];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt4, 0, arrayOfInt4.length);
      System.arraycopy(arrayOfInt1, j, arrayOfInt5, 0, arrayOfInt5.length);
      arrayOfInt4 = multiply(arrayOfInt4, arrayOfInt2);
      arrayOfInt5 = multiply(arrayOfInt5, arrayOfInt2);
      arrayOfInt5 = multWithMonomial(arrayOfInt5, j);
      arrayOfInt3 = add(arrayOfInt4, arrayOfInt5);
    } else {
      j = i + 1 >>> 1;
      int k = i - j;
      int[] arrayOfInt4 = new int[j];
      int[] arrayOfInt5 = new int[j];
      int[] arrayOfInt6 = new int[k];
      int[] arrayOfInt7 = new int[k];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt4, 0, arrayOfInt4.length);
      System.arraycopy(arrayOfInt1, j, arrayOfInt6, 0, arrayOfInt6.length);
      System.arraycopy(arrayOfInt2, 0, arrayOfInt5, 0, arrayOfInt5.length);
      System.arraycopy(arrayOfInt2, j, arrayOfInt7, 0, arrayOfInt7.length);
      int[] arrayOfInt8 = add(arrayOfInt4, arrayOfInt6);
      int[] arrayOfInt9 = add(arrayOfInt5, arrayOfInt7);
      int[] arrayOfInt10 = multiply(arrayOfInt4, arrayOfInt5);
      int[] arrayOfInt11 = multiply(arrayOfInt8, arrayOfInt9);
      int[] arrayOfInt12 = multiply(arrayOfInt6, arrayOfInt7);
      arrayOfInt11 = add(arrayOfInt11, arrayOfInt10);
      arrayOfInt11 = add(arrayOfInt11, arrayOfInt12);
      arrayOfInt12 = multWithMonomial(arrayOfInt12, j);
      arrayOfInt3 = add(arrayOfInt11, arrayOfInt12);
      arrayOfInt3 = multWithMonomial(arrayOfInt3, j);
      arrayOfInt3 = add(arrayOfInt3, arrayOfInt10);
    } 
    return arrayOfInt3;
  }
  
  private boolean isIrreducible(int[] paramArrayOfint) {
    if (paramArrayOfint[0] == 0)
      return false; 
    int i = computeDegree(paramArrayOfint) >> 1;
    int[] arrayOfInt1 = { 0, 1 };
    int[] arrayOfInt2 = { 0, 1 };
    int j = this.field.getDegree();
    for (byte b = 0; b < i; b++) {
      for (int k = j - 1; k >= 0; k--)
        arrayOfInt1 = modMultiply(arrayOfInt1, arrayOfInt1, paramArrayOfint); 
      arrayOfInt1 = normalForm(arrayOfInt1);
      int[] arrayOfInt = gcd(add(arrayOfInt1, arrayOfInt2), paramArrayOfint);
      if (computeDegree(arrayOfInt) != 0)
        return false; 
    } 
    return true;
  }
  
  public PolynomialGF2mSmallM mod(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt = mod(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  private int[] mod(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = computeDegree(paramArrayOfint2);
    if (i == -1)
      throw new ArithmeticException("Division by zero"); 
    int[] arrayOfInt = new int[paramArrayOfint1.length];
    int j = headCoefficient(paramArrayOfint2);
    j = this.field.inverse(j);
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt, 0, arrayOfInt.length);
    while (i <= computeDegree(arrayOfInt)) {
      int k = this.field.mult(headCoefficient(arrayOfInt), j);
      int[] arrayOfInt1 = multWithMonomial(paramArrayOfint2, computeDegree(arrayOfInt) - i);
      arrayOfInt1 = multWithElement(arrayOfInt1, k);
      arrayOfInt = add(arrayOfInt1, arrayOfInt);
    } 
    return arrayOfInt;
  }
  
  public PolynomialGF2mSmallM modMultiply(PolynomialGF2mSmallM paramPolynomialGF2mSmallM1, PolynomialGF2mSmallM paramPolynomialGF2mSmallM2) {
    int[] arrayOfInt = modMultiply(this.coefficients, paramPolynomialGF2mSmallM1.coefficients, paramPolynomialGF2mSmallM2.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  public PolynomialGF2mSmallM modSquareMatrix(PolynomialGF2mSmallM[] paramArrayOfPolynomialGF2mSmallM) {
    int i = paramArrayOfPolynomialGF2mSmallM.length;
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    byte b;
    for (b = 0; b < this.coefficients.length; b++)
      arrayOfInt2[b] = this.field.mult(this.coefficients[b], this.coefficients[b]); 
    for (b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        if (b < (paramArrayOfPolynomialGF2mSmallM[b1]).coefficients.length) {
          int j = this.field.mult((paramArrayOfPolynomialGF2mSmallM[b1]).coefficients[b], arrayOfInt2[b1]);
          arrayOfInt1[b] = this.field.add(arrayOfInt1[b], j);
        } 
      } 
    } 
    return new PolynomialGF2mSmallM(this.field, arrayOfInt1);
  }
  
  private int[] modMultiply(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    return mod(multiply(paramArrayOfint1, paramArrayOfint2), paramArrayOfint3);
  }
  
  public PolynomialGF2mSmallM modSquareRoot(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt1 = IntUtils.clone(this.coefficients);
    for (int[] arrayOfInt2 = modMultiply(arrayOfInt1, arrayOfInt1, paramPolynomialGF2mSmallM.coefficients); !isEqual(arrayOfInt2, this.coefficients); arrayOfInt2 = modMultiply(arrayOfInt1, arrayOfInt1, paramPolynomialGF2mSmallM.coefficients))
      arrayOfInt1 = normalForm(arrayOfInt2); 
    return new PolynomialGF2mSmallM(this.field, arrayOfInt1);
  }
  
  public PolynomialGF2mSmallM modSquareRootMatrix(PolynomialGF2mSmallM[] paramArrayOfPolynomialGF2mSmallM) {
    int i = paramArrayOfPolynomialGF2mSmallM.length;
    int[] arrayOfInt = new int[i];
    byte b;
    for (b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        if (b < (paramArrayOfPolynomialGF2mSmallM[b1]).coefficients.length && b1 < this.coefficients.length) {
          int j = this.field.mult((paramArrayOfPolynomialGF2mSmallM[b1]).coefficients[b], this.coefficients[b1]);
          arrayOfInt[b] = this.field.add(arrayOfInt[b], j);
        } 
      } 
    } 
    for (b = 0; b < i; b++)
      arrayOfInt[b] = this.field.sqRoot(arrayOfInt[b]); 
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  public PolynomialGF2mSmallM modDiv(PolynomialGF2mSmallM paramPolynomialGF2mSmallM1, PolynomialGF2mSmallM paramPolynomialGF2mSmallM2) {
    int[] arrayOfInt = modDiv(this.coefficients, paramPolynomialGF2mSmallM1.coefficients, paramPolynomialGF2mSmallM2.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt);
  }
  
  private int[] modDiv(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    int[] arrayOfInt1 = normalForm(paramArrayOfint3);
    int[] arrayOfInt2 = mod(paramArrayOfint2, paramArrayOfint3);
    null = new int[] { 0 };
    for (int[] arrayOfInt3 = mod(paramArrayOfint1, paramArrayOfint3); computeDegree(arrayOfInt2) != -1; arrayOfInt3 = normalForm(arrayOfInt)) {
      int[][] arrayOfInt4 = div(arrayOfInt1, arrayOfInt2);
      arrayOfInt1 = normalForm(arrayOfInt2);
      arrayOfInt2 = normalForm(arrayOfInt4[1]);
      int[] arrayOfInt = add(null, modMultiply(arrayOfInt4[0], arrayOfInt3, paramArrayOfint3));
      null = normalForm(arrayOfInt3);
    } 
    int i = headCoefficient(arrayOfInt1);
    return multWithElement(null, this.field.inverse(i));
  }
  
  public PolynomialGF2mSmallM modInverse(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int[] arrayOfInt1 = { 1 };
    int[] arrayOfInt2 = modDiv(arrayOfInt1, this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    return new PolynomialGF2mSmallM(this.field, arrayOfInt2);
  }
  
  public PolynomialGF2mSmallM[] modPolynomialToFracton(PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int i = paramPolynomialGF2mSmallM.degree >> 1;
    int[] arrayOfInt1 = normalForm(paramPolynomialGF2mSmallM.coefficients);
    int[] arrayOfInt2 = mod(this.coefficients, paramPolynomialGF2mSmallM.coefficients);
    int[] arrayOfInt3 = { 0 };
    int[] arrayOfInt4;
    for (arrayOfInt4 = new int[] { 1 }; computeDegree(arrayOfInt2) > i; arrayOfInt4 = arrayOfInt5) {
      int[][] arrayOfInt = div(arrayOfInt1, arrayOfInt2);
      arrayOfInt1 = arrayOfInt2;
      arrayOfInt2 = arrayOfInt[1];
      int[] arrayOfInt5 = add(arrayOfInt3, modMultiply(arrayOfInt[0], arrayOfInt4, paramPolynomialGF2mSmallM.coefficients));
      arrayOfInt3 = arrayOfInt4;
    } 
    return new PolynomialGF2mSmallM[] { new PolynomialGF2mSmallM(this.field, arrayOfInt2), new PolynomialGF2mSmallM(this.field, arrayOfInt4) };
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof PolynomialGF2mSmallM))
      return false; 
    PolynomialGF2mSmallM polynomialGF2mSmallM = (PolynomialGF2mSmallM)paramObject;
    return (this.field.equals(polynomialGF2mSmallM.field) && this.degree == polynomialGF2mSmallM.degree && isEqual(this.coefficients, polynomialGF2mSmallM.coefficients));
  }
  
  private static boolean isEqual(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = computeDegree(paramArrayOfint1);
    int j = computeDegree(paramArrayOfint2);
    if (i != j)
      return false; 
    for (byte b = 0; b <= i; b++) {
      if (paramArrayOfint1[b] != paramArrayOfint2[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = this.field.hashCode();
    for (byte b = 0; b < this.coefficients.length; b++)
      i = i * 31 + this.coefficients[b]; 
    return i;
  }
  
  public String toString() {
    null = " Polynomial over " + this.field.toString() + ": \n";
    for (byte b = 0; b < this.coefficients.length; b++)
      null = null + this.field.elementToStr(this.coefficients[b]) + "Y^" + b + "+"; 
    return null + ";";
  }
  
  private void computeDegree() {
    this.degree = this.coefficients.length - 1;
    while (this.degree >= 0 && this.coefficients[this.degree] == 0)
      this.degree--; 
  }
  
  private static int computeDegree(int[] paramArrayOfint) {
    int i;
    for (i = paramArrayOfint.length - 1; i >= 0 && paramArrayOfint[i] == 0; i--);
    return i;
  }
  
  private static int[] normalForm(int[] paramArrayOfint) {
    int i = computeDegree(paramArrayOfint);
    if (i == -1)
      return new int[1]; 
    if (paramArrayOfint.length == i + 1)
      return IntUtils.clone(paramArrayOfint); 
    int[] arrayOfInt = new int[i + 1];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, i + 1);
    return arrayOfInt;
  }
}
