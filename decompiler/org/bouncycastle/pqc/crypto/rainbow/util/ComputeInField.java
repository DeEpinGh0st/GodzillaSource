package org.bouncycastle.pqc.crypto.rainbow.util;

public class ComputeInField {
  private short[][] A;
  
  short[] x;
  
  public short[] solveEquation(short[][] paramArrayOfshort, short[] paramArrayOfshort1) {
    if (paramArrayOfshort.length != paramArrayOfshort1.length)
      return null; 
    try {
      this.A = new short[paramArrayOfshort.length][paramArrayOfshort.length + 1];
      this.x = new short[paramArrayOfshort.length];
      byte b;
      for (b = 0; b < paramArrayOfshort.length; b++) {
        for (byte b1 = 0; b1 < (paramArrayOfshort[0]).length; b1++)
          this.A[b][b1] = paramArrayOfshort[b][b1]; 
      } 
      for (b = 0; b < paramArrayOfshort1.length; b++)
        this.A[b][paramArrayOfshort1.length] = GF2Field.addElem(paramArrayOfshort1[b], this.A[b][paramArrayOfshort1.length]); 
      computeZerosUnder(false);
      substitute();
      return this.x;
    } catch (RuntimeException runtimeException) {
      return null;
    } 
  }
  
  public short[][] inverse(short[][] paramArrayOfshort) {
    try {
      this.A = new short[paramArrayOfshort.length][2 * paramArrayOfshort.length];
      if (paramArrayOfshort.length != (paramArrayOfshort[0]).length)
        throw new RuntimeException("The matrix is not invertible. Please choose another one!"); 
      byte b;
      for (b = 0; b < paramArrayOfshort.length; b++) {
        int i;
        for (i = 0; i < paramArrayOfshort.length; i++)
          this.A[b][i] = paramArrayOfshort[b][i]; 
        for (i = paramArrayOfshort.length; i < 2 * paramArrayOfshort.length; i++)
          this.A[b][i] = 0; 
        this.A[b][b + this.A.length] = 1;
      } 
      computeZerosUnder(true);
      for (b = 0; b < this.A.length; b++) {
        short s = GF2Field.invElem(this.A[b][b]);
        for (byte b1 = b; b1 < 2 * this.A.length; b1++)
          this.A[b][b1] = GF2Field.multElem(this.A[b][b1], s); 
      } 
      computeZerosAbove();
      short[][] arrayOfShort = new short[this.A.length][this.A.length];
      for (b = 0; b < this.A.length; b++) {
        for (int i = this.A.length; i < 2 * this.A.length; i++)
          arrayOfShort[b][i - this.A.length] = this.A[b][i]; 
      } 
      return arrayOfShort;
    } catch (RuntimeException runtimeException) {
      return (short[][])null;
    } 
  }
  
  private void computeZerosUnder(boolean paramBoolean) throws RuntimeException {
    int i;
    short s = 0;
    if (paramBoolean) {
      i = 2 * this.A.length;
    } else {
      i = this.A.length + 1;
    } 
    for (byte b = 0; b < this.A.length - 1; b++) {
      for (int j = b + 1; j < this.A.length; j++) {
        short s1 = this.A[j][b];
        short s2 = GF2Field.invElem(this.A[b][b]);
        if (s2 == 0)
          throw new IllegalStateException("Matrix not invertible! We have to choose another one!"); 
        for (byte b1 = b; b1 < i; b1++) {
          s = GF2Field.multElem(this.A[b][b1], s2);
          s = GF2Field.multElem(s1, s);
          this.A[j][b1] = GF2Field.addElem(this.A[j][b1], s);
        } 
      } 
    } 
  }
  
  private void computeZerosAbove() throws RuntimeException {
    short s = 0;
    for (int i = this.A.length - 1; i > 0; i--) {
      for (int j = i - 1; j >= 0; j--) {
        short s1 = this.A[j][i];
        short s2 = GF2Field.invElem(this.A[i][i]);
        if (s2 == 0)
          throw new RuntimeException("The matrix is not invertible"); 
        for (int k = i; k < 2 * this.A.length; k++) {
          s = GF2Field.multElem(this.A[i][k], s2);
          s = GF2Field.multElem(s1, s);
          this.A[j][k] = GF2Field.addElem(this.A[j][k], s);
        } 
      } 
    } 
  }
  
  private void substitute() throws IllegalStateException {
    short s = GF2Field.invElem(this.A[this.A.length - 1][this.A.length - 1]);
    if (s == 0)
      throw new IllegalStateException("The equation system is not solvable"); 
    this.x[this.A.length - 1] = GF2Field.multElem(this.A[this.A.length - 1][this.A.length], s);
    for (int i = this.A.length - 2; i >= 0; i--) {
      short s1 = this.A[i][this.A.length];
      for (int j = this.A.length - 1; j > i; j--) {
        s = GF2Field.multElem(this.A[i][j], this.x[j]);
        s1 = GF2Field.addElem(s1, s);
      } 
      s = GF2Field.invElem(this.A[i][i]);
      if (s == 0)
        throw new IllegalStateException("Not solvable equation system"); 
      this.x[i] = GF2Field.multElem(s1, s);
    } 
  }
  
  public short[][] multiplyMatrix(short[][] paramArrayOfshort1, short[][] paramArrayOfshort2) throws RuntimeException {
    if ((paramArrayOfshort1[0]).length != paramArrayOfshort2.length)
      throw new RuntimeException("Multiplication is not possible!"); 
    short s = 0;
    this.A = new short[paramArrayOfshort1.length][(paramArrayOfshort2[0]).length];
    for (byte b = 0; b < paramArrayOfshort1.length; b++) {
      for (byte b1 = 0; b1 < paramArrayOfshort2.length; b1++) {
        for (byte b2 = 0; b2 < (paramArrayOfshort2[0]).length; b2++) {
          s = GF2Field.multElem(paramArrayOfshort1[b][b1], paramArrayOfshort2[b1][b2]);
          this.A[b][b2] = GF2Field.addElem(this.A[b][b2], s);
        } 
      } 
    } 
    return this.A;
  }
  
  public short[] multiplyMatrix(short[][] paramArrayOfshort, short[] paramArrayOfshort1) throws RuntimeException {
    if ((paramArrayOfshort[0]).length != paramArrayOfshort1.length)
      throw new RuntimeException("Multiplication is not possible!"); 
    short s = 0;
    short[] arrayOfShort = new short[paramArrayOfshort.length];
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      for (byte b1 = 0; b1 < paramArrayOfshort1.length; b1++) {
        s = GF2Field.multElem(paramArrayOfshort[b][b1], paramArrayOfshort1[b1]);
        arrayOfShort[b] = GF2Field.addElem(arrayOfShort[b], s);
      } 
    } 
    return arrayOfShort;
  }
  
  public short[] addVect(short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      throw new RuntimeException("Multiplication is not possible!"); 
    short[] arrayOfShort = new short[paramArrayOfshort1.length];
    for (byte b = 0; b < arrayOfShort.length; b++)
      arrayOfShort[b] = GF2Field.addElem(paramArrayOfshort1[b], paramArrayOfshort2[b]); 
    return arrayOfShort;
  }
  
  public short[][] multVects(short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length)
      throw new RuntimeException("Multiplication is not possible!"); 
    short[][] arrayOfShort = new short[paramArrayOfshort1.length][paramArrayOfshort2.length];
    for (byte b = 0; b < paramArrayOfshort1.length; b++) {
      for (byte b1 = 0; b1 < paramArrayOfshort2.length; b1++)
        arrayOfShort[b][b1] = GF2Field.multElem(paramArrayOfshort1[b], paramArrayOfshort2[b1]); 
    } 
    return arrayOfShort;
  }
  
  public short[] multVect(short paramShort, short[] paramArrayOfshort) {
    short[] arrayOfShort = new short[paramArrayOfshort.length];
    for (byte b = 0; b < arrayOfShort.length; b++)
      arrayOfShort[b] = GF2Field.multElem(paramShort, paramArrayOfshort[b]); 
    return arrayOfShort;
  }
  
  public short[][] multMatrix(short paramShort, short[][] paramArrayOfshort) {
    short[][] arrayOfShort = new short[paramArrayOfshort.length][(paramArrayOfshort[0]).length];
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfshort[0]).length; b1++)
        arrayOfShort[b][b1] = GF2Field.multElem(paramShort, paramArrayOfshort[b][b1]); 
    } 
    return arrayOfShort;
  }
  
  public short[][] addSquareMatrix(short[][] paramArrayOfshort1, short[][] paramArrayOfshort2) {
    if (paramArrayOfshort1.length != paramArrayOfshort2.length || (paramArrayOfshort1[0]).length != (paramArrayOfshort2[0]).length)
      throw new RuntimeException("Addition is not possible!"); 
    short[][] arrayOfShort = new short[paramArrayOfshort1.length][paramArrayOfshort1.length];
    for (byte b = 0; b < paramArrayOfshort1.length; b++) {
      for (byte b1 = 0; b1 < paramArrayOfshort2.length; b1++)
        arrayOfShort[b][b1] = GF2Field.addElem(paramArrayOfshort1[b][b1], paramArrayOfshort2[b][b1]); 
    } 
    return arrayOfShort;
  }
}
