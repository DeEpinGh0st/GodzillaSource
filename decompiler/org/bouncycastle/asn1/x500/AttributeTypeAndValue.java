package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AttributeTypeAndValue extends ASN1Object {
  private ASN1ObjectIdentifier type;
  
  private ASN1Encodable value;
  
  private AttributeTypeAndValue(ASN1Sequence paramASN1Sequence) {
    this.type = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.value = paramASN1Sequence.getObjectAt(1);
  }
  
  public static AttributeTypeAndValue getInstance(Object paramObject) {
    if (paramObject instanceof AttributeTypeAndValue)
      return (AttributeTypeAndValue)paramObject; 
    if (paramObject != null)
      return new AttributeTypeAndValue(ASN1Sequence.getInstance(paramObject)); 
    throw new IllegalArgumentException("null value in getInstance()");
  }
  
  public AttributeTypeAndValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.type = paramASN1ObjectIdentifier;
    this.value = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getType() {
    return this.type;
  }
  
  public ASN1Encodable getValue() {
    return this.value;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.type);
    aSN1EncodableVector.add(this.value);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
