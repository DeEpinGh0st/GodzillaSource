package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

class ProviderEACHelper implements EACHelper {
  private final Provider provider;
  
  ProviderEACHelper(Provider paramProvider) {
    this.provider = paramProvider;
  }
  
  public KeyFactory createKeyFactory(String paramString) throws NoSuchAlgorithmException {
    return KeyFactory.getInstance(paramString, this.provider);
  }
}
