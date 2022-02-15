package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;

public class X509CertificateStructure extends ASN1Object implements X509ObjectIdentifiers, PKCSObjectIdentifiers {
  ASN1Sequence seq;
  
  TBSCertificateStructure tbsCert;
  
  AlgorithmIdentifier sigAlgId;
  
  DERBitString sig;
  
  public static X509CertificateStructure getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static X509CertificateStructure getInstance(Object paramObject) {
    return (paramObject instanceof X509CertificateStructure) ? (X509CertificateStructure)paramObject : ((paramObject != null) ? new X509CertificateStructure(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public X509CertificateStructure(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
    if (paramASN1Sequence.size() == 3) {
      this.tbsCert = TBSCertificateStructure.getInstance(paramASN1Sequence.getObjectAt(0));
      this.sigAlgId = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
      this.sig = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2));
    } else {
      throw new IllegalArgumentException("sequence wrong size for a certificate");
    } 
  }
  
  public TBSCertificateStructure getTBSCertificate() {
    return this.tbsCert;
  }
  
  public int getVersion() {
    return this.tbsCert.getVersion();
  }
  
  public ASN1Integer getSerialNumber() {
    return this.tbsCert.getSerialNumber();
  }
  
  public X500Name getIssuer() {
    return this.tbsCert.getIssuer();
  }
  
  public Time getStartDate() {
    return this.tbsCert.getStartDate();
  }
  
  public Time getEndDate() {
    return this.tbsCert.getEndDate();
  }
  
  public X500Name getSubject() {
    return this.tbsCert.getSubject();
  }
  
  public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
    return this.tbsCert.getSubjectPublicKeyInfo();
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.sigAlgId;
  }
  
  public DERBitString getSignature() {
    return this.sig;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
