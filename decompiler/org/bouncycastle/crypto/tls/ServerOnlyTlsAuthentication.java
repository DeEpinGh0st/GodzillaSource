package org.bouncycastle.crypto.tls;

public abstract class ServerOnlyTlsAuthentication implements TlsAuthentication {
  public final TlsCredentials getClientCredentials(CertificateRequest paramCertificateRequest) {
    return null;
  }
}
