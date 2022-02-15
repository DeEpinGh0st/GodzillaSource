package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;
import java.util.Vector;

public class GF2nPolynomialField extends GF2nField {
  GF2Polynomial[] squaringMatrix;
  
  private boolean isTrinomial = false;
  
  private boolean isPentanomial = false;
  
  private int tc;
  
  private int[] pc = new int[3];
  
  public GF2nPolynomialField(int paramInt, SecureRandom paramSecureRandom) {
    super(paramSecureRandom);
    if (paramInt < 3)
      throw new IllegalArgumentException("k must be at least 3"); 
    this.mDegree = paramInt;
    computeFieldPolynomial();
    computeSquaringMatrix();
    this.fields = new Vector();
    this.matrices = new Vector();
  }
  
  public GF2nPolynomialField(int paramInt, SecureRandom paramSecureRandom, boolean paramBoolean) {
    super(paramSecureRandom);
    if (paramInt < 3)
      throw new IllegalArgumentException("k must be at least 3"); 
    this.mDegree = paramInt;
    if (paramBoolean) {
      computeFieldPolynomial();
    } else {
      computeFieldPolynomial2();
    } 
    computeSquaringMatrix();
    this.fields = new Vector();
    this.matrices = new Vector();
  }
  
  public GF2nPolynomialField(int paramInt, SecureRandom paramSecureRandom, GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    super(paramSecureRandom);
    if (paramInt < 3)
      throw new IllegalArgumentException("degree must be at least 3"); 
    if (paramGF2Polynomial.getLength() != paramInt + 1)
      throw new RuntimeException(); 
    if (!paramGF2Polynomial.isIrreducible())
      throw new RuntimeException(); 
    this.mDegree = paramInt;
    this.fieldPolynomial = paramGF2Polynomial;
    computeSquaringMatrix();
    byte b1 = 2;
    for (byte b2 = 1; b2 < this.fieldPolynomial.getLength() - 1; b2++) {
      if (this.fieldPolynomial.testBit(b2)) {
        if (++b1 == 3)
          this.tc = b2; 
        if (b1 <= 5)
          this.pc[b1 - 3] = b2; 
      } 
    } 
    if (b1 == 3)
      this.isTrinomial = true; 
    if (b1 == 5)
      this.isPentanomial = true; 
    this.fields = new Vector();
    this.matrices = new Vector();
  }
  
  public boolean isTrinomial() {
    return this.isTrinomial;
  }
  
  public boolean isPentanomial() {
    return this.isPentanomial;
  }
  
  public int getTc() throws RuntimeException {
    if (!this.isTrinomial)
      throw new RuntimeException(); 
    return this.tc;
  }
  
  public int[] getPc() throws RuntimeException {
    if (!this.isPentanomial)
      throw new RuntimeException(); 
    int[] arrayOfInt = new int[3];
    System.arraycopy(this.pc, 0, arrayOfInt, 0, 3);
    return arrayOfInt;
  }
  
  public GF2Polynomial getSquaringVector(int paramInt) {
    return new GF2Polynomial(this.squaringMatrix[paramInt]);
  }
  
  protected GF2nElement getRandomRoot(GF2Polynomial paramGF2Polynomial) {
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(paramGF2Polynomial, this);
    int i;
    for (i = gF2nPolynomial.getDegree();; i = gF2nPolynomial.getDegree()) {
      if (i > 1) {
        while (true) {
          GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this, this.random);
          GF2nPolynomial gF2nPolynomial2 = new GF2nPolynomial(2, GF2nPolynomialElement.ZERO(this));
          gF2nPolynomial2.set(1, gF2nPolynomialElement);
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
      throw new IllegalArgumentException("GF2nPolynomialField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!"); 
    if (paramGF2nField instanceof GF2nONBField) {
      paramGF2nField.computeCOBMatrix(this);
      return;
    } 
    GF2Polynomial[] arrayOfGF2Polynomial = new GF2Polynomial[this.mDegree];
    int i;
    for (i = 0; i < this.mDegree; i++)
      arrayOfGF2Polynomial[i] = new GF2Polynomial(this.mDegree); 
    while (true) {
      GF2nElement gF2nElement = paramGF2nField.getRandomRoot(this.fieldPolynomial);
      if (!gF2nElement.isZero()) {
        GF2nPolynomialElement[] arrayOfGF2nPolynomialElement;
        if (gF2nElement instanceof GF2nONBElement) {
          GF2nONBElement[] arrayOfGF2nONBElement = new GF2nONBElement[this.mDegree];
          arrayOfGF2nONBElement[this.mDegree - 1] = GF2nONBElement.ONE((GF2nONBField)paramGF2nField);
        } else {
          arrayOfGF2nPolynomialElement = new GF2nPolynomialElement[this.mDegree];
          arrayOfGF2nPolynomialElement[this.mDegree - 1] = GF2nPolynomialElement.ONE((GF2nPolynomialField)paramGF2nField);
        } 
        arrayOfGF2nPolynomialElement[this.mDegree - 2] = (GF2nPolynomialElement)gF2nElement;
        for (i = this.mDegree - 3; i >= 0; i--)
          arrayOfGF2nPolynomialElement[i] = (GF2nPolynomialElement)arrayOfGF2nPolynomialElement[i + 1].multiply(gF2nElement); 
        if (paramGF2nField instanceof GF2nONBField) {
          for (i = 0; i < this.mDegree; i++) {
            for (byte b = 0; b < this.mDegree; b++) {
              if (arrayOfGF2nPolynomialElement[i].testBit(this.mDegree - b - 1))
                arrayOfGF2Polynomial[this.mDegree - b - 1].setBit(this.mDegree - i - 1); 
            } 
          } 
        } else {
          for (i = 0; i < this.mDegree; i++) {
            for (byte b = 0; b < this.mDegree; b++) {
              if (arrayOfGF2nPolynomialElement[i].testBit(b))
                arrayOfGF2Polynomial[this.mDegree - b - 1].setBit(this.mDegree - i - 1); 
            } 
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
  
  private void computeSquaringMatrix() {
    GF2Polynomial[] arrayOfGF2Polynomial = new GF2Polynomial[this.mDegree - 1];
    this.squaringMatrix = new GF2Polynomial[this.mDegree];
    int i;
    for (i = 0; i < this.squaringMatrix.length; i++)
      this.squaringMatrix[i] = new GF2Polynomial(this.mDegree, "ZERO"); 
    for (i = 0; i < this.mDegree - 1; i++)
      arrayOfGF2Polynomial[i] = (new GF2Polynomial(1, "ONE")).shiftLeft(this.mDegree + i).remainder(this.fieldPolynomial); 
    for (i = 1; i <= Math.abs(this.mDegree >> 1); i++) {
      for (byte b = 1; b <= this.mDegree; b++) {
        if (arrayOfGF2Polynomial[this.mDegree - (i << 1)].testBit(this.mDegree - b))
          this.squaringMatrix[b - 1].setBit(this.mDegree - i); 
      } 
    } 
    for (i = Math.abs(this.mDegree >> 1) + 1; i <= this.mDegree; i++)
      this.squaringMatrix[(i << 1) - this.mDegree - 1].setBit(this.mDegree - i); 
  }
  
  protected void computeFieldPolynomial() {
    if (testTrinomials())
      return; 
    if (testPentanomials())
      return; 
    testRandom();
  }
  
  protected void computeFieldPolynomial2() {
    if (testTrinomials())
      return; 
    if (testPentanomials())
      return; 
    testRandom();
  }
  
  private boolean testTrinomials() {
    boolean bool = false;
    byte b2 = 0;
    this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
    this.fieldPolynomial.setBit(0);
    this.fieldPolynomial.setBit(this.mDegree);
    for (byte b1 = 1; b1 < this.mDegree && !bool; b1++) {
      this.fieldPolynomial.setBit(b1);
      bool = this.fieldPolynomial.isIrreducible();
      b2++;
      if (bool) {
        this.isTrinomial = true;
        this.tc = b1;
        return bool;
      } 
      this.fieldPolynomial.resetBit(b1);
      bool = this.fieldPolynomial.isIrreducible();
    } 
    return bool;
  }
  
  private boolean testPentanomials() {
    boolean bool = false;
    byte b2 = 0;
    this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
    this.fieldPolynomial.setBit(0);
    this.fieldPolynomial.setBit(this.mDegree);
    for (byte b1 = 1; b1 <= this.mDegree - 3 && !bool; b1++) {
      this.fieldPolynomial.setBit(b1);
      for (int i = b1 + 1; i <= this.mDegree - 2 && !bool; i++) {
        this.fieldPolynomial.setBit(i);
        for (int j = i + 1; j <= this.mDegree - 1 && !bool; j++) {
          this.fieldPolynomial.setBit(j);
          if (((((this.mDegree & 0x1) != 0) ? 1 : 0) | (((b1 & 0x1) != 0) ? 1 : 0) | (((i & 0x1) != 0) ? 1 : 0) | (((j & 0x1) != 0) ? 1 : 0)) != 0) {
            bool = this.fieldPolynomial.isIrreducible();
            b2++;
            if (bool) {
              this.isPentanomial = true;
              this.pc[0] = b1;
              this.pc[1] = i;
              this.pc[2] = j;
              return bool;
            } 
          } 
          this.fieldPolynomial.resetBit(j);
        } 
        this.fieldPolynomial.resetBit(i);
      } 
      this.fieldPolynomial.resetBit(b1);
    } 
    return bool;
  }
  
  private boolean testRandom() {
    boolean bool = false;
    this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
    byte b = 0;
    while (!bool) {
      b++;
      this.fieldPolynomial.randomize();
      this.fieldPolynomial.setBit(this.mDegree);
      this.fieldPolynomial.setBit(0);
      if (this.fieldPolynomial.isIrreducible())
        return true; 
    } 
    return bool;
  }
}
