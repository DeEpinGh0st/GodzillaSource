package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PKIData extends ASN1Object {
  private final TaggedAttribute[] controlSequence;
  
  private final TaggedRequest[] reqSequence;
  
  private final TaggedContentInfo[] cmsSequence;
  
  private final OtherMsg[] otherMsgSequence;
  
  public PKIData(TaggedAttribute[] paramArrayOfTaggedAttribute, TaggedRequest[] paramArrayOfTaggedRequest, TaggedContentInfo[] paramArrayOfTaggedContentInfo, OtherMsg[] paramArrayOfOtherMsg) {
    this.controlSequence = paramArrayOfTaggedAttribute;
    this.reqSequence = paramArrayOfTaggedRequest;
    this.cmsSequence = paramArrayOfTaggedContentInfo;
    this.otherMsgSequence = paramArrayOfOtherMsg;
  }
  
  private PKIData(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 4)
      throw new IllegalArgumentException("Sequence not 4 elements."); 
    ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
    this.controlSequence = new TaggedAttribute[aSN1Sequence.size()];
    byte b;
    for (b = 0; b < this.controlSequence.length; b++)
      this.controlSequence[b] = TaggedAttribute.getInstance(aSN1Sequence.getObjectAt(b)); 
    aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(1);
    this.reqSequence = new TaggedRequest[aSN1Sequence.size()];
    for (b = 0; b < this.reqSequence.length; b++)
      this.reqSequence[b] = TaggedRequest.getInstance(aSN1Sequence.getObjectAt(b)); 
    aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(2);
    this.cmsSequence = new TaggedContentInfo[aSN1Sequence.size()];
    for (b = 0; b < this.cmsSequence.length; b++)
      this.cmsSequence[b] = TaggedContentInfo.getInstance(aSN1Sequence.getObjectAt(b)); 
    aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(3);
    this.otherMsgSequence = new OtherMsg[aSN1Sequence.size()];
    for (b = 0; b < this.otherMsgSequence.length; b++)
      this.otherMsgSequence[b] = OtherMsg.getInstance(aSN1Sequence.getObjectAt(b)); 
  }
  
  public static PKIData getInstance(Object paramObject) {
    return (paramObject instanceof PKIData) ? (PKIData)paramObject : ((paramObject != null) ? new PKIData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence(new ASN1Encodable[] { (ASN1Encodable)new DERSequence((ASN1Encodable[])this.controlSequence), (ASN1Encodable)new DERSequence((ASN1Encodable[])this.reqSequence), (ASN1Encodable)new DERSequence((ASN1Encodable[])this.cmsSequence), (ASN1Encodable)new DERSequence((ASN1Encodable[])this.otherMsgSequence) });
  }
  
  public TaggedAttribute[] getControlSequence() {
    return this.controlSequence;
  }
  
  public TaggedRequest[] getReqSequence() {
    return this.reqSequence;
  }
  
  public TaggedContentInfo[] getCmsSequence() {
    return this.cmsSequence;
  }
  
  public OtherMsg[] getOtherMsgSequence() {
    return this.otherMsgSequence;
  }
}
