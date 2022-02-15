package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;

public class CertificationRequestInfo extends ASN1Object {
  ASN1Integer version = new ASN1Integer(0L);
  
  X500Name subject;
  
  SubjectPublicKeyInfo subjectPKInfo;
  
  ASN1Set attributes = null;
  
  public static CertificationRequestInfo getInstance(Object paramObject) {
    return (paramObject instanceof CertificationRequestInfo) ? (CertificationRequestInfo)paramObject : ((paramObject != null) ? new CertificationRequestInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertificationRequestInfo(X500Name paramX500Name, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, ASN1Set paramASN1Set) {
    if (paramX500Name == null || paramSubjectPublicKeyInfo == null)
      throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator."); 
    validateAttributes(paramASN1Set);
    this.subject = paramX500Name;
    this.subjectPKInfo = paramSubjectPublicKeyInfo;
    this.attributes = paramASN1Set;
  }
  
  public CertificationRequestInfo(X509Name paramX509Name, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, ASN1Set paramASN1Set) {
    this(X500Name.getInstance(paramX509Name.toASN1Primitive()), paramSubjectPublicKeyInfo, paramASN1Set);
  }
  
  public CertificationRequestInfo(ASN1Sequence paramASN1Sequence) {
    this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(0);
    this.subject = X500Name.getInstance(paramASN1Sequence.getObjectAt(1));
    this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(paramASN1Sequence.getObjectAt(2));
    if (paramASN1Sequence.size() > 3) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(3);
      this.attributes = ASN1Set.getInstance(aSN1TaggedObject, false);
    } 
    validateAttributes(this.attributes);
    if (this.subject == null || this.version == null || this.subjectPKInfo == null)
      throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator."); 
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public X500Name getSubject() {
    return this.subject;
  }
  
  public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
    return this.subjectPKInfo;
  }
  
  public ASN1Set getAttributes() {
    return this.attributes;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.subject);
    aSN1EncodableVector.add((ASN1Encodable)this.subjectPKInfo);
    if (this.attributes != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.attributes)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private static void validateAttributes(ASN1Set paramASN1Set) {
    if (paramASN1Set == null)
      return; 
    Enumeration enumeration = paramASN1Set.getObjects();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = Attribute.getInstance(enumeration.nextElement());
      if (attribute.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_challengePassword) && attribute.getAttrValues().size() != 1)
        throw new IllegalArgumentException("challengePassword attribute must have one value"); 
    } 
  }
}
