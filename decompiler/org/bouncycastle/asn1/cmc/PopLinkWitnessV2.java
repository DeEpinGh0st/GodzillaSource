package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PopLinkWitnessV2 extends ASN1Object {
  private final AlgorithmIdentifier keyGenAlgorithm;
  
  private final AlgorithmIdentifier macAlgorithm;
  
  private final byte[] witness;
  
  public PopLinkWitnessV2(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) {
    this.keyGenAlgorithm = paramAlgorithmIdentifier1;
    this.macAlgorithm = paramAlgorithmIdentifier2;
    this.witness = Arrays.clone(paramArrayOfbyte);
  }
  
  private PopLinkWitnessV2(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.keyGenAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.macAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.witness = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets());
  }
  
  public static PopLinkWitnessV2 getInstance(Object paramObject) {
    return (paramObject instanceof PopLinkWitnessV2) ? (PopLinkWitnessV2)paramObject : ((paramObject != null) ? new PopLinkWitnessV2(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getKeyGenAlgorithm() {
    return this.keyGenAlgorithm;
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlgorithm;
  }
  
  public byte[] getWitness() {
    return Arrays.clone(this.witness);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyGenAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.macAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(getWitness()));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
