package org.bouncycastle.pqc.math.linearalgebra;

public class PolynomialRingGF2m {
  private GF2mField field;
  
  private PolynomialGF2mSmallM p;
  
  protected PolynomialGF2mSmallM[] sqMatrix;
  
  protected PolynomialGF2mSmallM[] sqRootMatrix;
  
  public PolynomialRingGF2m(GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    this.field = paramGF2mField;
    this.p = paramPolynomialGF2mSmallM;
    computeSquaringMatrix();
    computeSquareRootMatrix();
  }
  
  public PolynomialGF2mSmallM[] getSquaringMatrix() {
    return this.sqMatrix;
  }
  
  public PolynomialGF2mSmallM[] getSquareRootMatrix() {
    return this.sqRootMatrix;
  }
  
  private void computeSquaringMatrix() {
    int i = this.p.getDegree();
    this.sqMatrix = new PolynomialGF2mSmallM[i];
    int j;
    for (j = 0; j < i >> 1; j++) {
      int[] arrayOfInt = new int[(j << 1) + 1];
      arrayOfInt[j << 1] = 1;
      this.sqMatrix[j] = new PolynomialGF2mSmallM(this.field, arrayOfInt);
    } 
    for (j = i >> 1; j < i; j++) {
      int[] arrayOfInt = new int[(j << 1) + 1];
      arrayOfInt[j << 1] = 1;
      PolynomialGF2mSmallM polynomialGF2mSmallM = new PolynomialGF2mSmallM(this.field, arrayOfInt);
      this.sqMatrix[j] = polynomialGF2mSmallM.mod(this.p);
    } 
  }
  
  private void computeSquareRootMatrix() {
    int i = this.p.getDegree();
    PolynomialGF2mSmallM[] arrayOfPolynomialGF2mSmallM = new PolynomialGF2mSmallM[i];
    int j;
    for (j = i - 1; j >= 0; j--)
      arrayOfPolynomialGF2mSmallM[j] = new PolynomialGF2mSmallM(this.sqMatrix[j]); 
    this.sqRootMatrix = new PolynomialGF2mSmallM[i];
    for (j = i - 1; j >= 0; j--)
      this.sqRootMatrix[j] = new PolynomialGF2mSmallM(this.field, j); 
    for (j = 0; j < i; j++) {
      if (arrayOfPolynomialGF2mSmallM[j].getCoefficient(j) == 0) {
        boolean bool = false;
        for (int n = j + 1; n < i; n++) {
          if (arrayOfPolynomialGF2mSmallM[n].getCoefficient(j) != 0) {
            bool = true;
            swapColumns(arrayOfPolynomialGF2mSmallM, j, n);
            swapColumns(this.sqRootMatrix, j, n);
            n = i;
          } 
        } 
        if (!bool)
          throw new ArithmeticException("Squaring matrix is not invertible."); 
      } 
      int k = arrayOfPolynomialGF2mSmallM[j].getCoefficient(j);
      int m = this.field.inverse(k);
      arrayOfPolynomialGF2mSmallM[j].multThisWithElement(m);
      this.sqRootMatrix[j].multThisWithElement(m);
      for (byte b = 0; b < i; b++) {
        if (b != j) {
          k = arrayOfPolynomialGF2mSmallM[b].getCoefficient(j);
          if (k != 0) {
            PolynomialGF2mSmallM polynomialGF2mSmallM1 = arrayOfPolynomialGF2mSmallM[j].multWithElement(k);
            PolynomialGF2mSmallM polynomialGF2mSmallM2 = this.sqRootMatrix[j].multWithElement(k);
            arrayOfPolynomialGF2mSmallM[b].addToThis(polynomialGF2mSmallM1);
            this.sqRootMatrix[b].addToThis(polynomialGF2mSmallM2);
          } 
        } 
      } 
    } 
  }
  
  private static void swapColumns(PolynomialGF2mSmallM[] paramArrayOfPolynomialGF2mSmallM, int paramInt1, int paramInt2) {
    PolynomialGF2mSmallM polynomialGF2mSmallM = paramArrayOfPolynomialGF2mSmallM[paramInt1];
    paramArrayOfPolynomialGF2mSmallM[paramInt1] = paramArrayOfPolynomialGF2mSmallM[paramInt2];
    paramArrayOfPolynomialGF2mSmallM[paramInt2] = polynomialGF2mSmallM;
  }
}
