package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class ASN1Enumerated extends ASN1Primitive {
  private final byte[] bytes;
  
  private static ASN1Enumerated[] cache = new ASN1Enumerated[12];
  
  public static ASN1Enumerated getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1Enumerated)
      return (ASN1Enumerated)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (ASN1Enumerated)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1Enumerated getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1Enumerated) ? getInstance(aSN1Primitive) : fromOctetString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  public ASN1Enumerated(int paramInt) {
    this.bytes = BigInteger.valueOf(paramInt).toByteArray();
  }
  
  public ASN1Enumerated(BigInteger paramBigInteger) {
    this.bytes = paramBigInteger.toByteArray();
  }
  
  public ASN1Enumerated(byte[] paramArrayOfbyte) {
    if (!Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer") && ASN1Integer.isMalformed(paramArrayOfbyte))
      throw new IllegalArgumentException("malformed enumerated"); 
    this.bytes = Arrays.clone(paramArrayOfbyte);
  }
  
  public BigInteger getValue() {
    return new BigInteger(this.bytes);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.bytes.length) + this.bytes.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(10, this.bytes);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1Enumerated))
      return false; 
    ASN1Enumerated aSN1Enumerated = (ASN1Enumerated)paramASN1Primitive;
    return Arrays.areEqual(this.bytes, aSN1Enumerated.bytes);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.bytes);
  }
  
  static ASN1Enumerated fromOctetString(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length > 1)
      return new ASN1Enumerated(paramArrayOfbyte); 
    if (paramArrayOfbyte.length == 0)
      throw new IllegalArgumentException("ENUMERATED has zero length"); 
    int i = paramArrayOfbyte[0] & 0xFF;
    if (i >= cache.length)
      return new ASN1Enumerated(Arrays.clone(paramArrayOfbyte)); 
    ASN1Enumerated aSN1Enumerated = cache[i];
    if (aSN1Enumerated == null)
      aSN1Enumerated = cache[i] = new ASN1Enumerated(Arrays.clone(paramArrayOfbyte)); 
    return aSN1Enumerated;
  }
}
