package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERT61UTF8String extends ASN1Primitive implements ASN1String {
  private byte[] string;
  
  public static DERT61UTF8String getInstance(Object paramObject) {
    if (paramObject instanceof DERT61String)
      return new DERT61UTF8String(((DERT61String)paramObject).getOctets()); 
    if (paramObject == null || paramObject instanceof DERT61UTF8String)
      return (DERT61UTF8String)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return new DERT61UTF8String(((DERT61String)fromByteArray((byte[])paramObject)).getOctets());
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERT61UTF8String getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERT61String || aSN1Primitive instanceof DERT61UTF8String) ? getInstance(aSN1Primitive) : new DERT61UTF8String(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  public DERT61UTF8String(byte[] paramArrayOfbyte) {
    this.string = paramArrayOfbyte;
  }
  
  public DERT61UTF8String(String paramString) {
    this(Strings.toUTF8ByteArray(paramString));
  }
  
  public String getString() {
    return Strings.fromUTF8ByteArray(this.string);
  }
  
  public String toString() {
    return getString();
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(20, this.string);
  }
  
  public byte[] getOctets() {
    return Arrays.clone(this.string);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !(paramASN1Primitive instanceof DERT61UTF8String) ? false : Arrays.areEqual(this.string, ((DERT61UTF8String)paramASN1Primitive).string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
}
