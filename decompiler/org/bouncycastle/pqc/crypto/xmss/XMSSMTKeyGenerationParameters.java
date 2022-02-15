package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSMTKeyGenerationParameters extends KeyGenerationParameters {
  private final XMSSMTParameters xmssmtParameters;
  
  public XMSSMTKeyGenerationParameters(XMSSMTParameters paramXMSSMTParameters, SecureRandom paramSecureRandom) {
    super(paramSecureRandom, -1);
    this.xmssmtParameters = paramXMSSMTParameters;
  }
  
  public XMSSMTParameters getParameters() {
    return this.xmssmtParameters;
  }
}
