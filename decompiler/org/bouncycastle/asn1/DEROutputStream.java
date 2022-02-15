package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class DEROutputStream extends ASN1OutputStream {
  public DEROutputStream(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void writeObject(ASN1Encodable paramASN1Encodable) throws IOException {
    if (paramASN1Encodable != null) {
      paramASN1Encodable.toASN1Primitive().toDERObject().encode(this);
    } else {
      throw new IOException("null object detected");
    } 
  }
  
  ASN1OutputStream getDERSubStream() {
    return this;
  }
  
  ASN1OutputStream getDLSubStream() {
    return this;
  }
}
