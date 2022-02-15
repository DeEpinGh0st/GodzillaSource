package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertificatePair extends ASN1Object {
  private Certificate forward;
  
  private Certificate reverse;
  
  public static CertificatePair getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof CertificatePair)
      return (CertificatePair)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new CertificatePair((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private CertificatePair(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 1 && paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.forward = Certificate.getInstance(aSN1TaggedObject, true);
        continue;
      } 
      if (aSN1TaggedObject.getTagNo() == 1) {
        this.reverse = Certificate.getInstance(aSN1TaggedObject, true);
        continue;
      } 
      throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public CertificatePair(Certificate paramCertificate1, Certificate paramCertificate2) {
    this.forward = paramCertificate1;
    this.reverse = paramCertificate2;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.forward != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.forward)); 
    if (this.reverse != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(1, (ASN1Encodable)this.reverse)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public Certificate getForward() {
    return this.forward;
  }
  
  public Certificate getReverse() {
    return this.reverse;
  }
}
