package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BEROutputStream extends DEROutputStream {
  public BEROutputStream(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void writeObject(Object paramObject) throws IOException {
    if (paramObject == null) {
      writeNull();
    } else if (paramObject instanceof ASN1Primitive) {
      ((ASN1Primitive)paramObject).encode(this);
    } else if (paramObject instanceof ASN1Encodable) {
      ((ASN1Encodable)paramObject).toASN1Primitive().encode(this);
    } else {
      throw new IOException("object not BEREncodable");
    } 
  }
}
