package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class RSAPublicKey extends PublicKeyDataObject {
  private ASN1ObjectIdentifier usage;
  
  private BigInteger modulus;
  
  private BigInteger exponent;
  
  private int valid = 0;
  
  private static int modulusValid = 1;
  
  private static int exponentValid = 2;
  
  RSAPublicKey(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.usage = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      UnsignedInteger unsignedInteger = UnsignedInteger.getInstance(enumeration.nextElement());
      switch (unsignedInteger.getTagNo()) {
        case 1:
          setModulus(unsignedInteger);
          continue;
        case 2:
          setExponent(unsignedInteger);
          continue;
      } 
      throw new IllegalArgumentException("Unknown DERTaggedObject :" + unsignedInteger.getTagNo() + "-> not an Iso7816RSAPublicKeyStructure");
    } 
    if (this.valid != 3)
      throw new IllegalArgumentException("missing argument -> not an Iso7816RSAPublicKeyStructure"); 
  }
  
  public RSAPublicKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.usage = paramASN1ObjectIdentifier;
    this.modulus = paramBigInteger1;
    this.exponent = paramBigInteger2;
  }
  
  public ASN1ObjectIdentifier getUsage() {
    return this.usage;
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPublicExponent() {
    return this.exponent;
  }
  
  private void setModulus(UnsignedInteger paramUnsignedInteger) {
    if ((this.valid & modulusValid) == 0) {
      this.valid |= modulusValid;
      this.modulus = paramUnsignedInteger.getValue();
    } else {
      throw new IllegalArgumentException("Modulus already set");
    } 
  }
  
  private void setExponent(UnsignedInteger paramUnsignedInteger) {
    if ((this.valid & exponentValid) == 0) {
      this.valid |= exponentValid;
      this.exponent = paramUnsignedInteger.getValue();
    } else {
      throw new IllegalArgumentException("Exponent already set");
    } 
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.usage);
    aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(1, getModulus()));
    aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(2, getPublicExponent()));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
