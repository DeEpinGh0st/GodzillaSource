package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.engines.AESWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class AES {
  private static final Map<String, String> generalAesAttributes = new HashMap<String, String>();
  
  static {
    generalAesAttributes.put("SupportedKeyClasses", "javax.crypto.SecretKey");
    generalAesAttributes.put("SupportedKeyFormats", "RAW");
  }
  
  public static class AESCCMMAC extends BaseMac {
    public AESCCMMAC() {
      super(new CCMMac(null));
    }
    
    private static class CCMMac implements Mac {
      private final CCMBlockCipher ccm = new CCMBlockCipher((BlockCipher)new AESEngine());
      
      private int macLength = 8;
      
      private CCMMac() {}
      
      public void init(CipherParameters param2CipherParameters) throws IllegalArgumentException {
        this.ccm.init(true, param2CipherParameters);
        this.macLength = (this.ccm.getMac()).length;
      }
      
      public String getAlgorithmName() {
        return this.ccm.getAlgorithmName() + "Mac";
      }
      
      public int getMacSize() {
        return this.macLength;
      }
      
      public void update(byte param2Byte) throws IllegalStateException {
        this.ccm.processAADByte(param2Byte);
      }
      
      public void update(byte[] param2ArrayOfbyte, int param2Int1, int param2Int2) throws DataLengthException, IllegalStateException {
        this.ccm.processAADBytes(param2ArrayOfbyte, param2Int1, param2Int2);
      }
      
      public int doFinal(byte[] param2ArrayOfbyte, int param2Int) throws DataLengthException, IllegalStateException {
        try {
          return this.ccm.doFinal(param2ArrayOfbyte, 0);
        } catch (InvalidCipherTextException invalidCipherTextException) {
          throw new IllegalStateException("exception on doFinal(): " + invalidCipherTextException.toString());
        } 
      }
      
      public void reset() {
        this.ccm.reset();
      }
    }
  }
  
  public static class AESCMAC extends BaseMac {
    public AESCMAC() {
      super((Mac)new CMac((BlockCipher)new AESEngine()));
    }
  }
  
  public static class AESGMAC extends BaseMac {
    public AESGMAC() {
      super((Mac)new GMac(new GCMBlockCipher((BlockCipher)new AESEngine())));
    }
  }
  
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[16];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("AES");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParamGenCCM extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[12];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("CCM");
        algorithmParameters.init((new CCMParameters(arrayOfByte, 12)).getEncoded());
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParamGenGCM extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[12];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("GCM");
        algorithmParameters.init((new GCMParameters(arrayOfByte, 16)).getEncoded());
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "AES IV";
    }
  }
  
  public static class AlgParamsCCM extends BaseAlgorithmParameters {
    private CCMParameters ccmParams;
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (GcmSpecUtil.isGcmSpec(param1AlgorithmParameterSpec)) {
        this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(param1AlgorithmParameterSpec));
      } else if (param1AlgorithmParameterSpec instanceof AEADParameterSpec) {
        this.ccmParams = new CCMParameters(((AEADParameterSpec)param1AlgorithmParameterSpec).getNonce(), ((AEADParameterSpec)param1AlgorithmParameterSpec).getMacSizeInBits() / 8);
      } else {
        throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + param1AlgorithmParameterSpec.getClass().getName());
      } 
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.ccmParams = CCMParameters.getInstance(param1ArrayOfbyte);
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (!isASN1FormatString(param1String))
        throw new IOException("unknown format specified"); 
      this.ccmParams = CCMParameters.getInstance(param1ArrayOfbyte);
    }
    
    protected byte[] engineGetEncoded() throws IOException {
      return this.ccmParams.getEncoded();
    }
    
    protected byte[] engineGetEncoded(String param1String) throws IOException {
      if (!isASN1FormatString(param1String))
        throw new IOException("unknown format specified"); 
      return this.ccmParams.getEncoded();
    }
    
    protected String engineToString() {
      return "CCM";
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<AlgorithmParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(param1Class))
        return (AlgorithmParameterSpec)(GcmSpecUtil.gcmSpecExists() ? GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive()) : new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8)); 
      if (param1Class == AEADParameterSpec.class)
        return (AlgorithmParameterSpec)new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8); 
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.ccmParams.getNonce()); 
      throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + param1Class.getName());
    }
  }
  
  public static class AlgParamsGCM extends BaseAlgorithmParameters {
    private GCMParameters gcmParams;
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (GcmSpecUtil.isGcmSpec(param1AlgorithmParameterSpec)) {
        this.gcmParams = GcmSpecUtil.extractGcmParameters(param1AlgorithmParameterSpec);
      } else if (param1AlgorithmParameterSpec instanceof AEADParameterSpec) {
        this.gcmParams = new GCMParameters(((AEADParameterSpec)param1AlgorithmParameterSpec).getNonce(), ((AEADParameterSpec)param1AlgorithmParameterSpec).getMacSizeInBits() / 8);
      } else {
        throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + param1AlgorithmParameterSpec.getClass().getName());
      } 
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.gcmParams = GCMParameters.getInstance(param1ArrayOfbyte);
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (!isASN1FormatString(param1String))
        throw new IOException("unknown format specified"); 
      this.gcmParams = GCMParameters.getInstance(param1ArrayOfbyte);
    }
    
    protected byte[] engineGetEncoded() throws IOException {
      return this.gcmParams.getEncoded();
    }
    
    protected byte[] engineGetEncoded(String param1String) throws IOException {
      if (!isASN1FormatString(param1String))
        throw new IOException("unknown format specified"); 
      return this.gcmParams.getEncoded();
    }
    
    protected String engineToString() {
      return "GCM";
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<AlgorithmParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(param1Class))
        return (AlgorithmParameterSpec)(GcmSpecUtil.gcmSpecExists() ? GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive()) : new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8)); 
      if (param1Class == AEADParameterSpec.class)
        return (AlgorithmParameterSpec)new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8); 
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.gcmParams.getNonce()); 
      throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + param1Class.getName());
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 128);
    }
  }
  
  public static class CCM extends BaseBlockCipher {
    public CCM() {
      super((AEADBlockCipher)new CCMBlockCipher((BlockCipher)new AESEngine()), false, 16);
    }
  }
  
  public static class CFB extends BaseBlockCipher {
    public CFB() {
      super(new BufferedBlockCipher((BlockCipher)new CFBBlockCipher((BlockCipher)new AESEngine(), 128)), 128);
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new AESEngine();
            }
          });
    }
  }
  
  public static class GCM extends BaseBlockCipher {
    public GCM() {
      super((AEADBlockCipher)new GCMBlockCipher((BlockCipher)new AESEngine()));
    }
  }
  
  public static class KeyFactory extends BaseSecretKeyFactory {
    public KeyFactory() {
      super("AES", null);
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      this(192);
    }
    
    public KeyGen(int param1Int) {
      super("AES", param1Int, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGen128 extends KeyGen {
    public KeyGen128() {
      super(128);
    }
  }
  
  public static class KeyGen192 extends KeyGen {
    public KeyGen192() {
      super(192);
    }
  }
  
  public static class KeyGen256 extends KeyGen {
    public KeyGen256() {
      super(256);
    }
  }
  
  public static class Mappings extends SymmetricAlgorithmProvider {
    private static final String PREFIX = AES.class.getName();
    
    private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
    
    private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
    
    private static final String wrongAES256 = "2.16.840.1.101.3.4.42";
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.AES", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.GCM", PREFIX + "$AlgParamsGCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.CCM", PREFIX + "$AlgParamsCCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.AES", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
      param1ConfigurableProvider.addAttributes("Cipher.AES", AES.generalAesAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.AES", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$CFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$CFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$CFB");
      param1ConfigurableProvider.addAttributes("Cipher.AESWRAP", AES.generalAesAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.AESWRAP", PREFIX + "$Wrap");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap, "AESWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap, "AESWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap, "AESWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");
      param1ConfigurableProvider.addAttributes("Cipher.AESWRAPPAD", AES.generalAesAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.AESWRAPPAD", PREFIX + "$WrapPad");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap_pad, "AESWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap_pad, "AESWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap_pad, "AESWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKWP", "AESWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Cipher.AESRFC3211WRAP", PREFIX + "$RFC3211Wrap");
      param1ConfigurableProvider.addAlgorithm("Cipher.AESRFC5649WRAP", PREFIX + "$RFC5649Wrap");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.CCM", PREFIX + "$AlgParamGenCCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
      param1ConfigurableProvider.addAttributes("Cipher.CCM", AES.generalAesAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.CCM", PREFIX + "$CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_CCM, "CCM");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.GCM", PREFIX + "$AlgParamGenGCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
      param1ConfigurableProvider.addAttributes("Cipher.GCM", AES.generalAesAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.GCM", PREFIX + "$GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_GCM, "GCM");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.AES", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.2", PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.22", PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.42", PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.AESWRAP", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.AESWRAPPAD", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap_pad, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap_pad, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap_pad, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("Mac.AESCMAC", PREFIX + "$AESCMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.AESCCMMAC", PREFIX + "$AESCCMMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes128_CCM.getId(), "AESCCMMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes192_CCM.getId(), "AESCCMMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes256_CCM.getId(), "AESCCMMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC128");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC192");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC256");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC128");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC192");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.AES", PREFIX + "$KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.aes, PREFIX + "$KeyFactory");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And128BitAESCBCOpenSSL");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And192BitAESCBCOpenSSL");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And256BitAESCBCOpenSSL");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd128BitAESBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd192BitAESBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd256BitAESBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And128BitAESBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And192BitAESBC");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And256BitAESBC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
      addGMacAlgorithm(param1ConfigurableProvider, "AES", PREFIX + "$AESGMAC", PREFIX + "$KeyGen128");
      addPoly1305Algorithm(param1ConfigurableProvider, "AES", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
    }
  }
  
  public static class OFB extends BaseBlockCipher {
    public OFB() {
      super(new BufferedBlockCipher((BlockCipher)new OFBBlockCipher((BlockCipher)new AESEngine(), 128)), 128);
    }
  }
  
  public static class PBEWithAESCBC extends BaseBlockCipher {
    public PBEWithAESCBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()));
    }
  }
  
  public static class PBEWithMD5And128BitAESCBCOpenSSL extends PBESecretKeyFactory {
    public PBEWithMD5And128BitAESCBCOpenSSL() {
      super("PBEWithMD5And128BitAES-CBC-OpenSSL", null, true, 3, 0, 128, 128);
    }
  }
  
  public static class PBEWithMD5And192BitAESCBCOpenSSL extends PBESecretKeyFactory {
    public PBEWithMD5And192BitAESCBCOpenSSL() {
      super("PBEWithMD5And192BitAES-CBC-OpenSSL", null, true, 3, 0, 192, 128);
    }
  }
  
  public static class PBEWithMD5And256BitAESCBCOpenSSL extends PBESecretKeyFactory {
    public PBEWithMD5And256BitAESCBCOpenSSL() {
      super("PBEWithMD5And256BitAES-CBC-OpenSSL", null, true, 3, 0, 256, 128);
    }
  }
  
  public static class PBEWithSHA1AESCBC128 extends BaseBlockCipher {
    public PBEWithSHA1AESCBC128() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 1, 128, 16);
    }
  }
  
  public static class PBEWithSHA1AESCBC192 extends BaseBlockCipher {
    public PBEWithSHA1AESCBC192() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 1, 192, 16);
    }
  }
  
  public static class PBEWithSHA1AESCBC256 extends BaseBlockCipher {
    public PBEWithSHA1AESCBC256() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 1, 256, 16);
    }
  }
  
  public static class PBEWithSHA256AESCBC128 extends BaseBlockCipher {
    public PBEWithSHA256AESCBC128() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 4, 128, 16);
    }
  }
  
  public static class PBEWithSHA256AESCBC192 extends BaseBlockCipher {
    public PBEWithSHA256AESCBC192() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 4, 192, 16);
    }
  }
  
  public static class PBEWithSHA256AESCBC256 extends BaseBlockCipher {
    public PBEWithSHA256AESCBC256() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new AESEngine()), 2, 4, 256, 16);
    }
  }
  
  public static class PBEWithSHA256And128BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHA256And128BitAESBC() {
      super("PBEWithSHA256And128BitAES-CBC-BC", null, true, 2, 4, 128, 128);
    }
  }
  
  public static class PBEWithSHA256And192BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHA256And192BitAESBC() {
      super("PBEWithSHA256And192BitAES-CBC-BC", null, true, 2, 4, 192, 128);
    }
  }
  
  public static class PBEWithSHA256And256BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHA256And256BitAESBC() {
      super("PBEWithSHA256And256BitAES-CBC-BC", null, true, 2, 4, 256, 128);
    }
  }
  
  public static class PBEWithSHAAnd128BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHAAnd128BitAESBC() {
      super("PBEWithSHA1And128BitAES-CBC-BC", null, true, 2, 1, 128, 128);
    }
  }
  
  public static class PBEWithSHAAnd192BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHAAnd192BitAESBC() {
      super("PBEWithSHA1And192BitAES-CBC-BC", null, true, 2, 1, 192, 128);
    }
  }
  
  public static class PBEWithSHAAnd256BitAESBC extends PBESecretKeyFactory {
    public PBEWithSHAAnd256BitAESBC() {
      super("PBEWithSHA1And256BitAES-CBC-BC", null, true, 2, 1, 256, 128);
    }
  }
  
  public static class Poly1305 extends BaseMac {
    public Poly1305() {
      super((Mac)new org.bouncycastle.crypto.macs.Poly1305((BlockCipher)new AESEngine()));
    }
  }
  
  public static class Poly1305KeyGen extends BaseKeyGenerator {
    public Poly1305KeyGen() {
      super("Poly1305-AES", 256, (CipherKeyGenerator)new Poly1305KeyGenerator());
    }
  }
  
  public static class RFC3211Wrap extends BaseWrapCipher {
    public RFC3211Wrap() {
      super((Wrapper)new RFC3211WrapEngine((BlockCipher)new AESEngine()), 16);
    }
  }
  
  public static class RFC5649Wrap extends BaseWrapCipher {
    public RFC5649Wrap() {
      super((Wrapper)new RFC5649WrapEngine((BlockCipher)new AESEngine()));
    }
  }
  
  public static class Wrap extends BaseWrapCipher {
    public Wrap() {
      super((Wrapper)new AESWrapEngine());
    }
  }
  
  public static class WrapPad extends BaseWrapCipher {
    public WrapPad() {
      super((Wrapper)new AESWrapPadEngine());
    }
  }
}
