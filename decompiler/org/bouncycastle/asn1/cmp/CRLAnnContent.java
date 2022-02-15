package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;

public class CRLAnnContent extends ASN1Object {
  private ASN1Sequence content;
  
  private CRLAnnContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static CRLAnnContent getInstance(Object paramObject) {
    return (paramObject instanceof CRLAnnContent) ? (CRLAnnContent)paramObject : ((paramObject != null) ? new CRLAnnContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CRLAnnContent(CertificateList paramCertificateList) {
    this.content = (ASN1Sequence)new DERSequence((ASN1Encodable)paramCertificateList);
  }
  
  public CertificateList[] getCertificateLists() {
    CertificateList[] arrayOfCertificateList = new CertificateList[this.content.size()];
    for (byte b = 0; b != arrayOfCertificateList.length; b++)
      arrayOfCertificateList[b] = CertificateList.getInstance(this.content.getObjectAt(b)); 
    return arrayOfCertificateList;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
