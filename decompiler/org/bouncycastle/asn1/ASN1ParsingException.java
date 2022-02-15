package org.bouncycastle.asn1;

public class ASN1ParsingException extends IllegalStateException {
  private Throwable cause;
  
  public ASN1ParsingException(String paramString) {
    super(paramString);
  }
  
  public ASN1ParsingException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
