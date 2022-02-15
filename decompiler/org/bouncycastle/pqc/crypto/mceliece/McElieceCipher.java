package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageEncryptor;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.Vector;

public class McElieceCipher implements MessageEncryptor {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
  
  private SecureRandom sr;
  
  private int n;
  
  private int k;
  
  private int t;
  
  public int maxPlainTextSize;
  
  public int cipherTextSize;
  
  private McElieceKeyParameters key;
  
  private boolean forEncryption;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forEncryption = paramBoolean;
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.sr = parametersWithRandom.getRandom();
        this.key = (McEliecePublicKeyParameters)parametersWithRandom.getParameters();
        initCipherEncrypt((McEliecePublicKeyParameters)this.key);
      } else {
        this.sr = new SecureRandom();
        this.key = (McEliecePublicKeyParameters)paramCipherParameters;
        initCipherEncrypt((McEliecePublicKeyParameters)this.key);
      } 
    } else {
      this.key = (McEliecePrivateKeyParameters)paramCipherParameters;
      initCipherDecrypt((McEliecePrivateKeyParameters)this.key);
    } 
  }
  
  public int getKeySize(McElieceKeyParameters paramMcElieceKeyParameters) {
    if (paramMcElieceKeyParameters instanceof McEliecePublicKeyParameters)
      return ((McEliecePublicKeyParameters)paramMcElieceKeyParameters).getN(); 
    if (paramMcElieceKeyParameters instanceof McEliecePrivateKeyParameters)
      return ((McEliecePrivateKeyParameters)paramMcElieceKeyParameters).getN(); 
    throw new IllegalArgumentException("unsupported type");
  }
  
  private void initCipherEncrypt(McEliecePublicKeyParameters paramMcEliecePublicKeyParameters) {
    this.sr = (this.sr != null) ? this.sr : new SecureRandom();
    this.n = paramMcEliecePublicKeyParameters.getN();
    this.k = paramMcEliecePublicKeyParameters.getK();
    this.t = paramMcEliecePublicKeyParameters.getT();
    this.cipherTextSize = this.n >> 3;
    this.maxPlainTextSize = this.k >> 3;
  }
  
  private void initCipherDecrypt(McEliecePrivateKeyParameters paramMcEliecePrivateKeyParameters) {
    this.n = paramMcEliecePrivateKeyParameters.getN();
    this.k = paramMcEliecePrivateKeyParameters.getK();
    this.maxPlainTextSize = this.k >> 3;
    this.cipherTextSize = this.n >> 3;
  }
  
  public byte[] messageEncrypt(byte[] paramArrayOfbyte) {
    if (!this.forEncryption)
      throw new IllegalStateException("cipher initialised for decryption"); 
    GF2Vector gF2Vector1 = computeMessageRepresentative(paramArrayOfbyte);
    GF2Vector gF2Vector2 = new GF2Vector(this.n, this.t, this.sr);
    GF2Matrix gF2Matrix = ((McEliecePublicKeyParameters)this.key).getG();
    Vector vector = gF2Matrix.leftMultiply((Vector)gF2Vector1);
    GF2Vector gF2Vector3 = (GF2Vector)vector.add((Vector)gF2Vector2);
    return gF2Vector3.getEncoded();
  }
  
  private GF2Vector computeMessageRepresentative(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[this.maxPlainTextSize + (((this.k & 0x7) != 0) ? 1 : 0)];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
    arrayOfByte[paramArrayOfbyte.length] = 1;
    return GF2Vector.OS2VP(this.k, arrayOfByte);
  }
  
  public byte[] messageDecrypt(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    if (this.forEncryption)
      throw new IllegalStateException("cipher initialised for decryption"); 
    GF2Vector gF2Vector1 = GF2Vector.OS2VP(this.n, paramArrayOfbyte);
    McEliecePrivateKeyParameters mcEliecePrivateKeyParameters = (McEliecePrivateKeyParameters)this.key;
    GF2mField gF2mField = mcEliecePrivateKeyParameters.getField();
    PolynomialGF2mSmallM polynomialGF2mSmallM = mcEliecePrivateKeyParameters.getGoppaPoly();
    GF2Matrix gF2Matrix1 = mcEliecePrivateKeyParameters.getSInv();
    Permutation permutation1 = mcEliecePrivateKeyParameters.getP1();
    Permutation permutation2 = mcEliecePrivateKeyParameters.getP2();
    GF2Matrix gF2Matrix2 = mcEliecePrivateKeyParameters.getH();
    PolynomialGF2mSmallM[] arrayOfPolynomialGF2mSmallM = mcEliecePrivateKeyParameters.getQInv();
    Permutation permutation3 = permutation1.rightMultiply(permutation2);
    Permutation permutation4 = permutation3.computeInverse();
    GF2Vector gF2Vector2 = (GF2Vector)gF2Vector1.multiply(permutation4);
    GF2Vector gF2Vector3 = (GF2Vector)gF2Matrix2.rightMultiply((Vector)gF2Vector2);
    GF2Vector gF2Vector4 = GoppaCode.syndromeDecode(gF2Vector3, gF2mField, polynomialGF2mSmallM, arrayOfPolynomialGF2mSmallM);
    GF2Vector gF2Vector5 = (GF2Vector)gF2Vector2.add((Vector)gF2Vector4);
    gF2Vector5 = (GF2Vector)gF2Vector5.multiply(permutation1);
    gF2Vector4 = (GF2Vector)gF2Vector4.multiply(permutation3);
    GF2Vector gF2Vector6 = gF2Vector5.extractRightVector(this.k);
    GF2Vector gF2Vector7 = (GF2Vector)gF2Matrix1.leftMultiply((Vector)gF2Vector6);
    return computeMessage(gF2Vector7);
  }
  
  private byte[] computeMessage(GF2Vector paramGF2Vector) throws InvalidCipherTextException {
    byte[] arrayOfByte1 = paramGF2Vector.getEncoded();
    int i;
    for (i = arrayOfByte1.length - 1; i >= 0 && arrayOfByte1[i] == 0; i--);
    if (i < 0 || arrayOfByte1[i] != 1)
      throw new InvalidCipherTextException("Bad Padding: invalid ciphertext"); 
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
}
