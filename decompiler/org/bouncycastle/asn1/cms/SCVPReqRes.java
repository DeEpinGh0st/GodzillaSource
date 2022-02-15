package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class SCVPReqRes extends ASN1Object {
  private final ContentInfo request;
  
  private final ContentInfo response;
  
  public static SCVPReqRes getInstance(Object paramObject) {
    return (paramObject instanceof SCVPReqRes) ? (SCVPReqRes)paramObject : ((paramObject != null) ? new SCVPReqRes(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SCVPReqRes(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
      this.request = ContentInfo.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(0)), true);
      this.response = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(1));
    } else {
      this.request = null;
      this.response = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    } 
  }
  
  public SCVPReqRes(ContentInfo paramContentInfo) {
    this.request = null;
    this.response = paramContentInfo;
  }
  
  public SCVPReqRes(ContentInfo paramContentInfo1, ContentInfo paramContentInfo2) {
    this.request = paramContentInfo1;
    this.response = paramContentInfo2;
  }
  
  public ContentInfo getRequest() {
    return this.request;
  }
  
  public ContentInfo getResponse() {
    return this.response;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.request != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.request)); 
    aSN1EncodableVector.add((ASN1Encodable)this.response);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
