package org.bouncycastle.jce;

import java.security.cert.CertStoreParameters;
import java.util.Collection;

public class MultiCertStoreParameters implements CertStoreParameters {
  private Collection certStores;
  
  private boolean searchAllStores;
  
  public MultiCertStoreParameters(Collection paramCollection) {
    this(paramCollection, true);
  }
  
  public MultiCertStoreParameters(Collection paramCollection, boolean paramBoolean) {
    this.certStores = paramCollection;
    this.searchAllStores = paramBoolean;
  }
  
  public Collection getCertStores() {
    return this.certStores;
  }
  
  public boolean getSearchAllStores() {
    return this.searchAllStores;
  }
  
  public Object clone() {
    return this;
  }
}
