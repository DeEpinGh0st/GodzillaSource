package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
  static final BigInteger defaultPublicExponent = BigInteger.valueOf(65537L);
  
  RSAKeyGenerationParameters param;
  
  RSAKeyPairGenerator engine;
  
  public KeyPairGeneratorSpi(String paramString) {
    super(paramString);
  }
  
  public KeyPairGeneratorSpi() {
    super("RSA");
    this.engine = new RSAKeyPairGenerator();
    this.param = new RSAKeyGenerationParameters(defaultPublicExponent, new SecureRandom(), 2048, PrimeCertaintyCalculator.getDefaultCertainty(2048));
    this.engine.init((KeyGenerationParameters)this.param);
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    this.param = new RSAKeyGenerationParameters(defaultPublicExponent, paramSecureRandom, paramInt, PrimeCertaintyCalculator.getDefaultCertainty(paramInt));
    this.engine.init((KeyGenerationParameters)this.param);
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof RSAKeyGenParameterSpec))
      throw new InvalidAlgorithmParameterException("parameter object not a RSAKeyGenParameterSpec"); 
    RSAKeyGenParameterSpec rSAKeyGenParameterSpec = (RSAKeyGenParameterSpec)paramAlgorithmParameterSpec;
    this.param = new RSAKeyGenerationParameters(rSAKeyGenParameterSpec.getPublicExponent(), paramSecureRandom, rSAKeyGenParameterSpec.getKeysize(), PrimeCertaintyCalculator.getDefaultCertainty(2048));
    this.engine.init((KeyGenerationParameters)this.param);
  }
  
  public KeyPair generateKeyPair() {
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
    RSAKeyParameters rSAKeyParameters = (RSAKeyParameters)asymmetricCipherKeyPair.getPublic();
    RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)asymmetricCipherKeyPair.getPrivate();
    return new KeyPair(new BCRSAPublicKey(rSAKeyParameters), new BCRSAPrivateCrtKey(rSAPrivateCrtKeyParameters));
  }
}
