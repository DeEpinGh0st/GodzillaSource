package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;

public class TimeStampResp extends ASN1Object {
  PKIStatusInfo pkiStatusInfo;
  
  ContentInfo timeStampToken;
  
  public static TimeStampResp getInstance(Object paramObject) {
    return (paramObject instanceof TimeStampResp) ? (TimeStampResp)paramObject : ((paramObject != null) ? new TimeStampResp(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private TimeStampResp(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.pkiStatusInfo = PKIStatusInfo.getInstance(enumeration.nextElement());
    if (enumeration.hasMoreElements())
      this.timeStampToken = ContentInfo.getInstance(enumeration.nextElement()); 
  }
  
  public TimeStampResp(PKIStatusInfo paramPKIStatusInfo, ContentInfo paramContentInfo) {
    this.pkiStatusInfo = paramPKIStatusInfo;
    this.timeStampToken = paramContentInfo;
  }
  
  public PKIStatusInfo getStatus() {
    return this.pkiStatusInfo;
  }
  
  public ContentInfo getTimeStampToken() {
    return this.timeStampToken;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pkiStatusInfo);
    if (this.timeStampToken != null)
      aSN1EncodableVector.add((ASN1Encodable)this.timeStampToken); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
