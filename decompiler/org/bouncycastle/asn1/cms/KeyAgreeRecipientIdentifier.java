package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class KeyAgreeRecipientIdentifier extends ASN1Object implements ASN1Choice {
  private IssuerAndSerialNumber issuerSerial;
  
  private RecipientKeyIdentifier rKeyID;
  
  public static KeyAgreeRecipientIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static KeyAgreeRecipientIdentifier getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof KeyAgreeRecipientIdentifier)
      return (KeyAgreeRecipientIdentifier)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new KeyAgreeRecipientIdentifier(IssuerAndSerialNumber.getInstance(paramObject)); 
    if (paramObject instanceof ASN1TaggedObject && ((ASN1TaggedObject)paramObject).getTagNo() == 0)
      return new KeyAgreeRecipientIdentifier(RecipientKeyIdentifier.getInstance((ASN1TaggedObject)paramObject, false)); 
    throw new IllegalArgumentException("Invalid KeyAgreeRecipientIdentifier: " + paramObject.getClass().getName());
  }
  
  public KeyAgreeRecipientIdentifier(IssuerAndSerialNumber paramIssuerAndSerialNumber) {
    this.issuerSerial = paramIssuerAndSerialNumber;
    this.rKeyID = null;
  }
  
  public KeyAgreeRecipientIdentifier(RecipientKeyIdentifier paramRecipientKeyIdentifier) {
    this.issuerSerial = null;
    this.rKeyID = paramRecipientKeyIdentifier;
  }
  
  public IssuerAndSerialNumber getIssuerAndSerialNumber() {
    return this.issuerSerial;
  }
  
  public RecipientKeyIdentifier getRKeyID() {
    return this.rKeyID;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.issuerSerial != null) ? this.issuerSerial.toASN1Primitive() : new DERTaggedObject(false, 0, (ASN1Encodable)this.rKeyID));
  }
}
