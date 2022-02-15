package org.bouncycastle.cert.path;

import java.util.Collections;
import java.util.Set;

public class CertPathValidationResult {
  private final boolean isValid;
  
  private final CertPathValidationException cause;
  
  private final Set unhandledCriticalExtensionOIDs;
  
  private int[] certIndexes;
  
  public CertPathValidationResult(CertPathValidationContext paramCertPathValidationContext) {
    this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(paramCertPathValidationContext.getUnhandledCriticalExtensionOIDs());
    this.isValid = this.unhandledCriticalExtensionOIDs.isEmpty();
    this.cause = null;
  }
  
  public CertPathValidationResult(CertPathValidationContext paramCertPathValidationContext, int paramInt1, int paramInt2, CertPathValidationException paramCertPathValidationException) {
    this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(paramCertPathValidationContext.getUnhandledCriticalExtensionOIDs());
    this.isValid = false;
    this.cause = paramCertPathValidationException;
  }
  
  public CertPathValidationResult(CertPathValidationContext paramCertPathValidationContext, int[] paramArrayOfint1, int[] paramArrayOfint2, CertPathValidationException[] paramArrayOfCertPathValidationException) {
    this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(paramCertPathValidationContext.getUnhandledCriticalExtensionOIDs());
    this.isValid = false;
    this.cause = paramArrayOfCertPathValidationException[0];
    this.certIndexes = paramArrayOfint1;
  }
  
  public boolean isValid() {
    return this.isValid;
  }
  
  public Exception getCause() {
    return (this.cause != null) ? this.cause : (!this.unhandledCriticalExtensionOIDs.isEmpty() ? new CertPathValidationException("Unhandled Critical Extensions") : null);
  }
  
  public Set getUnhandledCriticalExtensionOIDs() {
    return this.unhandledCriticalExtensionOIDs;
  }
  
  public boolean isDetailed() {
    return (this.certIndexes != null);
  }
}
