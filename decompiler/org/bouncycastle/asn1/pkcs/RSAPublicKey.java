package org.bouncycastle.asn1.pkcs;

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

public class RSAPublicKey extends ASN1Object {
  private BigInteger modulus;
  
  private BigInteger publicExponent;
  
  public static RSAPublicKey getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static RSAPublicKey getInstance(Object paramObject) {
    return (paramObject instanceof RSAPublicKey) ? (RSAPublicKey)paramObject : ((paramObject != null) ? new RSAPublicKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RSAPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.modulus = paramBigInteger1;
    this.publicExponent = paramBigInteger2;
  }
  
  private RSAPublicKey(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.modulus = ASN1Integer.getInstance(enumeration.nextElement()).getPositiveValue();
    this.publicExponent = ASN1Integer.getInstance(enumeration.nextElement()).getPositiveValue();
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPublicExponent() {
    return this.publicExponent;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getModulus()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getPublicExponent()));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
