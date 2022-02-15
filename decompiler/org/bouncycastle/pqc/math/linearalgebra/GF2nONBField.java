package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Vector;

public class GF2nONBField extends GF2nField {
  private static final int MAXLONG = 64;
  
  private int mLength;
  
  private int mBit;
  
  private int mType;
  
  int[][] mMult;
  
  public GF2nONBField(int paramInt, SecureRandom paramSecureRandom) throws RuntimeException {
    super(paramSecureRandom);
    if (paramInt < 3)
      throw new IllegalArgumentException("k must be at least 3"); 
    this.mDegree = paramInt;
    this.mLength = this.mDegree / 64;
    this.mBit = this.mDegree & 0x3F;
    if (this.mBit == 0) {
      this.mBit = 64;
    } else {
      this.mLength++;
    } 
    computeType();
    if (this.mType < 3) {
      this.mMult = new int[this.mDegree][2];
      for (byte b = 0; b < this.mDegree; b++) {
        this.mMult[b][0] = -1;
        this.mMult[b][1] = -1;
      } 
      computeMultMatrix();
    } else {
      throw new RuntimeException("\nThe type of this field is " + this.mType);
    } 
    computeFieldPolynomial();
    this.fields = new Vector();
    this.matrices = new Vector();
  }
  
  int getONBLength() {
    return this.mLength;
  }
  
  int getONBBit() {
    return this.mBit;
  }
  
  protected GF2nElement getRandomRoot(GF2Polynomial paramGF2Polynomial) {
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(paramGF2Polynomial, this);
    int i;
    for (i = gF2nPolynomial.getDegree();; i = gF2nPolynomial.getDegree()) {
      if (i > 1) {
        while (true) {
          GF2nONBElement gF2nONBElement = new GF2nONBElement(this, this.random);
          GF2nPolynomial gF2nPolynomial2 = new GF2nPolynomial(2, GF2nONBElement.ZERO(this));
          gF2nPolynomial2.set(1, gF2nONBElement);
          GF2nPolynomial gF2nPolynomial1 = new GF2nPolynomial(gF2nPolynomial2);
          for (byte b = 1; b <= this.mDegree - 1; b++) {
            gF2nPolynomial1 = gF2nPolynomial1.multiplyAndReduce(gF2nPolynomial1, gF2nPolynomial);
            gF2nPolynomial1 = gF2nPolynomial1.add(gF2nPolynomial2);
          } 
          GF2nPolynomial gF2nPolynomial3 = gF2nPolynomial1.gcd(gF2nPolynomial);
          int j = gF2nPolynomial3.getDegree();
          i = gF2nPolynomial.getDegree();
          if (j != 0 && j != i) {
            if (j << 1 > i) {
              gF2nPolynomial = gF2nPolynomial.quotient(gF2nPolynomial3);
              break;
            } 
            gF2nPolynomial = new GF2nPolynomial(gF2nPolynomial3);
          } else {
            continue;
          } 
          i = gF2nPolynomial.getDegree();
        } 
      } else {
        break;
      } 
    } 
    return gF2nPolynomial.at(0);
  }
  
  protected void computeCOBMatrix(GF2nField paramGF2nField) {
    if (this.mDegree != paramGF2nField.mDegree)
      throw new IllegalArgumentException("GF2nField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!"); 
    GF2Polynomial[] arrayOfGF2Polynomial = new GF2Polynomial[this.mDegree];
    byte b;
    for (b = 0; b < this.mDegree; b++)
      arrayOfGF2Polynomial[b] = new GF2Polynomial(this.mDegree); 
    while (true) {
      GF2nElement gF2nElement = paramGF2nField.getRandomRoot(this.fieldPolynomial);
      if (!gF2nElement.isZero()) {
        GF2nPolynomialElement[] arrayOfGF2nPolynomialElement = new GF2nPolynomialElement[this.mDegree];
        arrayOfGF2nPolynomialElement[0] = (GF2nPolynomialElement)gF2nElement.clone();
        for (b = 1; b < this.mDegree; b++)
          arrayOfGF2nPolynomialElement[b] = (GF2nPolynomialElement)arrayOfGF2nPolynomialElement[b - 1].square(); 
        for (b = 0; b < this.mDegree; b++) {
          for (byte b1 = 0; b1 < this.mDegree; b1++) {
            if (arrayOfGF2nPolynomialElement[b].testBit(b1))
              arrayOfGF2Polynomial[this.mDegree - b1 - 1].setBit(this.mDegree - b - 1); 
          } 
        } 
        this.fields.addElement(paramGF2nField);
        this.matrices.addElement(arrayOfGF2Polynomial);
        paramGF2nField.fields.addElement(this);
        paramGF2nField.matrices.addElement(invertMatrix(arrayOfGF2Polynomial));
        return;
      } 
    } 
  }
  
  protected void computeFieldPolynomial() {
    if (this.mType == 1) {
      this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1, "ALL");
    } else if (this.mType == 2) {
      GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.mDegree + 1, "ONE");
      GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree + 1, "X");
      gF2Polynomial2.addToThis(gF2Polynomial1);
      for (byte b = 1; b < this.mDegree; b++) {
        GF2Polynomial gF2Polynomial = gF2Polynomial1;
        gF2Polynomial1 = gF2Polynomial2;
        gF2Polynomial2 = gF2Polynomial1.shiftLeft();
        gF2Polynomial2.addToThis(gF2Polynomial);
      } 
      this.fieldPolynomial = gF2Polynomial2;
    } 
  }
  
  int[][] invMatrix(int[][] paramArrayOfint) {
    int[][] arrayOfInt1 = new int[this.mDegree][this.mDegree];
    arrayOfInt1 = paramArrayOfint;
    int[][] arrayOfInt2 = new int[this.mDegree][this.mDegree];
    byte b;
    for (b = 0; b < this.mDegree; b++)
      arrayOfInt2[b][b] = 1; 
    for (b = 0; b < this.mDegree; b++) {
      for (byte b1 = b; b1 < this.mDegree; b1++)
        arrayOfInt1[this.mDegree - 1 - b][b1] = arrayOfInt1[b][b]; 
    } 
    return (int[][])null;
  }
  
  private void computeType() throws RuntimeException {
    if ((this.mDegree & 0x7) == 0)
      throw new RuntimeException("The extension degree is divisible by 8!"); 
    int i = 0;
    int j = 0;
    this.mType = 1;
    int k = 0;
    while (k != 1) {
      i = this.mType * this.mDegree + 1;
      if (IntegerFunctions.isPrime(i)) {
        j = IntegerFunctions.order(2, i);
        k = IntegerFunctions.gcd(this.mType * this.mDegree / j, this.mDegree);
      } 
      this.mType++;
    } 
    this.mType--;
    if (this.mType == 1) {
      i = (this.mDegree << 1) + 1;
      if (IntegerFunctions.isPrime(i)) {
        j = IntegerFunctions.order(2, i);
        k = IntegerFunctions.gcd((this.mDegree << 1) / j, this.mDegree);
        if (k == 1)
          this.mType++; 
      } 
    } 
  }
  
  private void computeMultMatrix() {
    if ((this.mType & 0x7) != 0) {
      int j;
      int i = this.mType * this.mDegree + 1;
      int[] arrayOfInt = new int[i];
      if (this.mType == 1) {
        j = 1;
      } else if (this.mType == 2) {
        j = i - 1;
      } else {
        j = elementOfOrder(this.mType, i);
      } 
      int k = 1;
      int m;
      for (m = 0; m < this.mType; m++) {
        int n = k;
        for (byte b = 0; b < this.mDegree; b++) {
          arrayOfInt[n] = b;
          n = (n << 1) % i;
          if (n < 0)
            n += i; 
        } 
        k = j * k % i;
        if (k < 0)
          k += i; 
      } 
      if (this.mType == 1) {
        for (m = 1; m < i - 1; m++) {
          if (this.mMult[arrayOfInt[m + 1]][0] == -1) {
            this.mMult[arrayOfInt[m + 1]][0] = arrayOfInt[i - m];
          } else {
            this.mMult[arrayOfInt[m + 1]][1] = arrayOfInt[i - m];
          } 
        } 
        m = this.mDegree >> 1;
        for (byte b = 1; b <= m; b++) {
          if (this.mMult[b - 1][0] == -1) {
            this.mMult[b - 1][0] = m + b - 1;
          } else {
            this.mMult[b - 1][1] = m + b - 1;
          } 
          if (this.mMult[m + b - 1][0] == -1) {
            this.mMult[m + b - 1][0] = b - 1;
          } else {
            this.mMult[m + b - 1][1] = b - 1;
          } 
        } 
      } else if (this.mType == 2) {
        for (m = 1; m < i - 1; m++) {
          if (this.mMult[arrayOfInt[m + 1]][0] == -1) {
            this.mMult[arrayOfInt[m + 1]][0] = arrayOfInt[i - m];
          } else {
            this.mMult[arrayOfInt[m + 1]][1] = arrayOfInt[i - m];
          } 
        } 
      } else {
        throw new RuntimeException("only type 1 or type 2 implemented");
      } 
    } else {
      throw new RuntimeException("bisher nur fuer Gausssche Normalbasen implementiert");
    } 
  }
  
  private int elementOfOrder(int paramInt1, int paramInt2) {
    Random random = new Random();
    int i = 0;
    while (!i) {
      i = random.nextInt();
      i %= paramInt2 - 1;
      if (i < 0)
        i += paramInt2 - 1; 
    } 
    int j = IntegerFunctions.order(i, paramInt2);
    while (true) {
      if (j % paramInt1 != 0 || j == 0) {
        while (i == 0) {
          i = random.nextInt();
          i %= paramInt2 - 1;
          if (i < 0)
            i += paramInt2 - 1; 
        } 
        j = IntegerFunctions.order(i, paramInt2);
        continue;
      } 
      int k = i;
      j = paramInt1 / j;
      for (byte b = 2; b <= j; b++)
        k *= i; 
      return k;
    } 
  }
}
