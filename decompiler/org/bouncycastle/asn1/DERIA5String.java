package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERIA5String extends ASN1Primitive implements ASN1String {
  private final byte[] string;
  
  public static DERIA5String getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERIA5String)
      return (DERIA5String)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERIA5String)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERIA5String getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERIA5String) ? getInstance(aSN1Primitive) : new DERIA5String(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  DERIA5String(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERIA5String(String paramString) {
    this(paramString, false);
  }
  
  public DERIA5String(String paramString, boolean paramBoolean) {
    if (paramString == null)
      throw new NullPointerException("string cannot be null"); 
    if (paramBoolean && !isIA5String(paramString))
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
    paramASN1OutputStream.writeEncoded(22, this.string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERIA5String))
      return false; 
    DERIA5String dERIA5String = (DERIA5String)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERIA5String.string);
  }
  
  public static boolean isIA5String(String paramString) {
    for (int i = paramString.length() - 1; i >= 0; i--) {
      char c = paramString.charAt(i);
      if (c > '')
        return false; 
    } 
    return true;
  }
}
