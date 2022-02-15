package org.bouncycastle.asn1.dvcs;

import java.util.Date;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.ContentInfo;

public class DVCSTime extends ASN1Object implements ASN1Choice {
  private final ASN1GeneralizedTime genTime;
  
  private final ContentInfo timeStampToken;
  
  public DVCSTime(Date paramDate) {
    this(new ASN1GeneralizedTime(paramDate));
  }
  
  public DVCSTime(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.genTime = paramASN1GeneralizedTime;
    this.timeStampToken = null;
  }
  
  public DVCSTime(ContentInfo paramContentInfo) {
    this.genTime = null;
    this.timeStampToken = paramContentInfo;
  }
  
  public static DVCSTime getInstance(Object paramObject) {
    return (paramObject instanceof DVCSTime) ? (DVCSTime)paramObject : ((paramObject instanceof ASN1GeneralizedTime) ? new DVCSTime(ASN1GeneralizedTime.getInstance(paramObject)) : ((paramObject != null) ? new DVCSTime(ContentInfo.getInstance(paramObject)) : null));
  }
  
  public static DVCSTime getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public ASN1GeneralizedTime getGenTime() {
    return this.genTime;
  }
  
  public ContentInfo getTimeStampToken() {
    return this.timeStampToken;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.genTime != null) ? this.genTime : this.timeStampToken.toASN1Primitive());
  }
  
  public String toString() {
    return (this.genTime != null) ? this.genTime.toString() : this.timeStampToken.toString();
  }
}
