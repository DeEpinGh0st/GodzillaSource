package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.KeySpec;

public class RainbowPublicKeySpec implements KeySpec {
  private short[][] coeffquadratic;
  
  private short[][] coeffsingular;
  
  private short[] coeffscalar;
  
  private int docLength;
  
  public RainbowPublicKeySpec(int paramInt, short[][] paramArrayOfshort1, short[][] paramArrayOfshort2, short[] paramArrayOfshort) {
    this.docLength = paramInt;
    this.coeffquadratic = paramArrayOfshort1;
    this.coeffsingular = paramArrayOfshort2;
    this.coeffscalar = paramArrayOfshort;
  }
  
  public int getDocLength() {
    return this.docLength;
  }
  
  public short[][] getCoeffQuadratic() {
    return this.coeffquadratic;
  }
  
  public short[][] getCoeffSingular() {
    return this.coeffsingular;
  }
  
  public short[] getCoeffScalar() {
    return this.coeffscalar;
  }
}
