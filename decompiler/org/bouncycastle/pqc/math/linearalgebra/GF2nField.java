package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;
import java.util.Vector;

public abstract class GF2nField {
  protected final SecureRandom random;
  
  protected int mDegree;
  
  protected GF2Polynomial fieldPolynomial;
  
  protected Vector fields;
  
  protected Vector matrices;
  
  protected GF2nField(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
  }
  
  public final int getDegree() {
    return this.mDegree;
  }
  
  public final GF2Polynomial getFieldPolynomial() {
    if (this.fieldPolynomial == null)
      computeFieldPolynomial(); 
    return new GF2Polynomial(this.fieldPolynomial);
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2nField))
      return false; 
    GF2nField gF2nField = (GF2nField)paramObject;
    return (gF2nField.mDegree != this.mDegree) ? false : (!this.fieldPolynomial.equals(gF2nField.fieldPolynomial) ? false : ((this instanceof GF2nPolynomialField && !(gF2nField instanceof GF2nPolynomialField)) ? false : (!(this instanceof GF2nONBField && !(gF2nField instanceof GF2nONBField)))));
  }
  
  public int hashCode() {
    return this.mDegree + this.fieldPolynomial.hashCode();
  }
  
  protected abstract GF2nElement getRandomRoot(GF2Polynomial paramGF2Polynomial);
  
  protected abstract void computeCOBMatrix(GF2nField paramGF2nField);
  
  protected abstract void computeFieldPolynomial();
  
  protected final GF2Polynomial[] invertMatrix(GF2Polynomial[] paramArrayOfGF2Polynomial) {
    GF2Polynomial[] arrayOfGF2Polynomial1 = new GF2Polynomial[paramArrayOfGF2Polynomial.length];
    GF2Polynomial[] arrayOfGF2Polynomial2 = new GF2Polynomial[paramArrayOfGF2Polynomial.length];
    int i;
    for (i = 0; i < this.mDegree; i++) {
      try {
        arrayOfGF2Polynomial1[i] = new GF2Polynomial(paramArrayOfGF2Polynomial[i]);
        arrayOfGF2Polynomial2[i] = new GF2Polynomial(this.mDegree);
        arrayOfGF2Polynomial2[i].setBit(this.mDegree - 1 - i);
      } catch (RuntimeException runtimeException) {
        runtimeException.printStackTrace();
      } 
    } 
    for (i = 0; i < this.mDegree - 1; i++) {
      int j;
      for (j = i; j < this.mDegree && !arrayOfGF2Polynomial1[j].testBit(this.mDegree - 1 - i); j++);
      if (j >= this.mDegree)
        throw new RuntimeException("GF2nField.invertMatrix: Matrix cannot be inverted!"); 
      if (i != j) {
        GF2Polynomial gF2Polynomial = arrayOfGF2Polynomial1[i];
        arrayOfGF2Polynomial1[i] = arrayOfGF2Polynomial1[j];
        arrayOfGF2Polynomial1[j] = gF2Polynomial;
        gF2Polynomial = arrayOfGF2Polynomial2[i];
        arrayOfGF2Polynomial2[i] = arrayOfGF2Polynomial2[j];
        arrayOfGF2Polynomial2[j] = gF2Polynomial;
      } 
      for (j = i + 1; j < this.mDegree; j++) {
        if (arrayOfGF2Polynomial1[j].testBit(this.mDegree - 1 - i)) {
          arrayOfGF2Polynomial1[j].addToThis(arrayOfGF2Polynomial1[i]);
          arrayOfGF2Polynomial2[j].addToThis(arrayOfGF2Polynomial2[i]);
        } 
      } 
    } 
    for (i = this.mDegree - 1; i > 0; i--) {
      for (int j = i - 1; j >= 0; j--) {
        if (arrayOfGF2Polynomial1[j].testBit(this.mDegree - 1 - i)) {
          arrayOfGF2Polynomial1[j].addToThis(arrayOfGF2Polynomial1[i]);
          arrayOfGF2Polynomial2[j].addToThis(arrayOfGF2Polynomial2[i]);
        } 
      } 
    } 
    return arrayOfGF2Polynomial2;
  }
  
  public final GF2nElement convert(GF2nElement paramGF2nElement, GF2nField paramGF2nField) throws RuntimeException {
    if (paramGF2nField == this)
      return (GF2nElement)paramGF2nElement.clone(); 
    if (this.fieldPolynomial.equals(paramGF2nField.fieldPolynomial))
      return (GF2nElement)paramGF2nElement.clone(); 
    if (this.mDegree != paramGF2nField.mDegree)
      throw new RuntimeException("GF2nField.convert: B1 has a different degree and thus cannot be coverted to!"); 
    int i = this.fields.indexOf(paramGF2nField);
    if (i == -1) {
      computeCOBMatrix(paramGF2nField);
      i = this.fields.indexOf(paramGF2nField);
    } 
    GF2Polynomial[] arrayOfGF2Polynomial = this.matrices.elementAt(i);
    GF2nElement gF2nElement = (GF2nElement)paramGF2nElement.clone();
    if (gF2nElement instanceof GF2nONBElement)
      ((GF2nONBElement)gF2nElement).reverseOrder(); 
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.mDegree, gF2nElement.toFlexiBigInt());
    gF2Polynomial1.expandN(this.mDegree);
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree);
    for (i = 0; i < this.mDegree; i++) {
      if (gF2Polynomial1.vectorMult(arrayOfGF2Polynomial[i]))
        gF2Polynomial2.setBit(this.mDegree - 1 - i); 
    } 
    if (paramGF2nField instanceof GF2nPolynomialField)
      return new GF2nPolynomialElement((GF2nPolynomialField)paramGF2nField, gF2Polynomial2); 
    if (paramGF2nField instanceof GF2nONBField) {
      GF2nONBElement gF2nONBElement = new GF2nONBElement((GF2nONBField)paramGF2nField, gF2Polynomial2.toFlexiBigInt());
      gF2nONBElement.reverseOrder();
      return gF2nONBElement;
    } 
    throw new RuntimeException("GF2nField.convert: B1 must be an instance of GF2nPolynomialField or GF2nONBField!");
  }
}
