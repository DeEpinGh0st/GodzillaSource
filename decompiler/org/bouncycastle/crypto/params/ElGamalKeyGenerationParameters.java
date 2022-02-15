package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class ElGamalKeyGenerationParameters extends KeyGenerationParameters {
  private ElGamalParameters params;
  
  public ElGamalKeyGenerationParameters(SecureRandom paramSecureRandom, ElGamalParameters paramElGamalParameters) {
    super(paramSecureRandom, getStrength(paramElGamalParameters));
    this.params = paramElGamalParameters;
  }
  
  public ElGamalParameters getParameters() {
    return this.params;
  }
  
  static int getStrength(ElGamalParameters paramElGamalParameters) {
    return (paramElGamalParameters.getL() != 0) ? paramElGamalParameters.getL() : paramElGamalParameters.getP().bitLength();
  }
}
