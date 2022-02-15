package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
  ElGamalKeyGenerationParameters param;
  
  ElGamalKeyPairGenerator engine = new ElGamalKeyPairGenerator();
  
  int strength = 1024;
  
  int certainty = 20;
  
  SecureRandom random = new SecureRandom();
  
  boolean initialised = false;
  
  public KeyPairGeneratorSpi() {
    super("ElGamal");
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    this.strength = paramInt;
    this.random = paramSecureRandom;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof ElGamalParameterSpec) && !(paramAlgorithmParameterSpec instanceof DHParameterSpec))
      throw new InvalidAlgorithmParameterException("parameter object not a DHParameterSpec or an ElGamalParameterSpec"); 
    if (paramAlgorithmParameterSpec instanceof ElGamalParameterSpec) {
      ElGamalParameterSpec elGamalParameterSpec = (ElGamalParameterSpec)paramAlgorithmParameterSpec;
      this.param = new ElGamalKeyGenerationParameters(paramSecureRandom, new ElGamalParameters(elGamalParameterSpec.getP(), elGamalParameterSpec.getG()));
    } else {
      DHParameterSpec dHParameterSpec = (DHParameterSpec)paramAlgorithmParameterSpec;
      this.param = new ElGamalKeyGenerationParameters(paramSecureRandom, new ElGamalParameters(dHParameterSpec.getP(), dHParameterSpec.getG(), dHParameterSpec.getL()));
    } 
    this.engine.init((KeyGenerationParameters)this.param);
    this.initialised = true;
  }
  
  public KeyPair generateKeyPair() {
    if (!this.initialised) {
      DHParameterSpec dHParameterSpec = BouncyCastleProvider.CONFIGURATION.getDHDefaultParameters(this.strength);
      if (dHParameterSpec != null) {
        this.param = new ElGamalKeyGenerationParameters(this.random, new ElGamalParameters(dHParameterSpec.getP(), dHParameterSpec.getG(), dHParameterSpec.getL()));
      } else {
        ElGamalParametersGenerator elGamalParametersGenerator = new ElGamalParametersGenerator();
        elGamalParametersGenerator.init(this.strength, this.certainty, this.random);
        this.param = new ElGamalKeyGenerationParameters(this.random, elGamalParametersGenerator.generateParameters());
      } 
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } 
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
    ElGamalPublicKeyParameters elGamalPublicKeyParameters = (ElGamalPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    ElGamalPrivateKeyParameters elGamalPrivateKeyParameters = (ElGamalPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    return new KeyPair(new BCElGamalPublicKey(elGamalPublicKeyParameters), new BCElGamalPrivateKey(elGamalPrivateKeyParameters));
  }
}
