package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface MessageEncryptor {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  byte[] messageEncrypt(byte[] paramArrayOfbyte);
  
  byte[] messageDecrypt(byte[] paramArrayOfbyte) throws InvalidCipherTextException;
}
