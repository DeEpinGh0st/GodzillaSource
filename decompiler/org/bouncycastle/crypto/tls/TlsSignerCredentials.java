package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsSignerCredentials extends TlsCredentials {
  byte[] generateCertificateSignature(byte[] paramArrayOfbyte) throws IOException;
  
  SignatureAndHashAlgorithm getSignatureAndHashAlgorithm();
}
