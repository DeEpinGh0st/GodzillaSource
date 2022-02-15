package org.bouncycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.util.CollectionStore;

public class JcaCRLStore extends CollectionStore {
  public JcaCRLStore(Collection paramCollection) throws CRLException {
    super(convertCRLs(paramCollection));
  }
  
  private static Collection convertCRLs(Collection paramCollection) throws CRLException {
    ArrayList<X509CRLHolder> arrayList = new ArrayList(paramCollection.size());
    for (X509CRL x509CRL : paramCollection) {
      if (x509CRL instanceof X509CRL) {
        try {
          arrayList.add(new X509CRLHolder(((X509CRL)x509CRL).getEncoded()));
        } catch (IOException iOException) {
          throw new CRLException("cannot read encoding: " + iOException.getMessage());
        } 
        continue;
      } 
      arrayList.add((X509CRLHolder)x509CRL);
    } 
    return arrayList;
  }
}
