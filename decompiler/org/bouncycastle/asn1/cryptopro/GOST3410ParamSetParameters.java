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

public class GOST3410ParamSetParameters extends ASN1Object {
  int keySize;
  
  ASN1Integer p;
  
  ASN1Integer q;
  
  ASN1Integer a;
  
  public static GOST3410ParamSetParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static GOST3410ParamSetParameters getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof GOST3410ParamSetParameters)
      return (GOST3410ParamSetParameters)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new GOST3410ParamSetParameters((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid GOST3410Parameter: " + paramObject.getClass().getName());
  }
  
  public GOST3410ParamSetParameters(int paramInt, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this.keySize = paramInt;
    this.p = new ASN1Integer(paramBigInteger1);
    this.q = new ASN1Integer(paramBigInteger2);
    this.a = new ASN1Integer(paramBigInteger3);
  }
  
  public GOST3410ParamSetParameters(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    this.keySize = ((ASN1Integer)enumeration.nextElement()).getValue().intValue();
    this.p = enumeration.nextElement();
    this.q = enumeration.nextElement();
    this.a = enumeration.nextElement();
  }
  
  public int getLKeySize() {
    return this.keySize;
  }
  
  public int getKeySize() {
    return this.keySize;
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
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.keySize));
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.q);
    aSN1EncodableVector.add((ASN1Encodable)this.a);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
