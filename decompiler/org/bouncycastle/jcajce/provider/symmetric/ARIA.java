package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.ARIAWrapEngine;
import org.bouncycastle.crypto.engines.ARIAWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class ARIA {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[16];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("ARIA");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "ARIA IV";
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
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new ARIAEngine()), 128);
    }
  }
  
  public static class CFB extends BaseBlockCipher {
    public CFB() {
      super(new BufferedBlockCipher((BlockCipher)new CFBBlockCipher((BlockCipher)new ARIAEngine(), 128)), 128);
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new ARIAEngine();
            }
          });
    }
  }
  
  public static class GMAC extends BaseMac {
    public GMAC() {
      super((Mac)new GMac(new GCMBlockCipher((BlockCipher)new ARIAEngine())));
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      this(256);
    }
    
    public KeyGen(int param1Int) {
      super("ARIA", param1Int, new CipherKeyGenerator());
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
    private static final String PREFIX = ARIA.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.ARIA", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIA", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_ofb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_ofb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_ofb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cfb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cfb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cfb, "ARIA");
      param1ConfigurableProvider.addAlgorithm("Cipher.ARIA", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$CFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$CFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$CFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$OFB");
      param1ConfigurableProvider.addAlgorithm("Cipher.ARIARFC3211WRAP", PREFIX + "$RFC3211Wrap");
      param1ConfigurableProvider.addAlgorithm("Cipher.ARIAWRAP", PREFIX + "$Wrap");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kw, "ARIAWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kw, "ARIAWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kw, "ARIAWRAP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKW", "ARIAWRAP");
      param1ConfigurableProvider.addAlgorithm("Cipher.ARIAWRAPPAD", PREFIX + "$WrapPad");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kwp, "ARIAWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kwp, "ARIAWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kwp, "ARIAWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", "ARIAWRAPPAD");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.ARIA", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kw, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kw, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kw, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kwp, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kwp, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kwp, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ccm, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ccm, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ccm, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_gcm, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_gcm, PREFIX + "$KeyGen192");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_gcm, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", PREFIX + "$AlgParamGenCCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", PREFIX + "$AlgParamGenGCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
      addGMacAlgorithm(param1ConfigurableProvider, "ARIA", PREFIX + "$GMAC", PREFIX + "$KeyGen");
      addPoly1305Algorithm(param1ConfigurableProvider, "ARIA", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
    }
  }
  
  public static class OFB extends BaseBlockCipher {
    public OFB() {
      super(new BufferedBlockCipher((BlockCipher)new OFBBlockCipher((BlockCipher)new ARIAEngine(), 128)), 128);
    }
  }
  
  public static class Poly1305 extends BaseMac {
    public Poly1305() {
      super((Mac)new org.bouncycastle.crypto.macs.Poly1305((BlockCipher)new ARIAEngine()));
    }
  }
  
  public static class Poly1305KeyGen extends BaseKeyGenerator {
    public Poly1305KeyGen() {
      super("Poly1305-ARIA", 256, (CipherKeyGenerator)new Poly1305KeyGenerator());
    }
  }
  
  public static class RFC3211Wrap extends BaseWrapCipher {
    public RFC3211Wrap() {
      super((Wrapper)new RFC3211WrapEngine((BlockCipher)new ARIAEngine()), 16);
    }
  }
  
  public static class Wrap extends BaseWrapCipher {
    public Wrap() {
      super((Wrapper)new ARIAWrapEngine());
    }
  }
  
  public static class WrapPad extends BaseWrapCipher {
    public WrapPad() {
      super((Wrapper)new ARIAWrapPadEngine());
    }
  }
}
