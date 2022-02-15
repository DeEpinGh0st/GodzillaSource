package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;

public class McElieceCCA2PublicKeyParameters extends McElieceCCA2KeyParameters {
  private int n;
  
  private int t;
  
  private GF2Matrix matrixG;
  
  public McElieceCCA2PublicKeyParameters(int paramInt1, int paramInt2, GF2Matrix paramGF2Matrix, String paramString) {
    super(false, paramString);
    this.n = paramInt1;
    this.t = paramInt2;
    this.matrixG = new GF2Matrix(paramGF2Matrix);
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getT() {
    return this.t;
  }
  
  public GF2Matrix getG() {
    return this.matrixG;
  }
  
  public int getK() {
    return this.matrixG.getNumRows();
  }
}
