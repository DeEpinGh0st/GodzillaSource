package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public final class GoppaCode {
  public static GF2Matrix createCanonicalCheckMatrix(GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM) {
    int i = paramGF2mField.getDegree();
    int j = 1 << i;
    int k = paramPolynomialGF2mSmallM.getDegree();
    int[][] arrayOfInt1 = new int[k][j];
    int[][] arrayOfInt2 = new int[k][j];
    byte b1;
    for (b1 = 0; b1 < j; b1++)
      arrayOfInt2[0][b1] = paramGF2mField.inverse(paramPolynomialGF2mSmallM.evaluateAt(b1)); 
    for (b1 = 1; b1 < k; b1++) {
      for (byte b = 0; b < j; b++)
        arrayOfInt2[b1][b] = paramGF2mField.mult(arrayOfInt2[b1 - 1][b], b); 
    } 
    for (b1 = 0; b1 < k; b1++) {
      for (byte b = 0; b < j; b++) {
        for (byte b3 = 0; b3 <= b1; b3++)
          arrayOfInt1[b1][b] = paramGF2mField.add(arrayOfInt1[b1][b], paramGF2mField.mult(arrayOfInt2[b3][b], paramPolynomialGF2mSmallM.getCoefficient(k + b3 - b1))); 
      } 
    } 
    int[][] arrayOfInt3 = new int[k * i][j + 31 >>> 5];
    for (byte b2 = 0; b2 < j; b2++) {
      int m = b2 >>> 5;
      int n = 1 << (b2 & 0x1F);
      for (byte b = 0; b < k; b++) {
        int i1 = arrayOfInt1[b][b2];
        for (byte b3 = 0; b3 < i; b3++) {
          int i2 = i1 >>> b3 & 0x1;
          if (i2 != 0) {
            int i3 = (b + 1) * i - b3 - 1;
            arrayOfInt3[i3][m] = arrayOfInt3[i3][m] ^ n;
          } 
        } 
      } 
    } 
    return new GF2Matrix(j, arrayOfInt3);
  }
  
  public static MaMaPe computeSystematicForm(GF2Matrix paramGF2Matrix, SecureRandom paramSecureRandom) {
    int i = paramGF2Matrix.getNumColumns();
    GF2Matrix gF2Matrix = null;
    boolean bool = false;
    while (true) {
      Permutation permutation = new Permutation(i, paramSecureRandom);
      GF2Matrix gF2Matrix1 = (GF2Matrix)paramGF2Matrix.rightMultiply(permutation);
      GF2Matrix gF2Matrix2 = gF2Matrix1.getLeftSubMatrix();
      try {
        bool = true;
        gF2Matrix = (GF2Matrix)gF2Matrix2.computeInverse();
      } catch (ArithmeticException arithmeticException) {
        bool = false;
      } 
      if (bool) {
        GF2Matrix gF2Matrix3 = (GF2Matrix)gF2Matrix.rightMultiply(gF2Matrix1);
        GF2Matrix gF2Matrix4 = gF2Matrix3.getRightSubMatrix();
        return new MaMaPe(gF2Matrix2, gF2Matrix4, permutation);
      } 
    } 
  }
  
  public static GF2Vector syndromeDecode(GF2Vector paramGF2Vector, GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM, PolynomialGF2mSmallM[] paramArrayOfPolynomialGF2mSmallM) {
    int i = 1 << paramGF2mField.getDegree();
    GF2Vector gF2Vector = new GF2Vector(i);
    if (!paramGF2Vector.isZero()) {
      PolynomialGF2mSmallM polynomialGF2mSmallM1 = new PolynomialGF2mSmallM(paramGF2Vector.toExtensionFieldVector(paramGF2mField));
      PolynomialGF2mSmallM polynomialGF2mSmallM2 = polynomialGF2mSmallM1.modInverse(paramPolynomialGF2mSmallM);
      PolynomialGF2mSmallM polynomialGF2mSmallM3 = polynomialGF2mSmallM2.addMonomial(1);
      polynomialGF2mSmallM3 = polynomialGF2mSmallM3.modSquareRootMatrix(paramArrayOfPolynomialGF2mSmallM);
      PolynomialGF2mSmallM[] arrayOfPolynomialGF2mSmallM = polynomialGF2mSmallM3.modPolynomialToFracton(paramPolynomialGF2mSmallM);
      PolynomialGF2mSmallM polynomialGF2mSmallM4 = arrayOfPolynomialGF2mSmallM[0].multiply(arrayOfPolynomialGF2mSmallM[0]);
      PolynomialGF2mSmallM polynomialGF2mSmallM5 = arrayOfPolynomialGF2mSmallM[1].multiply(arrayOfPolynomialGF2mSmallM[1]);
      PolynomialGF2mSmallM polynomialGF2mSmallM6 = polynomialGF2mSmallM5.multWithMonomial(1);
      PolynomialGF2mSmallM polynomialGF2mSmallM7 = polynomialGF2mSmallM4.add(polynomialGF2mSmallM6);
      int j = polynomialGF2mSmallM7.getHeadCoefficient();
      int k = paramGF2mField.inverse(j);
      PolynomialGF2mSmallM polynomialGF2mSmallM8 = polynomialGF2mSmallM7.multWithElement(k);
      for (byte b = 0; b < i; b++) {
        int m = polynomialGF2mSmallM8.evaluateAt(b);
        if (m == 0)
          gF2Vector.setBit(b); 
      } 
    } 
    return gF2Vector;
  }
  
  public static class MaMaPe {
    private GF2Matrix s;
    
    private GF2Matrix h;
    
    private Permutation p;
    
    public MaMaPe(GF2Matrix param1GF2Matrix1, GF2Matrix param1GF2Matrix2, Permutation param1Permutation) {
      this.s = param1GF2Matrix1;
      this.h = param1GF2Matrix2;
      this.p = param1Permutation;
    }
    
    public GF2Matrix getFirstMatrix() {
      return this.s;
    }
    
    public GF2Matrix getSecondMatrix() {
      return this.h;
    }
    
    public Permutation getPermutation() {
      return this.p;
    }
  }
  
  public static class MatrixSet {
    private GF2Matrix g;
    
    private int[] setJ;
    
    public MatrixSet(GF2Matrix param1GF2Matrix, int[] param1ArrayOfint) {
      this.g = param1GF2Matrix;
      this.setJ = param1ArrayOfint;
    }
    
    public GF2Matrix getG() {
      return this.g;
    }
    
    public int[] getSetJ() {
      return this.setJ;
    }
  }
}
