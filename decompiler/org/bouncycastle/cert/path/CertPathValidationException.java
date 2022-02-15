package org.bouncycastle.cert.path;

public class CertPathValidationException extends Exception {
  private final Exception cause;
  
  public CertPathValidationException(String paramString) {
    this(paramString, null);
  }
  
  public CertPathValidationException(String paramString, Exception paramException) {
    super(paramString);
    this.cause = paramException;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
