package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;

public class CertifiedKeyPair extends ASN1Object {
  private CertOrEncCert certOrEncCert;
  
  private EncryptedValue privateKey;
  
  private PKIPublicationInfo publicationInfo;
  
  private CertifiedKeyPair(ASN1Sequence paramASN1Sequence) {
    this.certOrEncCert = CertOrEncCert.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() >= 2)
      if (paramASN1Sequence.size() == 2) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1));
        if (aSN1TaggedObject.getTagNo() == 0) {
          this.privateKey = EncryptedValue.getInstance(aSN1TaggedObject.getObject());
        } else {
          this.publicationInfo = PKIPublicationInfo.getInstance(aSN1TaggedObject.getObject());
        } 
      } else {
        this.privateKey = EncryptedValue.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1)));
        this.publicationInfo = PKIPublicationInfo.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(2)));
      }  
  }
  
  public static CertifiedKeyPair getInstance(Object paramObject) {
    return (paramObject instanceof CertifiedKeyPair) ? (CertifiedKeyPair)paramObject : ((paramObject != null) ? new CertifiedKeyPair(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertifiedKeyPair(CertOrEncCert paramCertOrEncCert) {
    this(paramCertOrEncCert, null, null);
  }
  
  public CertifiedKeyPair(CertOrEncCert paramCertOrEncCert, EncryptedValue paramEncryptedValue, PKIPublicationInfo paramPKIPublicationInfo) {
    if (paramCertOrEncCert == null)
      throw new IllegalArgumentException("'certOrEncCert' cannot be null"); 
    this.certOrEncCert = paramCertOrEncCert;
    this.privateKey = paramEncryptedValue;
    this.publicationInfo = paramPKIPublicationInfo;
  }
  
  public CertOrEncCert getCertOrEncCert() {
    return this.certOrEncCert;
  }
  
  public EncryptedValue getPrivateKey() {
    return this.privateKey;
  }
  
  public PKIPublicationInfo getPublicationInfo() {
    return this.publicationInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certOrEncCert);
    if (this.privateKey != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.privateKey)); 
    if (this.publicationInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.publicationInfo)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
