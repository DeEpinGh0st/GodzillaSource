package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class BcAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper {
  private AsymmetricKeyParameter privateKey;
  
  public BcAsymmetricKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    super(paramAlgorithmIdentifier);
    this.privateKey = paramAsymmetricKeyParameter;
  }
  
  public GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException {
    AsymmetricBlockCipher asymmetricBlockCipher = createAsymmetricUnwrapper(getAlgorithmIdentifier().getAlgorithm());
    asymmetricBlockCipher.init(false, (CipherParameters)this.privateKey);
    try {
      byte[] arrayOfByte = asymmetricBlockCipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      return paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.des_EDE3_CBC) ? new GenericKey(paramAlgorithmIdentifier, arrayOfByte) : new GenericKey(paramAlgorithmIdentifier, arrayOfByte);
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new OperatorException("unable to recover secret key: " + invalidCipherTextException.getMessage(), invalidCipherTextException);
    } 
  }
  
  protected abstract AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
}
