package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class SingleResponse extends ASN1Object {
  private CertID certID;
  
  private CertStatus certStatus;
  
  private ASN1GeneralizedTime thisUpdate;
  
  private ASN1GeneralizedTime nextUpdate;
  
  private Extensions singleExtensions;
  
  public SingleResponse(CertID paramCertID, CertStatus paramCertStatus, ASN1GeneralizedTime paramASN1GeneralizedTime1, ASN1GeneralizedTime paramASN1GeneralizedTime2, X509Extensions paramX509Extensions) {
    this(paramCertID, paramCertStatus, paramASN1GeneralizedTime1, paramASN1GeneralizedTime2, Extensions.getInstance(paramX509Extensions));
  }
  
  public SingleResponse(CertID paramCertID, CertStatus paramCertStatus, ASN1GeneralizedTime paramASN1GeneralizedTime1, ASN1GeneralizedTime paramASN1GeneralizedTime2, Extensions paramExtensions) {
    this.certID = paramCertID;
    this.certStatus = paramCertStatus;
    this.thisUpdate = paramASN1GeneralizedTime1;
    this.nextUpdate = paramASN1GeneralizedTime2;
    this.singleExtensions = paramExtensions;
  }
  
  private SingleResponse(ASN1Sequence paramASN1Sequence) {
    this.certID = CertID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certStatus = CertStatus.getInstance(paramASN1Sequence.getObjectAt(1));
    this.thisUpdate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(2));
    if (paramASN1Sequence.size() > 4) {
      this.nextUpdate = ASN1GeneralizedTime.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(3), true);
      this.singleExtensions = Extensions.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(4), true);
    } else if (paramASN1Sequence.size() > 3) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(3);
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.nextUpdate = ASN1GeneralizedTime.getInstance(aSN1TaggedObject, true);
      } else {
        this.singleExtensions = Extensions.getInstance(aSN1TaggedObject, true);
      } 
    } 
  }
  
  public static SingleResponse getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static SingleResponse getInstance(Object paramObject) {
    return (paramObject instanceof SingleResponse) ? (SingleResponse)paramObject : ((paramObject != null) ? new SingleResponse(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertID getCertID() {
    return this.certID;
  }
  
  public CertStatus getCertStatus() {
    return this.certStatus;
  }
  
  public ASN1GeneralizedTime getThisUpdate() {
    return this.thisUpdate;
  }
  
  public ASN1GeneralizedTime getNextUpdate() {
    return this.nextUpdate;
  }
  
  public Extensions getSingleExtensions() {
    return this.singleExtensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certID);
    aSN1EncodableVector.add((ASN1Encodable)this.certStatus);
    aSN1EncodableVector.add((ASN1Encodable)this.thisUpdate);
    if (this.nextUpdate != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.nextUpdate)); 
    if (this.singleExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.singleExtensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
