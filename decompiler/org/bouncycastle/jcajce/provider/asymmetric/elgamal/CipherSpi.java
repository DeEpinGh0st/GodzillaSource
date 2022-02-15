package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.interfaces.DHKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedAsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jce.interfaces.ElGamalKey;
import org.bouncycastle.util.Strings;

public class CipherSpi extends BaseCipherSpi {
  private BufferedAsymmetricBlockCipher cipher;
  
  private AlgorithmParameterSpec paramSpec;
  
  private AlgorithmParameters engineParams;
  
  public CipherSpi(AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.cipher = new BufferedAsymmetricBlockCipher(paramAsymmetricBlockCipher);
  }
  
  private void initFromSpec(OAEPParameterSpec paramOAEPParameterSpec) throws NoSuchPaddingException {
    MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec)paramOAEPParameterSpec.getMGFParameters();
    Digest digest = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
    if (digest == null)
      throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mGF1ParameterSpec.getDigestAlgorithm()); 
    this.cipher = new BufferedAsymmetricBlockCipher((AsymmetricBlockCipher)new OAEPEncoding((AsymmetricBlockCipher)new ElGamalEngine(), digest, ((PSource.PSpecified)paramOAEPParameterSpec.getPSource()).getValue()));
    this.paramSpec = paramOAEPParameterSpec;
  }
  
  protected int engineGetBlockSize() {
    return this.cipher.getInputBlockSize();
  }
  
  protected int engineGetKeySize(Key paramKey) {
    if (paramKey instanceof ElGamalKey) {
      ElGamalKey elGamalKey = (ElGamalKey)paramKey;
      return elGamalKey.getParameters().getP().bitLength();
    } 
    if (paramKey instanceof DHKey) {
      DHKey dHKey = (DHKey)paramKey;
      return dHKey.getParams().getP().bitLength();
    } 
    throw new IllegalArgumentException("not an ElGamal key!");
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return this.cipher.getOutputBlockSize();
  }
  
  protected AlgorithmParameters engineGetParameters() {
    if (this.engineParams == null && this.paramSpec != null)
      try {
        this.engineParams = createParametersInstance("OAEP");
        this.engineParams.init(this.paramSpec);
      } catch (Exception exception) {
        throw new RuntimeException(exception.toString());
      }  
    return this.engineParams;
  }
  
  protected void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    String str = Strings.toUpperCase(paramString);
    if (str.equals("NONE") || str.equals("ECB"))
      return; 
    throw new NoSuchAlgorithmException("can't support mode " + paramString);
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    String str = Strings.toUpperCase(paramString);
    if (str.equals("NOPADDING")) {
      this.cipher = new BufferedAsymmetricBlockCipher((AsymmetricBlockCipher)new ElGamalEngine());
    } else if (str.equals("PKCS1PADDING")) {
      this.cipher = new BufferedAsymmetricBlockCipher((AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new ElGamalEngine()));
    } else if (str.equals("ISO9796-1PADDING")) {
      this.cipher = new BufferedAsymmetricBlockCipher((AsymmetricBlockCipher)new ISO9796d1Encoding((AsymmetricBlockCipher)new ElGamalEngine()));
    } else if (str.equals("OAEPPADDING")) {
      initFromSpec(OAEPParameterSpec.DEFAULT);
    } else if (str.equals("OAEPWITHMD5ANDMGF1PADDING")) {
      initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSource.PSpecified.DEFAULT));
    } else if (str.equals("OAEPWITHSHA1ANDMGF1PADDING")) {
      initFromSpec(OAEPParameterSpec.DEFAULT);
    } else if (str.equals("OAEPWITHSHA224ANDMGF1PADDING")) {
      initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
    } else if (str.equals("OAEPWITHSHA256ANDMGF1PADDING")) {
      initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
    } else if (str.equals("OAEPWITHSHA384ANDMGF1PADDING")) {
      initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
    } else if (str.equals("OAEPWITHSHA512ANDMGF1PADDING")) {
      initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
    } else {
      throw new NoSuchPaddingException(paramString + " unavailable with ElGamal.");
    } 
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter;
    ParametersWithRandom parametersWithRandom;
    if (paramAlgorithmParameterSpec == null) {
      if (paramKey instanceof org.bouncycastle.jce.interfaces.ElGamalPublicKey) {
        asymmetricKeyParameter = ElGamalUtil.generatePublicKeyParameter((PublicKey)paramKey);
      } else if (paramKey instanceof org.bouncycastle.jce.interfaces.ElGamalPrivateKey) {
        asymmetricKeyParameter = ElGamalUtil.generatePrivateKeyParameter((PrivateKey)paramKey);
      } else {
        throw new InvalidKeyException("unknown key type passed to ElGamal");
      } 
    } else {
      throw new IllegalArgumentException("unknown parameter type.");
    } 
    if (paramSecureRandom != null)
      parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, paramSecureRandom); 
    switch (paramInt) {
      case 1:
      case 3:
        this.cipher.init(true, (CipherParameters)parametersWithRandom);
        return;
      case 2:
      case 4:
        this.cipher.init(false, (CipherParameters)parametersWithRandom);
        return;
    } 
    throw new InvalidParameterException("unknown opmode " + paramInt + " passed to ElGamal");
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("can't handle parameters in ElGamal");
  }
  
  protected void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    engineInit(paramInt, paramKey, (AlgorithmParameterSpec)null, paramSecureRandom);
  }
  
  protected byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2);
    return null;
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2);
    return 0;
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    this.cipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2);
    return getOutput();
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, BadPaddingException {
    this.cipher.processBytes(paramArrayOfbyte1, paramInt1, paramInt2);
    byte[] arrayOfByte = getOutput();
    for (byte b = 0; b != arrayOfByte.length; b++)
      paramArrayOfbyte2[paramInt3 + b] = arrayOfByte[b]; 
    return arrayOfByte.length;
  }
  
  private byte[] getOutput() throws BadPaddingException {
    try {
      return this.cipher.doFinal();
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new BadPaddingException("unable to decrypt block") {
          public synchronized Throwable getCause() {
            return (Throwable)e;
          }
        };
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new BadBlockException("unable to decrypt block", arrayIndexOutOfBoundsException);
    } 
  }
  
  public static class NoPadding extends CipherSpi {
    public NoPadding() {
      super((AsymmetricBlockCipher)new ElGamalEngine());
    }
  }
  
  public static class PKCS1v1_5Padding extends CipherSpi {
    public PKCS1v1_5Padding() {
      super((AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new ElGamalEngine()));
    }
  }
}
