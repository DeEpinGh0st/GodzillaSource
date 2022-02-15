package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class PSKTlsClient extends AbstractTlsClient {
  protected TlsPSKIdentity pskIdentity;
  
  public PSKTlsClient(TlsPSKIdentity paramTlsPSKIdentity) {
    this(new DefaultTlsCipherFactory(), paramTlsPSKIdentity);
  }
  
  public PSKTlsClient(TlsCipherFactory paramTlsCipherFactory, TlsPSKIdentity paramTlsPSKIdentity) {
    super(paramTlsCipherFactory);
    this.pskIdentity = paramTlsPSKIdentity;
  }
  
  public int[] getCipherSuites() {
    return new int[] { 49207, 49205, 178, 144 };
  }
  
  public TlsKeyExchange getKeyExchange() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 13:
      case 14:
      case 15:
      case 24:
        return createPSKKeyExchange(i);
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsAuthentication getAuthentication() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsKeyExchange createPSKKeyExchange(int paramInt) {
    return new TlsPSKKeyExchange(paramInt, this.supportedSignatureAlgorithms, this.pskIdentity, null, null, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
  }
}
