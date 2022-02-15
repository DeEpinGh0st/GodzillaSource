package org.bouncycastle.jce.exception;

import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;

public class ExtCertPathBuilderException extends CertPathBuilderException implements ExtException {
  private Throwable cause;
  
  public ExtCertPathBuilderException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public ExtCertPathBuilderException(String paramString, Throwable paramThrowable, CertPath paramCertPath, int paramInt) {
    super(paramString, paramThrowable);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
