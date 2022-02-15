package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException extends IOException {
  private Throwable cause;
  
  public OperatorStreamException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
