package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public abstract class BaseWrapCipher extends CipherSpi implements PBE {
  private Class[] availableSpecs = new Class[] { GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class };
  
  protected int pbeType = 2;
  
  protected int pbeHash = 1;
  
  protected int pbeKeySize;
  
  protected int pbeIvSize;
  
  protected AlgorithmParameters engineParams = null;
  
  protected Wrapper wrapEngine = null;
  
  private int ivSize;
  
  private byte[] iv;
  
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  protected BaseWrapCipher() {}
  
  protected BaseWrapCipher(Wrapper paramWrapper) {
    this(paramWrapper, 0);
  }
  
  protected BaseWrapCipher(Wrapper paramWrapper, int paramInt) {
    this.wrapEngine = paramWrapper;
    this.ivSize = paramInt;
  }
  
  protected int engineGetBlockSize() {
    return 0;
  }
  
  protected byte[] engineGetIV() {
    return Arrays.clone(this.iv);
  }
  
  protected int engineGetKeySize(Key paramKey) {
    return (paramKey.getEncoded()).length * 8;
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
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    KeyParameter keyParameter;
    ParametersWithIV parametersWithIV2;
    ParametersWithUKM parametersWithUKM;
    ParametersWithIV parametersWithIV1;
    ParametersWithRandom parametersWithRandom;
    if (paramKey instanceof BCPBEKey) {
      BCPBEKey bCPBEKey = (BCPBEKey)paramKey;
      if (paramAlgorithmParameterSpec instanceof PBEParameterSpec) {
        CipherParameters cipherParameters = PBE.Util.makePBEParameters(bCPBEKey, paramAlgorithmParameterSpec, this.wrapEngine.getAlgorithmName());
      } else if (bCPBEKey.getParam() != null) {
        CipherParameters cipherParameters = bCPBEKey.getParam();
      } else {
        throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
      } 
    } else {
      keyParameter = new KeyParameter(paramKey.getEncoded());
    } 
    if (paramAlgorithmParameterSpec instanceof IvParameterSpec) {
      IvParameterSpec ivParameterSpec = (IvParameterSpec)paramAlgorithmParameterSpec;
      parametersWithIV2 = new ParametersWithIV((CipherParameters)keyParameter, ivParameterSpec.getIV());
    } 
    if (paramAlgorithmParameterSpec instanceof GOST28147WrapParameterSpec) {
      ParametersWithSBox parametersWithSBox;
      GOST28147WrapParameterSpec gOST28147WrapParameterSpec = (GOST28147WrapParameterSpec)paramAlgorithmParameterSpec;
      byte[] arrayOfByte = gOST28147WrapParameterSpec.getSBox();
      if (arrayOfByte != null)
        parametersWithSBox = new ParametersWithSBox((CipherParameters)parametersWithIV2, arrayOfByte); 
      parametersWithUKM = new ParametersWithUKM((CipherParameters)parametersWithSBox, gOST28147WrapParameterSpec.getUKM());
    } 
    if (parametersWithUKM instanceof KeyParameter && this.ivSize != 0) {
      this.iv = new byte[this.ivSize];
      paramSecureRandom.nextBytes(this.iv);
      parametersWithIV1 = new ParametersWithIV((CipherParameters)parametersWithUKM, this.iv);
    } 
    if (paramSecureRandom != null)
      parametersWithRandom = new ParametersWithRandom((CipherParameters)parametersWithIV1, paramSecureRandom); 
    switch (paramInt) {
      case 3:
        this.wrapEngine.init(true, (CipherParameters)parametersWithRandom);
        return;
      case 4:
        this.wrapEngine.init(false, (CipherParameters)parametersWithRandom);
        return;
      case 1:
      case 2:
        throw new IllegalArgumentException("engine only valid for wrapping");
    } 
    System.out.println("eeek!");
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AlgorithmParameterSpec algorithmParameterSpec = null;
    if (paramAlgorithmParameters != null) {
      byte b = 0;
      while (b != this.availableSpecs.length) {
        try {
          algorithmParameterSpec = paramAlgorithmParameters.getParameterSpec(this.availableSpecs[b]);
          break;
        } catch (Exception exception) {
          b++;
        } 
      } 
      if (algorithmParameterSpec == null)
        throw new InvalidAlgorithmParameterException("can't handle parameter " + paramAlgorithmParameters.toString()); 
    } 
    this.engineParams = paramAlgorithmParameters;
    engineInit(paramInt, paramKey, algorithmParameterSpec, paramSecureRandom);
  }
  
  protected void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      engineInit(paramInt, paramKey, (AlgorithmParameterSpec)null, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new IllegalArgumentException(invalidAlgorithmParameterException.getMessage());
    } 
  }
  
  protected byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    throw new RuntimeException("not supported for wrapping");
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    throw new RuntimeException("not supported for wrapping");
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    return null;
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
    return 0;
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
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
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
      throw new InvalidKeyException(badPaddingException.getMessage());
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
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new InvalidKeyException("Unknown key type " + noSuchProviderException.getMessage());
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("Unknown key type " + invalidKeySpecException.getMessage());
    } 
    throw new InvalidKeyException("Unknown key type " + paramInt);
  }
}
