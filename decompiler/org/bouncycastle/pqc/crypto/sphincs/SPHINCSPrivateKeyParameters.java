package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public class SPHINCSPrivateKeyParameters extends AsymmetricKeyParameter {
  private final byte[] keyData;
  
  public SPHINCSPrivateKeyParameters(byte[] paramArrayOfbyte) {
    super(true);
    this.keyData = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getKeyData() {
    return Arrays.clone(this.keyData);
  }
}
