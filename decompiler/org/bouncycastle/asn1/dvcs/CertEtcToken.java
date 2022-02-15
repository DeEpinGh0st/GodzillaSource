package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.smime.SMIMECapabilities;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;

public class CertEtcToken extends ASN1Object implements ASN1Choice {
  public static final int TAG_CERTIFICATE = 0;
  
  public static final int TAG_ESSCERTID = 1;
  
  public static final int TAG_PKISTATUS = 2;
  
  public static final int TAG_ASSERTION = 3;
  
  public static final int TAG_CRL = 4;
  
  public static final int TAG_OCSPCERTSTATUS = 5;
  
  public static final int TAG_OCSPCERTID = 6;
  
  public static final int TAG_OCSPRESPONSE = 7;
  
  public static final int TAG_CAPABILITIES = 8;
  
  private static final boolean[] explicit = new boolean[] { false, true, false, true, false, true, false, false, true };
  
  private int tagNo;
  
  private ASN1Encodable value;
  
  private Extension extension;
  
  public CertEtcToken(int paramInt, ASN1Encodable paramASN1Encodable) {
    this.tagNo = paramInt;
    this.value = paramASN1Encodable;
  }
  
  public CertEtcToken(Extension paramExtension) {
    this.tagNo = -1;
    this.extension = paramExtension;
  }
  
  private CertEtcToken(ASN1TaggedObject paramASN1TaggedObject) {
    this.tagNo = paramASN1TaggedObject.getTagNo();
    switch (this.tagNo) {
      case 0:
        this.value = (ASN1Encodable)Certificate.getInstance(paramASN1TaggedObject, false);
        return;
      case 1:
        this.value = (ASN1Encodable)ESSCertID.getInstance(paramASN1TaggedObject.getObject());
        return;
      case 2:
        this.value = (ASN1Encodable)PKIStatusInfo.getInstance(paramASN1TaggedObject, false);
        return;
      case 3:
        this.value = (ASN1Encodable)ContentInfo.getInstance(paramASN1TaggedObject.getObject());
        return;
      case 4:
        this.value = (ASN1Encodable)CertificateList.getInstance(paramASN1TaggedObject, false);
        return;
      case 5:
        this.value = (ASN1Encodable)CertStatus.getInstance(paramASN1TaggedObject.getObject());
        return;
      case 6:
        this.value = (ASN1Encodable)CertID.getInstance(paramASN1TaggedObject, false);
        return;
      case 7:
        this.value = (ASN1Encodable)OCSPResponse.getInstance(paramASN1TaggedObject, false);
        return;
      case 8:
        this.value = (ASN1Encodable)SMIMECapabilities.getInstance(paramASN1TaggedObject.getObject());
        return;
    } 
    throw new IllegalArgumentException("Unknown tag: " + this.tagNo);
  }
  
  public static CertEtcToken getInstance(Object paramObject) {
    return (paramObject instanceof CertEtcToken) ? (CertEtcToken)paramObject : ((paramObject instanceof ASN1TaggedObject) ? new CertEtcToken((ASN1TaggedObject)paramObject) : ((paramObject != null) ? new CertEtcToken(Extension.getInstance(paramObject)) : null));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.extension == null) ? new DERTaggedObject(explicit[this.tagNo], this.tagNo, this.value) : this.extension.toASN1Primitive());
  }
  
  public int getTagNo() {
    return this.tagNo;
  }
  
  public ASN1Encodable getValue() {
    return this.value;
  }
  
  public Extension getExtension() {
    return this.extension;
  }
  
  public String toString() {
    return "CertEtcToken {\n" + this.value + "}\n";
  }
  
  public static CertEtcToken[] arrayFromSequence(ASN1Sequence paramASN1Sequence) {
    CertEtcToken[] arrayOfCertEtcToken = new CertEtcToken[paramASN1Sequence.size()];
    for (byte b = 0; b != arrayOfCertEtcToken.length; b++)
      arrayOfCertEtcToken[b] = getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfCertEtcToken;
  }
}
