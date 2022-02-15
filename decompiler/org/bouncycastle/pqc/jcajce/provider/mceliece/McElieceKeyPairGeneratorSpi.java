package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;

public class McElieceKeyPairGeneratorSpi extends KeyPairGenerator {
  McElieceKeyPairGenerator kpg;
  
  public McElieceKeyPairGeneratorSpi() {
    super("McEliece");
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    this.kpg = new McElieceKeyPairGenerator();
    super.initialize(paramAlgorithmParameterSpec);
    McElieceKeyGenParameterSpec mcElieceKeyGenParameterSpec = (McElieceKeyGenParameterSpec)paramAlgorithmParameterSpec;
    McElieceKeyGenerationParameters mcElieceKeyGenerationParameters = new McElieceKeyGenerationParameters(new SecureRandom(), new McElieceParameters(mcElieceKeyGenParameterSpec.getM(), mcElieceKeyGenParameterSpec.getT()));
    this.kpg.init((KeyGenerationParameters)mcElieceKeyGenerationParameters);
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    McElieceKeyGenParameterSpec mcElieceKeyGenParameterSpec = new McElieceKeyGenParameterSpec();
    try {
      initialize((AlgorithmParameterSpec)mcElieceKeyGenParameterSpec);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {}
  }
  
  public KeyPair generateKeyPair() {
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.kpg.generateKeyPair();
    McEliecePrivateKeyParameters mcEliecePrivateKeyParameters = (McEliecePrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    McEliecePublicKeyParameters mcEliecePublicKeyParameters = (McEliecePublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    return new KeyPair(new BCMcEliecePublicKey(mcEliecePublicKeyParameters), new BCMcEliecePrivateKey(mcEliecePrivateKeyParameters));
  }
}
