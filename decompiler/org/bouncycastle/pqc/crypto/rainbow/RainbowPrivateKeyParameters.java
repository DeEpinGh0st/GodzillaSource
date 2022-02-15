package org.bouncycastle.pqc.crypto.rainbow;

public class RainbowPrivateKeyParameters extends RainbowKeyParameters {
  private short[][] A1inv;
  
  private short[] b1;
  
  private short[][] A2inv;
  
  private short[] b2;
  
  private int[] vi;
  
  private Layer[] layers;
  
  public RainbowPrivateKeyParameters(short[][] paramArrayOfshort1, short[] paramArrayOfshort2, short[][] paramArrayOfshort3, short[] paramArrayOfshort4, int[] paramArrayOfint, Layer[] paramArrayOfLayer) {
    super(true, paramArrayOfint[paramArrayOfint.length - 1] - paramArrayOfint[0]);
    this.A1inv = paramArrayOfshort1;
    this.b1 = paramArrayOfshort2;
    this.A2inv = paramArrayOfshort3;
    this.b2 = paramArrayOfshort4;
    this.vi = paramArrayOfint;
    this.layers = paramArrayOfLayer;
  }
  
  public short[] getB1() {
    return this.b1;
  }
  
  public short[][] getInvA1() {
    return this.A1inv;
  }
  
  public short[] getB2() {
    return this.b2;
  }
  
  public short[][] getInvA2() {
    return this.A2inv;
  }
  
  public Layer[] getLayers() {
    return this.layers;
  }
  
  public int[] getVi() {
    return this.vi;
  }
}
