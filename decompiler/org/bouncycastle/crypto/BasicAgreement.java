package org.bouncycastle.crypto;

import java.math.BigInteger;

public interface BasicAgreement {
  void init(CipherParameters paramCipherParameters);
  
  int getFieldSize();
  
  BigInteger calculateAgreement(CipherParameters paramCipherParameters);
}
