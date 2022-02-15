package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;

public class SignaturePolicyIdentifier extends ASN1Object {
  private SignaturePolicyId signaturePolicyId;
  
  private boolean isSignaturePolicyImplied;
  
  public static SignaturePolicyIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof SignaturePolicyIdentifier) ? (SignaturePolicyIdentifier)paramObject : ((paramObject instanceof org.bouncycastle.asn1.ASN1Null || hasEncodedTagValue(paramObject, 5)) ? new SignaturePolicyIdentifier() : ((paramObject != null) ? new SignaturePolicyIdentifier(SignaturePolicyId.getInstance(paramObject)) : null));
  }
  
  public SignaturePolicyIdentifier() {
    this.isSignaturePolicyImplied = true;
  }
  
  public SignaturePolicyIdentifier(SignaturePolicyId paramSignaturePolicyId) {
    this.signaturePolicyId = paramSignaturePolicyId;
    this.isSignaturePolicyImplied = false;
  }
  
  public SignaturePolicyId getSignaturePolicyId() {
    return this.signaturePolicyId;
  }
  
  public boolean isSignaturePolicyImplied() {
    return this.isSignaturePolicyImplied;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)(this.isSignaturePolicyImplied ? DERNull.INSTANCE : this.signaturePolicyId.toASN1Primitive());
  }
}
