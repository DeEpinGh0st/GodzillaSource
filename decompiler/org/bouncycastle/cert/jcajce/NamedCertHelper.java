package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

class NamedCertHelper extends CertHelper {
  private final String providerName;
  
  NamedCertHelper(String paramString) {
    this.providerName = paramString;
  }
  
  protected CertificateFactory createCertificateFactory(String paramString) throws CertificateException, NoSuchProviderException {
    return CertificateFactory.getInstance(paramString, this.providerName);
  }
}
