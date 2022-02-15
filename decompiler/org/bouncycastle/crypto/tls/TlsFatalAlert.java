package org.bouncycastle.crypto.tls;

public class TlsFatalAlert extends TlsException {
  protected short alertDescription;
  
  public TlsFatalAlert(short paramShort) {
    this(paramShort, null);
  }
  
  public TlsFatalAlert(short paramShort, Throwable paramThrowable) {
    super(AlertDescription.getText(paramShort), paramThrowable);
    this.alertDescription = paramShort;
  }
  
  public short getAlertDescription() {
    return this.alertDescription;
  }
}
