package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.CramerShoupKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupCoreEngine {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private CramerShoupKeyParameters key;
  
  private SecureRandom random;
  
  private boolean forEncryption;
  
  private String label = null;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters, String paramString) {
    init(paramBoolean, paramCipherParameters);
    this.label = paramString;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    SecureRandom secureRandom = null;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.key = (CramerShoupKeyParameters)parametersWithRandom.getParameters();
      secureRandom = parametersWithRandom.getRandom();
    } else {
      this.key = (CramerShoupKeyParameters)paramCipherParameters;
    } 
    this.random = initSecureRandom(paramBoolean, secureRandom);
    this.forEncryption = paramBoolean;
  }
  
  public int getInputBlockSize() {
    int i = this.key.getParameters().getP().bitLength();
    return this.forEncryption ? ((i + 7) / 8 - 1) : ((i + 7) / 8);
  }
  
  public int getOutputBlockSize() {
    int i = this.key.getParameters().getP().bitLength();
    return this.forEncryption ? ((i + 7) / 8) : ((i + 7) / 8 - 1);
  }
  
  public BigInteger convertInput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte;
    if (paramInt2 > getInputBlockSize() + 1)
      throw new DataLengthException("input too large for Cramer Shoup cipher."); 
    if (paramInt2 == getInputBlockSize() + 1 && this.forEncryption)
      throw new DataLengthException("input too large for Cramer Shoup cipher."); 
    if (paramInt1 != 0 || paramInt2 != paramArrayOfbyte.length) {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    } else {
      arrayOfByte = paramArrayOfbyte;
    } 
    BigInteger bigInteger = new BigInteger(1, arrayOfByte);
    if (bigInteger.compareTo(this.key.getParameters().getP()) >= 0)
      throw new DataLengthException("input too large for Cramer Shoup cipher."); 
    return bigInteger;
  }
  
  public byte[] convertOutput(BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (!this.forEncryption) {
      if (arrayOfByte[0] == 0 && arrayOfByte.length > getOutputBlockSize()) {
        byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
        System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
        return arrayOfByte1;
      } 
      if (arrayOfByte.length < getOutputBlockSize()) {
        byte[] arrayOfByte1 = new byte[getOutputBlockSize()];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte.length, arrayOfByte.length);
        return arrayOfByte1;
      } 
    } else if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  public CramerShoupCiphertext encryptBlock(BigInteger paramBigInteger) {
    CramerShoupCiphertext cramerShoupCiphertext = null;
    if (!this.key.isPrivate() && this.forEncryption && this.key instanceof CramerShoupPublicKeyParameters) {
      CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = (CramerShoupPublicKeyParameters)this.key;
      BigInteger bigInteger1 = cramerShoupPublicKeyParameters.getParameters().getP();
      BigInteger bigInteger2 = cramerShoupPublicKeyParameters.getParameters().getG1();
      BigInteger bigInteger3 = cramerShoupPublicKeyParameters.getParameters().getG2();
      BigInteger bigInteger4 = cramerShoupPublicKeyParameters.getH();
      if (!isValidMessage(paramBigInteger, bigInteger1))
        return cramerShoupCiphertext; 
      BigInteger bigInteger5 = generateRandomElement(bigInteger1, this.random);
      BigInteger bigInteger6 = bigInteger2.modPow(bigInteger5, bigInteger1);
      BigInteger bigInteger7 = bigInteger3.modPow(bigInteger5, bigInteger1);
      BigInteger bigInteger9 = bigInteger4.modPow(bigInteger5, bigInteger1).multiply(paramBigInteger).mod(bigInteger1);
      Digest digest = cramerShoupPublicKeyParameters.getParameters().getH();
      byte[] arrayOfByte1 = bigInteger6.toByteArray();
      digest.update(arrayOfByte1, 0, arrayOfByte1.length);
      byte[] arrayOfByte2 = bigInteger7.toByteArray();
      digest.update(arrayOfByte2, 0, arrayOfByte2.length);
      byte[] arrayOfByte3 = bigInteger9.toByteArray();
      digest.update(arrayOfByte3, 0, arrayOfByte3.length);
      if (this.label != null) {
        byte[] arrayOfByte = this.label.getBytes();
        digest.update(arrayOfByte, 0, arrayOfByte.length);
      } 
      byte[] arrayOfByte4 = new byte[digest.getDigestSize()];
      digest.doFinal(arrayOfByte4, 0);
      BigInteger bigInteger10 = new BigInteger(1, arrayOfByte4);
      BigInteger bigInteger8 = cramerShoupPublicKeyParameters.getC().modPow(bigInteger5, bigInteger1).multiply(cramerShoupPublicKeyParameters.getD().modPow(bigInteger5.multiply(bigInteger10), bigInteger1)).mod(bigInteger1);
      cramerShoupCiphertext = new CramerShoupCiphertext(bigInteger6, bigInteger7, bigInteger9, bigInteger8);
    } 
    return cramerShoupCiphertext;
  }
  
  public BigInteger decryptBlock(CramerShoupCiphertext paramCramerShoupCiphertext) throws CramerShoupCiphertextException {
    BigInteger bigInteger = null;
    if (this.key.isPrivate() && !this.forEncryption && this.key instanceof CramerShoupPrivateKeyParameters) {
      CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters = (CramerShoupPrivateKeyParameters)this.key;
      BigInteger bigInteger1 = cramerShoupPrivateKeyParameters.getParameters().getP();
      Digest digest = cramerShoupPrivateKeyParameters.getParameters().getH();
      byte[] arrayOfByte1 = paramCramerShoupCiphertext.getU1().toByteArray();
      digest.update(arrayOfByte1, 0, arrayOfByte1.length);
      byte[] arrayOfByte2 = paramCramerShoupCiphertext.getU2().toByteArray();
      digest.update(arrayOfByte2, 0, arrayOfByte2.length);
      byte[] arrayOfByte3 = paramCramerShoupCiphertext.getE().toByteArray();
      digest.update(arrayOfByte3, 0, arrayOfByte3.length);
      if (this.label != null) {
        byte[] arrayOfByte = this.label.getBytes();
        digest.update(arrayOfByte, 0, arrayOfByte.length);
      } 
      byte[] arrayOfByte4 = new byte[digest.getDigestSize()];
      digest.doFinal(arrayOfByte4, 0);
      BigInteger bigInteger2 = new BigInteger(1, arrayOfByte4);
      BigInteger bigInteger3 = paramCramerShoupCiphertext.u1.modPow(cramerShoupPrivateKeyParameters.getX1().add(cramerShoupPrivateKeyParameters.getY1().multiply(bigInteger2)), bigInteger1).multiply(paramCramerShoupCiphertext.u2.modPow(cramerShoupPrivateKeyParameters.getX2().add(cramerShoupPrivateKeyParameters.getY2().multiply(bigInteger2)), bigInteger1)).mod(bigInteger1);
      if (paramCramerShoupCiphertext.v.equals(bigInteger3)) {
        bigInteger = paramCramerShoupCiphertext.e.multiply(paramCramerShoupCiphertext.u1.modPow(cramerShoupPrivateKeyParameters.getZ(), bigInteger1).modInverse(bigInteger1)).mod(bigInteger1);
      } else {
        throw new CramerShoupCiphertextException("Sorry, that ciphertext is not correct");
      } 
    } 
    return bigInteger;
  }
  
  private BigInteger generateRandomElement(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    return BigIntegers.createRandomInRange(ONE, paramBigInteger.subtract(ONE), paramSecureRandom);
  }
  
  private boolean isValidMessage(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return (paramBigInteger1.compareTo(paramBigInteger2) < 0);
  }
  
  protected SecureRandom initSecureRandom(boolean paramBoolean, SecureRandom paramSecureRandom) {
    return !paramBoolean ? null : ((paramSecureRandom != null) ? paramSecureRandom : new SecureRandom());
  }
  
  public static class CramerShoupCiphertextException extends Exception {
    private static final long serialVersionUID = -6360977166495345076L;
    
    public CramerShoupCiphertextException(String param1String) {
      super(param1String);
    }
  }
}
