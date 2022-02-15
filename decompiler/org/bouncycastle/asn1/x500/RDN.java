package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;

public class RDN extends ASN1Object {
  private ASN1Set values;
  
  private RDN(ASN1Set paramASN1Set) {
    this.values = paramASN1Set;
  }
  
  public static RDN getInstance(Object paramObject) {
    return (paramObject instanceof RDN) ? (RDN)paramObject : ((paramObject != null) ? new RDN(ASN1Set.getInstance(paramObject)) : null);
  }
  
  public RDN(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramASN1ObjectIdentifier);
    aSN1EncodableVector.add(paramASN1Encodable);
    this.values = (ASN1Set)new DERSet((ASN1Encodable)new DERSequence(aSN1EncodableVector));
  }
  
  public RDN(AttributeTypeAndValue paramAttributeTypeAndValue) {
    this.values = (ASN1Set)new DERSet((ASN1Encodable)paramAttributeTypeAndValue);
  }
  
  public RDN(AttributeTypeAndValue[] paramArrayOfAttributeTypeAndValue) {
    this.values = (ASN1Set)new DERSet((ASN1Encodable[])paramArrayOfAttributeTypeAndValue);
  }
  
  public boolean isMultiValued() {
    return (this.values.size() > 1);
  }
  
  public int size() {
    return this.values.size();
  }
  
  public AttributeTypeAndValue getFirst() {
    return (this.values.size() == 0) ? null : AttributeTypeAndValue.getInstance(this.values.getObjectAt(0));
  }
  
  public AttributeTypeAndValue[] getTypesAndValues() {
    AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = new AttributeTypeAndValue[this.values.size()];
    for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++)
      arrayOfAttributeTypeAndValue[b] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(b)); 
    return arrayOfAttributeTypeAndValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.values;
  }
}
