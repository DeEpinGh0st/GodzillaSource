package org.bouncycastle.crypto.modes.gcm;

public interface GCMExponentiator {
  void init(byte[] paramArrayOfbyte);
  
  void exponentiateX(long paramLong, byte[] paramArrayOfbyte);
}
