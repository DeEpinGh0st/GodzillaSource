package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class KGMac implements Mac {
  private final KGCMBlockCipher cipher;
  
  private final int macSizeBits;
  
  public KGMac(KGCMBlockCipher paramKGCMBlockCipher) {
    this.cipher = paramKGCMBlockCipher;
    this.macSizeBits = paramKGCMBlockCipher.getUnderlyingCipher().getBlockSize() * 8;
  }
  
  public KGMac(KGCMBlockCipher paramKGCMBlockCipher, int paramInt) {
    this.cipher = paramKGCMBlockCipher;
    this.macSizeBits = paramInt;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
      this.cipher.init(true, (CipherParameters)new AEADParameters(keyParameter, this.macSizeBits, arrayOfByte));
    } else {
      throw new IllegalArgumentException("KGMAC requires ParametersWithIV");
    } 
  }
  
  public String getAlgorithmName() {
    return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-KGMAC";
  }
  
  public int getMacSize() {
    return this.macSizeBits / 8;
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    this.cipher.processAADByte(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    this.cipher.processAADBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    try {
      return this.cipher.doFinal(paramArrayOfbyte, paramInt);
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new IllegalStateException(invalidCipherTextException.toString());
    } 
  }
  
  public void reset() {
    this.cipher.reset();
  }
}
