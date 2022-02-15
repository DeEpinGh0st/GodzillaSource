package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class CertConfirmContent extends ASN1Object {
  private ASN1Sequence content;
  
  private CertConfirmContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static CertConfirmContent getInstance(Object paramObject) {
    return (paramObject instanceof CertConfirmContent) ? (CertConfirmContent)paramObject : ((paramObject != null) ? new CertConfirmContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertStatus[] toCertStatusArray() {
    CertStatus[] arrayOfCertStatus = new CertStatus[this.content.size()];
    for (byte b = 0; b != arrayOfCertStatus.length; b++)
      arrayOfCertStatus[b] = CertStatus.getInstance(this.content.getObjectAt(b)); 
    return arrayOfCertStatus;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
