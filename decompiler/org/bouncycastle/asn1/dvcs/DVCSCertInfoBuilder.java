package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfoBuilder {
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
  
  public DVCSCertInfoBuilder(DVCSRequestInformation paramDVCSRequestInformation, DigestInfo paramDigestInfo, ASN1Integer paramASN1Integer, DVCSTime paramDVCSTime) {
    this.dvReqInfo = paramDVCSRequestInformation;
    this.messageImprint = paramDigestInfo;
    this.serialNumber = paramASN1Integer;
    this.responseTime = paramDVCSTime;
  }
  
  public DVCSCertInfo build() {
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
    return DVCSCertInfo.getInstance(new DERSequence(aSN1EncodableVector));
  }
  
  public void setVersion(int paramInt) {
    this.version = paramInt;
  }
  
  public void setDvReqInfo(DVCSRequestInformation paramDVCSRequestInformation) {
    this.dvReqInfo = paramDVCSRequestInformation;
  }
  
  public void setMessageImprint(DigestInfo paramDigestInfo) {
    this.messageImprint = paramDigestInfo;
  }
  
  public void setSerialNumber(ASN1Integer paramASN1Integer) {
    this.serialNumber = paramASN1Integer;
  }
  
  public void setResponseTime(DVCSTime paramDVCSTime) {
    this.responseTime = paramDVCSTime;
  }
  
  public void setDvStatus(PKIStatusInfo paramPKIStatusInfo) {
    this.dvStatus = paramPKIStatusInfo;
  }
  
  public void setPolicy(PolicyInformation paramPolicyInformation) {
    this.policy = paramPolicyInformation;
  }
  
  public void setReqSignature(ASN1Set paramASN1Set) {
    this.reqSignature = paramASN1Set;
  }
  
  public void setCerts(TargetEtcChain[] paramArrayOfTargetEtcChain) {
    this.certs = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfTargetEtcChain);
  }
  
  public void setExtensions(Extensions paramExtensions) {
    this.extensions = paramExtensions;
  }
}
