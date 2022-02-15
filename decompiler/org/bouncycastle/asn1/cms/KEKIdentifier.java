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

public class KEKIdentifier extends ASN1Object {
  private ASN1OctetString keyIdentifier;
  
  private ASN1GeneralizedTime date;
  
  private OtherKeyAttribute other;
  
  public KEKIdentifier(byte[] paramArrayOfbyte, ASN1GeneralizedTime paramASN1GeneralizedTime, OtherKeyAttribute paramOtherKeyAttribute) {
    this.keyIdentifier = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.date = paramASN1GeneralizedTime;
    this.other = paramOtherKeyAttribute;
  }
  
  private KEKIdentifier(ASN1Sequence paramASN1Sequence) {
    this.keyIdentifier = (ASN1OctetString)paramASN1Sequence.getObjectAt(0);
    switch (paramASN1Sequence.size()) {
      case 1:
        return;
      case 2:
        if (paramASN1Sequence.getObjectAt(1) instanceof ASN1GeneralizedTime) {
          this.date = (ASN1GeneralizedTime)paramASN1Sequence.getObjectAt(1);
        } else {
          this.other = OtherKeyAttribute.getInstance(paramASN1Sequence.getObjectAt(1));
        } 
      case 3:
        this.date = (ASN1GeneralizedTime)paramASN1Sequence.getObjectAt(1);
        this.other = OtherKeyAttribute.getInstance(paramASN1Sequence.getObjectAt(2));
    } 
    throw new IllegalArgumentException("Invalid KEKIdentifier");
  }
  
  public static KEKIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static KEKIdentifier getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof KEKIdentifier)
      return (KEKIdentifier)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new KEKIdentifier((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid KEKIdentifier: " + paramObject.getClass().getName());
  }
  
  public ASN1OctetString getKeyIdentifier() {
    return this.keyIdentifier;
  }
  
  public ASN1GeneralizedTime getDate() {
    return this.date;
  }
  
  public OtherKeyAttribute getOther() {
    return this.other;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyIdentifier);
    if (this.date != null)
      aSN1EncodableVector.add((ASN1Encodable)this.date); 
    if (this.other != null)
      aSN1EncodableVector.add((ASN1Encodable)this.other); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
