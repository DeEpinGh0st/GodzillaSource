package org.bouncycastle.crypto.params;

public class GOST3410KeyParameters extends AsymmetricKeyParameter {
  private GOST3410Parameters params;
  
  public GOST3410KeyParameters(boolean paramBoolean, GOST3410Parameters paramGOST3410Parameters) {
    super(paramBoolean);
    this.params = paramGOST3410Parameters;
  }
  
  public GOST3410Parameters getParameters() {
    return this.params;
  }
}
