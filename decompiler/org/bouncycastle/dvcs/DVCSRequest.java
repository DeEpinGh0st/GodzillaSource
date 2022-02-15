package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cms.CMSSignedData;

public class DVCSRequest extends DVCSMessage {
  private org.bouncycastle.asn1.dvcs.DVCSRequest asn1;
  
  private DVCSRequestInfo reqInfo;
  
  private DVCSRequestData data;
  
  public DVCSRequest(CMSSignedData paramCMSSignedData) throws DVCSConstructionException {
    this(SignedData.getInstance(paramCMSSignedData.toASN1Structure().getContent()).getEncapContentInfo());
  }
  
  public DVCSRequest(ContentInfo paramContentInfo) throws DVCSConstructionException {
    super(paramContentInfo);
    if (!DVCSObjectIdentifiers.id_ct_DVCSRequestData.equals(paramContentInfo.getContentType()))
      throw new DVCSConstructionException("ContentInfo not a DVCS Request"); 
    try {
      if (paramContentInfo.getContent().toASN1Primitive() instanceof org.bouncycastle.asn1.ASN1Sequence) {
        this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(paramContentInfo.getContent());
      } else {
        this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(ASN1OctetString.getInstance(paramContentInfo.getContent()).getOctets());
      } 
    } catch (Exception exception) {
      throw new DVCSConstructionException("Unable to parse content: " + exception.getMessage(), exception);
    } 
    this.reqInfo = new DVCSRequestInfo(this.asn1.getRequestInformation());
    int i = this.reqInfo.getServiceType();
    if (i == ServiceType.CPD.getValue().intValue()) {
      this.data = new CPDRequestData(this.asn1.getData());
    } else if (i == ServiceType.VSD.getValue().intValue()) {
      this.data = new VSDRequestData(this.asn1.getData());
    } else if (i == ServiceType.VPKC.getValue().intValue()) {
      this.data = new VPKCRequestData(this.asn1.getData());
    } else if (i == ServiceType.CCPD.getValue().intValue()) {
      this.data = new CCPDRequestData(this.asn1.getData());
    } else {
      throw new DVCSConstructionException("Unknown service type: " + i);
    } 
  }
  
  public ASN1Encodable getContent() {
    return (ASN1Encodable)this.asn1;
  }
  
  public DVCSRequestInfo getRequestInfo() {
    return this.reqInfo;
  }
  
  public DVCSRequestData getData() {
    return this.data;
  }
  
  public GeneralName getTransactionIdentifier() {
    return this.asn1.getTransactionIdentifier();
  }
}
