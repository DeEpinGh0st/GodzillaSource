package org.bouncycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ElGamalParameter extends ASN1Object {
  ASN1Integer p;
  
  ASN1Integer g;
  
  public ElGamalParameter(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.p = new ASN1Integer(paramBigInteger1);
    this.g = new ASN1Integer(paramBigInteger2);
  }
  
  private ElGamalParameter(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    this.p = enumeration.nextElement();
    this.g = enumeration.nextElement();
  }
  
  public static ElGamalParameter getInstance(Object paramObject) {
    return (paramObject instanceof ElGamalParameter) ? (ElGamalParameter)paramObject : ((paramObject != null) ? new ElGamalParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BigInteger getP() {
    return this.p.getPositiveValue();
  }
  
  public BigInteger getG() {
    return this.g.getPositiveValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
