package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PolicyInformation extends ASN1Object {
  private ASN1ObjectIdentifier policyIdentifier;
  
  private ASN1Sequence policyQualifiers;
  
  private PolicyInformation(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.policyIdentifier = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.policyQualifiers = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public PolicyInformation(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.policyIdentifier = paramASN1ObjectIdentifier;
  }
  
  public PolicyInformation(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Sequence paramASN1Sequence) {
    this.policyIdentifier = paramASN1ObjectIdentifier;
    this.policyQualifiers = paramASN1Sequence;
  }
  
  public static PolicyInformation getInstance(Object paramObject) {
    return (paramObject == null || paramObject instanceof PolicyInformation) ? (PolicyInformation)paramObject : new PolicyInformation(ASN1Sequence.getInstance(paramObject));
  }
  
  public ASN1ObjectIdentifier getPolicyIdentifier() {
    return this.policyIdentifier;
  }
  
  public ASN1Sequence getPolicyQualifiers() {
    return this.policyQualifiers;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.policyIdentifier);
    if (this.policyQualifiers != null)
      aSN1EncodableVector.add((ASN1Encodable)this.policyQualifiers); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Policy information: ");
    stringBuffer.append(this.policyIdentifier);
    if (this.policyQualifiers != null) {
      StringBuffer stringBuffer1 = new StringBuffer();
      for (byte b = 0; b < this.policyQualifiers.size(); b++) {
        if (stringBuffer1.length() != 0)
          stringBuffer1.append(", "); 
        stringBuffer1.append(PolicyQualifierInfo.getInstance(this.policyQualifiers.getObjectAt(b)));
      } 
      stringBuffer.append("[");
      stringBuffer.append(stringBuffer1);
      stringBuffer.append("]");
    } 
    return stringBuffer.toString();
  }
}
