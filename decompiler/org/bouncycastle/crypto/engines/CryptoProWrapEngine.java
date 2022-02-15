package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.util.Pack;

public class CryptoProWrapEngine extends GOST28147WrapEngine {
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      paramCipherParameters = parametersWithRandom.getParameters();
    } 
    ParametersWithUKM parametersWithUKM = (ParametersWithUKM)paramCipherParameters;
    byte[] arrayOfByte = null;
    if (parametersWithUKM.getParameters() instanceof ParametersWithSBox) {
      keyParameter = (KeyParameter)((ParametersWithSBox)parametersWithUKM.getParameters()).getParameters();
      arrayOfByte = ((ParametersWithSBox)parametersWithUKM.getParameters()).getSBox();
    } else {
      keyParameter = (KeyParameter)parametersWithUKM.getParameters();
    } 
    KeyParameter keyParameter = new KeyParameter(cryptoProDiversify(keyParameter.getKey(), parametersWithUKM.getUKM(), arrayOfByte));
    if (arrayOfByte != null) {
      super.init(paramBoolean, (CipherParameters)new ParametersWithUKM((CipherParameters)new ParametersWithSBox((CipherParameters)keyParameter, arrayOfByte), parametersWithUKM.getUKM()));
    } else {
      super.init(paramBoolean, (CipherParameters)new ParametersWithUKM((CipherParameters)keyParameter, parametersWithUKM.getUKM()));
    } 
  }
  
  private static byte[] cryptoProDiversify(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    for (byte b = 0; b != 8; b++) {
      int i = 0;
      int j = 0;
      for (byte b1 = 0; b1 != 8; b1++) {
        int k = Pack.littleEndianToInt(paramArrayOfbyte1, b1 * 4);
        if (bitSet(paramArrayOfbyte2[b], b1)) {
          i += k;
        } else {
          j += k;
        } 
      } 
      byte[] arrayOfByte = new byte[8];
      Pack.intToLittleEndian(i, arrayOfByte, 0);
      Pack.intToLittleEndian(j, arrayOfByte, 4);
      GCFBBlockCipher gCFBBlockCipher = new GCFBBlockCipher(new GOST28147Engine());
      gCFBBlockCipher.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)new ParametersWithSBox((CipherParameters)new KeyParameter(paramArrayOfbyte1), paramArrayOfbyte3), arrayOfByte));
      gCFBBlockCipher.processBlock(paramArrayOfbyte1, 0, paramArrayOfbyte1, 0);
      gCFBBlockCipher.processBlock(paramArrayOfbyte1, 8, paramArrayOfbyte1, 8);
      gCFBBlockCipher.processBlock(paramArrayOfbyte1, 16, paramArrayOfbyte1, 16);
      gCFBBlockCipher.processBlock(paramArrayOfbyte1, 24, paramArrayOfbyte1, 24);
    } 
    return paramArrayOfbyte1;
  }
  
  private static boolean bitSet(byte paramByte, int paramInt) {
    return ((paramByte & 1 << paramInt) != 0);
  }
}
