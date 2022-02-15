package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class BaseKeyGenerator extends KeyGeneratorSpi {
  protected String algName;
  
  protected int keySize;
  
  protected int defaultKeySize;
  
  protected CipherKeyGenerator engine;
  
  protected boolean uninitialised = true;
  
  protected BaseKeyGenerator(String paramString, int paramInt, CipherKeyGenerator paramCipherKeyGenerator) {
    this.algName = paramString;
    this.keySize = this.defaultKeySize = paramInt;
    this.engine = paramCipherKeyGenerator;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("Not Implemented");
  }
  
  protected void engineInit(SecureRandom paramSecureRandom) {
    if (paramSecureRandom != null) {
      this.engine.init(new KeyGenerationParameters(paramSecureRandom, this.defaultKeySize));
      this.uninitialised = false;
    } 
  }
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    try {
      if (paramSecureRandom == null)
        paramSecureRandom = new SecureRandom(); 
      this.engine.init(new KeyGenerationParameters(paramSecureRandom, paramInt));
      this.uninitialised = false;
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidParameterException(illegalArgumentException.getMessage());
    } 
  }
  
  protected SecretKey engineGenerateKey() {
    if (this.uninitialised) {
      this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
      this.uninitialised = false;
    } 
    return new SecretKeySpec(this.engine.generateKey(), this.algName);
  }
}
