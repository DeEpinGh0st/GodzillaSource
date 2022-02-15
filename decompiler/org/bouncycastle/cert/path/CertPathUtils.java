package org.bouncycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.cert.X509CertificateHolder;

class CertPathUtils {
  static Set getCriticalExtensionsOIDs(X509CertificateHolder[] paramArrayOfX509CertificateHolder) {
    HashSet hashSet = new HashSet();
    for (byte b = 0; b != paramArrayOfX509CertificateHolder.length; b++)
      hashSet.addAll(paramArrayOfX509CertificateHolder[b].getCriticalExtensionOIDs()); 
    return hashSet;
  }
}
