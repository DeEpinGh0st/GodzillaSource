package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;

public interface TlsHandshakeHash extends Digest {
  void init(TlsContext paramTlsContext);
  
  TlsHandshakeHash notifyPRFDetermined();
  
  void trackHashAlgorithm(short paramShort);
  
  void sealHashAlgorithms();
  
  TlsHandshakeHash stopTracking();
  
  Digest forkPRFHash();
  
  byte[] getFinalHash(short paramShort);
}
