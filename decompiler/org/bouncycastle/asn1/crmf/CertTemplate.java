package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class CertTemplate extends ASN1Object {
  private ASN1Sequence seq;
  
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
  
  private CertTemplate(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.version = ASN1Integer.getInstance(aSN1TaggedObject, false);
          continue;
        case 1:
          this.serialNumber = ASN1Integer.getInstance(aSN1TaggedObject, false);
          continue;
        case 2:
          this.signingAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
          continue;
        case 3:
          this.issuer = X500Name.getInstance(aSN1TaggedObject, true);
          continue;
        case 4:
          this.validity = OptionalValidity.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, false));
          continue;
        case 5:
          this.subject = X500Name.getInstance(aSN1TaggedObject, true);
          continue;
        case 6:
          this.publicKey = SubjectPublicKeyInfo.getInstance(aSN1TaggedObject, false);
          continue;
        case 7:
          this.issuerUID = DERBitString.getInstance(aSN1TaggedObject, false);
          continue;
        case 8:
          this.subjectUID = DERBitString.getInstance(aSN1TaggedObject, false);
          continue;
        case 9:
          this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
          continue;
      } 
      throw new IllegalArgumentException("unknown tag: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public static CertTemplate getInstance(Object paramObject) {
    return (paramObject instanceof CertTemplate) ? (CertTemplate)paramObject : ((paramObject != null) ? new CertTemplate(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public int getVersion() {
    return this.version.getValue().intValue();
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public AlgorithmIdentifier getSigningAlg() {
    return this.signingAlg;
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public OptionalValidity getValidity() {
    return this.validity;
  }
  
  public X500Name getSubject() {
    return this.subject;
  }
  
  public SubjectPublicKeyInfo getPublicKey() {
    return this.publicKey;
  }
  
  public DERBitString getIssuerUID() {
    return this.issuerUID;
  }
  
  public DERBitString getSubjectUID() {
    return this.subjectUID;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
