package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PKIPublicationInfo extends ASN1Object {
  private ASN1Integer action;
  
  private ASN1Sequence pubInfos;
  
  private PKIPublicationInfo(ASN1Sequence paramASN1Sequence) {
    this.action = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.pubInfos = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static PKIPublicationInfo getInstance(Object paramObject) {
    return (paramObject instanceof PKIPublicationInfo) ? (PKIPublicationInfo)paramObject : ((paramObject != null) ? new PKIPublicationInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getAction() {
    return this.action;
  }
  
  public SinglePubInfo[] getPubInfos() {
    if (this.pubInfos == null)
      return null; 
    SinglePubInfo[] arrayOfSinglePubInfo = new SinglePubInfo[this.pubInfos.size()];
    for (byte b = 0; b != arrayOfSinglePubInfo.length; b++)
      arrayOfSinglePubInfo[b] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(b)); 
    return arrayOfSinglePubInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.action);
    aSN1EncodableVector.add((ASN1Encodable)this.pubInfos);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
