package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

class DerUtil {
  static ASN1OctetString getOctetString(byte[] paramArrayOfbyte) {
    return (ASN1OctetString)((paramArrayOfbyte == null) ? new DEROctetString(new byte[0]) : new DEROctetString(Arrays.clone(paramArrayOfbyte)));
  }
  
  static byte[] toByteArray(ASN1Primitive paramASN1Primitive) {
    try {
      return paramASN1Primitive.getEncoded();
    } catch (IOException iOException) {
      throw new IllegalStateException("Cannot get encoding: " + iOException.getMessage()) {
          public Throwable getCause() {
            return e;
          }
        };
    } 
  }
}
