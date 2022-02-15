package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;

public class HMacSP800DRBG implements SP80090DRBG {
  private static final long RESEED_MAX = 140737488355328L;
  
  private static final int MAX_BITS_REQUEST = 262144;
  
  private byte[] _K;
  
  private byte[] _V;
  
  private long _reseedCounter;
  
  private EntropySource _entropySource;
  
  private Mac _hMac;
  
  private int _securityStrength;
  
  public HMacSP800DRBG(Mac paramMac, int paramInt, EntropySource paramEntropySource, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramInt > Utils.getMaxSecurityStrength(paramMac))
      throw new IllegalArgumentException("Requested security strength is not supported by the derivation function"); 
    if (paramEntropySource.entropySize() < paramInt)
      throw new IllegalArgumentException("Not enough entropy for security strength required"); 
    this._securityStrength = paramInt;
    this._entropySource = paramEntropySource;
    this._hMac = paramMac;
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, paramArrayOfbyte2, paramArrayOfbyte1);
    this._K = new byte[paramMac.getMacSize()];
    this._V = new byte[this._K.length];
    Arrays.fill(this._V, (byte)1);
    hmac_DRBG_Update(arrayOfByte2);
    this._reseedCounter = 1L;
  }
  
  private void hmac_DRBG_Update(byte[] paramArrayOfbyte) {
    hmac_DRBG_Update_Func(paramArrayOfbyte, (byte)0);
    if (paramArrayOfbyte != null)
      hmac_DRBG_Update_Func(paramArrayOfbyte, (byte)1); 
  }
  
  private void hmac_DRBG_Update_Func(byte[] paramArrayOfbyte, byte paramByte) {
    this._hMac.init((CipherParameters)new KeyParameter(this._K));
    this._hMac.update(this._V, 0, this._V.length);
    this._hMac.update(paramByte);
    if (paramArrayOfbyte != null)
      this._hMac.update(paramArrayOfbyte, 0, paramArrayOfbyte.length); 
    this._hMac.doFinal(this._K, 0);
    this._hMac.init((CipherParameters)new KeyParameter(this._K));
    this._hMac.update(this._V, 0, this._V.length);
    this._hMac.doFinal(this._V, 0);
  }
  
  public int getBlockSize() {
    return this._V.length * 8;
  }
  
  public int generate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean) {
    int i = paramArrayOfbyte1.length * 8;
    if (i > 262144)
      throw new IllegalArgumentException("Number of bits per request limited to 262144"); 
    if (this._reseedCounter > 140737488355328L)
      return -1; 
    if (paramBoolean) {
      reseed(paramArrayOfbyte2);
      paramArrayOfbyte2 = null;
    } 
    if (paramArrayOfbyte2 != null)
      hmac_DRBG_Update(paramArrayOfbyte2); 
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length];
    int j = paramArrayOfbyte1.length / this._V.length;
    this._hMac.init((CipherParameters)new KeyParameter(this._K));
    for (byte b = 0; b < j; b++) {
      this._hMac.update(this._V, 0, this._V.length);
      this._hMac.doFinal(this._V, 0);
      System.arraycopy(this._V, 0, arrayOfByte, b * this._V.length, this._V.length);
    } 
    if (j * this._V.length < arrayOfByte.length) {
      this._hMac.update(this._V, 0, this._V.length);
      this._hMac.doFinal(this._V, 0);
      System.arraycopy(this._V, 0, arrayOfByte, j * this._V.length, arrayOfByte.length - j * this._V.length);
    } 
    hmac_DRBG_Update(paramArrayOfbyte2);
    this._reseedCounter++;
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    return i;
  }
  
  public void reseed(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, paramArrayOfbyte);
    hmac_DRBG_Update(arrayOfByte2);
    this._reseedCounter = 1L;
  }
  
  private byte[] getEntropy() {
    byte[] arrayOfByte = this._entropySource.getEntropy();
    if (arrayOfByte.length < (this._securityStrength + 7) / 8)
      throw new IllegalStateException("Insufficient entropy provided by entropy source"); 
    return arrayOfByte;
  }
}
