package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

public class McElieceCCA2KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2";
  
  private McElieceCCA2KeyGenerationParameters mcElieceCCA2Params;
  
  private int m;
  
  private int n;
  
  private int t;
  
  private int fieldPoly;
  
  private SecureRandom random;
  
  private boolean initialized = false;
  
  private void initializeDefault() {
    McElieceCCA2KeyGenerationParameters mcElieceCCA2KeyGenerationParameters = new McElieceCCA2KeyGenerationParameters(new SecureRandom(), new McElieceCCA2Parameters());
    init(mcElieceCCA2KeyGenerationParameters);
  }
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.mcElieceCCA2Params = (McElieceCCA2KeyGenerationParameters)paramKeyGenerationParameters;
    this.random = new SecureRandom();
    this.m = this.mcElieceCCA2Params.getParameters().getM();
    this.n = this.mcElieceCCA2Params.getParameters().getN();
    this.t = this.mcElieceCCA2Params.getParameters().getT();
    this.fieldPoly = this.mcElieceCCA2Params.getParameters().getFieldPoly();
    this.initialized = true;
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    if (!this.initialized)
      initializeDefault(); 
    GF2mField gF2mField = new GF2mField(this.m, this.fieldPoly);
    PolynomialGF2mSmallM polynomialGF2mSmallM = new PolynomialGF2mSmallM(gF2mField, this.t, 'I', this.random);
    GF2Matrix gF2Matrix1 = GoppaCode.createCanonicalCheckMatrix(gF2mField, polynomialGF2mSmallM);
    GoppaCode.MaMaPe maMaPe = GoppaCode.computeSystematicForm(gF2Matrix1, this.random);
    GF2Matrix gF2Matrix2 = maMaPe.getSecondMatrix();
    Permutation permutation = maMaPe.getPermutation();
    GF2Matrix gF2Matrix3 = (GF2Matrix)gF2Matrix2.computeTranspose();
    int i = gF2Matrix3.getNumRows();
    McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters = new McElieceCCA2PublicKeyParameters(this.n, this.t, gF2Matrix3, this.mcElieceCCA2Params.getParameters().getDigest());
    McElieceCCA2PrivateKeyParameters mcElieceCCA2PrivateKeyParameters = new McElieceCCA2PrivateKeyParameters(this.n, i, gF2mField, polynomialGF2mSmallM, permutation, this.mcElieceCCA2Params.getParameters().getDigest());
    return new AsymmetricCipherKeyPair(mcElieceCCA2PublicKeyParameters, mcElieceCCA2PrivateKeyParameters);
  }
}
