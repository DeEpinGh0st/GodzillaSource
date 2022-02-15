package org.bouncycastle.jce.provider;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.util.Strings;

public class BrokenJCEBlockCipher implements BrokenPBE {
  private Class[] availableSpecs = new Class[] { IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class };
  
  private BufferedBlockCipher cipher;
  
  private ParametersWithIV ivParam;
  
  private int pbeType = 2;
  
  private int pbeHash = 1;
  
  private int pbeKeySize;
  
  private int pbeIvSize;
  
  private int ivLength = 0;
  
  private AlgorithmParameters engineParams = null;
  
  protected BrokenJCEBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher(paramBlockCipher);
  }
  
  protected BrokenJCEBlockCipher(BlockCipher paramBlockCipher, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher(paramBlockCipher);
    this.pbeType = paramInt1;
    this.pbeHash = paramInt2;
    this.pbeKeySize = paramInt3;
    this.pbeIvSize = paramInt4;
  }
  
  protected int engineGetBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  protected byte[] engineGetIV() {
    return (this.ivParam != null) ? this.ivParam.getIV() : null;
  }
  
  protected int engineGetKeySize(Key paramKey) {
    return (paramKey.getEncoded()).length;
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return this.cipher.getOutputSize(paramInt);
  }
  
  protected AlgorithmParameters engineGetParameters() {
    if (this.engineParams == null && this.ivParam != null) {
      String str = this.cipher.getUnderlyingCipher().getAlgorithmName();
      if (str.indexOf('/') >= 0)
        str = str.substring(0, str.indexOf('/')); 
      try {
        this.engineParams = AlgorithmParameters.getInstance(str, "BC");
        this.engineParams.init(this.ivParam.getIV());
      } catch (Exception exception) {
        throw new RuntimeException(exception.toString());
      } 
    } 
    return this.engineParams;
  }
  
  protected void engineSetMode(String paramString) {
    String str = Strings.toUpperCase(paramString);
    if (str.equals("ECB")) {
      this.ivLength = 0;
      this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
    } else if (str.equals("CBC")) {
      this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
      this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher(this.cipher.getUnderlyingCipher()));
    } else if (str.startsWith("OFB")) {
      this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
      if (str.length() != 3) {
        int i = Integer.parseInt(str.substring(3));
        this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)new OFBBlockCipher(this.cipher.getUnderlyingCipher(), i));
      } else {
        this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)new OFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
      } 
    } else if (str.startsWith("CFB")) {
      this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
      if (str.length() != 3) {
        int i = Integer.parseInt(str.substring(3));
        this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)new CFBBlockCipher(this.cipher.getUnderlyingCipher(), i));
      } else {
        this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)new CFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
      } 
    } else {
      throw new IllegalArgumentException("can't support mode " + paramString);
    } 
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    String str = Strings.toUpperCase(paramString);
    if (str.equals("NOPADDING")) {
      this.cipher = new BufferedBlockCipher(this.cipher.getUnderlyingCipher());
    } else if (str.equals("PKCS5PADDING") || str.equals("PKCS7PADDING") || str.equals("ISO10126PADDING")) {
      this.cipher = (BufferedBlockCipher)new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
    } else if (str.equals("WITHCTS")) {
      this.cipher = (BufferedBlockCipher)new CTSBlockCipher(this.cipher.getUnderlyingCipher());
    } else {
      throw new NoSuchPaddingException("Padding " + paramString + " unknown.");
    } 
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    ParametersWithIV parametersWithIV;
    if (paramKey instanceof BCPBEKey) {
      CipherParameters cipherParameters = BrokenPBE.Util.makePBEParameters((BCPBEKey)paramKey, paramAlgorithmParameterSpec, this.pbeType, this.pbeHash, this.cipher.getUnderlyingCipher().getAlgorithmName(), this.pbeKeySize, this.pbeIvSize);
      if (this.pbeIvSize != 0)
        this.ivParam = (ParametersWithIV)cipherParameters; 
    } else if (paramAlgorithmParameterSpec == null) {
      KeyParameter keyParameter = new KeyParameter(paramKey.getEncoded());
    } else if (paramAlgorithmParameterSpec instanceof IvParameterSpec) {
      if (this.ivLength != 0) {
        parametersWithIV = new ParametersWithIV((CipherParameters)new KeyParameter(paramKey.getEncoded()), ((IvParameterSpec)paramAlgorithmParameterSpec).getIV());
        this.ivParam = parametersWithIV;
      } else {
        KeyParameter keyParameter = new KeyParameter(paramKey.getEncoded());
      } 
    } else if (paramAlgorithmParameterSpec instanceof RC2ParameterSpec) {
      RC2ParameterSpec rC2ParameterSpec = (RC2ParameterSpec)paramAlgorithmParameterSpec;
      RC2Parameters rC2Parameters = new RC2Parameters(paramKey.getEncoded(), ((RC2ParameterSpec)paramAlgorithmParameterSpec).getEffectiveKeyBits());
      if (rC2ParameterSpec.getIV() != null && this.ivLength != 0) {
        parametersWithIV = new ParametersWithIV((CipherParameters)rC2Parameters, rC2ParameterSpec.getIV());
        this.ivParam = parametersWithIV;
      } 
    } else if (paramAlgorithmParameterSpec instanceof RC5ParameterSpec) {
      RC5ParameterSpec rC5ParameterSpec = (RC5ParameterSpec)paramAlgorithmParameterSpec;
      RC5Parameters rC5Parameters = new RC5Parameters(paramKey.getEncoded(), ((RC5ParameterSpec)paramAlgorithmParameterSpec).getRounds());
      if (rC5ParameterSpec.getWordSize() != 32)
        throw new IllegalArgumentException("can only accept RC5 word size 32 (at the moment...)"); 
      if (rC5ParameterSpec.getIV() != null && this.ivLength != 0) {
        parametersWithIV = new ParametersWithIV((CipherParameters)rC5Parameters, rC5ParameterSpec.getIV());
        this.ivParam = parametersWithIV;
      } 
    } else {
      throw new InvalidAlgorithmParameterException("unknown parameter type.");
    } 
    if (this.ivLength != 0 && !(parametersWithIV instanceof ParametersWithIV)) {
      if (paramSecureRandom == null)
        paramSecureRandom = new SecureRandom(); 
      if (paramInt == 1 || paramInt == 3) {
        byte[] arrayOfByte = new byte[this.ivLength];
        paramSecureRandom.nextBytes(arrayOfByte);
        parametersWithIV = new ParametersWithIV((CipherParameters)parametersWithIV, arrayOfByte);
        this.ivParam = parametersWithIV;
      } else {
        throw new InvalidAlgorithmParameterException("no IV set when one expected");
      } 
    } 
    switch (paramInt) {
      case 1:
      case 3:
        this.cipher.init(true, (CipherParameters)parametersWithIV);
        return;
      case 2:
      case 4:
        this.cipher.init(false, (CipherParameters)parametersWithIV);
        return;
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
    int i = this.cipher.getUpdateOutputSize(paramInt2);
    if (i > 0) {
      byte[] arrayOfByte = new byte[i];
      this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
      return arrayOfByte;
    } 
    this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, null, 0);
    return null;
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    return this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    int i = 0;
    byte[] arrayOfByte1 = new byte[engineGetOutputSize(paramInt2)];
    if (paramInt2 != 0)
      i = this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte1, 0); 
    try {
      i += this.cipher.doFinal(arrayOfByte1, i);
    } catch (DataLengthException dataLengthException) {
      throw new IllegalBlockSizeException(dataLengthException.getMessage());
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new BadPaddingException(invalidCipherTextException.getMessage());
    } 
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, BadPaddingException {
    int i = 0;
    if (paramInt2 != 0)
      i = this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3); 
    try {
      return i + this.cipher.doFinal(paramArrayOfbyte2, paramInt3 + i);
    } catch (DataLengthException dataLengthException) {
      throw new IllegalBlockSizeException(dataLengthException.getMessage());
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new BadPaddingException(invalidCipherTextException.getMessage());
    } 
  }
  
  protected byte[] engineWrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    byte[] arrayOfByte = paramKey.getEncoded();
    if (arrayOfByte == null)
      throw new InvalidKeyException("Cannot wrap key, null encoding."); 
    try {
      return engineDoFinal(arrayOfByte, 0, arrayOfByte.length);
    } catch (BadPaddingException badPaddingException) {
      throw new IllegalBlockSizeException(badPaddingException.getMessage());
    } 
  }
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = engineDoFinal(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    } catch (BadPaddingException badPaddingException) {
      throw new InvalidKeyException(badPaddingException.getMessage());
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new InvalidKeyException(illegalBlockSizeException.getMessage());
    } 
    if (paramInt == 3)
      return new SecretKeySpec(arrayOfByte, paramString); 
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(paramString, "BC");
      if (paramInt == 1)
        return keyFactory.generatePublic(new X509EncodedKeySpec(arrayOfByte)); 
      if (paramInt == 2)
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(arrayOfByte)); 
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new InvalidKeyException("Unknown key type " + noSuchProviderException.getMessage());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InvalidKeyException("Unknown key type " + noSuchAlgorithmException.getMessage());
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("Unknown key type " + invalidKeySpecException.getMessage());
    } 
    throw new InvalidKeyException("Unknown key type " + paramInt);
  }
  
  public static class BrokePBEWithMD5AndDES extends BrokenJCEBlockCipher {
    public BrokePBEWithMD5AndDES() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 0, 0, 64, 64);
    }
  }
  
  public static class BrokePBEWithSHA1AndDES extends BrokenJCEBlockCipher {
    public BrokePBEWithSHA1AndDES() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 0, 1, 64, 64);
    }
  }
  
  public static class BrokePBEWithSHAAndDES2Key extends BrokenJCEBlockCipher {
    public BrokePBEWithSHAAndDES2Key() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 2, 1, 128, 64);
    }
  }
  
  public static class BrokePBEWithSHAAndDES3Key extends BrokenJCEBlockCipher {
    public BrokePBEWithSHAAndDES3Key() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 2, 1, 192, 64);
    }
  }
  
  public static class OldPBEWithSHAAndDES3Key extends BrokenJCEBlockCipher {
    public OldPBEWithSHAAndDES3Key() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 3, 1, 192, 64);
    }
  }
  
  public static class OldPBEWithSHAAndTwofish extends BrokenJCEBlockCipher {
    public OldPBEWithSHAAndTwofish() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new TwofishEngine()), 3, 1, 256, 128);
    }
  }
}
