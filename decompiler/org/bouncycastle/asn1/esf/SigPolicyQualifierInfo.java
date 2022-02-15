package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SigPolicyQualifierInfo extends ASN1Object {
  private ASN1ObjectIdentifier sigPolicyQualifierId;
  
  private ASN1Encodable sigQualifier;
  
  public SigPolicyQualifierInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.sigPolicyQualifierId = paramASN1ObjectIdentifier;
    this.sigQualifier = paramASN1Encodable;
  }
  
  private SigPolicyQualifierInfo(ASN1Sequence paramASN1Sequence) {
    this.sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.sigQualifier = paramASN1Sequence.getObjectAt(1);
  }
  
  public static SigPolicyQualifierInfo getInstance(Object paramObject) {
    return (paramObject instanceof SigPolicyQualifierInfo) ? (SigPolicyQualifierInfo)paramObject : ((paramObject != null) ? new SigPolicyQualifierInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getSigPolicyQualifierId() {
    return new ASN1ObjectIdentifier(this.sigPolicyQualifierId.getId());
  }
  
  public ASN1Encodable getSigQualifier() {
    return this.sigQualifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.sigPolicyQualifierId);
    aSN1EncodableVector.add(this.sigQualifier);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
