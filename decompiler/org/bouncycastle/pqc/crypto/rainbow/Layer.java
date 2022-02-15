package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.util.Arrays;

public class Layer {
  private int vi;
  
  private int viNext;
  
  private int oi;
  
  private short[][][] coeff_alpha;
  
  private short[][][] coeff_beta;
  
  private short[][] coeff_gamma;
  
  private short[] coeff_eta;
  
  public Layer(byte paramByte1, byte paramByte2, short[][][] paramArrayOfshort1, short[][][] paramArrayOfshort2, short[][] paramArrayOfshort, short[] paramArrayOfshort3) {
    this.vi = paramByte1 & 0xFF;
    this.viNext = paramByte2 & 0xFF;
    this.oi = this.viNext - this.vi;
    this.coeff_alpha = paramArrayOfshort1;
    this.coeff_beta = paramArrayOfshort2;
    this.coeff_gamma = paramArrayOfshort;
    this.coeff_eta = paramArrayOfshort3;
  }
  
  public Layer(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    this.vi = paramInt1;
    this.viNext = paramInt2;
    this.oi = paramInt2 - paramInt1;
    this.coeff_alpha = new short[this.oi][this.oi][this.vi];
    this.coeff_beta = new short[this.oi][this.vi][this.vi];
    this.coeff_gamma = new short[this.oi][this.viNext];
    this.coeff_eta = new short[this.oi];
    int i = this.oi;
    byte b;
    for (b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < this.oi; b1++) {
        for (byte b2 = 0; b2 < this.vi; b2++)
          this.coeff_alpha[b][b1][b2] = (short)(paramSecureRandom.nextInt() & 0xFF); 
      } 
    } 
    for (b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < this.vi; b1++) {
        for (byte b2 = 0; b2 < this.vi; b2++)
          this.coeff_beta[b][b1][b2] = (short)(paramSecureRandom.nextInt() & 0xFF); 
      } 
    } 
    for (b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < this.viNext; b1++)
        this.coeff_gamma[b][b1] = (short)(paramSecureRandom.nextInt() & 0xFF); 
    } 
    for (b = 0; b < i; b++)
      this.coeff_eta[b] = (short)(paramSecureRandom.nextInt() & 0xFF); 
  }
  
  public short[][] plugInVinegars(short[] paramArrayOfshort) {
    short s = 0;
    short[][] arrayOfShort = new short[this.oi][this.oi + 1];
    short[] arrayOfShort1 = new short[this.oi];
    byte b;
    for (b = 0; b < this.oi; b++) {
      for (byte b1 = 0; b1 < this.vi; b1++) {
        for (byte b2 = 0; b2 < this.vi; b2++) {
          s = GF2Field.multElem(this.coeff_beta[b][b1][b2], paramArrayOfshort[b1]);
          s = GF2Field.multElem(s, paramArrayOfshort[b2]);
          arrayOfShort1[b] = GF2Field.addElem(arrayOfShort1[b], s);
        } 
      } 
    } 
    for (b = 0; b < this.oi; b++) {
      for (byte b1 = 0; b1 < this.oi; b1++) {
        for (byte b2 = 0; b2 < this.vi; b2++) {
          s = GF2Field.multElem(this.coeff_alpha[b][b1][b2], paramArrayOfshort[b2]);
          arrayOfShort[b][b1] = GF2Field.addElem(arrayOfShort[b][b1], s);
        } 
      } 
    } 
    for (b = 0; b < this.oi; b++) {
      for (byte b1 = 0; b1 < this.vi; b1++) {
        s = GF2Field.multElem(this.coeff_gamma[b][b1], paramArrayOfshort[b1]);
        arrayOfShort1[b] = GF2Field.addElem(arrayOfShort1[b], s);
      } 
    } 
    for (b = 0; b < this.oi; b++) {
      for (int i = this.vi; i < this.viNext; i++)
        arrayOfShort[b][i - this.vi] = GF2Field.addElem(this.coeff_gamma[b][i], arrayOfShort[b][i - this.vi]); 
    } 
    for (b = 0; b < this.oi; b++)
      arrayOfShort1[b] = GF2Field.addElem(arrayOfShort1[b], this.coeff_eta[b]); 
    for (b = 0; b < this.oi; b++)
      arrayOfShort[b][this.oi] = arrayOfShort1[b]; 
    return arrayOfShort;
  }
  
  public int getVi() {
    return this.vi;
  }
  
  public int getViNext() {
    return this.viNext;
  }
  
  public int getOi() {
    return this.oi;
  }
  
  public short[][][] getCoeffAlpha() {
    return this.coeff_alpha;
  }
  
  public short[][][] getCoeffBeta() {
    return this.coeff_beta;
  }
  
  public short[][] getCoeffGamma() {
    return this.coeff_gamma;
  }
  
  public short[] getCoeffEta() {
    return this.coeff_eta;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Layer))
      return false; 
    Layer layer = (Layer)paramObject;
    return (this.vi == layer.getVi() && this.viNext == layer.getViNext() && this.oi == layer.getOi() && RainbowUtil.equals(this.coeff_alpha, layer.getCoeffAlpha()) && RainbowUtil.equals(this.coeff_beta, layer.getCoeffBeta()) && RainbowUtil.equals(this.coeff_gamma, layer.getCoeffGamma()) && RainbowUtil.equals(this.coeff_eta, layer.getCoeffEta()));
  }
  
  public int hashCode() {
    null = this.vi;
    null = null * 37 + this.viNext;
    null = null * 37 + this.oi;
    null = null * 37 + Arrays.hashCode(this.coeff_alpha);
    null = null * 37 + Arrays.hashCode(this.coeff_beta);
    null = null * 37 + Arrays.hashCode(this.coeff_gamma);
    return null * 37 + Arrays.hashCode(this.coeff_eta);
  }
}
