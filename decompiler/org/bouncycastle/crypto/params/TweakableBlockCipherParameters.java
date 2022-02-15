package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class TweakableBlockCipherParameters implements CipherParameters {
  private final byte[] tweak;
  
  private final KeyParameter key;
  
  public TweakableBlockCipherParameters(KeyParameter paramKeyParameter, byte[] paramArrayOfbyte) {
    this.key = paramKeyParameter;
    this.tweak = Arrays.clone(paramArrayOfbyte);
  }
  
  public KeyParameter getKey() {
    return this.key;
  }
  
  public byte[] getTweak() {
    return this.tweak;
  }
}
