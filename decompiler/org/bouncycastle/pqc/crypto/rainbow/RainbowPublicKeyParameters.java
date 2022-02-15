package org.bouncycastle.pqc.crypto.rainbow;

public class RainbowPublicKeyParameters extends RainbowKeyParameters {
  private short[][] coeffquadratic;
  
  private short[][] coeffsingular;
  
  private short[] coeffscalar;
  
  public RainbowPublicKeyParameters(int paramInt, short[][] paramArrayOfshort1, short[][] paramArrayOfshort2, short[] paramArrayOfshort) {
    super(false, paramInt);
    this.coeffquadratic = paramArrayOfshort1;
    this.coeffsingular = paramArrayOfshort2;
    this.coeffscalar = paramArrayOfshort;
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
