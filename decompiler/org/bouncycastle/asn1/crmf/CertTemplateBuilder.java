package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;

public class CertTemplateBuilder {
  private ASN1Integer version;
  
  private ASN1Integer serialNumber;
  
  private AlgorithmIdentifier signingAlg;
  
  private X500Name issuer;
  
  private OptionalValidity validity;
  
  private X500Name subject;
  
  private SubjectPublicKeyInfo publicKey;
  
  private DERBitString issuerUID;
  
  private DERBitString subjectUID;
  
  private Extensions extensions;
  
  public CertTemplateBuilder setVersion(int paramInt) {
    this.version = new ASN1Integer(paramInt);
    return this;
  }
  
  public CertTemplateBuilder setSerialNumber(ASN1Integer paramASN1Integer) {
    this.serialNumber = paramASN1Integer;
    return this;
  }
  
  public CertTemplateBuilder setSigningAlg(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.signingAlg = paramAlgorithmIdentifier;
    return this;
  }
  
  public CertTemplateBuilder setIssuer(X500Name paramX500Name) {
    this.issuer = paramX500Name;
    return this;
  }
  
  public CertTemplateBuilder setValidity(OptionalValidity paramOptionalValidity) {
    this.validity = paramOptionalValidity;
    return this;
  }
  
  public CertTemplateBuilder setSubject(X500Name paramX500Name) {
    this.subject = paramX500Name;
    return this;
  }
  
  public CertTemplateBuilder setPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.publicKey = paramSubjectPublicKeyInfo;
    return this;
  }
  
  public CertTemplateBuilder setIssuerUID(DERBitString paramDERBitString) {
    this.issuerUID = paramDERBitString;
    return this;
  }
  
  public CertTemplateBuilder setSubjectUID(DERBitString paramDERBitString) {
    this.subjectUID = paramDERBitString;
    return this;
  }
  
  public CertTemplateBuilder setExtensions(X509Extensions paramX509Extensions) {
    return setExtensions(Extensions.getInstance(paramX509Extensions));
  }
  
  public CertTemplateBuilder setExtensions(Extensions paramExtensions) {
    this.extensions = paramExtensions;
    return this;
  }
  
  public CertTemplate build() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    addOptional(aSN1EncodableVector, 0, false, (ASN1Encodable)this.version);
    addOptional(aSN1EncodableVector, 1, false, (ASN1Encodable)this.serialNumber);
    addOptional(aSN1EncodableVector, 2, false, (ASN1Encodable)this.signingAlg);
    addOptional(aSN1EncodableVector, 3, true, (ASN1Encodable)this.issuer);
    addOptional(aSN1EncodableVector, 4, false, (ASN1Encodable)this.validity);
    addOptional(aSN1EncodableVector, 5, true, (ASN1Encodable)this.subject);
    addOptional(aSN1EncodableVector, 6, false, (ASN1Encodable)this.publicKey);
    addOptional(aSN1EncodableVector, 7, false, (ASN1Encodable)this.issuerUID);
    addOptional(aSN1EncodableVector, 8, false, (ASN1Encodable)this.subjectUID);
    addOptional(aSN1EncodableVector, 9, false, (ASN1Encodable)this.extensions);
    return CertTemplate.getInstance(new DERSequence(aSN1EncodableVector));
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, boolean paramBoolean, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(paramBoolean, paramInt, paramASN1Encodable)); 
  }
}
