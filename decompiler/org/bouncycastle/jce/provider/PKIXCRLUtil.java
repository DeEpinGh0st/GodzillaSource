package org.bouncycastle.jce.provider;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

class PKIXCRLUtil {
  public Set findCRLs(PKIXCRLStoreSelector paramPKIXCRLStoreSelector, Date paramDate, List paramList1, List paramList2) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    try {
      hashSet.addAll(findCRLs(paramPKIXCRLStoreSelector, paramList2));
      hashSet.addAll(findCRLs(paramPKIXCRLStoreSelector, paramList1));
    } catch (AnnotatedException annotatedException) {
      throw new AnnotatedException("Exception obtaining complete CRLs.", annotatedException);
    } 
    HashSet<X509CRL> hashSet1 = new HashSet();
    for (X509CRL x509CRL : hashSet) {
      if (x509CRL.getNextUpdate().after(paramDate)) {
        X509Certificate x509Certificate = paramPKIXCRLStoreSelector.getCertificateChecking();
        if (x509Certificate != null) {
          if (x509CRL.getThisUpdate().before(x509Certificate.getNotAfter()))
            hashSet1.add(x509CRL); 
          continue;
        } 
        hashSet1.add(x509CRL);
      } 
    } 
    return hashSet1;
  }
  
  private final Collection findCRLs(PKIXCRLStoreSelector paramPKIXCRLStoreSelector, List paramList) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    Iterator<Object> iterator = paramList.iterator();
    AnnotatedException annotatedException = null;
    boolean bool = false;
    while (iterator.hasNext()) {
      Store store = (Store)iterator.next();
      if (store instanceof Store) {
        Store store1 = store;
        try {
          hashSet.addAll(store1.getMatches((Selector)paramPKIXCRLStoreSelector));
          bool = true;
        } catch (StoreException storeException) {
          annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", (Throwable)storeException);
        } 
        continue;
      } 
      CertStore certStore = (CertStore)store;
      try {
        hashSet.addAll(PKIXCRLStoreSelector.getCRLs(paramPKIXCRLStoreSelector, certStore));
        bool = true;
      } catch (CertStoreException certStoreException) {
        annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", certStoreException);
      } 
    } 
    if (!bool && annotatedException != null)
      throw annotatedException; 
    return hashSet;
  }
}
