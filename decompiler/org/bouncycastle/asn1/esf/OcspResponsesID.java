package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OcspResponsesID extends ASN1Object {
  private OcspIdentifier ocspIdentifier;
  
  private OtherHash ocspRepHash;
  
  public static OcspResponsesID getInstance(Object paramObject) {
    return (paramObject instanceof OcspResponsesID) ? (OcspResponsesID)paramObject : ((paramObject != null) ? new OcspResponsesID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OcspResponsesID(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.ocspIdentifier = OcspIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.ocspRepHash = OtherHash.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public OcspResponsesID(OcspIdentifier paramOcspIdentifier) {
    this(paramOcspIdentifier, null);
  }
  
  public OcspResponsesID(OcspIdentifier paramOcspIdentifier, OtherHash paramOtherHash) {
    this.ocspIdentifier = paramOcspIdentifier;
    this.ocspRepHash = paramOtherHash;
  }
  
  public OcspIdentifier getOcspIdentifier() {
    return this.ocspIdentifier;
  }
  
  public OtherHash getOcspRepHash() {
    return this.ocspRepHash;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.ocspIdentifier);
    if (null != this.ocspRepHash)
      aSN1EncodableVector.add((ASN1Encodable)this.ocspRepHash); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
