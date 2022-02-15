package org.bouncycastle.openssl;

public interface PEMDecryptor {
  byte[] decrypt(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws PEMException;
}
