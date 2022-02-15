package org.bouncycastle.asn1.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RsaKemParameters extends ASN1Object {
  private final AlgorithmIdentifier keyDerivationFunction;
  
  private final BigInteger keyLength;
  
  private RsaKemParameters(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2"); 
    this.keyDerivationFunction = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.keyLength = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1)).getValue();
  }
  
  public static RsaKemParameters getInstance(Object paramObject) {
    return (paramObject instanceof RsaKemParameters) ? (RsaKemParameters)paramObject : ((paramObject != null) ? new RsaKemParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RsaKemParameters(AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt) {
    this.keyDerivationFunction = paramAlgorithmIdentifier;
    this.keyLength = BigInteger.valueOf(paramInt);
  }
  
  public AlgorithmIdentifier getKeyDerivationFunction() {
    return this.keyDerivationFunction;
  }
  
  public BigInteger getKeyLength() {
    return this.keyLength;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyDerivationFunction);
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.keyLength));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
