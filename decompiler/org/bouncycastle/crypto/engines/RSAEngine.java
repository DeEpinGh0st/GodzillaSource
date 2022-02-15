package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;

public class RSAEngine implements AsymmetricBlockCipher {
  private RSACoreEngine core;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (this.core == null)
      this.core = new RSACoreEngine(); 
    this.core.init(paramBoolean, paramCipherParameters);
  }
  
  public int getInputBlockSize() {
    return this.core.getInputBlockSize();
  }
  
  public int getOutputBlockSize() {
    return this.core.getOutputBlockSize();
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.core == null)
      throw new IllegalStateException("RSA engine not initialised"); 
    return this.core.convertOutput(this.core.processBlock(this.core.convertInput(paramArrayOfbyte, paramInt1, paramInt2)));
  }
}
