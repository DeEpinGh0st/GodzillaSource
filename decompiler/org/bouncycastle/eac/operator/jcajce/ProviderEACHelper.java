package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;

class ProviderEACHelper extends EACHelper {
  private final Provider provider;
  
  ProviderEACHelper(Provider paramProvider) {
    this.provider = paramProvider;
  }
  
  protected Signature createSignature(String paramString) throws NoSuchAlgorithmException {
    return Signature.getInstance(paramString, this.provider);
  }
}
