package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class ASN1Integer extends ASN1Primitive {
  private final byte[] bytes;
  
  public static ASN1Integer getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1Integer)
      return (ASN1Integer)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (ASN1Integer)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1Integer getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1Integer) ? getInstance(aSN1Primitive) : new ASN1Integer(ASN1OctetString.getInstance(paramASN1TaggedObject.getObject()).getOctets());
  }
  
  public ASN1Integer(long paramLong) {
    this.bytes = BigInteger.valueOf(paramLong).toByteArray();
  }
  
  public ASN1Integer(BigInteger paramBigInteger) {
    this.bytes = paramBigInteger.toByteArray();
  }
  
  public ASN1Integer(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, true);
  }
  
  ASN1Integer(byte[] paramArrayOfbyte, boolean paramBoolean) {
    if (!Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer") && isMalformed(paramArrayOfbyte))
      throw new IllegalArgumentException("malformed integer"); 
    this.bytes = paramBoolean ? Arrays.clone(paramArrayOfbyte) : paramArrayOfbyte;
  }
  
  static boolean isMalformed(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length > 1) {
      if (paramArrayOfbyte[0] == 0 && (paramArrayOfbyte[1] & 0x80) == 0)
        return true; 
      if (paramArrayOfbyte[0] == -1 && (paramArrayOfbyte[1] & 0x80) != 0)
        return true; 
    } 
    return false;
  }
  
  public BigInteger getValue() {
    return new BigInteger(this.bytes);
  }
  
  public BigInteger getPositiveValue() {
    return new BigInteger(1, this.bytes);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.bytes.length) + this.bytes.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(2, this.bytes);
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b != this.bytes.length; b++)
      i ^= (this.bytes[b] & 0xFF) << b % 4; 
    return i;
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1Integer))
      return false; 
    ASN1Integer aSN1Integer = (ASN1Integer)paramASN1Primitive;
    return Arrays.areEqual(this.bytes, aSN1Integer.bytes);
  }
  
  public String toString() {
    return getValue().toString();
  }
}
