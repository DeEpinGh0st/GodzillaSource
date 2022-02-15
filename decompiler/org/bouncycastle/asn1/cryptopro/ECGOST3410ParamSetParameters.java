package org.bouncycastle.asn1.cryptopro;

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

public class ECGOST3410ParamSetParameters extends ASN1Object {
  ASN1Integer p;
  
  ASN1Integer q;
  
  ASN1Integer a;
  
  ASN1Integer b;
  
  ASN1Integer x;
  
  ASN1Integer y;
  
  public static ECGOST3410ParamSetParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static ECGOST3410ParamSetParameters getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ECGOST3410ParamSetParameters)
      return (ECGOST3410ParamSetParameters)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new ECGOST3410ParamSetParameters((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid GOST3410Parameter: " + paramObject.getClass().getName());
  }
  
  public ECGOST3410ParamSetParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, int paramInt, BigInteger paramBigInteger5) {
    this.a = new ASN1Integer(paramBigInteger1);
    this.b = new ASN1Integer(paramBigInteger2);
    this.p = new ASN1Integer(paramBigInteger3);
    this.q = new ASN1Integer(paramBigInteger4);
    this.x = new ASN1Integer(paramInt);
    this.y = new ASN1Integer(paramBigInteger5);
  }
  
  public ECGOST3410ParamSetParameters(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    this.a = enumeration.nextElement();
    this.b = enumeration.nextElement();
    this.p = enumeration.nextElement();
    this.q = enumeration.nextElement();
    this.x = enumeration.nextElement();
    this.y = enumeration.nextElement();
  }
  
  public BigInteger getP() {
    return this.p.getPositiveValue();
  }
  
  public BigInteger getQ() {
    return this.q.getPositiveValue();
  }
  
  public BigInteger getA() {
    return this.a.getPositiveValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.a);
    aSN1EncodableVector.add((ASN1Encodable)this.b);
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.q);
    aSN1EncodableVector.add((ASN1Encodable)this.x);
    aSN1EncodableVector.add((ASN1Encodable)this.y);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
