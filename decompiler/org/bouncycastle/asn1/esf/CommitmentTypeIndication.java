package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CommitmentTypeIndication extends ASN1Object {
  private ASN1ObjectIdentifier commitmentTypeId;
  
  private ASN1Sequence commitmentTypeQualifier;
  
  private CommitmentTypeIndication(ASN1Sequence paramASN1Sequence) {
    this.commitmentTypeId = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    if (paramASN1Sequence.size() > 1)
      this.commitmentTypeQualifier = (ASN1Sequence)paramASN1Sequence.getObjectAt(1); 
  }
  
  public CommitmentTypeIndication(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.commitmentTypeId = paramASN1ObjectIdentifier;
  }
  
  public CommitmentTypeIndication(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Sequence paramASN1Sequence) {
    this.commitmentTypeId = paramASN1ObjectIdentifier;
    this.commitmentTypeQualifier = paramASN1Sequence;
  }
  
  public static CommitmentTypeIndication getInstance(Object paramObject) {
    return (paramObject == null || paramObject instanceof CommitmentTypeIndication) ? (CommitmentTypeIndication)paramObject : new CommitmentTypeIndication(ASN1Sequence.getInstance(paramObject));
  }
  
  public ASN1ObjectIdentifier getCommitmentTypeId() {
    return this.commitmentTypeId;
  }
  
  public ASN1Sequence getCommitmentTypeQualifier() {
    return this.commitmentTypeQualifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.commitmentTypeId);
    if (this.commitmentTypeQualifier != null)
      aSN1EncodableVector.add((ASN1Encodable)this.commitmentTypeQualifier); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
