package org.bouncycastle.asn1;

public class DERObjectIdentifier extends ASN1ObjectIdentifier {
  public DERObjectIdentifier(String paramString) {
    super(paramString);
  }
  
  DERObjectIdentifier(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  DERObjectIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    super(paramASN1ObjectIdentifier, paramString);
  }
}
