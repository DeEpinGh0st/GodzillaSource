package org.bouncycastle.asn1;

import java.io.IOException;

public class ASN1Exception extends IOException {
  private Throwable cause;
  
  ASN1Exception(String paramString) {
    super(paramString);
  }
  
  ASN1Exception(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
