package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERPrintableString extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERPrintableString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERPrintableString)
      return (DERPrintableString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERPrintableString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERPrintableString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERPrintableString) ? getInstance(aSN1Primitive) : new DERPrintableString(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  DERPrintableString(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERPrintableString(String paramString) {
    this(paramString, false);
  }
  
  public DERPrintableString(String paramString, boolean paramBoolean) {
    if (paramBoolean && !isPrintableString(paramString))
      throw new IllegalArgumentException("string contains illegal characters"); 
    this.string = Strings.toByteArray(paramString);
  }
  
  public String getString() {
    return Strings.fromByteArray(this.string);
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
    paramASN1OutputStream.writeEncoded(19, this.string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERPrintableString))
      return false; 
    DERPrintableString dERPrintableString = (DERPrintableString)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERPrintableString.string);
  }
  
  public String toString() {
    return getString();
  }
  
  public static boolean isPrintableString(String paramString) {
    for (int i = paramString.length() - 1; i >= 0; i--) {
      char c = paramString.charAt(i);
      if (c > '')
        return false; 
      if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && ('0' > c || c > '9'))
        switch (c) {
          case ' ':
          case '\'':
          case '(':
          case ')':
          case '+':
          case ',':
          case '-':
          case '.':
          case '/':
          case ':':
          case '=':
          case '?':
            break;
          default:
            return false;
        }  
    } 
    return true;
  }
}
