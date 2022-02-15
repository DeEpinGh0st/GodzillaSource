package org.bouncycastle.pkix;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;

public class PKIXIdentity {
  private final PrivateKeyInfo privateKeyInfo;
  
  private final X509CertificateHolder[] certificateHolders;
  
  public PKIXIdentity(PrivateKeyInfo paramPrivateKeyInfo, X509CertificateHolder[] paramArrayOfX509CertificateHolder) {
    this.privateKeyInfo = paramPrivateKeyInfo;
    this.certificateHolders = new X509CertificateHolder[paramArrayOfX509CertificateHolder.length];
    System.arraycopy(paramArrayOfX509CertificateHolder, 0, this.certificateHolders, 0, paramArrayOfX509CertificateHolder.length);
  }
  
  public PrivateKeyInfo getPrivateKeyInfo() {
    return this.privateKeyInfo;
  }
  
  public X509CertificateHolder getCertificate() {
    return this.certificateHolders[0];
  }
  
  public RecipientId getRecipientId() {
    return (RecipientId)new KeyTransRecipientId(this.certificateHolders[0].getIssuer(), this.certificateHolders[0].getSerialNumber(), getSubjectKeyIdentifier());
  }
  
  private byte[] getSubjectKeyIdentifier() {
    SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.fromExtensions(this.certificateHolders[0].getExtensions());
    return (subjectKeyIdentifier == null) ? null : subjectKeyIdentifier.getKeyIdentifier();
  }
}
