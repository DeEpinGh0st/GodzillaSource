package org.bouncycastle.x509;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class X509Store implements Store {
  private Provider _provider;
  
  private X509StoreSpi _spi;
  
  public static X509Store getInstance(String paramString, X509StoreParameters paramX509StoreParameters) throws NoSuchStoreException {
    try {
      X509Util.Implementation implementation = X509Util.getImplementation("X509Store", paramString);
      return createStore(implementation, paramX509StoreParameters);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchStoreException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  public static X509Store getInstance(String paramString1, X509StoreParameters paramX509StoreParameters, String paramString2) throws NoSuchStoreException, NoSuchProviderException {
    return getInstance(paramString1, paramX509StoreParameters, X509Util.getProvider(paramString2));
  }
  
  public static X509Store getInstance(String paramString, X509StoreParameters paramX509StoreParameters, Provider paramProvider) throws NoSuchStoreException {
    try {
      X509Util.Implementation implementation = X509Util.getImplementation("X509Store", paramString, paramProvider);
      return createStore(implementation, paramX509StoreParameters);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchStoreException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  private static X509Store createStore(X509Util.Implementation paramImplementation, X509StoreParameters paramX509StoreParameters) {
    X509StoreSpi x509StoreSpi = (X509StoreSpi)paramImplementation.getEngine();
    x509StoreSpi.engineInit(paramX509StoreParameters);
    return new X509Store(paramImplementation.getProvider(), x509StoreSpi);
  }
  
  private X509Store(Provider paramProvider, X509StoreSpi paramX509StoreSpi) {
    this._provider = paramProvider;
    this._spi = paramX509StoreSpi;
  }
  
  public Provider getProvider() {
    return this._provider;
  }
  
  public Collection getMatches(Selector paramSelector) {
    return this._spi.engineGetMatches(paramSelector);
  }
}
