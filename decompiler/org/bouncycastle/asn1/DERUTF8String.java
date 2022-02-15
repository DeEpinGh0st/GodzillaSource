package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERUTF8String extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERUTF8String getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERUTF8String)
      return (DERUTF8String)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERUTF8String)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERUTF8String getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERUTF8String) ? getInstance(aSN1Primitive) : new DERUTF8String(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  DERUTF8String(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERUTF8String(String paramString) {
    this.string = Strings.toUTF8ByteArray(paramString);
  }
  
  public String getString() {
    return Strings.fromUTF8ByteArray(this.string);
  }
  
  public String toString() {
    return getString();
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERUTF8String))
      return false; 
    DERUTF8String dERUTF8String = (DERUTF8String)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERUTF8String.string);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() throws IOException {
    return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(12, this.string);
  }
}
