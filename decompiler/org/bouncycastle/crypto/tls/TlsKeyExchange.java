package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TlsKeyExchange {
  void init(TlsContext paramTlsContext);
  
  void skipServerCredentials() throws IOException;
  
  void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException;
  
  void processServerCertificate(Certificate paramCertificate) throws IOException;
  
  boolean requiresServerKeyExchange();
  
  byte[] generateServerKeyExchange() throws IOException;
  
  void skipServerKeyExchange() throws IOException;
  
  void processServerKeyExchange(InputStream paramInputStream) throws IOException;
  
  void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException;
  
  void skipClientCredentials() throws IOException;
  
  void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException;
  
  void processClientCertificate(Certificate paramCertificate) throws IOException;
  
  void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException;
  
  void processClientKeyExchange(InputStream paramInputStream) throws IOException;
  
  byte[] generatePremasterSecret() throws IOException;
}
