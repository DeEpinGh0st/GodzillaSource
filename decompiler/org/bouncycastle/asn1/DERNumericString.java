package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERNumericString extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERNumericString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERNumericString)
      return (DERNumericString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERNumericString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERNumericString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERNumericString) ? getInstance(aSN1Primitive) : new DERNumericString(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  DERNumericString(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERNumericString(String paramString) {
    this(paramString, false);
  }
  
  public DERNumericString(String paramString, boolean paramBoolean) {
    if (paramBoolean && !isNumericString(paramString))
      throw new IllegalArgumentException("string contains illegal characters"); 
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
    paramASN1OutputStream.writeEncoded(18, this.string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERNumericString))
      return false; 
    DERNumericString dERNumericString = (DERNumericString)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERNumericString.string);
  }
  
  public static boolean isNumericString(String paramString) {
    int i = paramString.length() - 1;
    while (i >= 0) {
      char c = paramString.charAt(i);
      if (c > '')
        return false; 
      if (('0' <= c && c <= '9') || c == ' ') {
        i--;
        continue;
      } 
      return false;
    } 
    return true;
  }
}
