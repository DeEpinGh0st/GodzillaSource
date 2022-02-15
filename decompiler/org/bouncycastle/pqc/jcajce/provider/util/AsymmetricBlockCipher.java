package org.bouncycastle.pqc.jcajce.provider.util;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

public abstract class AsymmetricBlockCipher extends CipherSpiExt {
  protected AlgorithmParameterSpec paramSpec;
  
  protected ByteArrayOutputStream buf = new ByteArrayOutputStream();
  
  protected int maxPlainTextSize;
  
  protected int cipherTextSize;
  
  public final int getBlockSize() {
    return (this.opMode == 1) ? this.maxPlainTextSize : this.cipherTextSize;
  }
  
  public final byte[] getIV() {
    return null;
  }
  
  public final int getOutputSize(int paramInt) {
    int i = paramInt + this.buf.size();
    int j = getBlockSize();
    return (i > j) ? 0 : j;
  }
  
  public final AlgorithmParameterSpec getParameters() {
    return this.paramSpec;
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
  
  public final byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 != 0)
      this.buf.write(paramArrayOfbyte, paramInt1, paramInt2); 
    return new byte[0];
  }
  
  public final int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    update(paramArrayOfbyte1, paramInt1, paramInt2);
    return 0;
  }
  
  public final byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    checkLength(paramInt2);
    update(paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte = this.buf.toByteArray();
    this.buf.reset();
    switch (this.opMode) {
      case 1:
        return messageEncrypt(arrayOfByte);
      case 2:
        return messageDecrypt(arrayOfByte);
    } 
    return null;
  }
  
  public final int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    if (paramArrayOfbyte2.length < getOutputSize(paramInt2))
      throw new ShortBufferException("Output buffer too short."); 
    byte[] arrayOfByte = doFinal(paramArrayOfbyte1, paramInt1, paramInt2);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected final void setMode(String paramString) {}
  
  protected final void setPadding(String paramString) {}
  
  protected void checkLength(int paramInt) throws IllegalBlockSizeException {
    int i = paramInt + this.buf.size();
    if (this.opMode == 1) {
      if (i > this.maxPlainTextSize)
        throw new IllegalBlockSizeException("The length of the plaintext (" + i + " bytes) is not supported by " + "the cipher (max. " + this.maxPlainTextSize + " bytes)."); 
    } else if (this.opMode == 2 && i != this.cipherTextSize) {
      throw new IllegalBlockSizeException("Illegal ciphertext length (expected " + this.cipherTextSize + " bytes, was " + i + " bytes).");
    } 
  }
  
  protected abstract void initCipherEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  protected abstract void initCipherDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  protected abstract byte[] messageEncrypt(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException;
  
  protected abstract byte[] messageDecrypt(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException;
}
