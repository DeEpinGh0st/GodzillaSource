package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertResponse extends ASN1Object {
  private ASN1Integer certReqId;
  
  private PKIStatusInfo status;
  
  private CertifiedKeyPair certifiedKeyPair;
  
  private ASN1OctetString rspInfo;
  
  private CertResponse(ASN1Sequence paramASN1Sequence) {
    this.certReqId = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.status = PKIStatusInfo.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() >= 3)
      if (paramASN1Sequence.size() == 3) {
        ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(2);
        if (aSN1Encodable instanceof ASN1OctetString) {
          this.rspInfo = ASN1OctetString.getInstance(aSN1Encodable);
        } else {
          this.certifiedKeyPair = CertifiedKeyPair.getInstance(aSN1Encodable);
        } 
      } else {
        this.certifiedKeyPair = CertifiedKeyPair.getInstance(paramASN1Sequence.getObjectAt(2));
        this.rspInfo = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(3));
      }  
  }
  
  public static CertResponse getInstance(Object paramObject) {
    return (paramObject instanceof CertResponse) ? (CertResponse)paramObject : ((paramObject != null) ? new CertResponse(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertResponse(ASN1Integer paramASN1Integer, PKIStatusInfo paramPKIStatusInfo) {
    this(paramASN1Integer, paramPKIStatusInfo, null, null);
  }
  
  public CertResponse(ASN1Integer paramASN1Integer, PKIStatusInfo paramPKIStatusInfo, CertifiedKeyPair paramCertifiedKeyPair, ASN1OctetString paramASN1OctetString) {
    if (paramASN1Integer == null)
      throw new IllegalArgumentException("'certReqId' cannot be null"); 
    if (paramPKIStatusInfo == null)
      throw new IllegalArgumentException("'status' cannot be null"); 
    this.certReqId = paramASN1Integer;
    this.status = paramPKIStatusInfo;
    this.certifiedKeyPair = paramCertifiedKeyPair;
    this.rspInfo = paramASN1OctetString;
  }
  
  public ASN1Integer getCertReqId() {
    return this.certReqId;
  }
  
  public PKIStatusInfo getStatus() {
    return this.status;
  }
  
  public CertifiedKeyPair getCertifiedKeyPair() {
    return this.certifiedKeyPair;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certReqId);
    aSN1EncodableVector.add((ASN1Encodable)this.status);
    if (this.certifiedKeyPair != null)
      aSN1EncodableVector.add((ASN1Encodable)this.certifiedKeyPair); 
    if (this.rspInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)this.rspInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
