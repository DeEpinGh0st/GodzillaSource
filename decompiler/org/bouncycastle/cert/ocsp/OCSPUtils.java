package org.bouncycastle.cert.ocsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;

class OCSPUtils {
  static final X509CertificateHolder[] EMPTY_CERTS = new X509CertificateHolder[0];
  
  static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
  
  static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
  
  static Date extractDate(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    try {
      return paramASN1GeneralizedTime.getDate();
    } catch (Exception exception) {
      throw new IllegalStateException("exception processing GeneralizedTime: " + exception.getMessage());
    } 
  }
  
  static Set getCriticalExtensionOIDs(Extensions paramExtensions) {
    return (paramExtensions == null) ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList((Object[])paramExtensions.getCriticalExtensionOIDs())));
  }
  
  static Set getNonCriticalExtensionOIDs(Extensions paramExtensions) {
    return (paramExtensions == null) ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList((Object[])paramExtensions.getNonCriticalExtensionOIDs())));
  }
  
  static List getExtensionOIDs(Extensions paramExtensions) {
    return (paramExtensions == null) ? EMPTY_LIST : Collections.unmodifiableList(Arrays.asList((Object[])paramExtensions.getExtensionOIDs()));
  }
}
