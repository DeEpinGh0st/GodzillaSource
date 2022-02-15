package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.AttributeCertificateIssuer;

public class JcaAttributeCertificateIssuer extends AttributeCertificateIssuer {
  public JcaAttributeCertificateIssuer(X509Certificate paramX509Certificate) {
    this(paramX509Certificate.getIssuerX500Principal());
  }
  
  public JcaAttributeCertificateIssuer(X500Principal paramX500Principal) {
    super(X500Name.getInstance(paramX500Principal.getEncoded()));
  }
}
