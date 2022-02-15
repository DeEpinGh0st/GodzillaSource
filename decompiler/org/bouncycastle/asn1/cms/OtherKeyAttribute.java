package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherKeyAttribute extends ASN1Object {
  private ASN1ObjectIdentifier keyAttrId;
  
  private ASN1Encodable keyAttr;
  
  public static OtherKeyAttribute getInstance(Object paramObject) {
    return (paramObject instanceof OtherKeyAttribute) ? (OtherKeyAttribute)paramObject : ((paramObject != null) ? new OtherKeyAttribute(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public OtherKeyAttribute(ASN1Sequence paramASN1Sequence) {
    this.keyAttrId = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.keyAttr = paramASN1Sequence.getObjectAt(1);
  }
  
  public OtherKeyAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.keyAttrId = paramASN1ObjectIdentifier;
    this.keyAttr = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getKeyAttrId() {
    return this.keyAttrId;
  }
  
  public ASN1Encodable getKeyAttr() {
    return this.keyAttr;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyAttrId);
    aSN1EncodableVector.add(this.keyAttr);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
