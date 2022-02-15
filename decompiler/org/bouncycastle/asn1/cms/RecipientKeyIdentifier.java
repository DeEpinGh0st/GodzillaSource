package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class RecipientKeyIdentifier extends ASN1Object {
  private ASN1OctetString subjectKeyIdentifier;
  
  private ASN1GeneralizedTime date;
  
  private OtherKeyAttribute other;
  
  public RecipientKeyIdentifier(ASN1OctetString paramASN1OctetString, ASN1GeneralizedTime paramASN1GeneralizedTime, OtherKeyAttribute paramOtherKeyAttribute) {
    this.subjectKeyIdentifier = paramASN1OctetString;
    this.date = paramASN1GeneralizedTime;
    this.other = paramOtherKeyAttribute;
  }
  
  public RecipientKeyIdentifier(byte[] paramArrayOfbyte, ASN1GeneralizedTime paramASN1GeneralizedTime, OtherKeyAttribute paramOtherKeyAttribute) {
    this.subjectKeyIdentifier = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.date = paramASN1GeneralizedTime;
    this.other = paramOtherKeyAttribute;
  }
  
  public RecipientKeyIdentifier(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, (ASN1GeneralizedTime)null, (OtherKeyAttribute)null);
  }
  
  public RecipientKeyIdentifier(ASN1Sequence paramASN1Sequence) {
    this.subjectKeyIdentifier = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0));
    switch (paramASN1Sequence.size()) {
      case 1:
        return;
      case 2:
        if (paramASN1Sequence.getObjectAt(1) instanceof ASN1GeneralizedTime) {
          this.date = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(1));
        } else {
          this.other = OtherKeyAttribute.getInstance(paramASN1Sequence.getObjectAt(2));
        } 
      case 3:
        this.date = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(1));
        this.other = OtherKeyAttribute.getInstance(paramASN1Sequence.getObjectAt(2));
    } 
    throw new IllegalArgumentException("Invalid RecipientKeyIdentifier");
  }
  
  public static RecipientKeyIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static RecipientKeyIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof RecipientKeyIdentifier) ? (RecipientKeyIdentifier)paramObject : ((paramObject != null) ? new RecipientKeyIdentifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1OctetString getSubjectKeyIdentifier() {
    return this.subjectKeyIdentifier;
  }
  
  public ASN1GeneralizedTime getDate() {
    return this.date;
  }
  
  public OtherKeyAttribute getOtherKeyAttribute() {
    return this.other;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.subjectKeyIdentifier);
    if (this.date != null)
      aSN1EncodableVector.add((ASN1Encodable)this.date); 
    if (this.other != null)
      aSN1EncodableVector.add((ASN1Encodable)this.other); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
