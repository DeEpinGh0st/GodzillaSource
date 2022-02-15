package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class CTRSP800DRBG implements SP80090DRBG {
  private static final long TDEA_RESEED_MAX = 2147483648L;
  
  private static final long AES_RESEED_MAX = 140737488355328L;
  
  private static final int TDEA_MAX_BITS_REQUEST = 4096;
  
  private static final int AES_MAX_BITS_REQUEST = 262144;
  
  private EntropySource _entropySource;
  
  private BlockCipher _engine;
  
  private int _keySizeInBits;
  
  private int _seedLength;
  
  private int _securityStrength;
  
  private byte[] _Key;
  
  private byte[] _V;
  
  private long _reseedCounter = 0L;
  
  private boolean _isTDEA = false;
  
  private static final byte[] K_BITS = Hex.decode("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");
  
  public CTRSP800DRBG(BlockCipher paramBlockCipher, int paramInt1, int paramInt2, EntropySource paramEntropySource, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this._entropySource = paramEntropySource;
    this._engine = paramBlockCipher;
    this._keySizeInBits = paramInt1;
    this._securityStrength = paramInt2;
    this._seedLength = paramInt1 + paramBlockCipher.getBlockSize() * 8;
    this._isTDEA = isTDEA(paramBlockCipher);
    if (paramInt2 > 256)
      throw new IllegalArgumentException("Requested security strength is not supported by the derivation function"); 
    if (getMaxSecurityStrength(paramBlockCipher, paramInt1) < paramInt2)
      throw new IllegalArgumentException("Requested security strength is not supported by block cipher and key size"); 
    if (paramEntropySource.entropySize() < paramInt2)
      throw new IllegalArgumentException("Not enough entropy for security strength required"); 
    byte[] arrayOfByte = getEntropy();
    CTR_DRBG_Instantiate_algorithm(arrayOfByte, paramArrayOfbyte2, paramArrayOfbyte1);
  }
  
  private void CTR_DRBG_Instantiate_algorithm(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    byte[] arrayOfByte1 = Arrays.concatenate(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3);
    byte[] arrayOfByte2 = Block_Cipher_df(arrayOfByte1, this._seedLength);
    int i = this._engine.getBlockSize();
    this._Key = new byte[(this._keySizeInBits + 7) / 8];
    this._V = new byte[i];
    CTR_DRBG_Update(arrayOfByte2, this._Key, this._V);
    this._reseedCounter = 1L;
  }
  
  private void CTR_DRBG_Update(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    byte[] arrayOfByte1 = new byte[paramArrayOfbyte1.length];
    byte[] arrayOfByte2 = new byte[this._engine.getBlockSize()];
    byte b = 0;
    int i = this._engine.getBlockSize();
    this._engine.init(true, (CipherParameters)new KeyParameter(expandKey(paramArrayOfbyte2)));
    while (b * i < paramArrayOfbyte1.length) {
      addOneTo(paramArrayOfbyte3);
      this._engine.processBlock(paramArrayOfbyte3, 0, arrayOfByte2, 0);
      int j = (arrayOfByte1.length - b * i > i) ? i : (arrayOfByte1.length - b * i);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * i, j);
      b++;
    } 
    XOR(arrayOfByte1, paramArrayOfbyte1, arrayOfByte1, 0);
    System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    System.arraycopy(arrayOfByte1, paramArrayOfbyte2.length, paramArrayOfbyte3, 0, paramArrayOfbyte3.length);
  }
  
  private void CTR_DRBG_Reseed_algorithm(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = Arrays.concatenate(getEntropy(), paramArrayOfbyte);
    arrayOfByte = Block_Cipher_df(arrayOfByte, this._seedLength);
    CTR_DRBG_Update(arrayOfByte, this._Key, this._V);
    this._reseedCounter = 1L;
  }
  
  private void XOR(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) {
    for (byte b = 0; b < paramArrayOfbyte1.length; b++)
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte2[b] ^ paramArrayOfbyte3[b + paramInt]); 
  }
  
  private void addOneTo(byte[] paramArrayOfbyte) {
    byte b1 = 1;
    for (byte b2 = 1; b2 <= paramArrayOfbyte.length; b2++) {
      int i = (paramArrayOfbyte[paramArrayOfbyte.length - b2] & 0xFF) + b1;
      b1 = (i > 255) ? 1 : 0;
      paramArrayOfbyte[paramArrayOfbyte.length - b2] = (byte)i;
    } 
  }
  
  private byte[] getEntropy() {
    byte[] arrayOfByte = this._entropySource.getEntropy();
    if (arrayOfByte.length < (this._securityStrength + 7) / 8)
      throw new IllegalStateException("Insufficient entropy provided by entropy source"); 
    return arrayOfByte;
  }
  
  private byte[] Block_Cipher_df(byte[] paramArrayOfbyte, int paramInt) {
    int i = this._engine.getBlockSize();
    int j = paramArrayOfbyte.length;
    int k = paramInt / 8;
    int m = 8 + j + 1;
    int n = (m + i - 1) / i * i;
    byte[] arrayOfByte1 = new byte[n];
    copyIntToByteArray(arrayOfByte1, j, 0);
    copyIntToByteArray(arrayOfByte1, k, 4);
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 8, j);
    arrayOfByte1[8 + j] = Byte.MIN_VALUE;
    byte[] arrayOfByte2 = new byte[this._keySizeInBits / 8 + i];
    byte[] arrayOfByte3 = new byte[i];
    byte[] arrayOfByte4 = new byte[i];
    byte b = 0;
    byte[] arrayOfByte5 = new byte[this._keySizeInBits / 8];
    System.arraycopy(K_BITS, 0, arrayOfByte5, 0, arrayOfByte5.length);
    while (b * i * 8 < this._keySizeInBits + i * 8) {
      copyIntToByteArray(arrayOfByte4, b, 0);
      BCC(arrayOfByte3, arrayOfByte5, arrayOfByte4, arrayOfByte1);
      int i1 = (arrayOfByte2.length - b * i > i) ? i : (arrayOfByte2.length - b * i);
      System.arraycopy(arrayOfByte3, 0, arrayOfByte2, b * i, i1);
      b++;
    } 
    byte[] arrayOfByte6 = new byte[i];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte5, 0, arrayOfByte5.length);
    System.arraycopy(arrayOfByte2, arrayOfByte5.length, arrayOfByte6, 0, arrayOfByte6.length);
    arrayOfByte2 = new byte[paramInt / 2];
    b = 0;
    this._engine.init(true, (CipherParameters)new KeyParameter(expandKey(arrayOfByte5)));
    while (b * i < arrayOfByte2.length) {
      this._engine.processBlock(arrayOfByte6, 0, arrayOfByte6, 0);
      int i1 = (arrayOfByte2.length - b * i > i) ? i : (arrayOfByte2.length - b * i);
      System.arraycopy(arrayOfByte6, 0, arrayOfByte2, b * i, i1);
      b++;
    } 
    return arrayOfByte2;
  }
  
  private void BCC(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) {
    int i = this._engine.getBlockSize();
    byte[] arrayOfByte1 = new byte[i];
    int j = paramArrayOfbyte4.length / i;
    byte[] arrayOfByte2 = new byte[i];
    this._engine.init(true, (CipherParameters)new KeyParameter(expandKey(paramArrayOfbyte2)));
    this._engine.processBlock(paramArrayOfbyte3, 0, arrayOfByte1, 0);
    for (byte b = 0; b < j; b++) {
      XOR(arrayOfByte2, arrayOfByte1, paramArrayOfbyte4, b * i);
      this._engine.processBlock(arrayOfByte2, 0, arrayOfByte1, 0);
    } 
    System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
  }
  
  private void copyIntToByteArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 0] = (byte)(paramInt1 >> 24);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2 + 3] = (byte)paramInt1;
  }
  
  public int getBlockSize() {
    return this._V.length * 8;
  }
  
  public int generate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean) {
    if (this._isTDEA) {
      if (this._reseedCounter > 2147483648L)
        return -1; 
      if (Utils.isTooLarge(paramArrayOfbyte1, 512))
        throw new IllegalArgumentException("Number of bits per request limited to 4096"); 
    } else {
      if (this._reseedCounter > 140737488355328L)
        return -1; 
      if (Utils.isTooLarge(paramArrayOfbyte1, 32768))
        throw new IllegalArgumentException("Number of bits per request limited to 262144"); 
    } 
    if (paramBoolean) {
      CTR_DRBG_Reseed_algorithm(paramArrayOfbyte2);
      paramArrayOfbyte2 = null;
    } 
    if (paramArrayOfbyte2 != null) {
      paramArrayOfbyte2 = Block_Cipher_df(paramArrayOfbyte2, this._seedLength);
      CTR_DRBG_Update(paramArrayOfbyte2, this._Key, this._V);
    } else {
      paramArrayOfbyte2 = new byte[this._seedLength];
    } 
    byte[] arrayOfByte = new byte[this._V.length];
    this._engine.init(true, (CipherParameters)new KeyParameter(expandKey(this._Key)));
    for (byte b = 0; b <= paramArrayOfbyte1.length / arrayOfByte.length; b++) {
      int i = (paramArrayOfbyte1.length - b * arrayOfByte.length > arrayOfByte.length) ? arrayOfByte.length : (paramArrayOfbyte1.length - b * this._V.length);
      if (i != 0) {
        addOneTo(this._V);
        this._engine.processBlock(this._V, 0, arrayOfByte, 0);
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte1, b * arrayOfByte.length, i);
      } 
    } 
    CTR_DRBG_Update(paramArrayOfbyte2, this._Key, this._V);
    this._reseedCounter++;
    return paramArrayOfbyte1.length * 8;
  }
  
  public void reseed(byte[] paramArrayOfbyte) {
    CTR_DRBG_Reseed_algorithm(paramArrayOfbyte);
  }
  
  private boolean isTDEA(BlockCipher paramBlockCipher) {
    return (paramBlockCipher.getAlgorithmName().equals("DESede") || paramBlockCipher.getAlgorithmName().equals("TDEA"));
  }
  
  private int getMaxSecurityStrength(BlockCipher paramBlockCipher, int paramInt) {
    return (isTDEA(paramBlockCipher) && paramInt == 168) ? 112 : (paramBlockCipher.getAlgorithmName().equals("AES") ? paramInt : -1);
  }
  
  byte[] expandKey(byte[] paramArrayOfbyte) {
    if (this._isTDEA) {
      byte[] arrayOfByte = new byte[24];
      padKey(paramArrayOfbyte, 0, arrayOfByte, 0);
      padKey(paramArrayOfbyte, 7, arrayOfByte, 8);
      padKey(paramArrayOfbyte, 14, arrayOfByte, 16);
      return arrayOfByte;
    } 
    return paramArrayOfbyte;
  }
  
  private void padKey(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    paramArrayOfbyte2[paramInt2 + 0] = (byte)(paramArrayOfbyte1[paramInt1 + 0] & 0xFE);
    paramArrayOfbyte2[paramInt2 + 1] = (byte)(paramArrayOfbyte1[paramInt1 + 0] << 7 | (paramArrayOfbyte1[paramInt1 + 1] & 0xFC) >>> 1);
    paramArrayOfbyte2[paramInt2 + 2] = (byte)(paramArrayOfbyte1[paramInt1 + 1] << 6 | (paramArrayOfbyte1[paramInt1 + 2] & 0xF8) >>> 2);
    paramArrayOfbyte2[paramInt2 + 3] = (byte)(paramArrayOfbyte1[paramInt1 + 2] << 5 | (paramArrayOfbyte1[paramInt1 + 3] & 0xF0) >>> 3);
    paramArrayOfbyte2[paramInt2 + 4] = (byte)(paramArrayOfbyte1[paramInt1 + 3] << 4 | (paramArrayOfbyte1[paramInt1 + 4] & 0xE0) >>> 4);
    paramArrayOfbyte2[paramInt2 + 5] = (byte)(paramArrayOfbyte1[paramInt1 + 4] << 3 | (paramArrayOfbyte1[paramInt1 + 5] & 0xC0) >>> 5);
    paramArrayOfbyte2[paramInt2 + 6] = (byte)(paramArrayOfbyte1[paramInt1 + 5] << 2 | (paramArrayOfbyte1[paramInt1 + 6] & 0x80) >>> 6);
    paramArrayOfbyte2[paramInt2 + 7] = (byte)(paramArrayOfbyte1[paramInt1 + 6] << 1);
    for (int i = paramInt2; i <= paramInt2 + 7; i++) {
      byte b = paramArrayOfbyte2[i];
      paramArrayOfbyte2[i] = (byte)(b & 0xFE | (b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 0x1) & 0x1);
    } 
  }
}
