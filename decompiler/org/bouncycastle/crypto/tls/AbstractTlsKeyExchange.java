package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public abstract class AbstractTlsKeyExchange implements TlsKeyExchange {
  protected int keyExchange;
  
  protected Vector supportedSignatureAlgorithms;
  
  protected TlsContext context;
  
  protected AbstractTlsKeyExchange(int paramInt, Vector paramVector) {
    this.keyExchange = paramInt;
    this.supportedSignatureAlgorithms = paramVector;
  }
  
  protected DigitallySigned parseSignature(InputStream paramInputStream) throws IOException {
    DigitallySigned digitallySigned = DigitallySigned.parse(this.context, paramInputStream);
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = digitallySigned.getAlgorithm();
    if (signatureAndHashAlgorithm != null)
      TlsUtils.verifySupportedSignatureAlgorithm(this.supportedSignatureAlgorithms, signatureAndHashAlgorithm); 
    return digitallySigned;
  }
  
  public void init(TlsContext paramTlsContext) {
    this.context = paramTlsContext;
    ProtocolVersion protocolVersion = paramTlsContext.getClientVersion();
    if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(protocolVersion))
      if (this.supportedSignatureAlgorithms == null) {
        switch (this.keyExchange) {
          case 3:
          case 7:
          case 22:
            this.supportedSignatureAlgorithms = TlsUtils.getDefaultDSSSignatureAlgorithms();
          case 16:
          case 17:
            this.supportedSignatureAlgorithms = TlsUtils.getDefaultECDSASignatureAlgorithms();
          case 1:
          case 5:
          case 9:
          case 15:
          case 18:
          case 19:
          case 23:
            this.supportedSignatureAlgorithms = TlsUtils.getDefaultRSASignatureAlgorithms();
          case 13:
          case 14:
          case 21:
          case 24:
            return;
        } 
        throw new IllegalStateException("unsupported key exchange algorithm");
      }  
    if (this.supportedSignatureAlgorithms != null)
      throw new IllegalStateException("supported_signature_algorithms not allowed for " + protocolVersion); 
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (this.supportedSignatureAlgorithms == null);
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    processServerCertificate(paramTlsCredentials.getCertificate());
  }
  
  public boolean requiresServerKeyExchange() {
    return false;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    if (requiresServerKeyExchange())
      throw new TlsFatalAlert((short)80); 
    return null;
  }
  
  public void skipServerKeyExchange() throws IOException {
    if (requiresServerKeyExchange())
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    if (!requiresServerKeyExchange())
      throw new TlsFatalAlert((short)10); 
  }
  
  public void skipClientCredentials() throws IOException {}
  
  public void processClientCertificate(Certificate paramCertificate) throws IOException {}
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    throw new TlsFatalAlert((short)80);
  }
}
