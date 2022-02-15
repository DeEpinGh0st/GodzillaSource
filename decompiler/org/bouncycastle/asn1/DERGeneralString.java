package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERGeneralString extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERGeneralString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERGeneralString)
      return (DERGeneralString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERGeneralString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERGeneralString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERGeneralString) ? getInstance(aSN1Primitive) : new DERGeneralString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  DERGeneralString(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERGeneralString(String paramString) {
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
    paramASN1OutputStream.writeEncoded(27, this.string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERGeneralString))
      return false; 
    DERGeneralString dERGeneralString = (DERGeneralString)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERGeneralString.string);
  }
}
