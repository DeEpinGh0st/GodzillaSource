package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class GMSSKeyParameters extends AsymmetricKeyParameter {
  private GMSSParameters params;
  
  public GMSSKeyParameters(boolean paramBoolean, GMSSParameters paramGMSSParameters) {
    super(paramBoolean);
    this.params = paramGMSSParameters;
  }
  
  public GMSSParameters getParameters() {
    return this.params;
  }
}
