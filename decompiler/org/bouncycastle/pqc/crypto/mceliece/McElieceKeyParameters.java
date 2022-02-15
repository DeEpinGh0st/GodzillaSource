package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class McElieceKeyParameters extends AsymmetricKeyParameter {
  private McElieceParameters params;
  
  public McElieceKeyParameters(boolean paramBoolean, McElieceParameters paramMcElieceParameters) {
    super(paramBoolean);
    this.params = paramMcElieceParameters;
  }
  
  public McElieceParameters getParameters() {
    return this.params;
  }
}
