package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.CRLReason;

public class RevokedInfo extends ASN1Object {
  private ASN1GeneralizedTime revocationTime;
  
  private CRLReason revocationReason;
  
  public RevokedInfo(ASN1GeneralizedTime paramASN1GeneralizedTime, CRLReason paramCRLReason) {
    this.revocationTime = paramASN1GeneralizedTime;
    this.revocationReason = paramCRLReason;
  }
  
  private RevokedInfo(ASN1Sequence paramASN1Sequence) {
    this.revocationTime = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.revocationReason = CRLReason.getInstance(ASN1Enumerated.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true)); 
  }
  
  public static RevokedInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static RevokedInfo getInstance(Object paramObject) {
    return (paramObject instanceof RevokedInfo) ? (RevokedInfo)paramObject : ((paramObject != null) ? new RevokedInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1GeneralizedTime getRevocationTime() {
    return this.revocationTime;
  }
  
  public CRLReason getRevocationReason() {
    return this.revocationReason;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.revocationTime);
    if (this.revocationReason != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.revocationReason)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
