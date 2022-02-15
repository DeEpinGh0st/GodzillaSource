package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class SigningCertificate extends ASN1Object {
  ASN1Sequence certs;
  
  ASN1Sequence policies;
  
  public static SigningCertificate getInstance(Object paramObject) {
    return (paramObject instanceof SigningCertificate) ? (SigningCertificate)paramObject : ((paramObject != null) ? new SigningCertificate(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SigningCertificate(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.certs = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.policies = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public SigningCertificate(ESSCertID paramESSCertID) {
    this.certs = (ASN1Sequence)new DERSequence((ASN1Encodable)paramESSCertID);
  }
  
  public ESSCertID[] getCerts() {
    ESSCertID[] arrayOfESSCertID = new ESSCertID[this.certs.size()];
    for (byte b = 0; b != this.certs.size(); b++)
      arrayOfESSCertID[b] = ESSCertID.getInstance(this.certs.getObjectAt(b)); 
    return arrayOfESSCertID;
  }
  
  public PolicyInformation[] getPolicies() {
    if (this.policies == null)
      return null; 
    PolicyInformation[] arrayOfPolicyInformation = new PolicyInformation[this.policies.size()];
    for (byte b = 0; b != this.policies.size(); b++)
      arrayOfPolicyInformation[b] = PolicyInformation.getInstance(this.policies.getObjectAt(b)); 
    return arrayOfPolicyInformation;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certs);
    if (this.policies != null)
      aSN1EncodableVector.add((ASN1Encodable)this.policies); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
