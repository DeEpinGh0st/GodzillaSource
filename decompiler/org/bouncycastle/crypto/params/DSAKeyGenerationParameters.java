package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DSAKeyGenerationParameters extends KeyGenerationParameters {
  private DSAParameters params;
  
  public DSAKeyGenerationParameters(SecureRandom paramSecureRandom, DSAParameters paramDSAParameters) {
    super(paramSecureRandom, paramDSAParameters.getP().bitLength() - 1);
    this.params = paramDSAParameters;
  }
  
  public DSAParameters getParameters() {
    return this.params;
  }
}
