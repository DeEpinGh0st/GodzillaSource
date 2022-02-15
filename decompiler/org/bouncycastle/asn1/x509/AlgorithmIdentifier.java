package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class AlgorithmIdentifier extends ASN1Object {
  private ASN1ObjectIdentifier algorithm;
  
  private ASN1Encodable parameters;
  
  public static AlgorithmIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static AlgorithmIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof AlgorithmIdentifier) ? (AlgorithmIdentifier)paramObject : ((paramObject != null) ? new AlgorithmIdentifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.algorithm = paramASN1ObjectIdentifier;
  }
  
  public AlgorithmIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.algorithm = paramASN1ObjectIdentifier;
    this.parameters = paramASN1Encodable;
  }
  
  private AlgorithmIdentifier(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.algorithm = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2) {
      this.parameters = paramASN1Sequence.getObjectAt(1);
    } else {
      this.parameters = null;
    } 
  }
  
  public ASN1ObjectIdentifier getAlgorithm() {
    return this.algorithm;
  }
  
  public ASN1Encodable getParameters() {
    return this.parameters;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.algorithm);
    if (this.parameters != null)
      aSN1EncodableVector.add(this.parameters); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
