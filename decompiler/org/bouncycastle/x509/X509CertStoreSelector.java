package org.bouncycastle.x509;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.Selector;

public class X509CertStoreSelector extends X509CertSelector implements Selector {
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509Certificate))
      return false; 
    X509Certificate x509Certificate = (X509Certificate)paramObject;
    return super.match(x509Certificate);
  }
  
  public boolean match(Certificate paramCertificate) {
    return match(paramCertificate);
  }
  
  public Object clone() {
    return super.clone();
  }
  
  public static X509CertStoreSelector getInstance(X509CertSelector paramX509CertSelector) {
    if (paramX509CertSelector == null)
      throw new IllegalArgumentException("cannot create from null selector"); 
    X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
    x509CertStoreSelector.setAuthorityKeyIdentifier(paramX509CertSelector.getAuthorityKeyIdentifier());
    x509CertStoreSelector.setBasicConstraints(paramX509CertSelector.getBasicConstraints());
    x509CertStoreSelector.setCertificate(paramX509CertSelector.getCertificate());
    x509CertStoreSelector.setCertificateValid(paramX509CertSelector.getCertificateValid());
    x509CertStoreSelector.setMatchAllSubjectAltNames(paramX509CertSelector.getMatchAllSubjectAltNames());
    try {
      x509CertStoreSelector.setPathToNames(paramX509CertSelector.getPathToNames());
      x509CertStoreSelector.setExtendedKeyUsage(paramX509CertSelector.getExtendedKeyUsage());
      x509CertStoreSelector.setNameConstraints(paramX509CertSelector.getNameConstraints());
      x509CertStoreSelector.setPolicy(paramX509CertSelector.getPolicy());
      x509CertStoreSelector.setSubjectPublicKeyAlgID(paramX509CertSelector.getSubjectPublicKeyAlgID());
      x509CertStoreSelector.setSubjectAlternativeNames(paramX509CertSelector.getSubjectAlternativeNames());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error in passed in selector: " + iOException);
    } 
    x509CertStoreSelector.setIssuer(paramX509CertSelector.getIssuer());
    x509CertStoreSelector.setKeyUsage(paramX509CertSelector.getKeyUsage());
    x509CertStoreSelector.setPrivateKeyValid(paramX509CertSelector.getPrivateKeyValid());
    x509CertStoreSelector.setSerialNumber(paramX509CertSelector.getSerialNumber());
    x509CertStoreSelector.setSubject(paramX509CertSelector.getSubject());
    x509CertStoreSelector.setSubjectKeyIdentifier(paramX509CertSelector.getSubjectKeyIdentifier());
    x509CertStoreSelector.setSubjectPublicKey(paramX509CertSelector.getSubjectPublicKey());
    return x509CertStoreSelector;
  }
}
