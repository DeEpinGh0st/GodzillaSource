package org.bouncycastle.jce.provider;

import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.x509.X509AttributeCertificate;

class PrincipalUtils {
  static X500Name getSubjectPrincipal(X509Certificate paramX509Certificate) {
    return X500Name.getInstance(paramX509Certificate.getSubjectX500Principal().getEncoded());
  }
  
  static X500Name getIssuerPrincipal(X509CRL paramX509CRL) {
    return X500Name.getInstance(paramX509CRL.getIssuerX500Principal().getEncoded());
  }
  
  static X500Name getIssuerPrincipal(X509Certificate paramX509Certificate) {
    return X500Name.getInstance(paramX509Certificate.getIssuerX500Principal().getEncoded());
  }
  
  static X500Name getCA(TrustAnchor paramTrustAnchor) {
    return X500Name.getInstance(paramTrustAnchor.getCA().getEncoded());
  }
  
  static X500Name getEncodedIssuerPrincipal(Object paramObject) {
    return (paramObject instanceof X509Certificate) ? getIssuerPrincipal((X509Certificate)paramObject) : X500Name.getInstance(((X500Principal)((X509AttributeCertificate)paramObject).getIssuer().getPrincipals()[0]).getEncoded());
  }
}
