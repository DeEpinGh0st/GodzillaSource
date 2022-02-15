package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

class NamedEACHelper implements EACHelper {
  private final String providerName;
  
  NamedEACHelper(String paramString) {
    this.providerName = paramString;
  }
  
  public KeyFactory createKeyFactory(String paramString) throws NoSuchProviderException, NoSuchAlgorithmException {
    return KeyFactory.getInstance(paramString, this.providerName);
  }
}
