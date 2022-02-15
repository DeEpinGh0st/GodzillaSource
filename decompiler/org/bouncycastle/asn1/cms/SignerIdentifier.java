package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class SignerIdentifier extends ASN1Object implements ASN1Choice {
  private ASN1Encodable id;
  
  public SignerIdentifier(IssuerAndSerialNumber paramIssuerAndSerialNumber) {
    this.id = (ASN1Encodable)paramIssuerAndSerialNumber;
  }
  
  public SignerIdentifier(ASN1OctetString paramASN1OctetString) {
    this.id = (ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)paramASN1OctetString);
  }
  
  public SignerIdentifier(ASN1Primitive paramASN1Primitive) {
    this.id = (ASN1Encodable)paramASN1Primitive;
  }
  
  public static SignerIdentifier getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof SignerIdentifier)
      return (SignerIdentifier)paramObject; 
    if (paramObject instanceof IssuerAndSerialNumber)
      return new SignerIdentifier((IssuerAndSerialNumber)paramObject); 
    if (paramObject instanceof ASN1OctetString)
      return new SignerIdentifier((ASN1OctetString)paramObject); 
    if (paramObject instanceof ASN1Primitive)
      return new SignerIdentifier((ASN1Primitive)paramObject); 
    throw new IllegalArgumentException("Illegal object in SignerIdentifier: " + paramObject.getClass().getName());
  }
  
  public boolean isTagged() {
    return this.id instanceof ASN1TaggedObject;
  }
  
  public ASN1Encodable getId() {
    return (ASN1Encodable)((this.id instanceof ASN1TaggedObject) ? ASN1OctetString.getInstance((ASN1TaggedObject)this.id, false) : this.id);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.id.toASN1Primitive();
  }
}
