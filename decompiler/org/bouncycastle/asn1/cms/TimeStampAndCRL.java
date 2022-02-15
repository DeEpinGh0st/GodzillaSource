package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;

public class TimeStampAndCRL extends ASN1Object {
  private ContentInfo timeStamp;
  
  private CertificateList crl;
  
  public TimeStampAndCRL(ContentInfo paramContentInfo) {
    this.timeStamp = paramContentInfo;
  }
  
  private TimeStampAndCRL(ASN1Sequence paramASN1Sequence) {
    this.timeStamp = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2)
      this.crl = CertificateList.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public static TimeStampAndCRL getInstance(Object paramObject) {
    return (paramObject instanceof TimeStampAndCRL) ? (TimeStampAndCRL)paramObject : ((paramObject != null) ? new TimeStampAndCRL(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ContentInfo getTimeStampToken() {
    return this.timeStamp;
  }
  
  public CertificateList getCertificateList() {
    return this.crl;
  }
  
  public CertificateList getCRL() {
    return this.crl;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.timeStamp);
    if (this.crl != null)
      aSN1EncodableVector.add((ASN1Encodable)this.crl); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
