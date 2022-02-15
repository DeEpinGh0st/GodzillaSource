package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class TlsException extends IOException {
  protected Throwable cause;
  
  public TlsException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
