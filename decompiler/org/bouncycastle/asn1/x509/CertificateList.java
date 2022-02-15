package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class CertificateList extends ASN1Object {
  TBSCertList tbsCertList;
  
  AlgorithmIdentifier sigAlgId;
  
  DERBitString sig;
  
  boolean isHashCodeSet = false;
  
  int hashCodeValue;
  
  public static CertificateList getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static CertificateList getInstance(Object paramObject) {
    return (paramObject instanceof CertificateList) ? (CertificateList)paramObject : ((paramObject != null) ? new CertificateList(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertificateList(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 3) {
      this.tbsCertList = TBSCertList.getInstance(paramASN1Sequence.getObjectAt(0));
      this.sigAlgId = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
      this.sig = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2));
    } else {
      throw new IllegalArgumentException("sequence wrong size for CertificateList");
    } 
  }
  
  public TBSCertList getTBSCertList() {
    return this.tbsCertList;
  }
  
  public TBSCertList.CRLEntry[] getRevokedCertificates() {
    return this.tbsCertList.getRevokedCertificates();
  }
  
  public Enumeration getRevokedCertificateEnumeration() {
    return this.tbsCertList.getRevokedCertificateEnumeration();
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.sigAlgId;
  }
  
  public DERBitString getSignature() {
    return this.sig;
  }
  
  public int getVersionNumber() {
    return this.tbsCertList.getVersionNumber();
  }
  
  public X500Name getIssuer() {
    return this.tbsCertList.getIssuer();
  }
  
  public Time getThisUpdate() {
    return this.tbsCertList.getThisUpdate();
  }
  
  public Time getNextUpdate() {
    return this.tbsCertList.getNextUpdate();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.tbsCertList);
    aSN1EncodableVector.add((ASN1Encodable)this.sigAlgId);
    aSN1EncodableVector.add((ASN1Encodable)this.sig);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public int hashCode() {
    if (!this.isHashCodeSet) {
      this.hashCodeValue = super.hashCode();
      this.isHashCodeSet = true;
    } 
    return this.hashCodeValue;
  }
}
