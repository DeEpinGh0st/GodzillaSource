package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

public class McEliecePrivateKeyParameters extends McElieceKeyParameters {
  private String oid;
  
  private int n;
  
  private int k;
  
  private GF2mField field;
  
  private PolynomialGF2mSmallM goppaPoly;
  
  private GF2Matrix sInv;
  
  private Permutation p1;
  
  private Permutation p2;
  
  private GF2Matrix h;
  
  private PolynomialGF2mSmallM[] qInv;
  
  public McEliecePrivateKeyParameters(int paramInt1, int paramInt2, GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM, Permutation paramPermutation1, Permutation paramPermutation2, GF2Matrix paramGF2Matrix) {
    super(true, null);
    this.k = paramInt2;
    this.n = paramInt1;
    this.field = paramGF2mField;
    this.goppaPoly = paramPolynomialGF2mSmallM;
    this.sInv = paramGF2Matrix;
    this.p1 = paramPermutation1;
    this.p2 = paramPermutation2;
    this.h = GoppaCode.createCanonicalCheckMatrix(paramGF2mField, paramPolynomialGF2mSmallM);
    PolynomialRingGF2m polynomialRingGF2m = new PolynomialRingGF2m(paramGF2mField, paramPolynomialGF2mSmallM);
    this.qInv = polynomialRingGF2m.getSquareRootMatrix();
  }
  
  public McEliecePrivateKeyParameters(int paramInt1, int paramInt2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, byte[] paramArrayOfbyte5, byte[] paramArrayOfbyte6, byte[][] paramArrayOfbyte) {
    super(true, null);
    this.n = paramInt1;
    this.k = paramInt2;
    this.field = new GF2mField(paramArrayOfbyte1);
    this.goppaPoly = new PolynomialGF2mSmallM(this.field, paramArrayOfbyte2);
    this.sInv = new GF2Matrix(paramArrayOfbyte3);
    this.p1 = new Permutation(paramArrayOfbyte4);
    this.p2 = new Permutation(paramArrayOfbyte5);
    this.h = new GF2Matrix(paramArrayOfbyte6);
    this.qInv = new PolynomialGF2mSmallM[paramArrayOfbyte.length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      this.qInv[b] = new PolynomialGF2mSmallM(this.field, paramArrayOfbyte[b]); 
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getK() {
    return this.k;
  }
  
  public GF2mField getField() {
    return this.field;
  }
  
  public PolynomialGF2mSmallM getGoppaPoly() {
    return this.goppaPoly;
  }
  
  public GF2Matrix getSInv() {
    return this.sInv;
  }
  
  public Permutation getP1() {
    return this.p1;
  }
  
  public Permutation getP2() {
    return this.p2;
  }
  
  public GF2Matrix getH() {
    return this.h;
  }
  
  public PolynomialGF2mSmallM[] getQInv() {
    return this.qInv;
  }
}
