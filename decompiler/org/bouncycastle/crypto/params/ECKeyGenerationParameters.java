package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class ECKeyGenerationParameters extends KeyGenerationParameters {
  private ECDomainParameters domainParams;
  
  public ECKeyGenerationParameters(ECDomainParameters paramECDomainParameters, SecureRandom paramSecureRandom) {
    super(paramSecureRandom, paramECDomainParameters.getN().bitLength());
    this.domainParams = paramECDomainParameters;
  }
  
  public ECDomainParameters getDomainParameters() {
    return this.domainParams;
  }
}
