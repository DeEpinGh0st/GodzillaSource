package org.bouncycastle.crypto;

import java.security.SecureRandom;

public class CipherKeyGenerator {
  protected SecureRandom random;
  
  protected int strength;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.random = paramKeyGenerationParameters.getRandom();
    this.strength = (paramKeyGenerationParameters.getStrength() + 7) / 8;
  }
  
  public byte[] generateKey() {
    byte[] arrayOfByte = new byte[this.strength];
    this.random.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
}
