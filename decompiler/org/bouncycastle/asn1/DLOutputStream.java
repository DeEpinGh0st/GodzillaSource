package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class DLOutputStream extends ASN1OutputStream {
  public DLOutputStream(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void writeObject(ASN1Encodable paramASN1Encodable) throws IOException {
    if (paramASN1Encodable != null) {
      paramASN1Encodable.toASN1Primitive().toDLObject().encode(this);
    } else {
      throw new IOException("null object detected");
    } 
  }
}
