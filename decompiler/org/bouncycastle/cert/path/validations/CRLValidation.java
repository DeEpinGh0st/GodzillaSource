package org.bouncycastle.cert.path.validations;

import java.util.Collection;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class CRLValidation implements CertPathValidation {
  private Store crls;
  
  private X500Name workingIssuerName;
  
  public CRLValidation(X500Name paramX500Name, Store paramStore) {
    this.workingIssuerName = paramX500Name;
    this.crls = paramStore;
  }
  
  public void validate(CertPathValidationContext paramCertPathValidationContext, X509CertificateHolder paramX509CertificateHolder) throws CertPathValidationException {
    Collection collection = this.crls.getMatches(new Selector() {
          public boolean match(Object param1Object) {
            X509CRLHolder x509CRLHolder = (X509CRLHolder)param1Object;
            return x509CRLHolder.getIssuer().equals(CRLValidation.this.workingIssuerName);
          }
          
          public Object clone() {
            return this;
          }
        });
    if (collection.isEmpty())
      throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found"); 
    for (X509CRLHolder x509CRLHolder : collection) {
      if (x509CRLHolder.getRevokedCertificate(paramX509CertificateHolder.getSerialNumber()) != null)
        throw new CertPathValidationException("Certificate revoked"); 
    } 
    this.workingIssuerName = paramX509CertificateHolder.getSubject();
  }
  
  public Memoable copy() {
    return (Memoable)new CRLValidation(this.workingIssuerName, this.crls);
  }
  
  public void reset(Memoable paramMemoable) {
    CRLValidation cRLValidation = (CRLValidation)paramMemoable;
    this.workingIssuerName = cRLValidation.workingIssuerName;
    this.crls = cRLValidation.crls;
  }
}
