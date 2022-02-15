package org.springframework.cglib.core;

















public class CodeGenerationException
  extends RuntimeException
{
  private Throwable cause;
  
  public CodeGenerationException(Throwable cause) {
    super(cause.getClass().getName() + "-->" + cause.getMessage());
    this.cause = cause;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
