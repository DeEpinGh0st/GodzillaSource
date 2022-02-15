package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public abstract class CipherSpiExt extends CipherSpi {
  public static final int ENCRYPT_MODE = 1;
  
  public static final int DECRYPT_MODE = 2;
  
  protected int opMode;
  
  protected final void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      engineInit(paramInt, paramKey, (AlgorithmParameterSpec)null, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidParameterException(invalidAlgorithmParameterException.getMessage());
    } 
  }
  
  protected final void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramAlgorithmParameters == null) {
      engineInit(paramInt, paramKey, paramSecureRandom);
      return;
    } 
    AlgorithmParameterSpec algorithmParameterSpec = null;
    engineInit(paramInt, paramKey, algorithmParameterSpec, paramSecureRandom);
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec != null && !(paramAlgorithmParameterSpec instanceof AlgorithmParameterSpec))
      throw new InvalidAlgorithmParameterException(); 
    if (paramKey == null || !(paramKey instanceof Key))
      throw new InvalidKeyException(); 
    this.opMode = paramInt;
    if (paramInt == 1) {
      SecureRandom secureRandom = paramSecureRandom;
      initEncrypt(paramKey, paramAlgorithmParameterSpec, secureRandom);
    } else if (paramInt == 2) {
      initDecrypt(paramKey, paramAlgorithmParameterSpec);
    } 
  }
  
  protected final byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    return doFinal(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected final int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    return doFinal(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  protected final int engineGetBlockSize() {
    return getBlockSize();
  }
  
  protected final int engineGetKeySize(Key paramKey) throws InvalidKeyException {
    if (!(paramKey instanceof Key))
      throw new InvalidKeyException("Unsupported key."); 
    return getKeySize(paramKey);
  }
  
  protected final byte[] engineGetIV() {
    return getIV();
  }
  
  protected final int engineGetOutputSize(int paramInt) {
    return getOutputSize(paramInt);
  }
  
  protected final AlgorithmParameters engineGetParameters() {
    return null;
  }
  
  protected final void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    setMode(paramString);
  }
  
  protected final void engineSetPadding(String paramString) throws NoSuchPaddingException {
    setPadding(paramString);
  }
  
  protected final byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    return update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected final int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    return update(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  public abstract void initEncrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  public abstract void initDecrypt(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  public abstract String getName();
  
  public abstract int getBlockSize();
  
  public abstract int getOutputSize(int paramInt);
  
  public abstract int getKeySize(Key paramKey) throws InvalidKeyException;
  
  public abstract AlgorithmParameterSpec getParameters();
  
  public abstract byte[] getIV();
  
  protected abstract void setMode(String paramString) throws NoSuchAlgorithmException;
  
  protected abstract void setPadding(String paramString) throws NoSuchPaddingException;
  
  public final byte[] update(byte[] paramArrayOfbyte) {
    return update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public abstract byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  public abstract int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException;
  
  public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
    return doFinal(null, 0, 0);
  }
  
  public final byte[] doFinal(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException {
    return doFinal(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public abstract byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException;
  
  public abstract int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException;
}
