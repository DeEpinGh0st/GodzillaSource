package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedData;

public class DVCSResponse extends DVCSMessage {
  private org.bouncycastle.asn1.dvcs.DVCSResponse asn1;
  
  public DVCSResponse(CMSSignedData paramCMSSignedData) throws DVCSConstructionException {
    this(SignedData.getInstance(paramCMSSignedData.toASN1Structure().getContent()).getEncapContentInfo());
  }
  
  public DVCSResponse(ContentInfo paramContentInfo) throws DVCSConstructionException {
    super(paramContentInfo);
    if (!DVCSObjectIdentifiers.id_ct_DVCSResponseData.equals(paramContentInfo.getContentType()))
      throw new DVCSConstructionException("ContentInfo not a DVCS Response"); 
    try {
      if (paramContentInfo.getContent().toASN1Primitive() instanceof org.bouncycastle.asn1.ASN1Sequence) {
        this.asn1 = org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance(paramContentInfo.getContent());
      } else {
        this.asn1 = org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance(ASN1OctetString.getInstance(paramContentInfo.getContent()).getOctets());
      } 
    } catch (Exception exception) {
      throw new DVCSConstructionException("Unable to parse content: " + exception.getMessage(), exception);
    } 
  }
  
  public ASN1Encodable getContent() {
    return (ASN1Encodable)this.asn1;
  }
}
