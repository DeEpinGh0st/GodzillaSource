package org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;

public class ECKeySpec implements KeySpec {
  private ECParameterSpec spec;
  
  protected ECKeySpec(ECParameterSpec paramECParameterSpec) {
    this.spec = paramECParameterSpec;
  }
  
  public ECParameterSpec getParams() {
    return this.spec;
  }
}
