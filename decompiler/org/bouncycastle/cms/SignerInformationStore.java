package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.Iterable;

public class SignerInformationStore implements Iterable<SignerInformation> {
  private List all = new ArrayList();
  
  private Map table = new HashMap<Object, Object>();
  
  public SignerInformationStore(SignerInformation paramSignerInformation) {
    this.all = new ArrayList(1);
    this.all.add(paramSignerInformation);
    SignerId signerId = paramSignerInformation.getSID();
    this.table.put(signerId, this.all);
  }
  
  public SignerInformationStore(Collection<SignerInformation> paramCollection) {
    for (SignerInformation signerInformation : paramCollection) {
      SignerId signerId = signerInformation.getSID();
      ArrayList<SignerInformation> arrayList = (ArrayList)this.table.get(signerId);
      if (arrayList == null) {
        arrayList = new ArrayList(1);
        this.table.put(signerId, arrayList);
      } 
      arrayList.add(signerInformation);
    } 
    this.all = new ArrayList<SignerInformation>(paramCollection);
  }
  
  public SignerInformation get(SignerId paramSignerId) {
    Collection<SignerInformation> collection = getSigners(paramSignerId);
    return (collection.size() == 0) ? null : collection.iterator().next();
  }
  
  public int size() {
    return this.all.size();
  }
  
  public Collection<SignerInformation> getSigners() {
    return new ArrayList<SignerInformation>(this.all);
  }
  
  public Collection<SignerInformation> getSigners(SignerId paramSignerId) {
    if (paramSignerId.getIssuer() != null && paramSignerId.getSubjectKeyIdentifier() != null) {
      ArrayList<SignerInformation> arrayList1 = new ArrayList();
      Collection<SignerInformation> collection1 = getSigners(new SignerId(paramSignerId.getIssuer(), paramSignerId.getSerialNumber()));
      if (collection1 != null)
        arrayList1.addAll(collection1); 
      Collection<SignerInformation> collection2 = getSigners(new SignerId(paramSignerId.getSubjectKeyIdentifier()));
      if (collection2 != null)
        arrayList1.addAll(collection2); 
      return arrayList1;
    } 
    ArrayList<? extends SignerInformation> arrayList = (ArrayList)this.table.get(paramSignerId);
    return (arrayList == null) ? new ArrayList<SignerInformation>() : new ArrayList<SignerInformation>(arrayList);
  }
  
  public Iterator<SignerInformation> iterator() {
    return getSigners().iterator();
  }
}
