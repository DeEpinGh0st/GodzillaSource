package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class BcAESSymmetricKeyWrapper extends BcSymmetricKeyWrapper {
  public BcAESSymmetricKeyWrapper(KeyParameter paramKeyParameter) {
    super(AESUtil.determineKeyEncAlg(paramKeyParameter), (Wrapper)new AESWrapEngine(), paramKeyParameter);
  }
}
