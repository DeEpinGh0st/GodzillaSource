package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class RecipientIdentifier extends ASN1Object implements ASN1Choice {
  private ASN1Encodable id;
  
  public RecipientIdentifier(IssuerAndSerialNumber paramIssuerAndSerialNumber) {
    this.id = (ASN1Encodable)paramIssuerAndSerialNumber;
  }
  
  public RecipientIdentifier(ASN1OctetString paramASN1OctetString) {
    this.id = (ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)paramASN1OctetString);
  }
  
  public RecipientIdentifier(ASN1Primitive paramASN1Primitive) {
    this.id = (ASN1Encodable)paramASN1Primitive;
  }
  
  public static RecipientIdentifier getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof RecipientIdentifier)
      return (RecipientIdentifier)paramObject; 
    if (paramObject instanceof IssuerAndSerialNumber)
      return new RecipientIdentifier((IssuerAndSerialNumber)paramObject); 
    if (paramObject instanceof ASN1OctetString)
      return new RecipientIdentifier((ASN1OctetString)paramObject); 
    if (paramObject instanceof ASN1Primitive)
      return new RecipientIdentifier((ASN1Primitive)paramObject); 
    throw new IllegalArgumentException("Illegal object in RecipientIdentifier: " + paramObject.getClass().getName());
  }
  
  public boolean isTagged() {
    return this.id instanceof ASN1TaggedObject;
  }
  
  public ASN1Encodable getId() {
    return (ASN1Encodable)((this.id instanceof ASN1TaggedObject) ? ASN1OctetString.getInstance((ASN1TaggedObject)this.id, false) : IssuerAndSerialNumber.getInstance(this.id));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.id.toASN1Primitive();
  }
}
