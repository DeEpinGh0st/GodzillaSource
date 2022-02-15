package org.bouncycastle.operator;

public class OperatorException extends Exception {
  private Throwable cause;
  
  public OperatorException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public OperatorException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
