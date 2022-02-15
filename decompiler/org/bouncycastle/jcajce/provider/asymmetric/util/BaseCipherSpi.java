package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public abstract class BaseCipherSpi extends CipherSpi {
  private Class[] availableSpecs = new Class[] { IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class };
  
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  protected AlgorithmParameters engineParams = null;
  
  protected Wrapper wrapEngine = null;
  
  private int ivSize;
  
  private byte[] iv;
  
  protected int engineGetBlockSize() {
    return 0;
  }
  
  protected byte[] engineGetIV() {
    return null;
  }
  
  protected int engineGetKeySize(Key paramKey) {
    return (paramKey.getEncoded()).length;
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return -1;
  }
  
  protected AlgorithmParameters engineGetParameters() {
    return null;
  }
  
  protected final AlgorithmParameters createParametersInstance(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException {
    return this.helper.createAlgorithmParameters(paramString);
  }
  
  protected void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    throw new NoSuchAlgorithmException("can't support mode " + paramString);
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    throw new NoSuchPaddingException("Padding " + paramString + " unknown.");
  }
  
  protected byte[] engineWrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    byte[] arrayOfByte = paramKey.getEncoded();
    if (arrayOfByte == null)
      throw new InvalidKeyException("Cannot wrap key, null encoding."); 
    try {
      return (this.wrapEngine == null) ? engineDoFinal(arrayOfByte, 0, arrayOfByte.length) : this.wrapEngine.wrap(arrayOfByte, 0, arrayOfByte.length);
    } catch (BadPaddingException badPaddingException) {
      throw new IllegalBlockSizeException(badPaddingException.getMessage());
    } 
  }
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException {
    byte[] arrayOfByte;
    try {
      if (this.wrapEngine == null) {
        arrayOfByte = engineDoFinal(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      } else {
        arrayOfByte = this.wrapEngine.unwrap(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      } 
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new InvalidKeyException(invalidCipherTextException.getMessage());
    } catch (BadPaddingException badPaddingException) {
      throw new InvalidKeyException("unable to unwrap") {
          public synchronized Throwable getCause() {
            return e;
          }
        };
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new InvalidKeyException(illegalBlockSizeException.getMessage());
    } 
    if (paramInt == 3)
      return new SecretKeySpec(arrayOfByte, paramString); 
    if (paramString.equals("") && paramInt == 2)
      try {
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(arrayOfByte);
        PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(privateKeyInfo);
        if (privateKey != null)
          return privateKey; 
        throw new InvalidKeyException("algorithm " + privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
      } catch (Exception exception) {
        throw new InvalidKeyException("Invalid key encoding.");
      }  
    try {
      KeyFactory keyFactory = this.helper.createKeyFactory(paramString);
      if (paramInt == 1)
        return keyFactory.generatePublic(new X509EncodedKeySpec(arrayOfByte)); 
      if (paramInt == 2)
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(arrayOfByte)); 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InvalidKeyException("Unknown key type " + noSuchAlgorithmException.getMessage());
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("Unknown key type " + invalidKeySpecException.getMessage());
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new InvalidKeyException("Unknown key type " + noSuchProviderException.getMessage());
    } 
    throw new InvalidKeyException("Unknown key type " + paramInt);
  }
}
