package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class AsymmetricKeyParameter implements CipherParameters {
  boolean privateKey;
  
  public AsymmetricKeyParameter(boolean paramBoolean) {
    this.privateKey = paramBoolean;
  }
  
  public boolean isPrivate() {
    return this.privateKey;
  }
}
