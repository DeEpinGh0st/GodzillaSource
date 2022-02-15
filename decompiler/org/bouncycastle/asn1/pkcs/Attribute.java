package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;

public class Attribute extends ASN1Object {
  private ASN1ObjectIdentifier attrType;
  
  private ASN1Set attrValues;
  
  public static Attribute getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof Attribute)
      return (Attribute)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new Attribute((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public Attribute(ASN1Sequence paramASN1Sequence) {
    this.attrType = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.attrValues = (ASN1Set)paramASN1Sequence.getObjectAt(1);
  }
  
  public Attribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Set paramASN1Set) {
    this.attrType = paramASN1ObjectIdentifier;
    this.attrValues = paramASN1Set;
  }
  
  public ASN1ObjectIdentifier getAttrType() {
    return this.attrType;
  }
  
  public ASN1Set getAttrValues() {
    return this.attrValues;
  }
  
  public ASN1Encodable[] getAttributeValues() {
    return this.attrValues.toArray();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.attrType);
    aSN1EncodableVector.add((ASN1Encodable)this.attrValues);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
