package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CommitmentTypeQualifier extends ASN1Object {
  private ASN1ObjectIdentifier commitmentTypeIdentifier;
  
  private ASN1Encodable qualifier;
  
  public CommitmentTypeQualifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, null);
  }
  
  public CommitmentTypeQualifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.commitmentTypeIdentifier = paramASN1ObjectIdentifier;
    this.qualifier = paramASN1Encodable;
  }
  
  private CommitmentTypeQualifier(ASN1Sequence paramASN1Sequence) {
    this.commitmentTypeIdentifier = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    if (paramASN1Sequence.size() > 1)
      this.qualifier = paramASN1Sequence.getObjectAt(1); 
  }
  
  public static CommitmentTypeQualifier getInstance(Object paramObject) {
    return (paramObject instanceof CommitmentTypeQualifier) ? (CommitmentTypeQualifier)paramObject : ((paramObject != null) ? new CommitmentTypeQualifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getCommitmentTypeIdentifier() {
    return this.commitmentTypeIdentifier;
  }
  
  public ASN1Encodable getQualifier() {
    return this.qualifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.commitmentTypeIdentifier);
    if (this.qualifier != null)
      aSN1EncodableVector.add(this.qualifier); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
