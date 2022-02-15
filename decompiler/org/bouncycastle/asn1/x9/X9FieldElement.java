package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECFieldElement;

public class X9FieldElement extends ASN1Object {
  protected ECFieldElement f;
  
  private static X9IntegerConverter converter = new X9IntegerConverter();
  
  public X9FieldElement(ECFieldElement paramECFieldElement) {
    this.f = paramECFieldElement;
  }
  
  public X9FieldElement(BigInteger paramBigInteger, ASN1OctetString paramASN1OctetString) {
    this((ECFieldElement)new ECFieldElement.Fp(paramBigInteger, new BigInteger(1, paramASN1OctetString.getOctets())));
  }
  
  public X9FieldElement(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ASN1OctetString paramASN1OctetString) {
    this((ECFieldElement)new ECFieldElement.F2m(paramInt1, paramInt2, paramInt3, paramInt4, new BigInteger(1, paramASN1OctetString.getOctets())));
  }
  
  public ECFieldElement getValue() {
    return this.f;
  }
  
  public ASN1Primitive toASN1Primitive() {
    int i = converter.getByteLength(this.f);
    byte[] arrayOfByte = converter.integerToBytes(this.f.toBigInteger(), i);
    return (ASN1Primitive)new DEROctetString(arrayOfByte);
  }
}
