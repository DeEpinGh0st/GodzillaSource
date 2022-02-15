package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class ResponderID extends ASN1Object implements ASN1Choice {
  private ASN1Encodable value;
  
  public ResponderID(ASN1OctetString paramASN1OctetString) {
    this.value = (ASN1Encodable)paramASN1OctetString;
  }
  
  public ResponderID(X500Name paramX500Name) {
    this.value = (ASN1Encodable)paramX500Name;
  }
  
  public static ResponderID getInstance(Object paramObject) {
    if (paramObject instanceof ResponderID)
      return (ResponderID)paramObject; 
    if (paramObject instanceof org.bouncycastle.asn1.DEROctetString)
      return new ResponderID((ASN1OctetString)paramObject); 
    if (paramObject instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramObject;
      return (aSN1TaggedObject.getTagNo() == 1) ? new ResponderID(X500Name.getInstance(aSN1TaggedObject, true)) : new ResponderID(ASN1OctetString.getInstance(aSN1TaggedObject, true));
    } 
    return new ResponderID(X500Name.getInstance(paramObject));
  }
  
  public static ResponderID getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public byte[] getKeyHash() {
    if (this.value instanceof ASN1OctetString) {
      ASN1OctetString aSN1OctetString = (ASN1OctetString)this.value;
      return aSN1OctetString.getOctets();
    } 
    return null;
  }
  
  public X500Name getName() {
    return (this.value instanceof ASN1OctetString) ? null : X500Name.getInstance(this.value);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.value instanceof ASN1OctetString) ? new DERTaggedObject(true, 2, this.value) : new DERTaggedObject(true, 1, this.value));
  }
}
