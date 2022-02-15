package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Pack;

public class OldIESEngine extends IESEngine {
  public OldIESEngine(BasicAgreement paramBasicAgreement, DerivationFunction paramDerivationFunction, Mac paramMac) {
    super(paramBasicAgreement, paramDerivationFunction, paramMac);
  }
  
  public OldIESEngine(BasicAgreement paramBasicAgreement, DerivationFunction paramDerivationFunction, Mac paramMac, BufferedBlockCipher paramBufferedBlockCipher) {
    super(paramBasicAgreement, paramDerivationFunction, paramMac, paramBufferedBlockCipher);
  }
  
  protected byte[] getLengthTag(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[4];
    if (paramArrayOfbyte != null)
      Pack.intToBigEndian(paramArrayOfbyte.length * 8, arrayOfByte, 0); 
    return arrayOfByte;
  }
}
