package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

class NamedEACHelper extends EACHelper {
  private final String providerName;
  
  NamedEACHelper(String paramString) {
    this.providerName = paramString;
  }
  
  protected Signature createSignature(String paramString) throws NoSuchProviderException, NoSuchAlgorithmException {
    return Signature.getInstance(paramString, this.providerName);
  }
}
