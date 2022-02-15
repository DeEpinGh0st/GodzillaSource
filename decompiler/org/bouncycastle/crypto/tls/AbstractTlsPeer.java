package org.bouncycastle.crypto.tls;

import java.io.IOException;

public abstract class AbstractTlsPeer implements TlsPeer {
  public boolean shouldUseGMTUnixTime() {
    return false;
  }
  
  public void notifySecureRenegotiation(boolean paramBoolean) throws IOException {
    if (!paramBoolean)
      throw new TlsFatalAlert((short)40); 
  }
  
  public void notifyAlertRaised(short paramShort1, short paramShort2, String paramString, Throwable paramThrowable) {}
  
  public void notifyAlertReceived(short paramShort1, short paramShort2) {}
  
  public void notifyHandshakeComplete() throws IOException {}
}
