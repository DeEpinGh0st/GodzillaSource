package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CrlValidatedID extends ASN1Object {
  private OtherHash crlHash;
  
  private CrlIdentifier crlIdentifier;
  
  public static CrlValidatedID getInstance(Object paramObject) {
    return (paramObject instanceof CrlValidatedID) ? (CrlValidatedID)paramObject : ((paramObject != null) ? new CrlValidatedID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CrlValidatedID(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.crlHash = OtherHash.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.crlIdentifier = CrlIdentifier.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public CrlValidatedID(OtherHash paramOtherHash) {
    this(paramOtherHash, null);
  }
  
  public CrlValidatedID(OtherHash paramOtherHash, CrlIdentifier paramCrlIdentifier) {
    this.crlHash = paramOtherHash;
    this.crlIdentifier = paramCrlIdentifier;
  }
  
  public OtherHash getCrlHash() {
    return this.crlHash;
  }
  
  public CrlIdentifier getCrlIdentifier() {
    return this.crlIdentifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.crlHash.toASN1Primitive());
    if (null != this.crlIdentifier)
      aSN1EncodableVector.add((ASN1Encodable)this.crlIdentifier.toASN1Primitive()); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
