package org.bouncycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;

public class JcaCertStore extends CollectionStore {
  public JcaCertStore(Collection paramCollection) throws CertificateEncodingException {
    super(convertCerts(paramCollection));
  }
  
  private static Collection convertCerts(Collection paramCollection) throws CertificateEncodingException {
    ArrayList<X509CertificateHolder> arrayList = new ArrayList(paramCollection.size());
    for (X509Certificate x509Certificate : paramCollection) {
      if (x509Certificate instanceof X509Certificate) {
        X509Certificate x509Certificate1 = x509Certificate;
        try {
          arrayList.add(new X509CertificateHolder(x509Certificate1.getEncoded()));
        } catch (IOException iOException) {
          throw new CertificateEncodingException("unable to read encoding: " + iOException.getMessage());
        } 
        continue;
      } 
      arrayList.add((X509CertificateHolder)x509Certificate);
    } 
    return arrayList;
  }
}
