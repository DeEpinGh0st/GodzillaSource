package org.bouncycastle.x509;

import java.security.cert.CRL;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.util.StoreException;

class PKIXCRLUtil {
  public Set findCRLs(X509CRLStoreSelector paramX509CRLStoreSelector, ExtendedPKIXParameters paramExtendedPKIXParameters, Date paramDate) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    try {
      hashSet.addAll(findCRLs(paramX509CRLStoreSelector, paramExtendedPKIXParameters.getAdditionalStores()));
      hashSet.addAll(findCRLs(paramX509CRLStoreSelector, paramExtendedPKIXParameters.getStores()));
      hashSet.addAll(findCRLs(paramX509CRLStoreSelector, paramExtendedPKIXParameters.getCertStores()));
    } catch (AnnotatedException annotatedException) {
      throw new AnnotatedException("Exception obtaining complete CRLs.", annotatedException);
    } 
    HashSet<X509CRL> hashSet1 = new HashSet();
    Date date = paramDate;
    if (paramExtendedPKIXParameters.getDate() != null)
      date = paramExtendedPKIXParameters.getDate(); 
    for (X509CRL x509CRL : hashSet) {
      if (x509CRL.getNextUpdate().after(date)) {
        X509Certificate x509Certificate = paramX509CRLStoreSelector.getCertificateChecking();
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
  
  public Set findCRLs(X509CRLStoreSelector paramX509CRLStoreSelector, PKIXParameters paramPKIXParameters) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    try {
      hashSet.addAll(findCRLs(paramX509CRLStoreSelector, paramPKIXParameters.getCertStores()));
    } catch (AnnotatedException annotatedException) {
      throw new AnnotatedException("Exception obtaining complete CRLs.", annotatedException);
    } 
    return hashSet;
  }
  
  private final Collection findCRLs(X509CRLStoreSelector paramX509CRLStoreSelector, List paramList) throws AnnotatedException {
    HashSet<CRL> hashSet = new HashSet();
    Iterator<Object> iterator = paramList.iterator();
    AnnotatedException annotatedException = null;
    boolean bool = false;
    while (iterator.hasNext()) {
      X509Store x509Store = (X509Store)iterator.next();
      if (x509Store instanceof X509Store) {
        X509Store x509Store1 = x509Store;
        try {
          hashSet.addAll(x509Store1.getMatches(paramX509CRLStoreSelector));
          bool = true;
        } catch (StoreException storeException) {
          annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", (Throwable)storeException);
        } 
        continue;
      } 
      CertStore certStore = (CertStore)x509Store;
      try {
        hashSet.addAll(certStore.getCRLs(paramX509CRLStoreSelector));
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
