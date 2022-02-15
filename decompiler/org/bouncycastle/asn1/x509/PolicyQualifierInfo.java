package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;

public class PolicyQualifierInfo extends ASN1Object {
  private ASN1ObjectIdentifier policyQualifierId;
  
  private ASN1Encodable qualifier;
  
  public PolicyQualifierInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.policyQualifierId = paramASN1ObjectIdentifier;
    this.qualifier = paramASN1Encodable;
  }
  
  public PolicyQualifierInfo(String paramString) {
    this.policyQualifierId = PolicyQualifierId.id_qt_cps;
    this.qualifier = (ASN1Encodable)new DERIA5String(paramString);
  }
  
  public PolicyQualifierInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.policyQualifierId = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.qualifier = paramASN1Sequence.getObjectAt(1);
  }
  
  public static PolicyQualifierInfo getInstance(Object paramObject) {
    return (paramObject instanceof PolicyQualifierInfo) ? (PolicyQualifierInfo)paramObject : ((paramObject != null) ? new PolicyQualifierInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getPolicyQualifierId() {
    return this.policyQualifierId;
  }
  
  public ASN1Encodable getQualifier() {
    return this.qualifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.policyQualifierId);
    aSN1EncodableVector.add(this.qualifier);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
