package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;

public class TBSCertificate extends ASN1Object {
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
  
  Extensions extensions;
  
  public static TBSCertificate getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static TBSCertificate getInstance(Object paramObject) {
    return (paramObject instanceof TBSCertificate) ? (TBSCertificate)paramObject : ((paramObject != null) ? new TBSCertificate(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private TBSCertificate(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.seq = paramASN1Sequence;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
      this.version = ASN1Integer.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), true);
    } else {
      b = -1;
      this.version = new ASN1Integer(0L);
    } 
    boolean bool1 = false;
    boolean bool2 = false;
    if (this.version.getValue().equals(BigInteger.valueOf(0L))) {
      bool1 = true;
    } else if (this.version.getValue().equals(BigInteger.valueOf(1L))) {
      bool2 = true;
    } else if (!this.version.getValue().equals(BigInteger.valueOf(2L))) {
      throw new IllegalArgumentException("version number not recognised");
    } 
    this.serialNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b + 1));
    this.signature = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b + 2));
    this.issuer = X500Name.getInstance(paramASN1Sequence.getObjectAt(b + 3));
    ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(b + 4);
    this.startDate = Time.getInstance(aSN1Sequence.getObjectAt(0));
    this.endDate = Time.getInstance(aSN1Sequence.getObjectAt(1));
    this.subject = X500Name.getInstance(paramASN1Sequence.getObjectAt(b + 5));
    this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(paramASN1Sequence.getObjectAt(b + 6));
    int i = paramASN1Sequence.size() - b + 6 - 1;
    if (i != 0 && bool1)
      throw new IllegalArgumentException("version 1 certificate contains extra data"); 
    while (i > 0) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(b + 6 + i);
      switch (aSN1TaggedObject.getTagNo()) {
        case 1:
          this.issuerUniqueId = DERBitString.getInstance(aSN1TaggedObject, false);
          break;
        case 2:
          this.subjectUniqueId = DERBitString.getInstance(aSN1TaggedObject, false);
          break;
        case 3:
          if (bool2)
            throw new IllegalArgumentException("version 2 certificate cannot contain extensions"); 
          this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, true));
          break;
      } 
      i--;
    } 
  }
  
  public int getVersionNumber() {
    return this.version.getValue().intValue() + 1;
  }
  
  public ASN1Integer getVersion() {
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
  
  public Extensions getExtensions() {
    return this.extensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
