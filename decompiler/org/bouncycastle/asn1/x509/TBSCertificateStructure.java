package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;

public class TBSCertificateStructure extends ASN1Object implements X509ObjectIdentifiers, PKCSObjectIdentifiers {
  ASN1Sequence seq;
  
  ASN1Integer version;
  
  ASN1Integer serialNumber;
  
  AlgorithmIdentifier signature;
  
  X500Name issuer;
  
  Time startDate;
  
  Time endDate;
  
  X500Name subject;
  
  SubjectPublicKeyInfo subjectPublicKeyInfo;
  
  DERBitString issuerUniqueId;
  
  DERBitString subjectUniqueId;
  
  X509Extensions extensions;
  
  public static TBSCertificateStructure getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static TBSCertificateStructure getInstance(Object paramObject) {
    return (paramObject instanceof TBSCertificateStructure) ? (TBSCertificateStructure)paramObject : ((paramObject != null) ? new TBSCertificateStructure(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public TBSCertificateStructure(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.seq = paramASN1Sequence;
    if (paramASN1Sequence.getObjectAt(0) instanceof DERTaggedObject) {
      this.version = ASN1Integer.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), true);
    } else {
      b = -1;
      this.version = new ASN1Integer(0L);
    } 
    this.serialNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b + 1));
    this.signature = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b + 2));
    this.issuer = X500Name.getInstance(paramASN1Sequence.getObjectAt(b + 3));
    ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(b + 4);
    this.startDate = Time.getInstance(aSN1Sequence.getObjectAt(0));
    this.endDate = Time.getInstance(aSN1Sequence.getObjectAt(1));
    this.subject = X500Name.getInstance(paramASN1Sequence.getObjectAt(b + 5));
    this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(paramASN1Sequence.getObjectAt(b + 6));
    for (int i = paramASN1Sequence.size() - b + 6 - 1; i > 0; i--) {
      DERTaggedObject dERTaggedObject = (DERTaggedObject)paramASN1Sequence.getObjectAt(b + 6 + i);
      switch (dERTaggedObject.getTagNo()) {
        case 1:
          this.issuerUniqueId = DERBitString.getInstance((ASN1TaggedObject)dERTaggedObject, false);
          break;
        case 2:
          this.subjectUniqueId = DERBitString.getInstance((ASN1TaggedObject)dERTaggedObject, false);
          break;
        case 3:
          this.extensions = X509Extensions.getInstance(dERTaggedObject);
          break;
      } 
    } 
  }
  
  public int getVersion() {
    return this.version.getValue().intValue() + 1;
  }
  
  public ASN1Integer getVersionNumber() {
    return this.version;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public AlgorithmIdentifier getSignature() {
    return this.signature;
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public Time getStartDate() {
    return this.startDate;
  }
  
  public Time getEndDate() {
    return this.endDate;
  }
  
  public X500Name getSubject() {
    return this.subject;
  }
  
  public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
    return this.subjectPublicKeyInfo;
  }
  
  public DERBitString getIssuerUniqueId() {
    return this.issuerUniqueId;
  }
  
  public DERBitString getSubjectUniqueId() {
    return this.subjectUniqueId;
  }
  
  public X509Extensions getExtensions() {
    return this.extensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
