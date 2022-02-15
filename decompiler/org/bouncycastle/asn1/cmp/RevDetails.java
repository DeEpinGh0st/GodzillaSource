package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class RevDetails extends ASN1Object {
  private CertTemplate certDetails;
  
  private Extensions crlEntryDetails;
  
  private RevDetails(ASN1Sequence paramASN1Sequence) {
    this.certDetails = CertTemplate.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.crlEntryDetails = Extensions.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public static RevDetails getInstance(Object paramObject) {
    return (paramObject instanceof RevDetails) ? (RevDetails)paramObject : ((paramObject != null) ? new RevDetails(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RevDetails(CertTemplate paramCertTemplate) {
    this.certDetails = paramCertTemplate;
  }
  
  public RevDetails(CertTemplate paramCertTemplate, X509Extensions paramX509Extensions) {
    this.certDetails = paramCertTemplate;
    this.crlEntryDetails = Extensions.getInstance(paramX509Extensions.toASN1Primitive());
  }
  
  public RevDetails(CertTemplate paramCertTemplate, Extensions paramExtensions) {
    this.certDetails = paramCertTemplate;
    this.crlEntryDetails = paramExtensions;
  }
  
  public CertTemplate getCertDetails() {
    return this.certDetails;
  }
  
  public Extensions getCrlEntryDetails() {
    return this.crlEntryDetails;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certDetails);
    if (this.crlEntryDetails != null)
      aSN1EncodableVector.add((ASN1Encodable)this.crlEntryDetails); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
