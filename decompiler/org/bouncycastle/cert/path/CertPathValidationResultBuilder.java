package org.bouncycastle.cert.path;

class CertPathValidationResultBuilder {
  public CertPathValidationResult build() {
    return new CertPathValidationResult(null, 0, 0, null);
  }
  
  public void addException(CertPathValidationException paramCertPathValidationException) {}
}
