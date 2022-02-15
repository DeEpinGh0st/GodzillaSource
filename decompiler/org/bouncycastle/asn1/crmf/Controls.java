package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class Controls extends ASN1Object {
  private ASN1Sequence content;
  
  private Controls(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static Controls getInstance(Object paramObject) {
    return (paramObject instanceof Controls) ? (Controls)paramObject : ((paramObject != null) ? new Controls(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Controls(AttributeTypeAndValue paramAttributeTypeAndValue) {
    this.content = (ASN1Sequence)new DERSequence((ASN1Encodable)paramAttributeTypeAndValue);
  }
  
  public Controls(AttributeTypeAndValue[] paramArrayOfAttributeTypeAndValue) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfAttributeTypeAndValue.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfAttributeTypeAndValue[b]); 
    this.content = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public AttributeTypeAndValue[] toAttributeTypeAndValueArray() {
    AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = new AttributeTypeAndValue[this.content.size()];
    for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++)
      arrayOfAttributeTypeAndValue[b] = AttributeTypeAndValue.getInstance(this.content.getObjectAt(b)); 
    return arrayOfAttributeTypeAndValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
