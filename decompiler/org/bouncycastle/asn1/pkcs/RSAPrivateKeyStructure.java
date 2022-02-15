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

public class RSAPrivateKeyStructure extends ASN1Object {
  private int version;
  
  private BigInteger modulus;
  
  private BigInteger publicExponent;
  
  private BigInteger privateExponent;
  
  private BigInteger prime1;
  
  private BigInteger prime2;
  
  private BigInteger exponent1;
  
  private BigInteger exponent2;
  
  private BigInteger coefficient;
  
  private ASN1Sequence otherPrimeInfos = null;
  
  public static RSAPrivateKeyStructure getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static RSAPrivateKeyStructure getInstance(Object paramObject) {
    if (paramObject instanceof RSAPrivateKeyStructure)
      return (RSAPrivateKeyStructure)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new RSAPrivateKeyStructure((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public RSAPrivateKeyStructure(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8) {
    this.version = 0;
    this.modulus = paramBigInteger1;
    this.publicExponent = paramBigInteger2;
    this.privateExponent = paramBigInteger3;
    this.prime1 = paramBigInteger4;
    this.prime2 = paramBigInteger5;
    this.exponent1 = paramBigInteger6;
    this.exponent2 = paramBigInteger7;
    this.coefficient = paramBigInteger8;
  }
  
  public RSAPrivateKeyStructure(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    BigInteger bigInteger = ((ASN1Integer)enumeration.nextElement()).getValue();
    if (bigInteger.intValue() != 0 && bigInteger.intValue() != 1)
      throw new IllegalArgumentException("wrong version for RSA private key"); 
    this.version = bigInteger.intValue();
    this.modulus = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.publicExponent = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.privateExponent = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.prime1 = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.prime2 = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.exponent1 = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.exponent2 = ((ASN1Integer)enumeration.nextElement()).getValue();
    this.coefficient = ((ASN1Integer)enumeration.nextElement()).getValue();
    if (enumeration.hasMoreElements())
      this.otherPrimeInfos = (ASN1Sequence)enumeration.nextElement(); 
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPublicExponent() {
    return this.publicExponent;
  }
  
  public BigInteger getPrivateExponent() {
    return this.privateExponent;
  }
  
  public BigInteger getPrime1() {
    return this.prime1;
  }
  
  public BigInteger getPrime2() {
    return this.prime2;
  }
  
  public BigInteger getExponent1() {
    return this.exponent1;
  }
  
  public BigInteger getExponent2() {
    return this.exponent2;
  }
  
  public BigInteger getCoefficient() {
    return this.coefficient;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.version));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getModulus()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getPublicExponent()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getPrivateExponent()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getPrime1()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getPrime2()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getExponent1()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getExponent2()));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(getCoefficient()));
    if (this.otherPrimeInfos != null)
      aSN1EncodableVector.add((ASN1Encodable)this.otherPrimeInfos); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
