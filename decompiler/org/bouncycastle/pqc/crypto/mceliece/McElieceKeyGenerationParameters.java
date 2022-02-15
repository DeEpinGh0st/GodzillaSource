package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class McElieceKeyGenerationParameters extends KeyGenerationParameters {
  private McElieceParameters params;
  
  public McElieceKeyGenerationParameters(SecureRandom paramSecureRandom, McElieceParameters paramMcElieceParameters) {
    super(paramSecureRandom, 256);
    this.params = paramMcElieceParameters;
  }
  
  public McElieceParameters getParameters() {
    return this.params;
  }
}
