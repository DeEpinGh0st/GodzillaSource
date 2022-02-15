package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;

public class JcaX509CRLHolder extends X509CRLHolder {
  public JcaX509CRLHolder(X509CRL paramX509CRL) throws CRLException {
    super(CertificateList.getInstance(paramX509CRL.getEncoded()));
  }
}
