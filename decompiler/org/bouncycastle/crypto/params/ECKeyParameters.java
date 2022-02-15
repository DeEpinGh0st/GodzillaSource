package org.bouncycastle.crypto.params;

public class ECKeyParameters extends AsymmetricKeyParameter {
  ECDomainParameters params;
  
  protected ECKeyParameters(boolean paramBoolean, ECDomainParameters paramECDomainParameters) {
    super(paramBoolean);
    this.params = paramECDomainParameters;
  }
  
  public ECDomainParameters getParameters() {
    return this.params;
  }
}
