package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherMsg extends ASN1Object {
  private final BodyPartID bodyPartID;
  
  private final ASN1ObjectIdentifier otherMsgType;
  
  private final ASN1Encodable otherMsgValue;
  
  public OtherMsg(BodyPartID paramBodyPartID, ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.bodyPartID = paramBodyPartID;
    this.otherMsgType = paramASN1ObjectIdentifier;
    this.otherMsgValue = paramASN1Encodable;
  }
  
  private OtherMsg(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartID = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.otherMsgType = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.otherMsgValue = paramASN1Sequence.getObjectAt(2);
  }
  
  public static OtherMsg getInstance(Object paramObject) {
    return (paramObject instanceof OtherMsg) ? (OtherMsg)paramObject : ((paramObject != null) ? new OtherMsg(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static OtherMsg getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.bodyPartID);
    aSN1EncodableVector.add((ASN1Encodable)this.otherMsgType);
    aSN1EncodableVector.add(this.otherMsgValue);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public BodyPartID getBodyPartID() {
    return this.bodyPartID;
  }
  
  public ASN1ObjectIdentifier getOtherMsgType() {
    return this.otherMsgType;
  }
  
  public ASN1Encodable getOtherMsgValue() {
    return this.otherMsgValue;
  }
}
