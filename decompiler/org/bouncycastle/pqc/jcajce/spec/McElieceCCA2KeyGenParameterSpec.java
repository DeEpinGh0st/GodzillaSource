package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2;

public class McElieceCCA2KeyGenParameterSpec implements AlgorithmParameterSpec {
  public static final String SHA1 = "SHA-1";
  
  public static final String SHA224 = "SHA-224";
  
  public static final String SHA256 = "SHA-256";
  
  public static final String SHA384 = "SHA-384";
  
  public static final String SHA512 = "SHA-512";
  
  public static final int DEFAULT_M = 11;
  
  public static final int DEFAULT_T = 50;
  
  private final int m;
  
  private final int t;
  
  private final int n;
  
  private int fieldPoly;
  
  private final String digest;
  
  public McElieceCCA2KeyGenParameterSpec() {
    this(11, 50, "SHA-256");
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt) {
    this(paramInt, "SHA-256");
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt, String paramString) {
    if (paramInt < 1)
      throw new IllegalArgumentException("key size must be positive"); 
    byte b = 0;
    int i = 1;
    while (i < paramInt) {
      i <<= 1;
      b++;
    } 
    this.t = (i >>> 1) / b;
    this.m = b;
    this.n = i;
    this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(b);
    this.digest = paramString;
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt1, int paramInt2) {
    this(paramInt1, paramInt2, "SHA-256");
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt1, int paramInt2, String paramString) {
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
    this.digest = paramString;
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt1, int paramInt2, int paramInt3) {
    this(paramInt1, paramInt2, paramInt3, "SHA-256");
  }
  
  public McElieceCCA2KeyGenParameterSpec(int paramInt1, int paramInt2, int paramInt3, String paramString) {
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
    this.digest = paramString;
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
  
  public String getDigest() {
    return this.digest;
  }
}
