package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERVideotexString extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERVideotexString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERVideotexString)
      return (DERVideotexString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERVideotexString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERVideotexString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERVideotexString) ? getInstance(aSN1Primitive) : new DERVideotexString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  public DERVideotexString(byte[] paramArrayOfbyte) {
    this.string = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getOctets() {
    return Arrays.clone(this.string);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(21, this.string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERVideotexString))
      return false; 
    DERVideotexString dERVideotexString = (DERVideotexString)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERVideotexString.string);
  }
  
  public String getString() {
    return Strings.fromByteArray(this.string);
  }
}
