package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherRecipientInfo extends ASN1Object {
  private ASN1ObjectIdentifier oriType;
  
  private ASN1Encodable oriValue;
  
  public OtherRecipientInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.oriType = paramASN1ObjectIdentifier;
    this.oriValue = paramASN1Encodable;
  }
  
  public OtherRecipientInfo(ASN1Sequence paramASN1Sequence) {
    this.oriType = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.oriValue = paramASN1Sequence.getObjectAt(1);
  }
  
  public static OtherRecipientInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static OtherRecipientInfo getInstance(Object paramObject) {
    return (paramObject instanceof OtherRecipientInfo) ? (OtherRecipientInfo)paramObject : ((paramObject != null) ? new OtherRecipientInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getType() {
    return this.oriType;
  }
  
  public ASN1Encodable getValue() {
    return this.oriValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.oriType);
    aSN1EncodableVector.add(this.oriValue);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
