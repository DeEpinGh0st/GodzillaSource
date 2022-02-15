package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class DANEEntryStore implements Store {
  private final Map entries;
  
  DANEEntryStore(List paramList) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    for (DANEEntry dANEEntry : paramList)
      hashMap.put(dANEEntry.getDomainName(), dANEEntry); 
    this.entries = Collections.unmodifiableMap(hashMap);
  }
  
  public Collection getMatches(Selector paramSelector) throws StoreException {
    if (paramSelector == null)
      return this.entries.values(); 
    ArrayList<?> arrayList = new ArrayList();
    for (Object object : this.entries.values()) {
      if (paramSelector.match(object))
        arrayList.add(object); 
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  public Store toCertificateStore() {
    Collection collection = getMatches(null);
    ArrayList<X509CertificateHolder> arrayList = new ArrayList(collection.size());
    for (DANEEntry dANEEntry : collection)
      arrayList.add(dANEEntry.getCertificate()); 
    return (Store)new CollectionStore(arrayList);
  }
}
