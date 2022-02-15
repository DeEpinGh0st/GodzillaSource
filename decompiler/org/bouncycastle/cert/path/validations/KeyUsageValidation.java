package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;

public class KeyUsageValidation implements CertPathValidation {
  private boolean isMandatory;
  
  public KeyUsageValidation() {
    this(true);
  }
  
  public KeyUsageValidation(boolean paramBoolean) {
    this.isMandatory = paramBoolean;
  }
  
  public void validate(CertPathValidationContext paramCertPathValidationContext, X509CertificateHolder paramX509CertificateHolder) throws CertPathValidationException {
    paramCertPathValidationContext.addHandledExtension(Extension.keyUsage);
    if (!paramCertPathValidationContext.isEndEntity()) {
      KeyUsage keyUsage = KeyUsage.fromExtensions(paramX509CertificateHolder.getExtensions());
      if (keyUsage != null) {
        if (!keyUsage.hasUsages(4))
          throw new CertPathValidationException("Issuer certificate KeyUsage extension does not permit key signing"); 
      } else if (this.isMandatory) {
        throw new CertPathValidationException("KeyUsage extension not present in CA certificate");
      } 
    } 
  }
  
  public Memoable copy() {
    return (Memoable)new KeyUsageValidation(this.isMandatory);
  }
  
  public void reset(Memoable paramMemoable) {
    KeyUsageValidation keyUsageValidation = (KeyUsageValidation)paramMemoable;
    this.isMandatory = keyUsageValidation.isMandatory;
  }
}
