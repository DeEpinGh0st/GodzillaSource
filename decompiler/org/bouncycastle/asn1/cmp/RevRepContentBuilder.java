package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContentBuilder {
  private ASN1EncodableVector status = new ASN1EncodableVector();
  
  private ASN1EncodableVector revCerts = new ASN1EncodableVector();
  
  private ASN1EncodableVector crls = new ASN1EncodableVector();
  
  public RevRepContentBuilder add(PKIStatusInfo paramPKIStatusInfo) {
    this.status.add((ASN1Encodable)paramPKIStatusInfo);
    return this;
  }
  
  public RevRepContentBuilder add(PKIStatusInfo paramPKIStatusInfo, CertId paramCertId) {
    if (this.status.size() != this.revCerts.size())
      throw new IllegalStateException("status and revCerts sequence must be in common order"); 
    this.status.add((ASN1Encodable)paramPKIStatusInfo);
    this.revCerts.add((ASN1Encodable)paramCertId);
    return this;
  }
  
  public RevRepContentBuilder addCrl(CertificateList paramCertificateList) {
    this.crls.add((ASN1Encodable)paramCertificateList);
    return this;
  }
  
  public RevRepContent build() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new DERSequence(this.status));
    if (this.revCerts.size() != 0)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(this.revCerts))); 
    if (this.crls.size() != 0)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERSequence(this.crls))); 
    return RevRepContent.getInstance(new DERSequence(aSN1EncodableVector));
  }
}
