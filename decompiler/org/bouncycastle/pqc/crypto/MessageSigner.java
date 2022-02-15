package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;

public interface MessageSigner {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  byte[] generateSignature(byte[] paramArrayOfbyte);
  
  boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2);
}
