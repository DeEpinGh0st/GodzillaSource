package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericHybridParameters extends ASN1Object {
  private final AlgorithmIdentifier kem;
  
  private final AlgorithmIdentifier dem;
  
  private GenericHybridParameters(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2"); 
    this.kem = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.dem = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static GenericHybridParameters getInstance(Object paramObject) {
    return (paramObject instanceof GenericHybridParameters) ? (GenericHybridParameters)paramObject : ((paramObject != null) ? new GenericHybridParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public GenericHybridParameters(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    this.kem = paramAlgorithmIdentifier1;
    this.dem = paramAlgorithmIdentifier2;
  }
  
  public AlgorithmIdentifier getDem() {
    return this.dem;
  }
  
  public AlgorithmIdentifier getKem() {
    return this.kem;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.kem);
    aSN1EncodableVector.add((ASN1Encodable)this.dem);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
