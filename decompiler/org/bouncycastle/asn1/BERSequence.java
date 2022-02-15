package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERSequence extends ASN1Sequence {
  public BERSequence() {}
  
  public BERSequence(ASN1Encodable paramASN1Encodable) {
    super(paramASN1Encodable);
  }
  
  public BERSequence(ASN1EncodableVector paramASN1EncodableVector) {
    super(paramASN1EncodableVector);
  }
  
  public BERSequence(ASN1Encodable[] paramArrayOfASN1Encodable) {
    super(paramArrayOfASN1Encodable);
  }
  
  int encodedLength() throws IOException {
    int i = 0;
    Enumeration<ASN1Encodable> enumeration = getObjects();
    while (enumeration.hasMoreElements())
      i += ((ASN1Encodable)enumeration.nextElement()).toASN1Primitive().encodedLength(); 
    return 2 + i + 2;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.write(48);
    paramASN1OutputStream.write(128);
    Enumeration<ASN1Encodable> enumeration = getObjects();
    while (enumeration.hasMoreElements())
      paramASN1OutputStream.writeObject(enumeration.nextElement()); 
    paramASN1OutputStream.write(0);
    paramASN1OutputStream.write(0);
  }
}
