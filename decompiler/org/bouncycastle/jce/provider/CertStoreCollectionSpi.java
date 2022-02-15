package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CertStoreCollectionSpi extends CertStoreSpi {
  private CollectionCertStoreParameters params;
  
  public CertStoreCollectionSpi(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters))
      throw new InvalidAlgorithmParameterException("org.bouncycastle.jce.provider.CertStoreCollectionSpi: parameter must be a CollectionCertStoreParameters object\n" + paramCertStoreParameters.toString()); 
    this.params = (CollectionCertStoreParameters)paramCertStoreParameters;
  }
  
  public Collection engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    ArrayList<Object> arrayList = new ArrayList();
    Iterator<?> iterator = this.params.getCollection().iterator();
    if (paramCertSelector == null) {
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof Certificate)
          arrayList.add(object); 
      } 
    } else {
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof Certificate && paramCertSelector.match((Certificate)object))
          arrayList.add(object); 
      } 
    } 
    return arrayList;
  }
  
  public Collection engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    ArrayList<Object> arrayList = new ArrayList();
    Iterator<?> iterator = this.params.getCollection().iterator();
    if (paramCRLSelector == null) {
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof CRL)
          arrayList.add(object); 
      } 
    } else {
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof CRL && paramCRLSelector.match((CRL)object))
          arrayList.add(object); 
      } 
    } 
    return arrayList;
  }
}
