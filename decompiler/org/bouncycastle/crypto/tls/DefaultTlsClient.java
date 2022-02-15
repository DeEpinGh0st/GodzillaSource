package org.bouncycastle.crypto.tls;

import java.io.IOException;

public abstract class DefaultTlsClient extends AbstractTlsClient {
  public DefaultTlsClient() {}
  
  public DefaultTlsClient(TlsCipherFactory paramTlsCipherFactory) {
    super(paramTlsCipherFactory);
  }
  
  public int[] getCipherSuites() {
    return new int[] { 
        49195, 49187, 49161, 49199, 49191, 49171, 162, 64, 50, 158, 
        103, 51, 156, 60, 47 };
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
    return new TlsDHKeyExchange(paramInt, this.supportedSignatureAlgorithms, null);
  }
  
  protected TlsKeyExchange createDHEKeyExchange(int paramInt) {
    return new TlsDHEKeyExchange(paramInt, this.supportedSignatureAlgorithms, null);
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
