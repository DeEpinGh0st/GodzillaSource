package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

public class SRPTlsServer extends AbstractTlsServer {
  protected TlsSRPIdentityManager srpIdentityManager;
  
  protected byte[] srpIdentity = null;
  
  protected TlsSRPLoginParameters loginParameters = null;
  
  public SRPTlsServer(TlsSRPIdentityManager paramTlsSRPIdentityManager) {
    this(new DefaultTlsCipherFactory(), paramTlsSRPIdentityManager);
  }
  
  public SRPTlsServer(TlsCipherFactory paramTlsCipherFactory, TlsSRPIdentityManager paramTlsSRPIdentityManager) {
    super(paramTlsCipherFactory);
    this.srpIdentityManager = paramTlsSRPIdentityManager;
  }
  
  protected TlsSignerCredentials getDSASignerCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsSignerCredentials getRSASignerCredentials() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected int[] getCipherSuites() {
    return new int[] { 49186, 49183, 49185, 49182, 49184, 49181 };
  }
  
  public void processClientExtensions(Hashtable paramHashtable) throws IOException {
    super.processClientExtensions(paramHashtable);
    this.srpIdentity = TlsSRPUtils.getSRPExtension(paramHashtable);
  }
  
  public int getSelectedCipherSuite() throws IOException {
    int i = super.getSelectedCipherSuite();
    if (TlsSRPUtils.isSRPCipherSuite(i)) {
      if (this.srpIdentity != null)
        this.loginParameters = this.srpIdentityManager.getLoginParameters(this.srpIdentity); 
      if (this.loginParameters == null)
        throw new TlsFatalAlert((short)115); 
    } 
    return i;
  }
  
  public TlsCredentials getCredentials() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 21:
        return null;
      case 22:
        return getDSASignerCredentials();
      case 23:
        return getRSASignerCredentials();
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsKeyExchange getKeyExchange() throws IOException {
    int i = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
    switch (i) {
      case 21:
      case 22:
      case 23:
        return createSRPKeyExchange(i);
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsKeyExchange createSRPKeyExchange(int paramInt) {
    return new TlsSRPKeyExchange(paramInt, this.supportedSignatureAlgorithms, this.srpIdentity, this.loginParameters);
  }
}
