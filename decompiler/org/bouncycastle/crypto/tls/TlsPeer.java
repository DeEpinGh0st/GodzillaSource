package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsPeer {
  boolean shouldUseGMTUnixTime();
  
  void notifySecureRenegotiation(boolean paramBoolean) throws IOException;
  
  TlsCompression getCompression() throws IOException;
  
  TlsCipher getCipher() throws IOException;
  
  void notifyAlertRaised(short paramShort1, short paramShort2, String paramString, Throwable paramThrowable);
  
  void notifyAlertReceived(short paramShort1, short paramShort2);
  
  void notifyHandshakeComplete() throws IOException;
}
