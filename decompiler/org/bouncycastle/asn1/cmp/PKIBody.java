package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.pkcs.CertificationRequest;

public class PKIBody extends ASN1Object implements ASN1Choice {
  public static final int TYPE_INIT_REQ = 0;
  
  public static final int TYPE_INIT_REP = 1;
  
  public static final int TYPE_CERT_REQ = 2;
  
  public static final int TYPE_CERT_REP = 3;
  
  public static final int TYPE_P10_CERT_REQ = 4;
  
  public static final int TYPE_POPO_CHALL = 5;
  
  public static final int TYPE_POPO_REP = 6;
  
  public static final int TYPE_KEY_UPDATE_REQ = 7;
  
  public static final int TYPE_KEY_UPDATE_REP = 8;
  
  public static final int TYPE_KEY_RECOVERY_REQ = 9;
  
  public static final int TYPE_KEY_RECOVERY_REP = 10;
  
  public static final int TYPE_REVOCATION_REQ = 11;
  
  public static final int TYPE_REVOCATION_REP = 12;
  
  public static final int TYPE_CROSS_CERT_REQ = 13;
  
  public static final int TYPE_CROSS_CERT_REP = 14;
  
  public static final int TYPE_CA_KEY_UPDATE_ANN = 15;
  
  public static final int TYPE_CERT_ANN = 16;
  
  public static final int TYPE_REVOCATION_ANN = 17;
  
  public static final int TYPE_CRL_ANN = 18;
  
  public static final int TYPE_CONFIRM = 19;
  
  public static final int TYPE_NESTED = 20;
  
  public static final int TYPE_GEN_MSG = 21;
  
  public static final int TYPE_GEN_REP = 22;
  
  public static final int TYPE_ERROR = 23;
  
  public static final int TYPE_CERT_CONFIRM = 24;
  
  public static final int TYPE_POLL_REQ = 25;
  
  public static final int TYPE_POLL_REP = 26;
  
  private int tagNo;
  
  private ASN1Encodable body;
  
  public static PKIBody getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof PKIBody)
      return (PKIBody)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new PKIBody((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("Invalid object: " + paramObject.getClass().getName());
  }
  
  private PKIBody(ASN1TaggedObject paramASN1TaggedObject) {
    this.tagNo = paramASN1TaggedObject.getTagNo();
    this.body = getBodyForType(this.tagNo, (ASN1Encodable)paramASN1TaggedObject.getObject());
  }
  
  public PKIBody(int paramInt, ASN1Encodable paramASN1Encodable) {
    this.tagNo = paramInt;
    this.body = getBodyForType(paramInt, paramASN1Encodable);
  }
  
  private static ASN1Encodable getBodyForType(int paramInt, ASN1Encodable paramASN1Encodable) {
    switch (paramInt) {
      case 0:
        return (ASN1Encodable)CertReqMessages.getInstance(paramASN1Encodable);
      case 1:
        return (ASN1Encodable)CertRepMessage.getInstance(paramASN1Encodable);
      case 2:
        return (ASN1Encodable)CertReqMessages.getInstance(paramASN1Encodable);
      case 3:
        return (ASN1Encodable)CertRepMessage.getInstance(paramASN1Encodable);
      case 4:
        return (ASN1Encodable)CertificationRequest.getInstance(paramASN1Encodable);
      case 5:
        return (ASN1Encodable)POPODecKeyChallContent.getInstance(paramASN1Encodable);
      case 6:
        return (ASN1Encodable)POPODecKeyRespContent.getInstance(paramASN1Encodable);
      case 7:
        return (ASN1Encodable)CertReqMessages.getInstance(paramASN1Encodable);
      case 8:
        return (ASN1Encodable)CertRepMessage.getInstance(paramASN1Encodable);
      case 9:
        return (ASN1Encodable)CertReqMessages.getInstance(paramASN1Encodable);
      case 10:
        return (ASN1Encodable)KeyRecRepContent.getInstance(paramASN1Encodable);
      case 11:
        return (ASN1Encodable)RevReqContent.getInstance(paramASN1Encodable);
      case 12:
        return (ASN1Encodable)RevRepContent.getInstance(paramASN1Encodable);
      case 13:
        return (ASN1Encodable)CertReqMessages.getInstance(paramASN1Encodable);
      case 14:
        return (ASN1Encodable)CertRepMessage.getInstance(paramASN1Encodable);
      case 15:
        return (ASN1Encodable)CAKeyUpdAnnContent.getInstance(paramASN1Encodable);
      case 16:
        return (ASN1Encodable)CMPCertificate.getInstance(paramASN1Encodable);
      case 17:
        return (ASN1Encodable)RevAnnContent.getInstance(paramASN1Encodable);
      case 18:
        return (ASN1Encodable)CRLAnnContent.getInstance(paramASN1Encodable);
      case 19:
        return (ASN1Encodable)PKIConfirmContent.getInstance(paramASN1Encodable);
      case 20:
        return (ASN1Encodable)PKIMessages.getInstance(paramASN1Encodable);
      case 21:
        return (ASN1Encodable)GenMsgContent.getInstance(paramASN1Encodable);
      case 22:
        return (ASN1Encodable)GenRepContent.getInstance(paramASN1Encodable);
      case 23:
        return (ASN1Encodable)ErrorMsgContent.getInstance(paramASN1Encodable);
      case 24:
        return (ASN1Encodable)CertConfirmContent.getInstance(paramASN1Encodable);
      case 25:
        return (ASN1Encodable)PollReqContent.getInstance(paramASN1Encodable);
      case 26:
        return (ASN1Encodable)PollRepContent.getInstance(paramASN1Encodable);
    } 
    throw new IllegalArgumentException("unknown tag number: " + paramInt);
  }
  
  public int getType() {
    return this.tagNo;
  }
  
  public ASN1Encodable getContent() {
    return this.body;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(true, this.tagNo, this.body);
  }
}
