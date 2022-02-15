package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfo extends ASN1Object {
  private int version = 1;
  
  private DVCSRequestInformation dvReqInfo;
  
  private DigestInfo messageImprint;
  
  private ASN1Integer serialNumber;
  
  private DVCSTime responseTime;
  
  private PKIStatusInfo dvStatus;
  
  private PolicyInformation policy;
  
  private ASN1Set reqSignature;
  
  private ASN1Sequence certs;
  
  private Extensions extensions;
  
  private static final int DEFAULT_VERSION = 1;
  
  private static final int TAG_DV_STATUS = 0;
  
  private static final int TAG_POLICY = 1;
  
  private static final int TAG_REQ_SIGNATURE = 2;
  
  private static final int TAG_CERTS = 3;
  
  public DVCSCertInfo(DVCSRequestInformation paramDVCSRequestInformation, DigestInfo paramDigestInfo, ASN1Integer paramASN1Integer, DVCSTime paramDVCSTime) {
    this.dvReqInfo = paramDVCSRequestInformation;
    this.messageImprint = paramDigestInfo;
    this.serialNumber = paramASN1Integer;
    this.responseTime = paramDVCSTime;
  }
  
  private DVCSCertInfo(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    try {
      ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Encodable);
      this.version = aSN1Integer.getValue().intValue();
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    } catch (IllegalArgumentException illegalArgumentException) {}
    this.dvReqInfo = DVCSRequestInformation.getInstance(aSN1Encodable);
    aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    this.messageImprint = DigestInfo.getInstance(aSN1Encodable);
    aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    this.serialNumber = ASN1Integer.getInstance(aSN1Encodable);
    aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    this.responseTime = DVCSTime.getInstance(aSN1Encodable);
    while (b < paramASN1Sequence.size()) {
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
      if (aSN1Encodable instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Encodable);
        int i = aSN1TaggedObject.getTagNo();
        switch (i) {
          case 0:
            this.dvStatus = PKIStatusInfo.getInstance(aSN1TaggedObject, false);
            continue;
          case 1:
            this.policy = PolicyInformation.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, false));
            continue;
          case 2:
            this.reqSignature = ASN1Set.getInstance(aSN1TaggedObject, false);
            continue;
          case 3:
            this.certs = ASN1Sequence.getInstance(aSN1TaggedObject, false);
            continue;
        } 
        throw new IllegalArgumentException("Unknown tag encountered: " + i);
      } 
      try {
        this.extensions = Extensions.getInstance(aSN1Encodable);
      } catch (IllegalArgumentException illegalArgumentException) {}
    } 
  }
  
  public static DVCSCertInfo getInstance(Object paramObject) {
    return (paramObject instanceof DVCSCertInfo) ? (DVCSCertInfo)paramObject : ((paramObject != null) ? new DVCSCertInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static DVCSCertInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.version != 1)
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.version)); 
    aSN1EncodableVector.add((ASN1Encodable)this.dvReqInfo);
    aSN1EncodableVector.add((ASN1Encodable)this.messageImprint);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.responseTime);
    if (this.dvStatus != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.dvStatus)); 
    if (this.policy != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.policy)); 
    if (this.reqSignature != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.reqSignature)); 
    if (this.certs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)this.certs)); 
    if (this.extensions != null)
      aSN1EncodableVector.add((ASN1Encodable)this.extensions); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("DVCSCertInfo {\n");
    if (this.version != 1)
      stringBuffer.append("version: " + this.version + "\n"); 
    stringBuffer.append("dvReqInfo: " + this.dvReqInfo + "\n");
    stringBuffer.append("messageImprint: " + this.messageImprint + "\n");
    stringBuffer.append("serialNumber: " + this.serialNumber + "\n");
    stringBuffer.append("responseTime: " + this.responseTime + "\n");
    if (this.dvStatus != null)
      stringBuffer.append("dvStatus: " + this.dvStatus + "\n"); 
    if (this.policy != null)
      stringBuffer.append("policy: " + this.policy + "\n"); 
    if (this.reqSignature != null)
      stringBuffer.append("reqSignature: " + this.reqSignature + "\n"); 
    if (this.certs != null)
      stringBuffer.append("certs: " + this.certs + "\n"); 
    if (this.extensions != null)
      stringBuffer.append("extensions: " + this.extensions + "\n"); 
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public int getVersion() {
    return this.version;
  }
  
  private void setVersion(int paramInt) {
    this.version = paramInt;
  }
  
  public DVCSRequestInformation getDvReqInfo() {
    return this.dvReqInfo;
  }
  
  private void setDvReqInfo(DVCSRequestInformation paramDVCSRequestInformation) {
    this.dvReqInfo = paramDVCSRequestInformation;
  }
  
  public DigestInfo getMessageImprint() {
    return this.messageImprint;
  }
  
  private void setMessageImprint(DigestInfo paramDigestInfo) {
    this.messageImprint = paramDigestInfo;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public DVCSTime getResponseTime() {
    return this.responseTime;
  }
  
  public PKIStatusInfo getDvStatus() {
    return this.dvStatus;
  }
  
  public PolicyInformation getPolicy() {
    return this.policy;
  }
  
  public ASN1Set getReqSignature() {
    return this.reqSignature;
  }
  
  public TargetEtcChain[] getCerts() {
    return (this.certs != null) ? TargetEtcChain.arrayFromSequence(this.certs) : null;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
}
