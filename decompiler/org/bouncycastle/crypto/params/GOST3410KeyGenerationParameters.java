package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class GOST3410KeyGenerationParameters extends KeyGenerationParameters {
  private GOST3410Parameters params;
  
  public GOST3410KeyGenerationParameters(SecureRandom paramSecureRandom, GOST3410Parameters paramGOST3410Parameters) {
    super(paramSecureRandom, paramGOST3410Parameters.getP().bitLength() - 1);
    this.params = paramGOST3410Parameters;
  }
  
  public GOST3410Parameters getParameters() {
    return this.params;
  }
}
