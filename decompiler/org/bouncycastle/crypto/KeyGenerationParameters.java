package org.bouncycastle.crypto;

import java.security.SecureRandom;

public class KeyGenerationParameters {
  private SecureRandom random;
  
  private int strength;
  
  public KeyGenerationParameters(SecureRandom paramSecureRandom, int paramInt) {
    this.random = paramSecureRandom;
    this.strength = paramInt;
  }
  
  public SecureRandom getRandom() {
    return this.random;
  }
  
  public int getStrength() {
    return this.strength;
  }
}
