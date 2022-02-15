package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;

public class KDFParameters implements DerivationParameters {
  byte[] iv;
  
  byte[] shared;
  
  public KDFParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.shared = paramArrayOfbyte1;
    this.iv = paramArrayOfbyte2;
  }
  
  public byte[] getSharedSecret() {
    return this.shared;
  }
  
  public byte[] getIV() {
    return this.iv;
  }
}
