package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class ECNamedCurveGenParameterSpec implements AlgorithmParameterSpec {
  private String name;
  
  public ECNamedCurveGenParameterSpec(String paramString) {
    this.name = paramString;
  }
  
  public String getName() {
    return this.name;
  }
}
