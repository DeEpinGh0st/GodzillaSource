package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsAuthentication {
  void notifyServerCertificate(Certificate paramCertificate) throws IOException;
  
  TlsCredentials getClientCredentials(CertificateRequest paramCertificateRequest) throws IOException;
}
