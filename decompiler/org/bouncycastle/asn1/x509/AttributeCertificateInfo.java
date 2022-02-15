package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class AttributeCertificateInfo extends ASN1Object {
  private ASN1Integer version;
  
  private Holder holder;
  
  private AttCertIssuer issuer;
  
  private AlgorithmIdentifier signature;
  
  private ASN1Integer serialNumber;
  
  private AttCertValidityPeriod attrCertValidityPeriod;
  
  private ASN1Sequence attributes;
  
  private DERBitString issuerUniqueID;
  
  private Extensions extensions;
  
  public static AttributeCertificateInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static AttributeCertificateInfo getInstance(Object paramObject) {
    return (paramObject instanceof AttributeCertificateInfo) ? (AttributeCertificateInfo)paramObject : ((paramObject != null) ? new AttributeCertificateInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private AttributeCertificateInfo(ASN1Sequence paramASN1Sequence) {
    byte b;
    if (paramASN1Sequence.size() < 6 || paramASN1Sequence.size() > 9)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
      this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
      b = 1;
    } else {
      this.version = new ASN1Integer(0L);
      b = 0;
    } 
    this.holder = Holder.getInstance(paramASN1Sequence.getObjectAt(b));
    this.issuer = AttCertIssuer.getInstance(paramASN1Sequence.getObjectAt(b + 1));
    this.signature = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b + 2));
    this.serialNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b + 3));
    this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(paramASN1Sequence.getObjectAt(b + 4));
    this.attributes = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(b + 5));
    for (int i = b + 6; i < paramASN1Sequence.size(); i++) {
      ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(i);
      if (aSN1Encodable instanceof DERBitString) {
        this.issuerUniqueID = DERBitString.getInstance(paramASN1Sequence.getObjectAt(i));
      } else if (aSN1Encodable instanceof ASN1Sequence || aSN1Encodable instanceof Extensions) {
        this.extensions = Extensions.getInstance(paramASN1Sequence.getObjectAt(i));
      } 
    } 
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public Holder getHolder() {
    return this.holder;
  }
  
  public AttCertIssuer getIssuer() {
    return this.issuer;
  }
  
  public AlgorithmIdentifier getSignature() {
    return this.signature;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public AttCertValidityPeriod getAttrCertValidityPeriod() {
    return this.attrCertValidityPeriod;
  }
  
  public ASN1Sequence getAttributes() {
    return this.attributes;
  }
  
  public DERBitString getIssuerUniqueID() {
    return this.issuerUniqueID;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.version.getValue().intValue() != 0)
      aSN1EncodableVector.add((ASN1Encodable)this.version); 
    aSN1EncodableVector.add((ASN1Encodable)this.holder);
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.attrCertValidityPeriod);
    aSN1EncodableVector.add((ASN1Encodable)this.attributes);
    if (this.issuerUniqueID != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerUniqueID); 
    if (this.extensions != null)
      aSN1EncodableVector.add((ASN1Encodable)this.extensions); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
