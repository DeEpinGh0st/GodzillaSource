package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSKeyGenerationParameters extends KeyGenerationParameters {
  private final XMSSParameters xmssParameters;
  
  public XMSSKeyGenerationParameters(XMSSParameters paramXMSSParameters, SecureRandom paramSecureRandom) {
    super(paramSecureRandom, -1);
    this.xmssParameters = paramXMSSParameters;
  }
  
  public XMSSParameters getParameters() {
    return this.xmssParameters;
  }
}
