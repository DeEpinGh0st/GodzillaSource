package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OtherHashAlgAndValue extends ASN1Object {
  private AlgorithmIdentifier hashAlgorithm;
  
  private ASN1OctetString hashValue;
  
  public static OtherHashAlgAndValue getInstance(Object paramObject) {
    return (paramObject instanceof OtherHashAlgAndValue) ? (OtherHashAlgAndValue)paramObject : ((paramObject != null) ? new OtherHashAlgAndValue(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OtherHashAlgAndValue(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.hashValue = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public OtherHashAlgAndValue(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.hashValue = paramASN1OctetString;
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public ASN1OctetString getHashValue() {
    return this.hashValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.hashValue);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
