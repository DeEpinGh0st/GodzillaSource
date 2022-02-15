package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class UnsignedInteger extends ASN1Object {
  private int tagNo;
  
  private BigInteger value;
  
  public UnsignedInteger(int paramInt, BigInteger paramBigInteger) {
    this.tagNo = paramInt;
    this.value = paramBigInteger;
  }
  
  private UnsignedInteger(ASN1TaggedObject paramASN1TaggedObject) {
    this.tagNo = paramASN1TaggedObject.getTagNo();
    this.value = new BigInteger(1, ASN1OctetString.getInstance(paramASN1TaggedObject, false).getOctets());
  }
  
  public static UnsignedInteger getInstance(Object paramObject) {
    return (paramObject instanceof UnsignedInteger) ? (UnsignedInteger)paramObject : ((paramObject != null) ? new UnsignedInteger(ASN1TaggedObject.getInstance(paramObject)) : null);
  }
  
  private byte[] convertValue() {
    byte[] arrayOfByte = this.value.toByteArray();
    if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  public int getTagNo() {
    return this.tagNo;
  }
  
  public BigInteger getValue() {
    return this.value;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.tagNo, (ASN1Encodable)new DEROctetString(convertValue()));
  }
}
