package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AttCertValidityPeriod extends ASN1Object {
  ASN1GeneralizedTime notBeforeTime;
  
  ASN1GeneralizedTime notAfterTime;
  
  public static AttCertValidityPeriod getInstance(Object paramObject) {
    return (paramObject instanceof AttCertValidityPeriod) ? (AttCertValidityPeriod)paramObject : ((paramObject != null) ? new AttCertValidityPeriod(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private AttCertValidityPeriod(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.notBeforeTime = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(0));
    this.notAfterTime = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public AttCertValidityPeriod(ASN1GeneralizedTime paramASN1GeneralizedTime1, ASN1GeneralizedTime paramASN1GeneralizedTime2) {
    this.notBeforeTime = paramASN1GeneralizedTime1;
    this.notAfterTime = paramASN1GeneralizedTime2;
  }
  
  public ASN1GeneralizedTime getNotBeforeTime() {
    return this.notBeforeTime;
  }
  
  public ASN1GeneralizedTime getNotAfterTime() {
    return this.notAfterTime;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.notBeforeTime);
    aSN1EncodableVector.add((ASN1Encodable)this.notAfterTime);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
