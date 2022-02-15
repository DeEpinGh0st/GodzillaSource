package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;

public class TaggedContentInfo extends ASN1Object {
  private final BodyPartID bodyPartID;
  
  private final ContentInfo contentInfo;
  
  public TaggedContentInfo(BodyPartID paramBodyPartID, ContentInfo paramContentInfo) {
    this.bodyPartID = paramBodyPartID;
    this.contentInfo = paramContentInfo;
  }
  
  private TaggedContentInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartID = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.contentInfo = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static TaggedContentInfo getInstance(Object paramObject) {
    return (paramObject instanceof TaggedContentInfo) ? (TaggedContentInfo)paramObject : ((paramObject != null) ? new TaggedContentInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static TaggedContentInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.bodyPartID);
    aSN1EncodableVector.add((ASN1Encodable)this.contentInfo);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public BodyPartID getBodyPartID() {
    return this.bodyPartID;
  }
  
  public ContentInfo getContentInfo() {
    return this.contentInfo;
  }
}
