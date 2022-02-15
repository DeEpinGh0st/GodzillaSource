package org.bouncycastle.cert.dane;

public class DANEEntryStoreBuilder {
  private final DANEEntryFetcherFactory daneEntryFetcher;
  
  public DANEEntryStoreBuilder(DANEEntryFetcherFactory paramDANEEntryFetcherFactory) {
    this.daneEntryFetcher = paramDANEEntryFetcherFactory;
  }
  
  public DANEEntryStore build(String paramString) throws DANEException {
    return new DANEEntryStore(this.daneEntryFetcher.build(paramString).getEntries());
  }
}
