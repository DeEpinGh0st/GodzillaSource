package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;

public class TaggedAttribute extends ASN1Object {
  private final BodyPartID bodyPartID;
  
  private final ASN1ObjectIdentifier attrType;
  
  private final ASN1Set attrValues;
  
  public static TaggedAttribute getInstance(Object paramObject) {
    return (paramObject instanceof TaggedAttribute) ? (TaggedAttribute)paramObject : ((paramObject != null) ? new TaggedAttribute(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private TaggedAttribute(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartID = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.attrType = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.attrValues = ASN1Set.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public TaggedAttribute(BodyPartID paramBodyPartID, ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Set paramASN1Set) {
    this.bodyPartID = paramBodyPartID;
    this.attrType = paramASN1ObjectIdentifier;
    this.attrValues = paramASN1Set;
  }
  
  public BodyPartID getBodyPartID() {
    return this.bodyPartID;
  }
  
  public ASN1ObjectIdentifier getAttrType() {
    return this.attrType;
  }
  
  public ASN1Set getAttrValues() {
    return this.attrValues;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence(new ASN1Encodable[] { (ASN1Encodable)this.bodyPartID, (ASN1Encodable)this.attrType, (ASN1Encodable)this.attrValues });
  }
}
