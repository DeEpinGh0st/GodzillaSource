package org.bouncycastle.crypto.engines;

public class ARIAWrapEngine extends RFC3394WrapEngine {
  public ARIAWrapEngine() {
    super(new ARIAEngine());
  }
  
  public ARIAWrapEngine(boolean paramBoolean) {
    super(new ARIAEngine(), paramBoolean);
  }
}
