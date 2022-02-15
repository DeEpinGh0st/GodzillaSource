package org.bouncycastle.crypto;

public interface DerivationFunction {
  void init(DerivationParameters paramDerivationParameters);
  
  int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException;
}
