package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v2CRLBuilder;

public class JcaX509v2CRLBuilder extends X509v2CRLBuilder {
  public JcaX509v2CRLBuilder(X500Principal paramX500Principal, Date paramDate) {
    super(X500Name.getInstance(paramX500Principal.getEncoded()), paramDate);
  }
  
  public JcaX509v2CRLBuilder(X509Certificate paramX509Certificate, Date paramDate) {
    this(paramX509Certificate.getSubjectX500Principal(), paramDate);
  }
}
