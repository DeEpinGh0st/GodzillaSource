package org.bouncycastle.pqc.crypto.gmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class GMSSKeyGenerationParameters extends KeyGenerationParameters {
  private GMSSParameters params;
  
  public GMSSKeyGenerationParameters(SecureRandom paramSecureRandom, GMSSParameters paramGMSSParameters) {
    super(paramSecureRandom, 1);
    this.params = paramGMSSParameters;
  }
  
  public GMSSParameters getParameters() {
    return this.params;
  }
}
