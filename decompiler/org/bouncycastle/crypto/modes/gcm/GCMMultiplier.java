package org.bouncycastle.crypto.modes.gcm;

public interface GCMMultiplier {
  void init(byte[] paramArrayOfbyte);
  
  void multiplyH(byte[] paramArrayOfbyte);
}
