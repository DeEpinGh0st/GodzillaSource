package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class IESUtil {
  public static IESParameterSpec guessParameterSpec(BufferedBlockCipher paramBufferedBlockCipher, byte[] paramArrayOfbyte) {
    if (paramBufferedBlockCipher == null)
      return new IESParameterSpec(null, null, 128); 
    BlockCipher blockCipher = paramBufferedBlockCipher.getUnderlyingCipher();
    return (blockCipher.getAlgorithmName().equals("DES") || blockCipher.getAlgorithmName().equals("RC2") || blockCipher.getAlgorithmName().equals("RC5-32") || blockCipher.getAlgorithmName().equals("RC5-64")) ? new IESParameterSpec(null, null, 64, 64, paramArrayOfbyte) : (blockCipher.getAlgorithmName().equals("SKIPJACK") ? new IESParameterSpec(null, null, 80, 80, paramArrayOfbyte) : (blockCipher.getAlgorithmName().equals("GOST28147") ? new IESParameterSpec(null, null, 256, 256, paramArrayOfbyte) : new IESParameterSpec(null, null, 128, 128, paramArrayOfbyte)));
  }
}
