package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.util.Arrays;

public class SRPTlsClient extends AbstractTlsClient {
  protected TlsSRPGroupVerifier groupVerifier;
  
  protected byte[] identity;
  
  protected byte[] password;
  
  public SRPTlsClient(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(new DefaultTlsCipherFactory(), new DefaultTlsSRPGroupVerifier(), paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public SRPTlsClient(TlsCipherFactory paramTlsCipherFactory, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(paramTlsCipherFactory, new DefaultTlsSRPGroupVerifier(), paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public SRPTlsClient(TlsCipherFactory paramTlsCipherFactory, TlsSRPGroupVerifier paramTlsSRPGroupVerifier, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramTlsCipherFactory);
    this.groupVerifier = paramTlsSRPGroupVerifier;
    this.identity = Arrays.clone(paramArrayOfbyte1);
    this.password = Arrays.clone(paramArrayOfbyte2);
  }
  
  protected boolean requireSRPServerExtension() {
    return false;
  }
  
  public int[] getCipherSuites() {
    return new int[] { 49182 };
  }
  
  public Hashtable getClientExtensions() throws IOException {
    Hashtable hashtable = TlsExtensionsUtils.ensureExtensionsInitialised(super.getClientExtensions());
    TlsSRPUtils.addSRPExtension(hashtable, this.identity);
    return hashtable;
  }
  
  public void processServerExtensions(Hashtable paramHashtable) throws IOException {
    if (!TlsUtils.hasExpectedEmptyExtensionData(paramHashtable, TlsSRPUtils.EXT_SRP, (short)47) && requireSRPServerExtension())
      throw new TlsFatalAlert((short)47); 
    super.processServerExtensions(paramHashtable);
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
  
  public TlsAuthentication getAuthentication() throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  protected TlsKeyExchange createSRPKeyExchange(int paramInt) {
    return new TlsSRPKeyExchange(paramInt, this.supportedSignatureAlgorithms, this.groupVerifier, this.identity, this.password);
  }
}
