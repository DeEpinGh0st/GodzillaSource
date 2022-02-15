package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Primitive extends ASN1Object {
  public static ASN1Primitive fromByteArray(byte[] paramArrayOfbyte) throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramArrayOfbyte);
    try {
      ASN1Primitive aSN1Primitive = aSN1InputStream.readObject();
      if (aSN1InputStream.available() != 0)
        throw new IOException("Extra data detected in stream"); 
      return aSN1Primitive;
    } catch (ClassCastException classCastException) {
      throw new IOException("cannot recognise object in stream");
    } 
  }
  
  public final boolean equals(Object paramObject) {
    return (this == paramObject) ? true : ((paramObject instanceof ASN1Encodable && asn1Equals(((ASN1Encodable)paramObject).toASN1Primitive())));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this;
  }
  
  ASN1Primitive toDERObject() {
    return this;
  }
  
  ASN1Primitive toDLObject() {
    return this;
  }
  
  public abstract int hashCode();
  
  abstract boolean isConstructed();
  
  abstract int encodedLength() throws IOException;
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  abstract boolean asn1Equals(ASN1Primitive paramASN1Primitive);
}
