package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SignaturePolicyId extends ASN1Object {
  private ASN1ObjectIdentifier sigPolicyId;
  
  private OtherHashAlgAndValue sigPolicyHash;
  
  private SigPolicyQualifiers sigPolicyQualifiers;
  
  public static SignaturePolicyId getInstance(Object paramObject) {
    return (paramObject instanceof SignaturePolicyId) ? (SignaturePolicyId)paramObject : ((paramObject != null) ? new SignaturePolicyId(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SignaturePolicyId(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2 && paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.sigPolicyId = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.sigPolicyHash = OtherHashAlgAndValue.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() == 3)
      this.sigPolicyQualifiers = SigPolicyQualifiers.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public SignaturePolicyId(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OtherHashAlgAndValue paramOtherHashAlgAndValue) {
    this(paramASN1ObjectIdentifier, paramOtherHashAlgAndValue, null);
  }
  
  public SignaturePolicyId(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OtherHashAlgAndValue paramOtherHashAlgAndValue, SigPolicyQualifiers paramSigPolicyQualifiers) {
    this.sigPolicyId = paramASN1ObjectIdentifier;
    this.sigPolicyHash = paramOtherHashAlgAndValue;
    this.sigPolicyQualifiers = paramSigPolicyQualifiers;
  }
  
  public ASN1ObjectIdentifier getSigPolicyId() {
    return new ASN1ObjectIdentifier(this.sigPolicyId.getId());
  }
  
  public OtherHashAlgAndValue getSigPolicyHash() {
    return this.sigPolicyHash;
  }
  
  public SigPolicyQualifiers getSigPolicyQualifiers() {
    return this.sigPolicyQualifiers;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.sigPolicyId);
    aSN1EncodableVector.add((ASN1Encodable)this.sigPolicyHash);
    if (this.sigPolicyQualifiers != null)
      aSN1EncodableVector.add((ASN1Encodable)this.sigPolicyQualifiers); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
