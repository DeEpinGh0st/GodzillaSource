package org.bouncycastle.cert.crmf.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;

public class JcaCertificateRequestMessageBuilder extends CertificateRequestMessageBuilder {
  public JcaCertificateRequestMessageBuilder(BigInteger paramBigInteger) {
    super(paramBigInteger);
  }
  
  public JcaCertificateRequestMessageBuilder setIssuer(X500Principal paramX500Principal) {
    if (paramX500Principal != null)
      setIssuer(X500Name.getInstance(paramX500Principal.getEncoded())); 
    return this;
  }
  
  public JcaCertificateRequestMessageBuilder setSubject(X500Principal paramX500Principal) {
    if (paramX500Principal != null)
      setSubject(X500Name.getInstance(paramX500Principal.getEncoded())); 
    return this;
  }
  
  public JcaCertificateRequestMessageBuilder setAuthInfoSender(X500Principal paramX500Principal) {
    if (paramX500Principal != null)
      setAuthInfoSender(new GeneralName(X500Name.getInstance(paramX500Principal.getEncoded()))); 
    return this;
  }
  
  public JcaCertificateRequestMessageBuilder setPublicKey(PublicKey paramPublicKey) {
    setPublicKey(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
    return this;
  }
}
