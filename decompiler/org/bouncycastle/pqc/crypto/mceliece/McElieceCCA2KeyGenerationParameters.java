package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class McElieceCCA2KeyGenerationParameters extends KeyGenerationParameters {
  private McElieceCCA2Parameters params;
  
  public McElieceCCA2KeyGenerationParameters(SecureRandom paramSecureRandom, McElieceCCA2Parameters paramMcElieceCCA2Parameters) {
    super(paramSecureRandom, 128);
    this.params = paramMcElieceCCA2Parameters;
  }
  
  public McElieceCCA2Parameters getParameters() {
    return this.params;
  }
}
