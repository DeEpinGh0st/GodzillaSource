package org.springframework.expression.spel;
























public class InternalParseException
  extends RuntimeException
{
  public InternalParseException(SpelParseException cause) {
    super((Throwable)cause);
  }

  
  public SpelParseException getCause() {
    return (SpelParseException)super.getCause();
  }
}
