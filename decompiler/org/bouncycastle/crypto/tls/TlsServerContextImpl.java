package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;

class TlsServerContextImpl extends AbstractTlsContext implements TlsServerContext {
  TlsServerContextImpl(SecureRandom paramSecureRandom, SecurityParameters paramSecurityParameters) {
    super(paramSecureRandom, paramSecurityParameters);
  }
  
  public boolean isServer() {
    return true;
  }
}
