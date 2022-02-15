package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;

public class CertPath {
  private final X509CertificateHolder[] certificates;
  
  public CertPath(X509CertificateHolder[] paramArrayOfX509CertificateHolder) {
    this.certificates = copyArray(paramArrayOfX509CertificateHolder);
  }
  
  public X509CertificateHolder[] getCertificates() {
    return copyArray(this.certificates);
  }
  
  public CertPathValidationResult validate(CertPathValidation[] paramArrayOfCertPathValidation) {
    CertPathValidationContext certPathValidationContext = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
    for (byte b = 0; b != paramArrayOfCertPathValidation.length; b++) {
      for (int i = this.certificates.length - 1; i >= 0; i--) {
        try {
          certPathValidationContext.setIsEndEntity((i == 0));
          paramArrayOfCertPathValidation[b].validate(certPathValidationContext, this.certificates[i]);
        } catch (CertPathValidationException certPathValidationException) {
          return new CertPathValidationResult(certPathValidationContext, i, b, certPathValidationException);
        } 
      } 
    } 
    return new CertPathValidationResult(certPathValidationContext);
  }
  
  public CertPathValidationResult evaluate(CertPathValidation[] paramArrayOfCertPathValidation) {
    CertPathValidationContext certPathValidationContext = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
    CertPathValidationResultBuilder certPathValidationResultBuilder = new CertPathValidationResultBuilder();
    for (byte b = 0; b != paramArrayOfCertPathValidation.length; b++) {
      for (int i = this.certificates.length - 1; i >= 0; i--) {
        try {
          certPathValidationContext.setIsEndEntity((i == 0));
          paramArrayOfCertPathValidation[b].validate(certPathValidationContext, this.certificates[i]);
        } catch (CertPathValidationException certPathValidationException) {
          certPathValidationResultBuilder.addException(certPathValidationException);
        } 
      } 
    } 
    return certPathValidationResultBuilder.build();
  }
  
  private X509CertificateHolder[] copyArray(X509CertificateHolder[] paramArrayOfX509CertificateHolder) {
    X509CertificateHolder[] arrayOfX509CertificateHolder = new X509CertificateHolder[paramArrayOfX509CertificateHolder.length];
    System.arraycopy(paramArrayOfX509CertificateHolder, 0, arrayOfX509CertificateHolder, 0, arrayOfX509CertificateHolder.length);
    return arrayOfX509CertificateHolder;
  }
  
  public int length() {
    return this.certificates.length;
  }
}
