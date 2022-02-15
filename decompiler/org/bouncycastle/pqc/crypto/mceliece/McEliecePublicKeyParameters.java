package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;

public class McEliecePublicKeyParameters extends McElieceKeyParameters {
  private int n;
  
  private int t;
  
  private GF2Matrix g;
  
  public McEliecePublicKeyParameters(int paramInt1, int paramInt2, GF2Matrix paramGF2Matrix) {
    super(false, null);
    this.n = paramInt1;
    this.t = paramInt2;
    this.g = new GF2Matrix(paramGF2Matrix);
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getT() {
    return this.t;
  }
  
  public GF2Matrix getG() {
    return this.g;
  }
  
  public int getK() {
    return this.g.getNumRows();
  }
}
