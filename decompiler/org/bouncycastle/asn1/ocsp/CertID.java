package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertID extends ASN1Object {
  AlgorithmIdentifier hashAlgorithm;
  
  ASN1OctetString issuerNameHash;
  
  ASN1OctetString issuerKeyHash;
  
  ASN1Integer serialNumber;
  
  public CertID(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString1, ASN1OctetString paramASN1OctetString2, ASN1Integer paramASN1Integer) {
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.issuerNameHash = paramASN1OctetString1;
    this.issuerKeyHash = paramASN1OctetString2;
    this.serialNumber = paramASN1Integer;
  }
  
  private CertID(ASN1Sequence paramASN1Sequence) {
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.issuerNameHash = (ASN1OctetString)paramASN1Sequence.getObjectAt(1);
    this.issuerKeyHash = (ASN1OctetString)paramASN1Sequence.getObjectAt(2);
    this.serialNumber = (ASN1Integer)paramASN1Sequence.getObjectAt(3);
  }
  
  public static CertID getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static CertID getInstance(Object paramObject) {
    return (paramObject instanceof CertID) ? (CertID)paramObject : ((paramObject != null) ? new CertID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public ASN1OctetString getIssuerNameHash() {
    return this.issuerNameHash;
  }
  
  public ASN1OctetString getIssuerKeyHash() {
    return this.issuerKeyHash;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.issuerNameHash);
    aSN1EncodableVector.add((ASN1Encodable)this.issuerKeyHash);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
