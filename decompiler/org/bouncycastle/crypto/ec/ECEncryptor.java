package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

public interface ECEncryptor {
  void init(CipherParameters paramCipherParameters);
  
  ECPair encrypt(ECPoint paramECPoint);
}
