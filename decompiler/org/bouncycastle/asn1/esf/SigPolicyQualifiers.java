package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SigPolicyQualifiers extends ASN1Object {
  ASN1Sequence qualifiers;
  
  public static SigPolicyQualifiers getInstance(Object paramObject) {
    return (paramObject instanceof SigPolicyQualifiers) ? (SigPolicyQualifiers)paramObject : ((paramObject instanceof ASN1Sequence) ? new SigPolicyQualifiers(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SigPolicyQualifiers(ASN1Sequence paramASN1Sequence) {
    this.qualifiers = paramASN1Sequence;
  }
  
  public SigPolicyQualifiers(SigPolicyQualifierInfo[] paramArrayOfSigPolicyQualifierInfo) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfSigPolicyQualifierInfo.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfSigPolicyQualifierInfo[b]); 
    this.qualifiers = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public int size() {
    return this.qualifiers.size();
  }
  
  public SigPolicyQualifierInfo getInfoAt(int paramInt) {
    return SigPolicyQualifierInfo.getInstance(this.qualifiers.getObjectAt(paramInt));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.qualifiers;
  }
}
