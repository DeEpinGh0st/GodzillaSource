package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERVisibleString extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERVisibleString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERVisibleString)
      return (DERVisibleString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERVisibleString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERVisibleString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERVisibleString) ? getInstance(aSN1Primitive) : new DERVisibleString(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  DERVisibleString(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERVisibleString(String paramString) {
    this.string = Strings.toByteArray(paramString);
  }
  
  public String getString() {
    return Strings.fromByteArray(this.string);
  }
  
  public String toString() {
    return getString();
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
    paramASN1OutputStream.writeEncoded(26, this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !(paramASN1Primitive instanceof DERVisibleString) ? false : Arrays.areEqual(this.string, ((DERVisibleString)paramASN1Primitive).string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
}
