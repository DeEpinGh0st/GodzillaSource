package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;

public class RainbowKeyPairGeneratorSpi extends KeyPairGenerator {
  RainbowKeyGenerationParameters param;
  
  RainbowKeyPairGenerator engine = new RainbowKeyPairGenerator();
  
  int strength = 1024;
  
  SecureRandom random = new SecureRandom();
  
  boolean initialised = false;
  
  public RainbowKeyPairGeneratorSpi() {
    super("Rainbow");
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    this.strength = paramInt;
    this.random = paramSecureRandom;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof RainbowParameterSpec))
      throw new InvalidAlgorithmParameterException("parameter object not a RainbowParameterSpec"); 
    RainbowParameterSpec rainbowParameterSpec = (RainbowParameterSpec)paramAlgorithmParameterSpec;
    this.param = new RainbowKeyGenerationParameters(paramSecureRandom, new RainbowParameters(rainbowParameterSpec.getVi()));
    this.engine.init((KeyGenerationParameters)this.param);
    this.initialised = true;
  }
  
  public KeyPair generateKeyPair() {
    if (!this.initialised) {
      this.param = new RainbowKeyGenerationParameters(this.random, new RainbowParameters((new RainbowParameterSpec()).getVi()));
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } 
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
    RainbowPublicKeyParameters rainbowPublicKeyParameters = (RainbowPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    RainbowPrivateKeyParameters rainbowPrivateKeyParameters = (RainbowPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    return new KeyPair(new BCRainbowPublicKey(rainbowPublicKeyParameters), new BCRainbowPrivateKey(rainbowPrivateKeyParameters));
  }
}
