package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;

public class ISO18033KDFParameters implements DerivationParameters {
  byte[] seed;
  
  public ISO18033KDFParameters(byte[] paramArrayOfbyte) {
    this.seed = paramArrayOfbyte;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
}
