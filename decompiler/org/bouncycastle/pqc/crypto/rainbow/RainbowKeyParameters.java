package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class RainbowKeyParameters extends AsymmetricKeyParameter {
  private int docLength;
  
  public RainbowKeyParameters(boolean paramBoolean, int paramInt) {
    super(paramBoolean);
    this.docLength = paramInt;
  }
  
  public int getDocLength() {
    return this.docLength;
  }
}
