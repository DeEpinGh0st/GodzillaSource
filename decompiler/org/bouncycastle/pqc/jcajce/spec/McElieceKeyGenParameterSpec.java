package org.bouncycastle.pqc.jcajce.spec;

import java.security.InvalidParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2;

public class McElieceKeyGenParameterSpec implements AlgorithmParameterSpec {
  public static final int DEFAULT_M = 11;
  
  public static final int DEFAULT_T = 50;
  
  private int m;
  
  private int t;
  
  private int n;
  
  private int fieldPoly;
  
  public McElieceKeyGenParameterSpec() {
    this(11, 50);
  }
  
  public McElieceKeyGenParameterSpec(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("key size must be positive"); 
    this.m = 0;
    this.n = 1;
    while (this.n < paramInt) {
      this.n <<= 1;
      this.m++;
    } 
    this.t = this.n >>> 1;
    this.t /= this.m;
    this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(this.m);
  }
  
  public McElieceKeyGenParameterSpec(int paramInt1, int paramInt2) throws InvalidParameterException {
    if (paramInt1 < 1)
      throw new IllegalArgumentException("m must be positive"); 
    if (paramInt1 > 32)
      throw new IllegalArgumentException("m is too large"); 
    this.m = paramInt1;
    this.n = 1 << paramInt1;
    if (paramInt2 < 0)
      throw new IllegalArgumentException("t must be positive"); 
    if (paramInt2 > this.n)
      throw new IllegalArgumentException("t must be less than n = 2^m"); 
    this.t = paramInt2;
    this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(paramInt1);
  }
  
  public McElieceKeyGenParameterSpec(int paramInt1, int paramInt2, int paramInt3) {
    this.m = paramInt1;
    if (paramInt1 < 1)
      throw new IllegalArgumentException("m must be positive"); 
    if (paramInt1 > 32)
      throw new IllegalArgumentException(" m is too large"); 
    this.n = 1 << paramInt1;
    this.t = paramInt2;
    if (paramInt2 < 0)
      throw new IllegalArgumentException("t must be positive"); 
    if (paramInt2 > this.n)
      throw new IllegalArgumentException("t must be less than n = 2^m"); 
    if (PolynomialRingGF2.degree(paramInt3) == paramInt1 && PolynomialRingGF2.isIrreducible(paramInt3)) {
      this.fieldPoly = paramInt3;
    } else {
      throw new IllegalArgumentException("polynomial is not a field polynomial for GF(2^m)");
    } 
  }
  
  public int getM() {
    return this.m;
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getT() {
    return this.t;
  }
  
  public int getFieldPoly() {
    return this.fieldPoly;
  }
}
