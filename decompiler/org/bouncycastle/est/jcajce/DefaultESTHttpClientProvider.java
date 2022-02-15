package org.bouncycastle.est.jcajce;

import java.util.Set;
import javax.net.ssl.SSLSocketFactory;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTException;

class DefaultESTHttpClientProvider implements ESTClientProvider {
  private final JsseHostnameAuthorizer hostNameAuthorizer;
  
  private final SSLSocketFactoryCreator socketFactoryCreator;
  
  private final int timeout;
  
  private final ChannelBindingProvider bindingProvider;
  
  private final Set<String> cipherSuites;
  
  private final Long absoluteLimit;
  
  private final boolean filterCipherSuites;
  
  public DefaultESTHttpClientProvider(JsseHostnameAuthorizer paramJsseHostnameAuthorizer, SSLSocketFactoryCreator paramSSLSocketFactoryCreator, int paramInt, ChannelBindingProvider paramChannelBindingProvider, Set<String> paramSet, Long paramLong, boolean paramBoolean) {
    this.hostNameAuthorizer = paramJsseHostnameAuthorizer;
    this.socketFactoryCreator = paramSSLSocketFactoryCreator;
    this.timeout = paramInt;
    this.bindingProvider = paramChannelBindingProvider;
    this.cipherSuites = paramSet;
    this.absoluteLimit = paramLong;
    this.filterCipherSuites = paramBoolean;
  }
  
  public ESTClient makeClient() throws ESTException {
    try {
      SSLSocketFactory sSLSocketFactory = this.socketFactoryCreator.createFactory();
      return new DefaultESTClient(new DefaultESTClientSourceProvider(sSLSocketFactory, this.hostNameAuthorizer, this.timeout, this.bindingProvider, this.cipherSuites, this.absoluteLimit, this.filterCipherSuites));
    } catch (Exception exception) {
      throw new ESTException(exception.getMessage(), exception.getCause());
    } 
  }
  
  public boolean isTrusted() {
    return this.socketFactoryCreator.isTrusted();
  }
}
