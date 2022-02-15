package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;

public class PKIXCertStoreSelector<T extends Certificate> implements Selector<T> {
  private final CertSelector baseSelector;
  
  private PKIXCertStoreSelector(CertSelector paramCertSelector) {
    this.baseSelector = paramCertSelector;
  }
  
  public boolean match(Certificate paramCertificate) {
    return this.baseSelector.match(paramCertificate);
  }
  
  public Object clone() {
    return new PKIXCertStoreSelector(this.baseSelector);
  }
  
  public static Collection<? extends Certificate> getCertificates(PKIXCertStoreSelector paramPKIXCertStoreSelector, CertStore paramCertStore) throws CertStoreException {
    return paramCertStore.getCertificates(new SelectorClone(paramPKIXCertStoreSelector));
  }
  
  public static class Builder {
    private final CertSelector baseSelector;
    
    public Builder(CertSelector param1CertSelector) {
      this.baseSelector = (CertSelector)param1CertSelector.clone();
    }
    
    public PKIXCertStoreSelector<? extends Certificate> build() {
      return new PKIXCertStoreSelector<Certificate>(this.baseSelector);
    }
  }
  
  private static class SelectorClone extends X509CertSelector {
    private final PKIXCertStoreSelector selector;
    
    SelectorClone(PKIXCertStoreSelector param1PKIXCertStoreSelector) {
      this.selector = param1PKIXCertStoreSelector;
      if (param1PKIXCertStoreSelector.baseSelector instanceof X509CertSelector) {
        X509CertSelector x509CertSelector = (X509CertSelector)param1PKIXCertStoreSelector.baseSelector;
        setAuthorityKeyIdentifier(x509CertSelector.getAuthorityKeyIdentifier());
        setBasicConstraints(x509CertSelector.getBasicConstraints());
        setCertificate(x509CertSelector.getCertificate());
        setCertificateValid(x509CertSelector.getCertificateValid());
        setKeyUsage(x509CertSelector.getKeyUsage());
        setMatchAllSubjectAltNames(x509CertSelector.getMatchAllSubjectAltNames());
        setPrivateKeyValid(x509CertSelector.getPrivateKeyValid());
        setSerialNumber(x509CertSelector.getSerialNumber());
        setSubjectKeyIdentifier(x509CertSelector.getSubjectKeyIdentifier());
        setSubjectPublicKey(x509CertSelector.getSubjectPublicKey());
        try {
          setExtendedKeyUsage(x509CertSelector.getExtendedKeyUsage());
          setIssuer(x509CertSelector.getIssuerAsBytes());
          setNameConstraints(x509CertSelector.getNameConstraints());
          setPathToNames(x509CertSelector.getPathToNames());
          setPolicy(x509CertSelector.getPolicy());
          setSubject(x509CertSelector.getSubjectAsBytes());
          setSubjectAlternativeNames(x509CertSelector.getSubjectAlternativeNames());
          setSubjectPublicKeyAlgID(x509CertSelector.getSubjectPublicKeyAlgID());
        } catch (IOException iOException) {
          throw new IllegalStateException("base selector invalid: " + iOException.getMessage(), iOException);
        } 
      } 
    }
    
    public boolean match(Certificate param1Certificate) {
      return (this.selector == null) ? ((param1Certificate != null)) : this.selector.match(param1Certificate);
    }
  }
}
