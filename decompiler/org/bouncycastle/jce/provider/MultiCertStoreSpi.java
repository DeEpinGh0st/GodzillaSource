package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.jce.MultiCertStoreParameters;

public class MultiCertStoreSpi extends CertStoreSpi {
  private MultiCertStoreParameters params;
  
  public MultiCertStoreSpi(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof MultiCertStoreParameters))
      throw new InvalidAlgorithmParameterException("org.bouncycastle.jce.provider.MultiCertStoreSpi: parameter must be a MultiCertStoreParameters object\n" + paramCertStoreParameters.toString()); 
    this.params = (MultiCertStoreParameters)paramCertStoreParameters;
  }
  
  public Collection engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    boolean bool = this.params.getSearchAllStores();
    Iterator<CertStore> iterator = this.params.getCertStores().iterator();
    List<Certificate> list = bool ? new ArrayList() : Collections.EMPTY_LIST;
    while (iterator.hasNext()) {
      CertStore certStore = iterator.next();
      Collection<? extends Certificate> collection = certStore.getCertificates(paramCertSelector);
      if (bool) {
        list.addAll(collection);
        continue;
      } 
      if (!collection.isEmpty())
        return collection; 
    } 
    return list;
  }
  
  public Collection engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    boolean bool = this.params.getSearchAllStores();
    Iterator<CertStore> iterator = this.params.getCertStores().iterator();
    List<CRL> list = bool ? new ArrayList() : Collections.EMPTY_LIST;
    while (iterator.hasNext()) {
      CertStore certStore = iterator.next();
      Collection<? extends CRL> collection = certStore.getCRLs(paramCRLSelector);
      if (bool) {
        list.addAll(collection);
        continue;
      } 
      if (!collection.isEmpty())
        return collection; 
    } 
    return list;
  }
}
