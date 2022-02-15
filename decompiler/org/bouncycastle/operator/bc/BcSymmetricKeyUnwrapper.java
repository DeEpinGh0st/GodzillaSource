package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class BcSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper {
  private SecureRandom random;
  
  private Wrapper wrapper;
  
  private KeyParameter wrappingKey;
  
  public BcSymmetricKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, Wrapper paramWrapper, KeyParameter paramKeyParameter) {
    super(paramAlgorithmIdentifier);
    this.wrapper = paramWrapper;
    this.wrappingKey = paramKeyParameter;
  }
  
  public BcSymmetricKeyUnwrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException {
    this.wrapper.init(false, (CipherParameters)this.wrappingKey);
    try {
      return new GenericKey(paramAlgorithmIdentifier, this.wrapper.unwrap(paramArrayOfbyte, 0, paramArrayOfbyte.length));
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new OperatorException("unable to unwrap key: " + invalidCipherTextException.getMessage(), invalidCipherTextException);
    } 
  }
}
