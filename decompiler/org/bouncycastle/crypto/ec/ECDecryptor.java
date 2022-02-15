package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

public interface ECDecryptor {
  void init(CipherParameters paramCipherParameters);
  
  ECPoint decrypt(ECPair paramECPair);
}
