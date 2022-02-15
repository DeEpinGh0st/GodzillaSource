package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class BcAsymmetricKeyWrapper extends AsymmetricKeyWrapper {
  private AsymmetricKeyParameter publicKey;
  
  private SecureRandom random;
  
  public BcAsymmetricKeyWrapper(AlgorithmIdentifier paramAlgorithmIdentifier, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    super(paramAlgorithmIdentifier);
    this.publicKey = paramAsymmetricKeyParameter;
  }
  
  public BcAsymmetricKeyWrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public byte[] generateWrappedKey(GenericKey paramGenericKey) throws OperatorException {
    ParametersWithRandom parametersWithRandom;
    AsymmetricBlockCipher asymmetricBlockCipher = createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm());
    AsymmetricKeyParameter asymmetricKeyParameter = this.publicKey;
    if (this.random != null)
      parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, this.random); 
    try {
      byte[] arrayOfByte = OperatorUtils.getKeyBytes(paramGenericKey);
      asymmetricBlockCipher.init(true, (CipherParameters)parametersWithRandom);
      return asymmetricBlockCipher.processBlock(arrayOfByte, 0, arrayOfByte.length);
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new OperatorException("unable to encrypt contents key", invalidCipherTextException);
    } 
  }
  
  protected abstract AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
}
