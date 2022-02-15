package org.bouncycastle.crypto.tls;

public class TlsFatalAlertReceived extends TlsException {
  protected short alertDescription;
  
  public TlsFatalAlertReceived(short paramShort) {
    super(AlertDescription.getText(paramShort), null);
    this.alertDescription = paramShort;
  }
  
  public short getAlertDescription() {
    return this.alertDescription;
  }
}
