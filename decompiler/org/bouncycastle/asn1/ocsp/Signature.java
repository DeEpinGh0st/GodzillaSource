package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Signature extends ASN1Object {
  AlgorithmIdentifier signatureAlgorithm;
  
  DERBitString signature;
  
  ASN1Sequence certs;
  
  public Signature(AlgorithmIdentifier paramAlgorithmIdentifier, DERBitString paramDERBitString) {
    this.signatureAlgorithm = paramAlgorithmIdentifier;
    this.signature = paramDERBitString;
  }
  
  public Signature(AlgorithmIdentifier paramAlgorithmIdentifier, DERBitString paramDERBitString, ASN1Sequence paramASN1Sequence) {
    this.signatureAlgorithm = paramAlgorithmIdentifier;
    this.signature = paramDERBitString;
    this.certs = paramASN1Sequence;
  }
  
  private Signature(ASN1Sequence paramASN1Sequence) {
    this.signatureAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.signature = (DERBitString)paramASN1Sequence.getObjectAt(1);
    if (paramASN1Sequence.size() == 3)
      this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(2), true); 
  }
  
  public static Signature getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static Signature getInstance(Object paramObject) {
    return (paramObject instanceof Signature) ? (Signature)paramObject : ((paramObject != null) ? new Signature(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.signatureAlgorithm;
  }
  
  public DERBitString getSignature() {
    return this.signature;
  }
  
  public ASN1Sequence getCerts() {
    return this.certs;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.signatureAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    if (this.certs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.certs)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
