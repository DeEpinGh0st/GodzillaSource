package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Matrix;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

public class McElieceKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
  
  private McElieceKeyGenerationParameters mcElieceParams;
  
  private int m;
  
  private int n;
  
  private int t;
  
  private int fieldPoly;
  
  private SecureRandom random;
  
  private boolean initialized = false;
  
  private void initializeDefault() {
    McElieceKeyGenerationParameters mcElieceKeyGenerationParameters = new McElieceKeyGenerationParameters(new SecureRandom(), new McElieceParameters());
    initialize(mcElieceKeyGenerationParameters);
  }
  
  private void initialize(KeyGenerationParameters paramKeyGenerationParameters) {
    this.mcElieceParams = (McElieceKeyGenerationParameters)paramKeyGenerationParameters;
    this.random = new SecureRandom();
    this.m = this.mcElieceParams.getParameters().getM();
    this.n = this.mcElieceParams.getParameters().getN();
    this.t = this.mcElieceParams.getParameters().getT();
    this.fieldPoly = this.mcElieceParams.getParameters().getFieldPoly();
    this.initialized = true;
  }
  
  private AsymmetricCipherKeyPair genKeyPair() {
    if (!this.initialized)
      initializeDefault(); 
    GF2mField gF2mField = new GF2mField(this.m, this.fieldPoly);
    PolynomialGF2mSmallM polynomialGF2mSmallM = new PolynomialGF2mSmallM(gF2mField, this.t, 'I', this.random);
    PolynomialRingGF2m polynomialRingGF2m = new PolynomialRingGF2m(gF2mField, polynomialGF2mSmallM);
    PolynomialGF2mSmallM[] arrayOfPolynomialGF2mSmallM = polynomialRingGF2m.getSquareRootMatrix();
    GF2Matrix gF2Matrix1 = GoppaCode.createCanonicalCheckMatrix(gF2mField, polynomialGF2mSmallM);
    GoppaCode.MaMaPe maMaPe = GoppaCode.computeSystematicForm(gF2Matrix1, this.random);
    GF2Matrix gF2Matrix2 = maMaPe.getSecondMatrix();
    Permutation permutation1 = maMaPe.getPermutation();
    GF2Matrix gF2Matrix3 = (GF2Matrix)gF2Matrix2.computeTranspose();
    GF2Matrix gF2Matrix4 = gF2Matrix3.extendLeftCompactForm();
    int i = gF2Matrix3.getNumRows();
    GF2Matrix[] arrayOfGF2Matrix = GF2Matrix.createRandomRegularMatrixAndItsInverse(i, this.random);
    Permutation permutation2 = new Permutation(this.n, this.random);
    GF2Matrix gF2Matrix5 = (GF2Matrix)arrayOfGF2Matrix[0].rightMultiply((Matrix)gF2Matrix4);
    gF2Matrix5 = (GF2Matrix)gF2Matrix5.rightMultiply(permutation2);
    McEliecePublicKeyParameters mcEliecePublicKeyParameters = new McEliecePublicKeyParameters(this.n, this.t, gF2Matrix5);
    McEliecePrivateKeyParameters mcEliecePrivateKeyParameters = new McEliecePrivateKeyParameters(this.n, i, gF2mField, polynomialGF2mSmallM, permutation1, permutation2, arrayOfGF2Matrix[1]);
    return new AsymmetricCipherKeyPair(mcEliecePublicKeyParameters, mcEliecePrivateKeyParameters);
  }
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    initialize(paramKeyGenerationParameters);
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    return genKeyPair();
  }
}
