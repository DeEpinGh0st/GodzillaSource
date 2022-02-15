package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class McElieceCCA2KeyParameters extends AsymmetricKeyParameter {
  private String params;
  
  public McElieceCCA2KeyParameters(boolean paramBoolean, String paramString) {
    super(paramBoolean);
    this.params = paramString;
  }
  
  public String getDigest() {
    return this.params;
  }
}
