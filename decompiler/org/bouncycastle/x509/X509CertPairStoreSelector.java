package org.bouncycastle.x509;

import org.bouncycastle.util.Selector;

public class X509CertPairStoreSelector implements Selector {
  private X509CertStoreSelector forwardSelector;
  
  private X509CertStoreSelector reverseSelector;
  
  private X509CertificatePair certPair;
  
  public X509CertificatePair getCertPair() {
    return this.certPair;
  }
  
  public void setCertPair(X509CertificatePair paramX509CertificatePair) {
    this.certPair = paramX509CertificatePair;
  }
  
  public void setForwardSelector(X509CertStoreSelector paramX509CertStoreSelector) {
    this.forwardSelector = paramX509CertStoreSelector;
  }
  
  public void setReverseSelector(X509CertStoreSelector paramX509CertStoreSelector) {
    this.reverseSelector = paramX509CertStoreSelector;
  }
  
  public Object clone() {
    X509CertPairStoreSelector x509CertPairStoreSelector = new X509CertPairStoreSelector();
    x509CertPairStoreSelector.certPair = this.certPair;
    if (this.forwardSelector != null)
      x509CertPairStoreSelector.setForwardSelector((X509CertStoreSelector)this.forwardSelector.clone()); 
    if (this.reverseSelector != null)
      x509CertPairStoreSelector.setReverseSelector((X509CertStoreSelector)this.reverseSelector.clone()); 
    return x509CertPairStoreSelector;
  }
  
  public boolean match(Object paramObject) {
    try {
      if (!(paramObject instanceof X509CertificatePair))
        return false; 
      X509CertificatePair x509CertificatePair = (X509CertificatePair)paramObject;
      return (this.forwardSelector != null && !this.forwardSelector.match(x509CertificatePair.getForward())) ? false : ((this.reverseSelector != null && !this.reverseSelector.match(x509CertificatePair.getReverse())) ? false : ((this.certPair != null) ? this.certPair.equals(paramObject) : true));
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public X509CertStoreSelector getForwardSelector() {
    return this.forwardSelector;
  }
  
  public X509CertStoreSelector getReverseSelector() {
    return this.reverseSelector;
  }
}
