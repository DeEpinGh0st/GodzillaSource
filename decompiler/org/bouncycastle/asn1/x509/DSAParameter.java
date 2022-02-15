package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class DSAParameter extends ASN1Object {
  ASN1Integer p;
  
  ASN1Integer q;
  
  ASN1Integer g;
  
  public static DSAParameter getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DSAParameter getInstance(Object paramObject) {
    return (paramObject instanceof DSAParameter) ? (DSAParameter)paramObject : ((paramObject != null) ? new DSAParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DSAParameter(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this.p = new ASN1Integer(paramBigInteger1);
    this.q = new ASN1Integer(paramBigInteger2);
    this.g = new ASN1Integer(paramBigInteger3);
  }
  
  private DSAParameter(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.p = ASN1Integer.getInstance(enumeration.nextElement());
    this.q = ASN1Integer.getInstance(enumeration.nextElement());
    this.g = ASN1Integer.getInstance(enumeration.nextElement());
  }
  
  public BigInteger getP() {
    return this.p.getPositiveValue();
  }
  
  public BigInteger getQ() {
    return this.q.getPositiveValue();
  }
  
  public BigInteger getG() {
    return this.g.getPositiveValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.q);
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
