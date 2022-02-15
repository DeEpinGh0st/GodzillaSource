package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedValue;

public class CertOrEncCert extends ASN1Object implements ASN1Choice {
  private CMPCertificate certificate;
  
  private EncryptedValue encryptedCert;
  
  private CertOrEncCert(ASN1TaggedObject paramASN1TaggedObject) {
    if (paramASN1TaggedObject.getTagNo() == 0) {
      this.certificate = CMPCertificate.getInstance(paramASN1TaggedObject.getObject());
    } else if (paramASN1TaggedObject.getTagNo() == 1) {
      this.encryptedCert = EncryptedValue.getInstance(paramASN1TaggedObject.getObject());
    } else {
      throw new IllegalArgumentException("unknown tag: " + paramASN1TaggedObject.getTagNo());
    } 
  }
  
  public static CertOrEncCert getInstance(Object paramObject) {
    return (paramObject instanceof CertOrEncCert) ? (CertOrEncCert)paramObject : ((paramObject instanceof ASN1TaggedObject) ? new CertOrEncCert((ASN1TaggedObject)paramObject) : null);
  }
  
  public CertOrEncCert(CMPCertificate paramCMPCertificate) {
    if (paramCMPCertificate == null)
      throw new IllegalArgumentException("'certificate' cannot be null"); 
    this.certificate = paramCMPCertificate;
  }
  
  public CertOrEncCert(EncryptedValue paramEncryptedValue) {
    if (paramEncryptedValue == null)
      throw new IllegalArgumentException("'encryptedCert' cannot be null"); 
    this.encryptedCert = paramEncryptedValue;
  }
  
  public CMPCertificate getCertificate() {
    return this.certificate;
  }
  
  public EncryptedValue getEncryptedCert() {
    return this.encryptedCert;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.certificate != null) ? new DERTaggedObject(true, 0, (ASN1Encodable)this.certificate) : new DERTaggedObject(true, 1, (ASN1Encodable)this.encryptedCert));
  }
}
