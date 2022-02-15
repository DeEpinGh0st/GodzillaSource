package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher;

public class McEliecePKCSCipherSpi extends AsymmetricBlockCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
  private McElieceCipher cipher;
  
  public McEliecePKCSCipherSpi(McElieceCipher paramMcElieceCipher) {
    this.cipher = paramMcElieceCipher;
  }
  
  protected void initCipherEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceKeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
    ParametersWithRandom parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, paramSecureRandom);
    this.cipher.init(true, (CipherParameters)parametersWithRandom);
    this.maxPlainTextSize = this.cipher.maxPlainTextSize;
    this.cipherTextSize = this.cipher.cipherTextSize;
  }
  
  protected void initCipherDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AsymmetricKeyParameter asymmetricKeyParameter = McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
    this.cipher.init(false, (CipherParameters)asymmetricKeyParameter);
    this.maxPlainTextSize = this.cipher.maxPlainTextSize;
    this.cipherTextSize = this.cipher.cipherTextSize;
  }
  
  protected byte[] messageEncrypt(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = this.cipher.messageEncrypt(paramArrayOfbyte);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return arrayOfByte;
  }
  
  protected byte[] messageDecrypt(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = this.cipher.messageDecrypt(paramArrayOfbyte);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return arrayOfByte;
  }
  
  public String getName() {
    return "McEliecePKCS";
  }
  
  public int getKeySize(Key paramKey) throws InvalidKeyException {
    McElieceKeyParameters mcElieceKeyParameters;
    if (paramKey instanceof PublicKey) {
      mcElieceKeyParameters = (McElieceKeyParameters)McElieceKeysToParams.generatePublicKeyParameter((PublicKey)paramKey);
    } else {
      mcElieceKeyParameters = (McElieceKeyParameters)McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey)paramKey);
    } 
    return this.cipher.getKeySize(mcElieceKeyParameters);
  }
  
  public static class McEliecePKCS extends McEliecePKCSCipherSpi {
    public McEliecePKCS() {
      super(new McElieceCipher());
    }
  }
}
