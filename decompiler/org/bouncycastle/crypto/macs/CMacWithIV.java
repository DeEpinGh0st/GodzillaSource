package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;

public class CMacWithIV extends CMac {
  public CMacWithIV(BlockCipher paramBlockCipher) {
    super(paramBlockCipher);
  }
  
  public CMacWithIV(BlockCipher paramBlockCipher, int paramInt) {
    super(paramBlockCipher, paramInt);
  }
  
  void validate(CipherParameters paramCipherParameters) {}
}
