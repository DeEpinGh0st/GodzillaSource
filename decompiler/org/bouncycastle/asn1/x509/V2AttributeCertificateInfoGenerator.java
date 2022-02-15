package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;

public class V2AttributeCertificateInfoGenerator {
  private ASN1Integer version = new ASN1Integer(1L);
  
  private Holder holder;
  
  private AttCertIssuer issuer;
  
  private AlgorithmIdentifier signature;
  
  private ASN1Integer serialNumber;
  
  private ASN1EncodableVector attributes = new ASN1EncodableVector();
  
  private DERBitString issuerUniqueID;
  
  private Extensions extensions;
  
  private ASN1GeneralizedTime startDate;
  
  private ASN1GeneralizedTime endDate;
  
  public void setHolder(Holder paramHolder) {
    this.holder = paramHolder;
  }
  
  public void addAttribute(String paramString, ASN1Encodable paramASN1Encodable) {
    this.attributes.add((ASN1Encodable)new Attribute(new ASN1ObjectIdentifier(paramString), (ASN1Set)new DERSet(paramASN1Encodable)));
  }
  
  public void addAttribute(Attribute paramAttribute) {
    this.attributes.add((ASN1Encodable)paramAttribute);
  }
  
  public void setSerialNumber(ASN1Integer paramASN1Integer) {
    this.serialNumber = paramASN1Integer;
  }
  
  public void setSignature(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.signature = paramAlgorithmIdentifier;
  }
  
  public void setIssuer(AttCertIssuer paramAttCertIssuer) {
    this.issuer = paramAttCertIssuer;
  }
  
  public void setStartDate(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.startDate = paramASN1GeneralizedTime;
  }
  
  public void setEndDate(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.endDate = paramASN1GeneralizedTime;
  }
  
  public void setIssuerUniqueID(DERBitString paramDERBitString) {
    this.issuerUniqueID = paramDERBitString;
  }
  
  public void setExtensions(X509Extensions paramX509Extensions) {
    this.extensions = Extensions.getInstance(paramX509Extensions.toASN1Primitive());
  }
  
  public void setExtensions(Extensions paramExtensions) {
    this.extensions = paramExtensions;
  }
  
  public AttributeCertificateInfo generateAttributeCertificateInfo() {
    if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.holder == null || this.attributes == null)
      throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator"); 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.holder);
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    AttCertValidityPeriod attCertValidityPeriod = new AttCertValidityPeriod(this.startDate, this.endDate);
    aSN1EncodableVector.add((ASN1Encodable)attCertValidityPeriod);
    aSN1EncodableVector.add((ASN1Encodable)new DERSequence(this.attributes));
    if (this.issuerUniqueID != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerUniqueID); 
    if (this.extensions != null)
      aSN1EncodableVector.add((ASN1Encodable)this.extensions); 
    return AttributeCertificateInfo.getInstance(new DERSequence(aSN1EncodableVector));
  }
}
