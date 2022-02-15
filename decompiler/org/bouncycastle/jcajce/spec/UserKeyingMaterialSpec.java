package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class UserKeyingMaterialSpec implements AlgorithmParameterSpec {
  private final byte[] userKeyingMaterial;
  
  public UserKeyingMaterialSpec(byte[] paramArrayOfbyte) {
    this.userKeyingMaterial = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getUserKeyingMaterial() {
    return Arrays.clone(this.userKeyingMaterial);
  }
}
