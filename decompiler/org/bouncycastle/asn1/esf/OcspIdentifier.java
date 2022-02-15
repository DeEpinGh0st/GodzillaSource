package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.ResponderID;

public class OcspIdentifier extends ASN1Object {
  private ResponderID ocspResponderID;
  
  private ASN1GeneralizedTime producedAt;
  
  public static OcspIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof OcspIdentifier) ? (OcspIdentifier)paramObject : ((paramObject != null) ? new OcspIdentifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OcspIdentifier(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.ocspResponderID = ResponderID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.producedAt = (ASN1GeneralizedTime)paramASN1Sequence.getObjectAt(1);
  }
  
  public OcspIdentifier(ResponderID paramResponderID, ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.ocspResponderID = paramResponderID;
    this.producedAt = paramASN1GeneralizedTime;
  }
  
  public ResponderID getOcspResponderID() {
    return this.ocspResponderID;
  }
  
  public ASN1GeneralizedTime getProducedAt() {
    return this.producedAt;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.ocspResponderID);
    aSN1EncodableVector.add((ASN1Encodable)this.producedAt);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
