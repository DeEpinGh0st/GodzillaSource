package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

public class PSKTlsServer extends AbstractTlsServer {
  protected TlsPSKIdentityManager pskIdentityManager;
  
  public PSKTlsServer(TlsPSKIdentityManager paramTlsPSKIdentityManager) {
    this(new DefaultTlsCipherFactory(), paramTlsPSKIdentityManager);
  }
  
  public PSKTlsServer(TlsCipherFactory paramTlsCipherFactory, TlsPSKIdentityManager paramTlsPSKIdentityManager) {
    super(paramTlsCipherFactory);
    this.pskIdentityManager = paramTlsPSKIdentityManager;
  }
  
  protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected DHParameters getDHParameters() {
    return DHStandardGroups.rfc7919_ffdhe2048;
  }
  
  protected int[] getCipherSuites() {
    return new int[] { 49207, 49205, 178, 144 };
  }
  
  public TlsCredentials getCredentials() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 13:
      case 14:
      case 24:
        return null;
      case 15:
        return getRSAEncryptionCredentials();
    } 
    throw new TlsFatalAlert((short)80);
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
  
  protected TlsKeyExchange createPSKKeyExchange(int paramInt) {
    return new TlsPSKKeyExchange(paramInt, this.supportedSignatureAlgorithms, null, this.pskIdentityManager, getDHParameters(), this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
  }
}
