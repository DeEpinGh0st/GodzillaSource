package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DESedeWrapEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DESede {
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
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 64);
    }
  }
  
  public static class CBCMAC extends BaseMac {
    public CBCMAC() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESedeEngine()));
    }
  }
  
  public static class CMAC extends BaseMac {
    public CMAC() {
      super((Mac)new CMac((BlockCipher)new DESedeEngine()));
    }
  }
  
  public static class DESede64 extends BaseMac {
    public DESede64() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESedeEngine(), 64));
    }
  }
  
  public static class DESede64with7816d4 extends BaseMac {
    public DESede64with7816d4() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new DESedeEngine(), 64, (BlockCipherPadding)new ISO7816d4Padding()));
    }
  }
  
  public static class DESedeCFB8 extends BaseMac {
    public DESedeCFB8() {
      super((Mac)new CFBBlockCipherMac((BlockCipher)new DESedeEngine()));
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new DESedeEngine());
    }
  }
  
  public static class KeyFactory extends BaseSecretKeyFactory {
    public KeyFactory() {
      super("DESede", null);
    }
    
    protected KeySpec engineGetKeySpec(SecretKey param1SecretKey, Class<?> param1Class) throws InvalidKeySpecException {
      if (param1Class == null)
        throw new InvalidKeySpecException("keySpec parameter is null"); 
      if (param1SecretKey == null)
        throw new InvalidKeySpecException("key parameter is null"); 
      if (SecretKeySpec.class.isAssignableFrom(param1Class))
        return new SecretKeySpec(param1SecretKey.getEncoded(), this.algName); 
      if (DESedeKeySpec.class.isAssignableFrom(param1Class)) {
        byte[] arrayOfByte = param1SecretKey.getEncoded();
        try {
          if (arrayOfByte.length == 16) {
            byte[] arrayOfByte1 = new byte[24];
            System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, 16);
            System.arraycopy(arrayOfByte, 0, arrayOfByte1, 16, 8);
            return new DESedeKeySpec(arrayOfByte1);
          } 
          return new DESedeKeySpec(arrayOfByte);
        } catch (Exception exception) {
          throw new InvalidKeySpecException(exception.toString());
        } 
      } 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof DESedeKeySpec) {
        DESedeKeySpec dESedeKeySpec = (DESedeKeySpec)param1KeySpec;
        return new SecretKeySpec(dESedeKeySpec.getKey(), "DESede");
      } 
      return super.engineGenerateSecret(param1KeySpec);
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    private boolean keySizeSet = false;
    
    public KeyGenerator() {
      super("DESede", 192, (CipherKeyGenerator)new DESedeKeyGenerator());
    }
    
    protected void engineInit(int param1Int, SecureRandom param1SecureRandom) {
      super.engineInit(param1Int, param1SecureRandom);
      this.keySizeSet = true;
    }
    
    protected SecretKey engineGenerateKey() {
      if (this.uninitialised) {
        this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
        this.uninitialised = false;
      } 
      if (!this.keySizeSet) {
        byte[] arrayOfByte = this.engine.generateKey();
        System.arraycopy(arrayOfByte, 0, arrayOfByte, 16, 8);
        return new SecretKeySpec(arrayOfByte, this.algName);
      } 
      return new SecretKeySpec(this.engine.generateKey(), this.algName);
    }
  }
  
  public static class KeyGenerator3 extends BaseKeyGenerator {
    public KeyGenerator3() {
      super("DESede3", 192, (CipherKeyGenerator)new DESedeKeyGenerator());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = DESede.class.getName();
    
    private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.DESEDE", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.des_EDE3_CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher.DESEDEWRAP", PREFIX + "$Wrap");
      param1ConfigurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.id_alg_CMS3DESwrap, PREFIX + "$Wrap");
      param1ConfigurableProvider.addAlgorithm("Cipher.DESEDERFC3211WRAP", PREFIX + "$RFC3211");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.DESEDERFC3217WRAP", "DESEDEWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEA", "DESEDE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEAWRAP", "DESEDEWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.TDEA", "DESEDE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.TDEA", "DESEDE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.TDEA", "DESEDE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.TDEA", "DESEDE");
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "SHA-1")) {
        param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES3Key");
        param1ConfigurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$BrokePBEWithSHAAndDES3Key");
        param1ConfigurableProvider.addAlgorithm("Cipher.OLDPBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$OldPBEWithSHAAndDES3Key");
        param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES2Key");
        param1ConfigurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND2-KEYTRIPLEDES-CBC", PREFIX + "$BrokePBEWithSHAAndDES2Key");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYTRIPLEDES-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYTRIPLEDES-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
      } 
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.DESEDE", PREFIX + "$KeyGenerator");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, PREFIX + "$KeyGenerator3");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.DESEDEWRAP", PREFIX + "$KeyGenerator");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.DESEDE", PREFIX + "$KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory", OIWObjectIdentifiers.desEDE, PREFIX + "$KeyFactory");
      param1ConfigurableProvider.addAlgorithm("Mac.DESEDECMAC", PREFIX + "$CMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DESEDEMAC", PREFIX + "$CBCMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE", "DESEDEMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DESEDEMAC/CFB8", PREFIX + "$DESedeCFB8");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE/CFB8", "DESEDEMAC/CFB8");
      param1ConfigurableProvider.addAlgorithm("Mac.DESEDEMAC64", PREFIX + "$DESede64");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64", "DESEDEMAC64");
      param1ConfigurableProvider.addAlgorithm("Mac.DESEDEMAC64WITHISO7816-4PADDING", PREFIX + "$DESede64with7816d4");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1MACWITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.DESEDE", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.DESEDE", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES3KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES2KeyFactory");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES3KEY-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES2KEY-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.3", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.4", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.3", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.4", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
    }
  }
  
  public static class PBEWithSHAAndDES2Key extends BaseBlockCipher {
    public PBEWithSHAAndDES2Key() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 2, 1, 128, 8);
    }
  }
  
  public static class PBEWithSHAAndDES2KeyFactory extends DES.DESPBEKeyFactory {
    public PBEWithSHAAndDES2KeyFactory() {
      super("PBEwithSHAandDES2Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, true, 2, 1, 128, 64);
    }
  }
  
  public static class PBEWithSHAAndDES3Key extends BaseBlockCipher {
    public PBEWithSHAAndDES3Key() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()), 2, 1, 192, 8);
    }
  }
  
  public static class PBEWithSHAAndDES3KeyFactory extends DES.DESPBEKeyFactory {
    public PBEWithSHAAndDES3KeyFactory() {
      super("PBEwithSHAandDES3Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, true, 2, 1, 192, 64);
    }
  }
  
  public static class RFC3211 extends BaseWrapCipher {
    public RFC3211() {
      super((Wrapper)new RFC3211WrapEngine((BlockCipher)new DESedeEngine()), 8);
    }
  }
  
  public static class Wrap extends BaseWrapCipher {
    public Wrap() {
      super((Wrapper)new DESedeWrapEngine());
    }
  }
}
