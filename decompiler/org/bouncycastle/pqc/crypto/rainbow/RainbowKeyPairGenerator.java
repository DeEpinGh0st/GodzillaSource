package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.util.ComputeInField;
import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;

public class RainbowKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private boolean initialized = false;
  
  private SecureRandom sr;
  
  private RainbowKeyGenerationParameters rainbowParams;
  
  private short[][] A1;
  
  private short[][] A1inv;
  
  private short[] b1;
  
  private short[][] A2;
  
  private short[][] A2inv;
  
  private short[] b2;
  
  private int numOfLayers;
  
  private Layer[] layers;
  
  private int[] vi;
  
  private short[][] pub_quadratic;
  
  private short[][] pub_singular;
  
  private short[] pub_scalar;
  
  public AsymmetricCipherKeyPair genKeyPair() {
    if (!this.initialized)
      initializeDefault(); 
    keygen();
    RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowPrivateKeyParameters(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers);
    RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.vi[this.vi.length - 1] - this.vi[0], this.pub_quadratic, this.pub_singular, this.pub_scalar);
    return new AsymmetricCipherKeyPair(rainbowPublicKeyParameters, rainbowPrivateKeyParameters);
  }
  
  public void initialize(KeyGenerationParameters paramKeyGenerationParameters) {
    this.rainbowParams = (RainbowKeyGenerationParameters)paramKeyGenerationParameters;
    this.sr = this.rainbowParams.getRandom();
    this.vi = this.rainbowParams.getParameters().getVi();
    this.numOfLayers = this.rainbowParams.getParameters().getNumOfLayers();
    this.initialized = true;
  }
  
  private void initializeDefault() {
    RainbowKeyGenerationParameters rainbowKeyGenerationParameters = new RainbowKeyGenerationParameters(new SecureRandom(), new RainbowParameters());
    initialize(rainbowKeyGenerationParameters);
  }
  
  private void keygen() {
    generateL1();
    generateL2();
    generateF();
    computePublicKey();
  }
  
  private void generateL1() {
    int i = this.vi[this.vi.length - 1] - this.vi[0];
    this.A1 = new short[i][i];
    this.A1inv = (short[][])null;
    ComputeInField computeInField = new ComputeInField();
    while (this.A1inv == null) {
      for (byte b1 = 0; b1 < i; b1++) {
        for (byte b2 = 0; b2 < i; b2++)
          this.A1[b1][b2] = (short)(this.sr.nextInt() & 0xFF); 
      } 
      this.A1inv = computeInField.inverse(this.A1);
    } 
    this.b1 = new short[i];
    for (byte b = 0; b < i; b++)
      this.b1[b] = (short)(this.sr.nextInt() & 0xFF); 
  }
  
  private void generateL2() {
    int i = this.vi[this.vi.length - 1];
    this.A2 = new short[i][i];
    this.A2inv = (short[][])null;
    ComputeInField computeInField = new ComputeInField();
    while (this.A2inv == null) {
      for (byte b1 = 0; b1 < i; b1++) {
        for (byte b2 = 0; b2 < i; b2++)
          this.A2[b1][b2] = (short)(this.sr.nextInt() & 0xFF); 
      } 
      this.A2inv = computeInField.inverse(this.A2);
    } 
    this.b2 = new short[i];
    for (byte b = 0; b < i; b++)
      this.b2[b] = (short)(this.sr.nextInt() & 0xFF); 
  }
  
  private void generateF() {
    this.layers = new Layer[this.numOfLayers];
    for (byte b = 0; b < this.numOfLayers; b++)
      this.layers[b] = new Layer(this.vi[b], this.vi[b + 1], this.sr); 
  }
  
  private void computePublicKey() {
    ComputeInField computeInField = new ComputeInField();
    int i = this.vi[this.vi.length - 1] - this.vi[0];
    int j = this.vi[this.vi.length - 1];
    short[][][] arrayOfShort1 = new short[i][j][j];
    this.pub_singular = new short[i][j];
    this.pub_scalar = new short[i];
    int k = 0;
    int m = 0;
    int n = 0;
    short[] arrayOfShort2 = new short[j];
    short s = 0;
    for (byte b1 = 0; b1 < this.layers.length; b1++) {
      short[][][] arrayOfShort5 = this.layers[b1].getCoeffAlpha();
      short[][][] arrayOfShort6 = this.layers[b1].getCoeffBeta();
      short[][] arrayOfShort7 = this.layers[b1].getCoeffGamma();
      short[] arrayOfShort8 = this.layers[b1].getCoeffEta();
      k = (arrayOfShort5[0]).length;
      m = (arrayOfShort6[0]).length;
      for (byte b = 0; b < k; b++) {
        byte b3;
        for (b3 = 0; b3 < k; b3++) {
          for (byte b4 = 0; b4 < m; b4++) {
            arrayOfShort2 = computeInField.multVect(arrayOfShort5[b][b3][b4], this.A2[b3 + m]);
            arrayOfShort1[n + b] = computeInField.addSquareMatrix(arrayOfShort1[n + b], computeInField.multVects(arrayOfShort2, this.A2[b4]));
            arrayOfShort2 = computeInField.multVect(this.b2[b4], arrayOfShort2);
            this.pub_singular[n + b] = computeInField.addVect(arrayOfShort2, this.pub_singular[n + b]);
            arrayOfShort2 = computeInField.multVect(arrayOfShort5[b][b3][b4], this.A2[b4]);
            arrayOfShort2 = computeInField.multVect(this.b2[b3 + m], arrayOfShort2);
            this.pub_singular[n + b] = computeInField.addVect(arrayOfShort2, this.pub_singular[n + b]);
            s = GF2Field.multElem(arrayOfShort5[b][b3][b4], this.b2[b3 + m]);
            this.pub_scalar[n + b] = GF2Field.addElem(this.pub_scalar[n + b], GF2Field.multElem(s, this.b2[b4]));
          } 
        } 
        for (b3 = 0; b3 < m; b3++) {
          for (byte b4 = 0; b4 < m; b4++) {
            arrayOfShort2 = computeInField.multVect(arrayOfShort6[b][b3][b4], this.A2[b3]);
            arrayOfShort1[n + b] = computeInField.addSquareMatrix(arrayOfShort1[n + b], computeInField.multVects(arrayOfShort2, this.A2[b4]));
            arrayOfShort2 = computeInField.multVect(this.b2[b4], arrayOfShort2);
            this.pub_singular[n + b] = computeInField.addVect(arrayOfShort2, this.pub_singular[n + b]);
            arrayOfShort2 = computeInField.multVect(arrayOfShort6[b][b3][b4], this.A2[b4]);
            arrayOfShort2 = computeInField.multVect(this.b2[b3], arrayOfShort2);
            this.pub_singular[n + b] = computeInField.addVect(arrayOfShort2, this.pub_singular[n + b]);
            s = GF2Field.multElem(arrayOfShort6[b][b3][b4], this.b2[b3]);
            this.pub_scalar[n + b] = GF2Field.addElem(this.pub_scalar[n + b], GF2Field.multElem(s, this.b2[b4]));
          } 
        } 
        for (b3 = 0; b3 < m + k; b3++) {
          arrayOfShort2 = computeInField.multVect(arrayOfShort7[b][b3], this.A2[b3]);
          this.pub_singular[n + b] = computeInField.addVect(arrayOfShort2, this.pub_singular[n + b]);
          this.pub_scalar[n + b] = GF2Field.addElem(this.pub_scalar[n + b], GF2Field.multElem(arrayOfShort7[b][b3], this.b2[b3]));
        } 
        this.pub_scalar[n + b] = GF2Field.addElem(this.pub_scalar[n + b], arrayOfShort8[b]);
      } 
      n += k;
    } 
    short[][][] arrayOfShort3 = new short[i][j][j];
    short[][] arrayOfShort = new short[i][j];
    short[] arrayOfShort4 = new short[i];
    for (byte b2 = 0; b2 < i; b2++) {
      for (byte b = 0; b < this.A1.length; b++) {
        arrayOfShort3[b2] = computeInField.addSquareMatrix(arrayOfShort3[b2], computeInField.multMatrix(this.A1[b2][b], arrayOfShort1[b]));
        arrayOfShort[b2] = computeInField.addVect(arrayOfShort[b2], computeInField.multVect(this.A1[b2][b], this.pub_singular[b]));
        arrayOfShort4[b2] = GF2Field.addElem(arrayOfShort4[b2], GF2Field.multElem(this.A1[b2][b], this.pub_scalar[b]));
      } 
      arrayOfShort4[b2] = GF2Field.addElem(arrayOfShort4[b2], this.b1[b2]);
    } 
    arrayOfShort1 = arrayOfShort3;
    this.pub_singular = arrayOfShort;
    this.pub_scalar = arrayOfShort4;
    compactPublicKey(arrayOfShort1);
  }
  
  private void compactPublicKey(short[][][] paramArrayOfshort) {
    int i = paramArrayOfshort.length;
    int j = (paramArrayOfshort[0]).length;
    int k = j * (j + 1) / 2;
    this.pub_quadratic = new short[i][k];
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      b1 = 0;
      for (byte b = 0; b < j; b++) {
        for (byte b3 = b; b3 < j; b3++) {
          if (b3 == b) {
            this.pub_quadratic[b2][b1] = paramArrayOfshort[b2][b][b3];
          } else {
            this.pub_quadratic[b2][b1] = GF2Field.addElem(paramArrayOfshort[b2][b][b3], paramArrayOfshort[b2][b3][b]);
          } 
          b1++;
        } 
      } 
    } 
  }
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    initialize(paramKeyGenerationParameters);
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    return genKeyPair();
  }
}
