package org.bouncycastle.cert.crmf.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JcaCertificateRequestMessage extends CertificateRequestMessage {
  private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  public JcaCertificateRequestMessage(byte[] paramArrayOfbyte) {
    this(CertReqMsg.getInstance(paramArrayOfbyte));
  }
  
  public JcaCertificateRequestMessage(CertificateRequestMessage paramCertificateRequestMessage) {
    this(paramCertificateRequestMessage.toASN1Structure());
  }
  
  public JcaCertificateRequestMessage(CertReqMsg paramCertReqMsg) {
    super(paramCertReqMsg);
  }
  
  public JcaCertificateRequestMessage setProvider(String paramString) {
    this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JcaCertificateRequestMessage setProvider(Provider paramProvider) {
    this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public X500Principal getSubjectX500Principal() {
    X500Name x500Name = getCertTemplate().getSubject();
    if (x500Name != null)
      try {
        return new X500Principal(x500Name.getEncoded("DER"));
      } catch (IOException iOException) {
        throw new IllegalStateException("unable to construct DER encoding of name: " + iOException.getMessage());
      }  
    return null;
  }
  
  public PublicKey getPublicKey() throws CRMFException {
    SubjectPublicKeyInfo subjectPublicKeyInfo = getCertTemplate().getPublicKey();
    return (subjectPublicKeyInfo != null) ? this.helper.toPublicKey(subjectPublicKeyInfo) : null;
  }
}
