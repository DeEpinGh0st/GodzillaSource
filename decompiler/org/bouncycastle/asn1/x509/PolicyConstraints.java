package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class PolicyConstraints extends ASN1Object {
  private BigInteger requireExplicitPolicyMapping;
  
  private BigInteger inhibitPolicyMapping;
  
  public PolicyConstraints(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.requireExplicitPolicyMapping = paramBigInteger1;
    this.inhibitPolicyMapping = paramBigInteger2;
  }
  
  private PolicyConstraints(ASN1Sequence paramASN1Sequence) {
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b));
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.requireExplicitPolicyMapping = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue();
      } else if (aSN1TaggedObject.getTagNo() == 1) {
        this.inhibitPolicyMapping = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue();
      } else {
        throw new IllegalArgumentException("Unknown tag encountered.");
      } 
    } 
  }
  
  public static PolicyConstraints getInstance(Object paramObject) {
    return (paramObject instanceof PolicyConstraints) ? (PolicyConstraints)paramObject : ((paramObject != null) ? new PolicyConstraints(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static PolicyConstraints fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.policyConstraints));
  }
  
  public BigInteger getRequireExplicitPolicyMapping() {
    return this.requireExplicitPolicyMapping;
  }
  
  public BigInteger getInhibitPolicyMapping() {
    return this.inhibitPolicyMapping;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.requireExplicitPolicyMapping != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(this.requireExplicitPolicyMapping))); 
    if (this.inhibitPolicyMapping != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new ASN1Integer(this.inhibitPolicyMapping))); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
