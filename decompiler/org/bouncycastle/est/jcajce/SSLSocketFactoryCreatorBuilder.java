package org.bouncycastle.est.jcajce;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class SSLSocketFactoryCreatorBuilder {
  protected String tlsVersion = "TLS";
  
  protected Provider tlsProvider;
  
  protected KeyManager[] keyManagers;
  
  protected X509TrustManager[] trustManagers;
  
  protected SecureRandom secureRandom = new SecureRandom();
  
  public SSLSocketFactoryCreatorBuilder(X509TrustManager paramX509TrustManager) {
    if (paramX509TrustManager == null)
      throw new NullPointerException("Trust managers can not be null"); 
    this.trustManagers = new X509TrustManager[] { paramX509TrustManager };
  }
  
  public SSLSocketFactoryCreatorBuilder(X509TrustManager[] paramArrayOfX509TrustManager) {
    if (paramArrayOfX509TrustManager == null)
      throw new NullPointerException("Trust managers can not be null"); 
    this.trustManagers = paramArrayOfX509TrustManager;
  }
  
  public SSLSocketFactoryCreatorBuilder withTLSVersion(String paramString) {
    this.tlsVersion = paramString;
    return this;
  }
  
  public SSLSocketFactoryCreatorBuilder withSecureRandom(SecureRandom paramSecureRandom) {
    this.secureRandom = paramSecureRandom;
    return this;
  }
  
  public SSLSocketFactoryCreatorBuilder withProvider(String paramString) throws NoSuchProviderException {
    this.tlsProvider = Security.getProvider(paramString);
    if (this.tlsProvider == null)
      throw new NoSuchProviderException("JSSE provider not found: " + paramString); 
    return this;
  }
  
  public SSLSocketFactoryCreatorBuilder withProvider(Provider paramProvider) {
    this.tlsProvider = paramProvider;
    return this;
  }
  
  public SSLSocketFactoryCreatorBuilder withKeyManager(KeyManager paramKeyManager) {
    if (paramKeyManager == null) {
      this.keyManagers = null;
    } else {
      this.keyManagers = new KeyManager[] { paramKeyManager };
    } 
    return this;
  }
  
  public SSLSocketFactoryCreatorBuilder withKeyManagers(KeyManager[] paramArrayOfKeyManager) {
    this.keyManagers = paramArrayOfKeyManager;
    return this;
  }
  
  public SSLSocketFactoryCreator build() {
    return new SSLSocketFactoryCreator() {
        public boolean isTrusted() {
          for (byte b = 0; b != SSLSocketFactoryCreatorBuilder.this.trustManagers.length; b++) {
            X509TrustManager x509TrustManager = SSLSocketFactoryCreatorBuilder.this.trustManagers[b];
            if ((x509TrustManager.getAcceptedIssuers()).length > 0)
              return true; 
          } 
          return false;
        }
        
        public SSLSocketFactory createFactory() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
          SSLContext sSLContext;
          if (SSLSocketFactoryCreatorBuilder.this.tlsProvider != null) {
            sSLContext = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion, SSLSocketFactoryCreatorBuilder.this.tlsProvider);
          } else {
            sSLContext = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion);
          } 
          sSLContext.init(SSLSocketFactoryCreatorBuilder.this.keyManagers, (TrustManager[])SSLSocketFactoryCreatorBuilder.this.trustManagers, SSLSocketFactoryCreatorBuilder.this.secureRandom);
          return sSLContext.getSocketFactory();
        }
      };
  }
}
