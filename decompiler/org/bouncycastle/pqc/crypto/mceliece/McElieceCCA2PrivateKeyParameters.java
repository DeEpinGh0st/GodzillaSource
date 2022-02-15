package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

public class McElieceCCA2PrivateKeyParameters extends McElieceCCA2KeyParameters {
  private int n;
  
  private int k;
  
  private GF2mField field;
  
  private PolynomialGF2mSmallM goppaPoly;
  
  private Permutation p;
  
  private GF2Matrix h;
  
  private PolynomialGF2mSmallM[] qInv;
  
  public McElieceCCA2PrivateKeyParameters(int paramInt1, int paramInt2, GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM, Permutation paramPermutation, String paramString) {
    this(paramInt1, paramInt2, paramGF2mField, paramPolynomialGF2mSmallM, GoppaCode.createCanonicalCheckMatrix(paramGF2mField, paramPolynomialGF2mSmallM), paramPermutation, paramString);
  }
  
  public McElieceCCA2PrivateKeyParameters(int paramInt1, int paramInt2, GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM, GF2Matrix paramGF2Matrix, Permutation paramPermutation, String paramString) {
    super(true, paramString);
    this.n = paramInt1;
    this.k = paramInt2;
    this.field = paramGF2mField;
    this.goppaPoly = paramPolynomialGF2mSmallM;
    this.h = paramGF2Matrix;
    this.p = paramPermutation;
    PolynomialRingGF2m polynomialRingGF2m = new PolynomialRingGF2m(paramGF2mField, paramPolynomialGF2mSmallM);
    this.qInv = polynomialRingGF2m.getSquareRootMatrix();
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getK() {
    return this.k;
  }
  
  public int getT() {
    return this.goppaPoly.getDegree();
  }
  
  public GF2mField getField() {
    return this.field;
  }
  
  public PolynomialGF2mSmallM getGoppaPoly() {
    return this.goppaPoly;
  }
  
  public Permutation getP() {
    return this.p;
  }
  
  public GF2Matrix getH() {
    return this.h;
  }
  
  public PolynomialGF2mSmallM[] getQInv() {
    return this.qInv;
  }
}
