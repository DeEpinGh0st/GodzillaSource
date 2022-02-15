package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

public abstract class DefaultTlsServer extends AbstractTlsServer {
  public DefaultTlsServer() {}
  
  public DefaultTlsServer(TlsCipherFactory paramTlsCipherFactory) {
    super(paramTlsCipherFactory);
  }
  
  protected TlsSignerCredentials getDSASignerCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsSignerCredentials getECDSASignerCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsSignerCredentials getRSASignerCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected DHParameters getDHParameters() {
    return DHStandardGroups.rfc7919_ffdhe2048;
  }
  
  protected int[] getCipherSuites() {
    return new int[] { 
        49200, 49199, 49192, 49191, 49172, 49171, 159, 158, 107, 103, 
        57, 51, 157, 156, 61, 60, 53, 47 };
  }
  
  public TlsCredentials getCredentials() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 3:
        return getDSASignerCredentials();
      case 11:
      case 20:
        return null;
      case 17:
        return getECDSASignerCredentials();
      case 5:
      case 19:
        return getRSASignerCredentials();
      case 1:
        return getRSAEncryptionCredentials();
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsKeyExchange getKeyExchange() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 7:
      case 9:
      case 11:
        return createDHKeyExchange(i);
      case 3:
      case 5:
        return createDHEKeyExchange(i);
      case 16:
      case 18:
      case 20:
        return createECDHKeyExchange(i);
      case 17:
      case 19:
        return createECDHEKeyExchange(i);
      case 1:
        return createRSAKeyExchange();
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsKeyExchange createDHKeyExchange(int paramInt) {
    return new TlsDHKeyExchange(paramInt, this.supportedSignatureAlgorithms, getDHParameters());
  }
  
  protected TlsKeyExchange createDHEKeyExchange(int paramInt) {
    return new TlsDHEKeyExchange(paramInt, this.supportedSignatureAlgorithms, getDHParameters());
  }
  
  protected TlsKeyExchange createECDHKeyExchange(int paramInt) {
    return new TlsECDHKeyExchange(paramInt, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
  }
  
  protected TlsKeyExchange createECDHEKeyExchange(int paramInt) {
    return new TlsECDHEKeyExchange(paramInt, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
  }
  
  protected TlsKeyExchange createRSAKeyExchange() {
    return new TlsRSAKeyExchange(this.supportedSignatureAlgorithms);
  }
}
