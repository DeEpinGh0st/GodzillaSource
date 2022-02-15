package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;

public class BaseStreamCipher extends BaseWrapCipher implements PBE {
  private Class[] availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
  
  private StreamCipher cipher;
  
  private int keySizeInBits;
  
  private int digest;
  
  private ParametersWithIV ivParam;
  
  private int ivLength = 0;
  
  private PBEParameterSpec pbeSpec = null;
  
  private String pbeAlgorithm = null;
  
  protected BaseStreamCipher(StreamCipher paramStreamCipher, int paramInt) {
    this(paramStreamCipher, paramInt, -1, -1);
  }
  
  protected BaseStreamCipher(StreamCipher paramStreamCipher, int paramInt1, int paramInt2, int paramInt3) {
    this.cipher = paramStreamCipher;
    this.ivLength = paramInt1;
    this.keySizeInBits = paramInt2;
    this.digest = paramInt3;
  }
  
  protected int engineGetBlockSize() {
    return 0;
  }
  
  protected byte[] engineGetIV() {
    return (this.ivParam != null) ? this.ivParam.getIV() : null;
  }
  
  protected int engineGetKeySize(Key paramKey) {
    return (paramKey.getEncoded()).length * 8;
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return paramInt;
  }
  
  protected AlgorithmParameters engineGetParameters() {
    if (this.engineParams == null && this.pbeSpec != null)
      try {
        AlgorithmParameters algorithmParameters = createParametersInstance(this.pbeAlgorithm);
        algorithmParameters.init(this.pbeSpec);
        return algorithmParameters;
      } catch (Exception exception) {
        return null;
      }  
    return this.engineParams;
  }
  
  protected void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    if (!paramString.equalsIgnoreCase("ECB"))
      throw new NoSuchAlgorithmException("can't support mode " + paramString); 
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    if (!paramString.equalsIgnoreCase("NoPadding"))
      throw new NoSuchPaddingException("Padding " + paramString + " unknown."); 
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    ParametersWithIV parametersWithIV;
    this.pbeSpec = null;
    this.pbeAlgorithm = null;
    this.engineParams = null;
    if (!(paramKey instanceof javax.crypto.SecretKey))
      throw new InvalidKeyException("Key for algorithm " + paramKey.getAlgorithm() + " not suitable for symmetric enryption."); 
    if (paramKey instanceof PKCS12Key) {
      PKCS12Key pKCS12Key = (PKCS12Key)paramKey;
      this.pbeSpec = (PBEParameterSpec)paramAlgorithmParameterSpec;
      if (pKCS12Key instanceof PKCS12KeyWithParameters && this.pbeSpec == null)
        this.pbeSpec = new PBEParameterSpec(((PKCS12KeyWithParameters)pKCS12Key).getSalt(), ((PKCS12KeyWithParameters)pKCS12Key).getIterationCount()); 
      CipherParameters cipherParameters = PBE.Util.makePBEParameters(pKCS12Key.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
    } else if (paramKey instanceof BCPBEKey) {
      CipherParameters cipherParameters;
      BCPBEKey bCPBEKey = (BCPBEKey)paramKey;
      if (bCPBEKey.getOID() != null) {
        this.pbeAlgorithm = bCPBEKey.getOID().getId();
      } else {
        this.pbeAlgorithm = bCPBEKey.getAlgorithm();
      } 
      if (bCPBEKey.getParam() != null) {
        cipherParameters = bCPBEKey.getParam();
        this.pbeSpec = new PBEParameterSpec(bCPBEKey.getSalt(), bCPBEKey.getIterationCount());
      } else if (paramAlgorithmParameterSpec instanceof PBEParameterSpec) {
        cipherParameters = PBE.Util.makePBEParameters(bCPBEKey, paramAlgorithmParameterSpec, this.cipher.getAlgorithmName());
        this.pbeSpec = (PBEParameterSpec)paramAlgorithmParameterSpec;
      } else {
        throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
      } 
      if (bCPBEKey.getIvSize() != 0)
        this.ivParam = (ParametersWithIV)cipherParameters; 
    } else if (paramAlgorithmParameterSpec == null) {
      if (this.digest > 0)
        throw new InvalidKeyException("Algorithm requires a PBE key"); 
      KeyParameter keyParameter = new KeyParameter(paramKey.getEncoded());
    } else if (paramAlgorithmParameterSpec instanceof IvParameterSpec) {
      parametersWithIV = new ParametersWithIV((CipherParameters)new KeyParameter(paramKey.getEncoded()), ((IvParameterSpec)paramAlgorithmParameterSpec).getIV());
      this.ivParam = parametersWithIV;
    } else {
      throw new InvalidAlgorithmParameterException("unknown parameter type.");
    } 
    if (this.ivLength != 0 && !(parametersWithIV instanceof ParametersWithIV)) {
      SecureRandom secureRandom = paramSecureRandom;
      if (secureRandom == null)
        secureRandom = new SecureRandom(); 
      if (paramInt == 1 || paramInt == 3) {
        byte[] arrayOfByte = new byte[this.ivLength];
        secureRandom.nextBytes(arrayOfByte);
        parametersWithIV = new ParametersWithIV((CipherParameters)parametersWithIV, arrayOfByte);
        this.ivParam = parametersWithIV;
      } else {
        throw new InvalidAlgorithmParameterException("no IV set when one expected");
      } 
    } 
    try {
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
      throw new InvalidParameterException("unknown opmode " + paramInt + " passed");
    } catch (Exception exception) {
      throw new InvalidKeyException(exception.getMessage());
    } 
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
    engineInit(paramInt, paramKey, algorithmParameterSpec, paramSecureRandom);
    this.engineParams = paramAlgorithmParameters;
  }
  
  protected void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      engineInit(paramInt, paramKey, (AlgorithmParameterSpec)null, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidKeyException(invalidAlgorithmParameterException.getMessage());
    } 
  }
  
  protected byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt2];
    this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new ShortBufferException("output buffer too short for input."); 
    try {
      this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
      return paramInt2;
    } catch (DataLengthException dataLengthException) {
      throw new IllegalStateException(dataLengthException.getMessage());
    } 
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 != 0) {
      byte[] arrayOfByte = engineUpdate(paramArrayOfbyte, paramInt1, paramInt2);
      this.cipher.reset();
      return arrayOfByte;
    } 
    this.cipher.reset();
    return new byte[0];
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new ShortBufferException("output buffer too short for input."); 
    if (paramInt2 != 0)
      this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3); 
    this.cipher.reset();
    return paramInt2;
  }
}
