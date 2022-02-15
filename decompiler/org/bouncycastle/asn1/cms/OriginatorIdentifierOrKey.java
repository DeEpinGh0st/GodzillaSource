package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

public class OriginatorIdentifierOrKey extends ASN1Object implements ASN1Choice {
  private ASN1Encodable id;
  
  public OriginatorIdentifierOrKey(IssuerAndSerialNumber paramIssuerAndSerialNumber) {
    this.id = (ASN1Encodable)paramIssuerAndSerialNumber;
  }
  
  public OriginatorIdentifierOrKey(ASN1OctetString paramASN1OctetString) {
    this(new SubjectKeyIdentifier(paramASN1OctetString.getOctets()));
  }
  
  public OriginatorIdentifierOrKey(SubjectKeyIdentifier paramSubjectKeyIdentifier) {
    this.id = (ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)paramSubjectKeyIdentifier);
  }
  
  public OriginatorIdentifierOrKey(OriginatorPublicKey paramOriginatorPublicKey) {
    this.id = (ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)paramOriginatorPublicKey);
  }
  
  public OriginatorIdentifierOrKey(ASN1Primitive paramASN1Primitive) {
    this.id = (ASN1Encodable)paramASN1Primitive;
  }
  
  public static OriginatorIdentifierOrKey getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (!paramBoolean)
      throw new IllegalArgumentException("Can't implicitly tag OriginatorIdentifierOrKey"); 
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public static OriginatorIdentifierOrKey getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof OriginatorIdentifierOrKey)
      return (OriginatorIdentifierOrKey)paramObject; 
    if (paramObject instanceof IssuerAndSerialNumber || paramObject instanceof org.bouncycastle.asn1.ASN1Sequence)
      return new OriginatorIdentifierOrKey(IssuerAndSerialNumber.getInstance(paramObject)); 
    if (paramObject instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramObject;
      if (aSN1TaggedObject.getTagNo() == 0)
        return new OriginatorIdentifierOrKey(SubjectKeyIdentifier.getInstance(aSN1TaggedObject, false)); 
      if (aSN1TaggedObject.getTagNo() == 1)
        return new OriginatorIdentifierOrKey(OriginatorPublicKey.getInstance(aSN1TaggedObject, false)); 
    } 
    throw new IllegalArgumentException("Invalid OriginatorIdentifierOrKey: " + paramObject.getClass().getName());
  }
  
  public ASN1Encodable getId() {
    return this.id;
  }
  
  public IssuerAndSerialNumber getIssuerAndSerialNumber() {
    return (this.id instanceof IssuerAndSerialNumber) ? (IssuerAndSerialNumber)this.id : null;
  }
  
  public SubjectKeyIdentifier getSubjectKeyIdentifier() {
    return (this.id instanceof ASN1TaggedObject && ((ASN1TaggedObject)this.id).getTagNo() == 0) ? SubjectKeyIdentifier.getInstance((ASN1TaggedObject)this.id, false) : null;
  }
  
  public OriginatorPublicKey getOriginatorKey() {
    return (this.id instanceof ASN1TaggedObject && ((ASN1TaggedObject)this.id).getTagNo() == 1) ? OriginatorPublicKey.getInstance((ASN1TaggedObject)this.id, false) : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.id.toASN1Primitive();
  }
}
