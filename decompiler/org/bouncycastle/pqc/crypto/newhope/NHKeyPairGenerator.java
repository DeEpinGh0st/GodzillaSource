package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NHKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private SecureRandom random;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.random = paramKeyGenerationParameters.getRandom();
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    byte[] arrayOfByte = new byte[1824];
    short[] arrayOfShort = new short[1024];
    NewHope.keygen(this.random, arrayOfByte, arrayOfShort);
    return new AsymmetricCipherKeyPair(new NHPublicKeyParameters(arrayOfByte), new NHPrivateKeyParameters(arrayOfShort));
  }
}
