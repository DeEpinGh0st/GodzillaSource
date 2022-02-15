package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

public class PKCS7ProcessableObject implements CMSTypedData {
  private final ASN1ObjectIdentifier type;
  
  private final ASN1Encodable structure;
  
  public PKCS7ProcessableObject(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.type = paramASN1ObjectIdentifier;
    this.structure = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.type;
  }
  
  public void write(OutputStream paramOutputStream) throws IOException, CMSException {
    if (this.structure instanceof ASN1Sequence) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.structure);
      for (ASN1Encodable aSN1Encodable : aSN1Sequence)
        paramOutputStream.write(aSN1Encodable.toASN1Primitive().getEncoded("DER")); 
    } else {
      byte[] arrayOfByte = this.structure.toASN1Primitive().getEncoded("DER");
      byte b;
      for (b = 1; (arrayOfByte[b] & 0xFF) > 127; b++);
      paramOutputStream.write(arrayOfByte, ++b, arrayOfByte.length - b);
    } 
  }
  
  public Object getContent() {
    return this.structure;
  }
}
