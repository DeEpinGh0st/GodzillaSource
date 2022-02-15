package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;

class TlsClientContextImpl extends AbstractTlsContext implements TlsClientContext {
  TlsClientContextImpl(SecureRandom paramSecureRandom, SecurityParameters paramSecurityParameters) {
    super(paramSecureRandom, paramSecurityParameters);
  }
  
  public boolean isServer() {
    return false;
  }
}
