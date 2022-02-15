package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKCS7TypedStream extends CMSTypedStream {
  private final ASN1Encodable content;
  
  public PKCS7TypedStream(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) throws IOException {
    super(paramASN1ObjectIdentifier);
    this.content = paramASN1Encodable;
  }
  
  public ASN1Encodable getContent() {
    return this.content;
  }
  
  public InputStream getContentStream() {
    try {
      return getContentStream(this.content);
    } catch (IOException iOException) {
      throw new CMSRuntimeException("unable to convert content to stream: " + iOException.getMessage(), iOException);
    } 
  }
  
  public void drain() throws IOException {
    getContentStream(this.content);
  }
  
  private InputStream getContentStream(ASN1Encodable paramASN1Encodable) throws IOException {
    byte[] arrayOfByte = paramASN1Encodable.toASN1Primitive().getEncoded("DER");
    byte b;
    for (b = 1; (arrayOfByte[b] & 0xFF) > 127; b++);
    return new ByteArrayInputStream(arrayOfByte, ++b, arrayOfByte.length - b);
  }
}
