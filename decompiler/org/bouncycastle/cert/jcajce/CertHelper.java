package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

abstract class CertHelper {
  public CertificateFactory getCertificateFactory(String paramString) throws NoSuchProviderException, CertificateException {
    return createCertificateFactory(paramString);
  }
  
  protected abstract CertificateFactory createCertificateFactory(String paramString) throws CertificateException, NoSuchProviderException;
}
