package org.bouncycastle.cert;

import java.io.IOException;

public class CertIOException extends IOException {
  private Throwable cause;
  
  public CertIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public CertIOException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
