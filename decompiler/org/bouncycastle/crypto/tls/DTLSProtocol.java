package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public abstract class DTLSProtocol {
  protected final SecureRandom secureRandom;
  
  protected DTLSProtocol(SecureRandom paramSecureRandom) {
    if (paramSecureRandom == null)
      throw new IllegalArgumentException("'secureRandom' cannot be null"); 
    this.secureRandom = paramSecureRandom;
  }
  
  protected void processFinished(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte1);
    byte[] arrayOfByte = TlsUtils.readFully(paramArrayOfbyte2.length, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    if (!Arrays.constantTimeAreEqual(paramArrayOfbyte2, arrayOfByte))
      throw new TlsFatalAlert((short)40); 
  }
  
  protected static void applyMaxFragmentLengthExtension(DTLSRecordLayer paramDTLSRecordLayer, short paramShort) throws IOException {
    if (paramShort >= 0) {
      if (!MaxFragmentLength.isValid(paramShort))
        throw new TlsFatalAlert((short)80); 
      int i = 1 << 8 + paramShort;
      paramDTLSRecordLayer.setPlaintextLimit(i);
    } 
  }
  
  protected static short evaluateMaxFragmentLengthExtension(boolean paramBoolean, Hashtable paramHashtable1, Hashtable paramHashtable2, short paramShort) throws IOException {
    short s = TlsExtensionsUtils.getMaxFragmentLengthExtension(paramHashtable2);
    if (s >= 0 && (!MaxFragmentLength.isValid(s) || (!paramBoolean && s != TlsExtensionsUtils.getMaxFragmentLengthExtension(paramHashtable1))))
      throw new TlsFatalAlert(paramShort); 
    return s;
  }
  
  protected static byte[] generateCertificate(Certificate paramCertificate) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramCertificate.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected static byte[] generateSupplementalData(Vector paramVector) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    TlsProtocol.writeSupplementalData(byteArrayOutputStream, paramVector);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected static void validateSelectedCipherSuite(int paramInt, short paramShort) throws IOException {
    switch (TlsUtils.getEncryptionAlgorithm(paramInt)) {
      case 1:
      case 2:
        throw new TlsFatalAlert(paramShort);
    } 
  }
}
