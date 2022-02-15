package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class RevocationDetailsBuilder {
  private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();
  
  public RevocationDetailsBuilder setPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    if (paramSubjectPublicKeyInfo != null)
      this.templateBuilder.setPublicKey(paramSubjectPublicKeyInfo); 
    return this;
  }
  
  public RevocationDetailsBuilder setIssuer(X500Name paramX500Name) {
    if (paramX500Name != null)
      this.templateBuilder.setIssuer(paramX500Name); 
    return this;
  }
  
  public RevocationDetailsBuilder setSerialNumber(BigInteger paramBigInteger) {
    if (paramBigInteger != null)
      this.templateBuilder.setSerialNumber(new ASN1Integer(paramBigInteger)); 
    return this;
  }
  
  public RevocationDetailsBuilder setSubject(X500Name paramX500Name) {
    if (paramX500Name != null)
      this.templateBuilder.setSubject(paramX500Name); 
    return this;
  }
  
  public RevocationDetails build() {
    return new RevocationDetails(new RevDetails(this.templateBuilder.build()));
  }
}
