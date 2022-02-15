package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public class BcSymmetricKeyWrapper extends SymmetricKeyWrapper {
  private SecureRandom random;
  
  private Wrapper wrapper;
  
  private KeyParameter wrappingKey;
  
  public BcSymmetricKeyWrapper(AlgorithmIdentifier paramAlgorithmIdentifier, Wrapper paramWrapper, KeyParameter paramKeyParameter) {
    super(paramAlgorithmIdentifier);
    this.wrapper = paramWrapper;
    this.wrappingKey = paramKeyParameter;
  }
  
  public BcSymmetricKeyWrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public byte[] generateWrappedKey(GenericKey paramGenericKey) throws OperatorException {
    byte[] arrayOfByte = OperatorUtils.getKeyBytes(paramGenericKey);
    if (this.random == null) {
      this.wrapper.init(true, (CipherParameters)this.wrappingKey);
    } else {
      this.wrapper.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)this.wrappingKey, this.random));
    } 
    return this.wrapper.wrap(arrayOfByte, 0, arrayOfByte.length);
  }
}
