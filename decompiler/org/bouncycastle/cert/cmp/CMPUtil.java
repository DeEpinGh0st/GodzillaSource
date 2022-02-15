package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROutputStream;

class CMPUtil {
  static void derEncodeToStream(ASN1Encodable paramASN1Encodable, OutputStream paramOutputStream) {
    DEROutputStream dEROutputStream = new DEROutputStream(paramOutputStream);
    try {
      dEROutputStream.writeObject(paramASN1Encodable);
      dEROutputStream.close();
    } catch (IOException iOException) {
      throw new CMPRuntimeException("unable to DER encode object: " + iOException.getMessage(), iOException);
    } 
  }
}
