package org.bouncycastle.crypto.io;

import java.io.IOException;

public class CipherIOException extends IOException {
  private static final long serialVersionUID = 1L;
  
  private final Throwable cause;
  
  public CipherIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
