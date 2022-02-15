package org.bouncycastle.cms;

public class CMSAttributeTableGenerationException extends CMSRuntimeException {
  Exception e;
  
  public CMSAttributeTableGenerationException(String paramString) {
    super(paramString);
  }
  
  public CMSAttributeTableGenerationException(String paramString, Exception paramException) {
    super(paramString);
    this.e = paramException;
  }
  
  public Exception getUnderlyingException() {
    return this.e;
  }
  
  public Throwable getCause() {
    return this.e;
  }
}
