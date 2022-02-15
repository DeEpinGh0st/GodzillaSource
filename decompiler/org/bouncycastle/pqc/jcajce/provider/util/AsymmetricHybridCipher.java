package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;

public abstract class AsymmetricHybridCipher extends CipherSpiExt {
  protected AlgorithmParameterSpec paramSpec;
  
  protected final void setMode(String paramString) {}
  
  protected final void setPadding(String paramString) {}
  
  public final byte[] getIV() {
    return null;
  }
  
  public final int getBlockSize() {
    return 0;
  }
  
  public final AlgorithmParameterSpec getParameters() {
    return this.paramSpec;
  }
  
  public final int getOutputSize(int paramInt) {
    return (this.opMode == 1) ? encryptOutputSize(paramInt) : decryptOutputSize(paramInt);
  }
  
  public final void initEncrypt(Key paramKey) throws InvalidKeyException {
    try {
      initEncrypt(paramKey, null, new SecureRandom());
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
    } 
  }
  
  public final void initEncrypt(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      initEncrypt(paramKey, null, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
    } 
  }
  
  public final void initEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    initEncrypt(paramKey, paramAlgorithmParameterSpec, new SecureRandom());
  }
  
  public final void initEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.opMode = 1;
    initCipherEncrypt(paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
  }
  
  public final void initDecrypt(Key paramKey) throws InvalidKeyException {
    try {
      initDecrypt(paramKey, null);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
    } 
  }
  
  public final void initDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.opMode = 2;
    initCipherDecrypt(paramKey, paramAlgorithmParameterSpec);
  }
  
  public abstract byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  public final int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    if (paramArrayOfbyte2.length < getOutputSize(paramInt2))
      throw new ShortBufferException("output"); 
    byte[] arrayOfByte = update(paramArrayOfbyte1, paramInt1, paramInt2);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public abstract byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException;
  
  public final int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, BadPaddingException {
    if (paramArrayOfbyte2.length < getOutputSize(paramInt2))
      throw new ShortBufferException("Output buffer too short."); 
    byte[] arrayOfByte = doFinal(paramArrayOfbyte1, paramInt1, paramInt2);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected abstract int encryptOutputSize(int paramInt);
  
  protected abstract int decryptOutputSize(int paramInt);
  
  protected abstract void initCipherEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  protected abstract void initCipherDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;
}
