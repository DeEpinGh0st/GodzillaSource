package org.bouncycastle.crypto.params;

public class DSAKeyParameters extends AsymmetricKeyParameter {
  private DSAParameters params;
  
  public DSAKeyParameters(boolean paramBoolean, DSAParameters paramDSAParameters) {
    super(paramBoolean);
    this.params = paramDSAParameters;
  }
  
  public DSAParameters getParameters() {
    return this.params;
  }
}
