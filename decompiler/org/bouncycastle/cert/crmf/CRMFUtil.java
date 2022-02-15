package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.CertIOException;

class CRMFUtil {
  static void derEncodeToStream(ASN1Encodable paramASN1Encodable, OutputStream paramOutputStream) {
    DEROutputStream dEROutputStream = new DEROutputStream(paramOutputStream);
    try {
      dEROutputStream.writeObject(paramASN1Encodable);
      dEROutputStream.close();
    } catch (IOException iOException) {
      throw new CRMFRuntimeException("unable to DER encode object: " + iOException.getMessage(), iOException);
    } 
  }
  
  static void addExtension(ExtensionsGenerator paramExtensionsGenerator, ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws CertIOException {
    try {
      paramExtensionsGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    } catch (IOException iOException) {
      throw new CertIOException("cannot encode extension: " + iOException.getMessage(), iOException);
    } 
  }
}
