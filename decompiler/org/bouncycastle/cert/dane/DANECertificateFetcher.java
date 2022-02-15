package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;

public class DANECertificateFetcher {
  private final DANEEntryFetcherFactory fetcherFactory;
  
  private final DANEEntrySelectorFactory selectorFactory;
  
  public DANECertificateFetcher(DANEEntryFetcherFactory paramDANEEntryFetcherFactory, DigestCalculator paramDigestCalculator) {
    this.fetcherFactory = paramDANEEntryFetcherFactory;
    this.selectorFactory = new DANEEntrySelectorFactory(paramDigestCalculator);
  }
  
  public List fetch(String paramString) throws DANEException {
    DANEEntrySelector dANEEntrySelector = this.selectorFactory.createSelector(paramString);
    List list = this.fetcherFactory.build(dANEEntrySelector.getDomainName()).getEntries();
    ArrayList<X509CertificateHolder> arrayList = new ArrayList(list.size());
    for (DANEEntry dANEEntry : list) {
      if (dANEEntrySelector.match(dANEEntry))
        arrayList.add(dANEEntry.getCertificate()); 
    } 
    return Collections.unmodifiableList(arrayList);
  }
}
