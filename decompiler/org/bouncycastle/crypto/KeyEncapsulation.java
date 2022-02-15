package org.bouncycastle.crypto;

public interface KeyEncapsulation {
  void init(CipherParameters paramCipherParameters);
  
  CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3);
}
