package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class V3TBSCertificateGenerator {
  DERTaggedObject version = new DERTaggedObject(true, 0, (ASN1Encodable)new ASN1Integer(2L));
  
  ASN1Integer serialNumber;
  
  AlgorithmIdentifier signature;
  
  X500Name issuer;
  
  Time startDate;
  
  Time endDate;
  
  X500Name subject;
  
  SubjectPublicKeyInfo subjectPublicKeyInfo;
  
  Extensions extensions;
  
  private boolean altNamePresentAndCritical;
  
  private DERBitString issuerUniqueID;
  
  private DERBitString subjectUniqueID;
  
  public void setSerialNumber(ASN1Integer paramASN1Integer) {
    this.serialNumber = paramASN1Integer;
  }
  
  public void setSignature(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.signature = paramAlgorithmIdentifier;
  }
  
  public void setIssuer(X509Name paramX509Name) {
    this.issuer = X500Name.getInstance(paramX509Name);
  }
  
  public void setIssuer(X500Name paramX500Name) {
    this.issuer = paramX500Name;
  }
  
  public void setStartDate(ASN1UTCTime paramASN1UTCTime) {
    this.startDate = new Time((ASN1Primitive)paramASN1UTCTime);
  }
  
  public void setStartDate(Time paramTime) {
    this.startDate = paramTime;
  }
  
  public void setEndDate(ASN1UTCTime paramASN1UTCTime) {
    this.endDate = new Time((ASN1Primitive)paramASN1UTCTime);
  }
  
  public void setEndDate(Time paramTime) {
    this.endDate = paramTime;
  }
  
  public void setSubject(X509Name paramX509Name) {
    this.subject = X500Name.getInstance(paramX509Name.toASN1Primitive());
  }
  
  public void setSubject(X500Name paramX500Name) {
    this.subject = paramX500Name;
  }
  
  public void setIssuerUniqueID(DERBitString paramDERBitString) {
    this.issuerUniqueID = paramDERBitString;
  }
  
  public void setSubjectUniqueID(DERBitString paramDERBitString) {
    this.subjectUniqueID = paramDERBitString;
  }
  
  public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.subjectPublicKeyInfo = paramSubjectPublicKeyInfo;
  }
  
  public void setExtensions(X509Extensions paramX509Extensions) {
    setExtensions(Extensions.getInstance(paramX509Extensions));
  }
  
  public void setExtensions(Extensions paramExtensions) {
    this.extensions = paramExtensions;
    if (paramExtensions != null) {
      Extension extension = paramExtensions.getExtension(Extension.subjectAlternativeName);
      if (extension != null && extension.isCritical())
        this.altNamePresentAndCritical = true; 
    } 
  }
  
  public TBSCertificate generateTBSCertificate() {
    if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || (this.subject == null && !this.altNamePresentAndCritical) || this.subjectPublicKeyInfo == null)
      throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator"); 
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    aSN1EncodableVector1.add((ASN1Encodable)this.version);
    aSN1EncodableVector1.add((ASN1Encodable)this.serialNumber);
    aSN1EncodableVector1.add((ASN1Encodable)this.signature);
    aSN1EncodableVector1.add((ASN1Encodable)this.issuer);
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    aSN1EncodableVector2.add((ASN1Encodable)this.startDate);
    aSN1EncodableVector2.add((ASN1Encodable)this.endDate);
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    if (this.subject != null) {
      aSN1EncodableVector1.add((ASN1Encodable)this.subject);
    } else {
      aSN1EncodableVector1.add((ASN1Encodable)new DERSequence());
    } 
    aSN1EncodableVector1.add((ASN1Encodable)this.subjectPublicKeyInfo);
    if (this.issuerUniqueID != null)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.issuerUniqueID)); 
    if (this.subjectUniqueID != null)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.subjectUniqueID)); 
    if (this.extensions != null)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.extensions)); 
    return TBSCertificate.getInstance(new DERSequence(aSN1EncodableVector1));
  }
}
