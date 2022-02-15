package org.bouncycastle.asn1;

import java.io.IOException;

public class DEROctetString extends ASN1OctetString {
  public DEROctetString(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public DEROctetString(ASN1Encodable paramASN1Encodable) throws IOException {
    super(paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(4, this.string);
  }
  
  static void encode(DEROutputStream paramDEROutputStream, byte[] paramArrayOfbyte) throws IOException {
    paramDEROutputStream.writeEncoded(4, paramArrayOfbyte);
  }
}
