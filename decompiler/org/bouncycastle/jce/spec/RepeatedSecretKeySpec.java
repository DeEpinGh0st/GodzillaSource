package org.bouncycastle.jce.spec;

import org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec;

public class RepeatedSecretKeySpec extends RepeatedSecretKeySpec {
  private String algorithm;
  
  public RepeatedSecretKeySpec(String paramString) {
    super(paramString);
  }
}
