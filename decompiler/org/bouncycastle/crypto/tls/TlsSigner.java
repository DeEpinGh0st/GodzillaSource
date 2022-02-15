package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface TlsSigner {
  void init(TlsContext paramTlsContext);
  
  byte[] generateRawSignature(AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte) throws CryptoException;
  
  byte[] generateRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte) throws CryptoException;
  
  boolean verifyRawSignature(byte[] paramArrayOfbyte1, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte2) throws CryptoException;
  
  boolean verifyRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, byte[] paramArrayOfbyte1, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte2) throws CryptoException;
  
  Signer createSigner(AsymmetricKeyParameter paramAsymmetricKeyParameter);
  
  Signer createSigner(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter);
  
  Signer createVerifyer(AsymmetricKeyParameter paramAsymmetricKeyParameter);
  
  Signer createVerifyer(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter);
  
  boolean isValidPublicKey(AsymmetricKeyParameter paramAsymmetricKeyParameter);
}
