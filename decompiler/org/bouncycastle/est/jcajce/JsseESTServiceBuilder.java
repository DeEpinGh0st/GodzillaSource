package org.bouncycastle.est.jcajce;

import java.net.Socket;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTService;
import org.bouncycastle.est.ESTServiceBuilder;

public class JsseESTServiceBuilder extends ESTServiceBuilder {
  protected SSLSocketFactoryCreator socketFactoryCreator;
  
  protected JsseHostnameAuthorizer hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
  
  protected int timeoutMillis = 0;
  
  protected ChannelBindingProvider bindingProvider;
  
  protected Set<String> supportedSuites = new HashSet<String>();
  
  protected Long absoluteLimit;
  
  protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
  
  protected boolean filterCipherSuites = true;
  
  public JsseESTServiceBuilder(String paramString, SSLSocketFactoryCreator paramSSLSocketFactoryCreator) {
    super(paramString);
    if (paramSSLSocketFactoryCreator == null)
      throw new NullPointerException("No socket factory creator."); 
    this.socketFactoryCreator = paramSSLSocketFactoryCreator;
  }
  
  public JsseESTServiceBuilder(String paramString) {
    super(paramString);
    this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
  }
  
  public JsseESTServiceBuilder(String paramString, X509TrustManager paramX509TrustManager) {
    super(paramString);
    this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(paramX509TrustManager);
  }
  
  public JsseESTServiceBuilder(String paramString, X509TrustManager[] paramArrayOfX509TrustManager) {
    super(paramString);
    this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(paramArrayOfX509TrustManager);
  }
  
  public JsseESTServiceBuilder withHostNameAuthorizer(JsseHostnameAuthorizer paramJsseHostnameAuthorizer) {
    this.hostNameAuthorizer = paramJsseHostnameAuthorizer;
    return this;
  }
  
  public JsseESTServiceBuilder withClientProvider(ESTClientProvider paramESTClientProvider) {
    this.clientProvider = paramESTClientProvider;
    return this;
  }
  
  public JsseESTServiceBuilder withTimeout(int paramInt) {
    this.timeoutMillis = paramInt;
    return this;
  }
  
  public JsseESTServiceBuilder withReadLimit(long paramLong) {
    this.absoluteLimit = Long.valueOf(paramLong);
    return this;
  }
  
  public JsseESTServiceBuilder withChannelBindingProvider(ChannelBindingProvider paramChannelBindingProvider) {
    this.bindingProvider = paramChannelBindingProvider;
    return this;
  }
  
  public JsseESTServiceBuilder addCipherSuites(String paramString) {
    this.supportedSuites.add(paramString);
    return this;
  }
  
  public JsseESTServiceBuilder addCipherSuites(String[] paramArrayOfString) {
    this.supportedSuites.addAll(Arrays.asList(paramArrayOfString));
    return this;
  }
  
  public JsseESTServiceBuilder withTLSVersion(String paramString) {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withTLSVersion(paramString);
    return this;
  }
  
  public JsseESTServiceBuilder withSecureRandom(SecureRandom paramSecureRandom) {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withSecureRandom(paramSecureRandom);
    return this;
  }
  
  public JsseESTServiceBuilder withProvider(String paramString) throws NoSuchProviderException {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withProvider(paramString);
    return this;
  }
  
  public JsseESTServiceBuilder withProvider(Provider paramProvider) {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withProvider(paramProvider);
    return this;
  }
  
  public JsseESTServiceBuilder withKeyManager(KeyManager paramKeyManager) {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withKeyManager(paramKeyManager);
    return this;
  }
  
  public JsseESTServiceBuilder withKeyManagers(KeyManager[] paramArrayOfKeyManager) {
    if (this.socketFactoryCreator != null)
      throw new IllegalStateException("Socket Factory Creator was defined in the constructor."); 
    this.sslSocketFactoryCreatorBuilder.withKeyManagers(paramArrayOfKeyManager);
    return this;
  }
  
  public JsseESTServiceBuilder withFilterCipherSuites(boolean paramBoolean) {
    this.filterCipherSuites = paramBoolean;
    return this;
  }
  
  public ESTService build() {
    if (this.bindingProvider == null)
      this.bindingProvider = new ChannelBindingProvider() {
          public boolean canAccessChannelBinding(Socket param1Socket) {
            return false;
          }
          
          public byte[] getChannelBinding(Socket param1Socket, String param1String) {
            return null;
          }
        }; 
    if (this.socketFactoryCreator == null)
      this.socketFactoryCreator = this.sslSocketFactoryCreatorBuilder.build(); 
    if (this.clientProvider == null)
      this.clientProvider = new DefaultESTHttpClientProvider(this.hostNameAuthorizer, this.socketFactoryCreator, this.timeoutMillis, this.bindingProvider, this.supportedSuites, this.absoluteLimit, this.filterCipherSuites); 
    return super.build();
  }
}
