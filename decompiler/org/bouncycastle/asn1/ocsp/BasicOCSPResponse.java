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

public class BasicOCSPResponse extends ASN1Object {
  private ResponseData tbsResponseData;
  
  private AlgorithmIdentifier signatureAlgorithm;
  
  private DERBitString signature;
  
  private ASN1Sequence certs;
  
  public BasicOCSPResponse(ResponseData paramResponseData, AlgorithmIdentifier paramAlgorithmIdentifier, DERBitString paramDERBitString, ASN1Sequence paramASN1Sequence) {
    this.tbsResponseData = paramResponseData;
    this.signatureAlgorithm = paramAlgorithmIdentifier;
    this.signature = paramDERBitString;
    this.certs = paramASN1Sequence;
  }
  
  private BasicOCSPResponse(ASN1Sequence paramASN1Sequence) {
    this.tbsResponseData = ResponseData.getInstance(paramASN1Sequence.getObjectAt(0));
    this.signatureAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.signature = (DERBitString)paramASN1Sequence.getObjectAt(2);
    if (paramASN1Sequence.size() > 3)
      this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(3), true); 
  }
  
  public static BasicOCSPResponse getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static BasicOCSPResponse getInstance(Object paramObject) {
    return (paramObject instanceof BasicOCSPResponse) ? (BasicOCSPResponse)paramObject : ((paramObject != null) ? new BasicOCSPResponse(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ResponseData getTbsResponseData() {
    return this.tbsResponseData;
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
    aSN1EncodableVector.add((ASN1Encodable)this.tbsResponseData);
    aSN1EncodableVector.add((ASN1Encodable)this.signatureAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    if (this.certs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.certs)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
