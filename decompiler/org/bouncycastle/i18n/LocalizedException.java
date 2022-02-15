package org.bouncycastle.i18n;

import java.util.Locale;

public class LocalizedException extends Exception {
  protected ErrorBundle message;
  
  private Throwable cause;
  
  public LocalizedException(ErrorBundle paramErrorBundle) {
    super(paramErrorBundle.getText(Locale.getDefault()));
    this.message = paramErrorBundle;
  }
  
  public LocalizedException(ErrorBundle paramErrorBundle, Throwable paramThrowable) {
    super(paramErrorBundle.getText(Locale.getDefault()));
    this.message = paramErrorBundle;
    this.cause = paramThrowable;
  }
  
  public ErrorBundle getErrorMessage() {
    return this.message;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
