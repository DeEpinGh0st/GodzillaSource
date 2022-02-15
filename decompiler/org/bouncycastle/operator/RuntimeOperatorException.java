package org.bouncycastle.operator;

public class RuntimeOperatorException extends RuntimeException {
  private Throwable cause;
  
  public RuntimeOperatorException(String paramString) {
    super(paramString);
  }
  
  public RuntimeOperatorException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
