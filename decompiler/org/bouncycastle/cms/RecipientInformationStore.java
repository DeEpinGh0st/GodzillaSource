package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Iterable;

public class RecipientInformationStore implements Iterable<RecipientInformation> {
  private final List all;
  
  private final Map table = new HashMap<Object, Object>();
  
  public RecipientInformationStore(RecipientInformation paramRecipientInformation) {
    this.all = new ArrayList(1);
    this.all.add(paramRecipientInformation);
    RecipientId recipientId = paramRecipientInformation.getRID();
    this.table.put(recipientId, this.all);
  }
  
  public RecipientInformationStore(Collection<RecipientInformation> paramCollection) {
    for (RecipientInformation recipientInformation : paramCollection) {
      RecipientId recipientId = recipientInformation.getRID();
      ArrayList<RecipientInformation> arrayList = (ArrayList)this.table.get(recipientId);
      if (arrayList == null) {
        arrayList = new ArrayList(1);
        this.table.put(recipientId, arrayList);
      } 
      arrayList.add(recipientInformation);
    } 
    this.all = new ArrayList<RecipientInformation>(paramCollection);
  }
  
  public RecipientInformation get(RecipientId paramRecipientId) {
    Collection<Recipient> collection = getRecipients(paramRecipientId);
    return (collection.size() == 0) ? null : collection.iterator().next();
  }
  
  public int size() {
    return this.all.size();
  }
  
  public Collection<RecipientInformation> getRecipients() {
    return new ArrayList<RecipientInformation>(this.all);
  }
  
  public Collection<Recipient> getRecipients(RecipientId paramRecipientId) {
    if (paramRecipientId instanceof KeyTransRecipientId) {
      KeyTransRecipientId keyTransRecipientId = (KeyTransRecipientId)paramRecipientId;
      X500Name x500Name = keyTransRecipientId.getIssuer();
      byte[] arrayOfByte = keyTransRecipientId.getSubjectKeyIdentifier();
      if (x500Name != null && arrayOfByte != null) {
        ArrayList<Recipient> arrayList1 = new ArrayList();
        Collection<Recipient> collection1 = getRecipients(new KeyTransRecipientId(x500Name, keyTransRecipientId.getSerialNumber()));
        if (collection1 != null)
          arrayList1.addAll(collection1); 
        Collection<Recipient> collection2 = getRecipients(new KeyTransRecipientId(arrayOfByte));
        if (collection2 != null)
          arrayList1.addAll(collection2); 
        return arrayList1;
      } 
    } 
    ArrayList<? extends Recipient> arrayList = (ArrayList)this.table.get(paramRecipientId);
    return (arrayList == null) ? new ArrayList<Recipient>() : new ArrayList<Recipient>(arrayList);
  }
  
  public Iterator<RecipientInformation> iterator() {
    return getRecipients().iterator();
  }
}
