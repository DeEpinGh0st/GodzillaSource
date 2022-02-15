package org.bouncycastle.pkcs.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

public class JcaPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder {
  public JcaPKCS10CertificationRequestBuilder(X500Name paramX500Name, PublicKey paramPublicKey) {
    super(paramX500Name, SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public JcaPKCS10CertificationRequestBuilder(X500Principal paramX500Principal, PublicKey paramPublicKey) {
    super(X500Name.getInstance(paramX500Principal.getEncoded()), SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
}
