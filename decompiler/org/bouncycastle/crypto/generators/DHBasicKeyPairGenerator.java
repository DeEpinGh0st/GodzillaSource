package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class DHBasicKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private DHKeyGenerationParameters param;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.param = (DHKeyGenerationParameters)paramKeyGenerationParameters;
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    DHKeyGeneratorHelper dHKeyGeneratorHelper = DHKeyGeneratorHelper.INSTANCE;
    DHParameters dHParameters = this.param.getParameters();
    BigInteger bigInteger1 = dHKeyGeneratorHelper.calculatePrivate(dHParameters, this.param.getRandom());
    BigInteger bigInteger2 = dHKeyGeneratorHelper.calculatePublic(dHParameters, bigInteger1);
    return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)new DHPublicKeyParameters(bigInteger2, dHParameters), (AsymmetricKeyParameter)new DHPrivateKeyParameters(bigInteger1, dHParameters));
  }
}
