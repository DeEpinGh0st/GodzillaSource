package org.bouncycastle.jce.exception;

import java.io.IOException;

public class ExtIOException extends IOException implements ExtException {
  private Throwable cause;
  
  public ExtIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
