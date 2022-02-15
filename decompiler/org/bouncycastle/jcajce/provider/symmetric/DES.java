package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.ISO9797Alg3Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DES {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DES parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[8];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("DES");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 64);
    }
  }
  
  public static class CBCMAC extends BaseMac {
    public CBCMAC() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESEngine()));
    }
  }
  
  public static class CMAC extends BaseMac {
    public CMAC() {
      super((Mac)new CMac((BlockCipher)new DESEngine()));
    }
  }
  
  public static class DES64 extends BaseMac {
    public DES64() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESEngine(), 64));
    }
  }
  
  public static class DES64with7816d4 extends BaseMac {
    public DES64with7816d4() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESEngine(), 64, (BlockCipherPadding)new ISO7816d4Padding()));
    }
  }
  
  public static class DES9797Alg3 extends BaseMac {
    public DES9797Alg3() {
      super((Mac)new ISO9797Alg3Mac((BlockCipher)new DESEngine()));
    }
  }
  
  public static class DES9797Alg3with7816d4 extends BaseMac {
    public DES9797Alg3with7816d4() {
      super((Mac)new ISO9797Alg3Mac((BlockCipher)new DESEngine(), (BlockCipherPadding)new ISO7816d4Padding()));
    }
  }
  
  public static class DESCFB8 extends BaseMac {
    public DESCFB8() {
      super((Mac)new CFBBlockCipherMac((BlockCipher)new DESEngine()));
    }
  }
  
  public static class DESPBEKeyFactory extends BaseSecretKeyFactory {
    private boolean forCipher;
    
    private int scheme;
    
    private int digest;
    
    private int keySize;
    
    private int ivSize;
    
    public DESPBEKeyFactory(String param1String, ASN1ObjectIdentifier param1ASN1ObjectIdentifier, boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      super(param1String, param1ASN1ObjectIdentifier);
      this.forCipher = param1Boolean;
      this.scheme = param1Int1;
      this.digest = param1Int2;
      this.keySize = param1Int3;
      this.ivSize = param1Int4;
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof PBEKeySpec) {
        CipherParameters cipherParameters;
        KeyParameter keyParameter;
        PBEKeySpec pBEKeySpec = (PBEKeySpec)param1KeySpec;
        if (pBEKeySpec.getSalt() == null)
          return (SecretKey)((this.scheme == 0 || this.scheme == 4) ? new PBKDF1Key(pBEKeySpec.getPassword(), (this.scheme == 0) ? (CharToByteConverter)PasswordConverter.ASCII : (CharToByteConverter)PasswordConverter.UTF8) : new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, null)); 
        if (this.forCipher) {
          cipherParameters = PBE.Util.makePBEParameters(pBEKeySpec, this.scheme, this.digest, this.keySize, this.ivSize);
        } else {
          cipherParameters = PBE.Util.makePBEMacParameters(pBEKeySpec, this.scheme, this.digest, this.keySize);
        } 
        if (cipherParameters instanceof ParametersWithIV) {
          keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
        } else {
          keyParameter = (KeyParameter)cipherParameters;
        } 
        DESParameters.setOddParity(keyParameter.getKey());
        return (SecretKey)new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, cipherParameters);
      } 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new DESEngine());
    }
  }
  
  public static class KeyFactory extends BaseSecretKeyFactory {
    public KeyFactory() {
      super("DES", null);
    }
    
    protected KeySpec engineGetKeySpec(SecretKey param1SecretKey, Class<?> param1Class) throws InvalidKeySpecException {
      if (param1Class == null)
        throw new InvalidKeySpecException("keySpec parameter is null"); 
      if (param1SecretKey == null)
        throw new InvalidKeySpecException("key parameter is null"); 
      if (SecretKeySpec.class.isAssignableFrom(param1Class))
        return new SecretKeySpec(param1SecretKey.getEncoded(), this.algName); 
      if (DESKeySpec.class.isAssignableFrom(param1Class)) {
        byte[] arrayOfByte = param1SecretKey.getEncoded();
        try {
          return new DESKeySpec(arrayOfByte);
        } catch (Exception exception) {
          throw new InvalidKeySpecException(exception.toString());
        } 
      } 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof DESKeySpec) {
        DESKeySpec dESKeySpec = (DESKeySpec)param1KeySpec;
        return new SecretKeySpec(dESKeySpec.getKey(), "DES");
      } 
      return super.engineGenerateSecret(param1KeySpec);
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("DES", 64, (CipherKeyGenerator)new DESKeyGenerator());
    }
    
    protected void engineInit(int param1Int, SecureRandom param1SecureRandom) {
      super.engineInit(param1Int, param1SecureRandom);
    }
    
    protected SecretKey engineGenerateKey() {
      if (this.uninitialised) {
        this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
        this.uninitialised = false;
      } 
      return new SecretKeySpec(this.engine.generateKey(), this.algName);
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = DES.class.getName();
    
    private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.DES", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", OIWObjectIdentifiers.desCBC, PREFIX + "$CBC");
      addAlias(param1ConfigurableProvider, OIWObjectIdentifiers.desCBC, "DES");
      param1ConfigurableProvider.addAlgorithm("Cipher.DESRFC3211WRAP", PREFIX + "$RFC3211");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.DES", PREFIX + "$KeyGenerator");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.DES", PREFIX + "$KeyFactory");
      param1ConfigurableProvider.addAlgorithm("Mac.DESCMAC", PREFIX + "$CMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DESMAC", PREFIX + "$CBCMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DES", "DESMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DESMAC/CFB8", PREFIX + "$DESCFB8");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DES/CFB8", "DESMAC/CFB8");
      param1ConfigurableProvider.addAlgorithm("Mac.DESMAC64", PREFIX + "$DES64");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DES64", "DESMAC64");
      param1ConfigurableProvider.addAlgorithm("Mac.DESMAC64WITHISO7816-4PADDING", PREFIX + "$DES64with7816d4");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DES64WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1MACWITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("Mac.DESWITHISO9797", PREFIX + "$DES9797Alg3");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797MAC", "DESWITHISO9797");
      param1ConfigurableProvider.addAlgorithm("Mac.ISO9797ALG3MAC", PREFIX + "$DES9797Alg3");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3", "ISO9797ALG3MAC");
      param1ConfigurableProvider.addAlgorithm("Mac.ISO9797ALG3WITHISO7816-4PADDING", PREFIX + "$DES9797Alg3with7816d4");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3MACWITHISO7816-4PADDING", "ISO9797ALG3WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.DES", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", OIWObjectIdentifiers.desCBC, "DES");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.DES", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + OIWObjectIdentifiers.desCBC, "DES");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHMD2ANDDES", PREFIX + "$PBEWithMD2");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHMD5ANDDES", PREFIX + "$PBEWithMD5");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHA1ANDDES", PREFIX + "$PBEWithSHA1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD2ANDDES", PREFIX + "$PBEWithMD2KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5ANDDES", PREFIX + "$PBEWithMD5KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA1ANDDES", PREFIX + "$PBEWithSHA1KeyFactory");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
    }
    
    private void addAlias(ConfigurableProvider param1ConfigurableProvider, ASN1ObjectIdentifier param1ASN1ObjectIdentifier, String param1String) {
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + param1ASN1ObjectIdentifier.getId(), param1String);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory." + param1ASN1ObjectIdentifier.getId(), param1String);
    }
  }
  
  public static class PBEWithMD2 extends BaseBlockCipher {
    public PBEWithMD2() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 0, 5, 64, 8);
    }
  }
  
  public static class PBEWithMD2KeyFactory extends DESPBEKeyFactory {
    public PBEWithMD2KeyFactory() {
      super("PBEwithMD2andDES", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, true, 0, 5, 64, 64);
    }
  }
  
  public static class PBEWithMD5 extends BaseBlockCipher {
    public PBEWithMD5() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 0, 0, 64, 8);
    }
  }
  
  public static class PBEWithMD5KeyFactory extends DESPBEKeyFactory {
    public PBEWithMD5KeyFactory() {
      super("PBEwithMD5andDES", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, true, 0, 0, 64, 64);
    }
  }
  
  public static class PBEWithSHA1 extends BaseBlockCipher {
    public PBEWithSHA1() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESEngine()), 0, 1, 64, 8);
    }
  }
  
  public static class PBEWithSHA1KeyFactory extends DESPBEKeyFactory {
    public PBEWithSHA1KeyFactory() {
      super("PBEwithSHA1andDES", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, true, 0, 1, 64, 64);
    }
  }
  
  public static class RFC3211 extends BaseWrapCipher {
    public RFC3211() {
      super((Wrapper)new RFC3211WrapEngine((BlockCipher)new DESEngine()), 8);
    }
  }
}
