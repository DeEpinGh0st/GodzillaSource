package org.bouncycastle.asn1.x500.style;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;

public class BCStrictStyle extends BCStyle {
  public static final X500NameStyle INSTANCE = new BCStrictStyle();
  
  public boolean areEqual(X500Name paramX500Name1, X500Name paramX500Name2) {
    RDN[] arrayOfRDN1 = paramX500Name1.getRDNs();
    RDN[] arrayOfRDN2 = paramX500Name2.getRDNs();
    if (arrayOfRDN1.length != arrayOfRDN2.length)
      return false; 
    for (byte b = 0; b != arrayOfRDN1.length; b++) {
      if (!rdnAreEqual(arrayOfRDN1[b], arrayOfRDN2[b]))
        return false; 
    } 
    return true;
  }
}
