package org.bouncycastle.crypto.tls;

public interface TlsPSKIdentity {
  void skipIdentityHint();
  
  void notifyIdentityHint(byte[] paramArrayOfbyte);
  
  byte[] getPSKIdentity();
  
  byte[] getPSK();
}
