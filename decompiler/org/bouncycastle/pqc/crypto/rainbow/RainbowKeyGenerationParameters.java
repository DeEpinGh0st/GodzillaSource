package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class RainbowKeyGenerationParameters extends KeyGenerationParameters {
  private RainbowParameters params;
  
  public RainbowKeyGenerationParameters(SecureRandom paramSecureRandom, RainbowParameters paramRainbowParameters) {
    super(paramSecureRandom, paramRainbowParameters.getVi()[(paramRainbowParameters.getVi()).length - 1] - paramRainbowParameters.getVi()[0]);
    this.params = paramRainbowParameters;
  }
  
  public RainbowParameters getParameters() {
    return this.params;
  }
}
