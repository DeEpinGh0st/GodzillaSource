package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.engines.DSTU7624WrapEngine;
import org.bouncycastle.crypto.macs.KGMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public class DSTU7624 {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    private final int ivLength;
    
    public AlgParamGen(int param1Int) {
      this.ivLength = param1Int / 8;
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSTU7624 parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[this.ivLength];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("DSTU7624");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParamGen128 extends AlgParamGen {
    AlgParamGen128() {
      super(128);
    }
  }
  
  public static class AlgParamGen256 extends AlgParamGen {
    AlgParamGen256() {
      super(256);
    }
  }
  
  public static class AlgParamGen512 extends AlgParamGen {
    AlgParamGen512() {
      super(512);
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "DSTU7624 IV";
    }
  }
  
  public static class CBC128 extends BaseBlockCipher {
    public CBC128() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DSTU7624Engine(128)), 128);
    }
  }
  
  public static class CBC256 extends BaseBlockCipher {
    public CBC256() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DSTU7624Engine(256)), 256);
    }
  }
  
  public static class CBC512 extends BaseBlockCipher {
    public CBC512() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new DSTU7624Engine(512)), 512);
    }
  }
  
  public static class CCM128 extends BaseBlockCipher {
    public CCM128() {
      super((AEADBlockCipher)new KCCMBlockCipher((BlockCipher)new DSTU7624Engine(128)));
    }
  }
  
  public static class CCM256 extends BaseBlockCipher {
    public CCM256() {
      super((AEADBlockCipher)new KCCMBlockCipher((BlockCipher)new DSTU7624Engine(256)));
    }
  }
  
  public static class CCM512 extends BaseBlockCipher {
    public CCM512() {
      super((AEADBlockCipher)new KCCMBlockCipher((BlockCipher)new DSTU7624Engine(512)));
    }
  }
  
  public static class CFB128 extends BaseBlockCipher {
    public CFB128() {
      super(new BufferedBlockCipher((BlockCipher)new CFBBlockCipher((BlockCipher)new DSTU7624Engine(128), 128)), 128);
    }
  }
  
  public static class CFB256 extends BaseBlockCipher {
    public CFB256() {
      super(new BufferedBlockCipher((BlockCipher)new CFBBlockCipher((BlockCipher)new DSTU7624Engine(256), 256)), 256);
    }
  }
  
  public static class CFB512 extends BaseBlockCipher {
    public CFB512() {
      super(new BufferedBlockCipher((BlockCipher)new CFBBlockCipher((BlockCipher)new DSTU7624Engine(512), 512)), 512);
    }
  }
  
  public static class CTR128 extends BaseBlockCipher {
    public CTR128() {
      super(new BufferedBlockCipher((BlockCipher)new KCTRBlockCipher((BlockCipher)new DSTU7624Engine(128))), 128);
    }
  }
  
  public static class CTR256 extends BaseBlockCipher {
    public CTR256() {
      super(new BufferedBlockCipher((BlockCipher)new KCTRBlockCipher((BlockCipher)new DSTU7624Engine(256))), 256);
    }
  }
  
  public static class CTR512 extends BaseBlockCipher {
    public CTR512() {
      super(new BufferedBlockCipher((BlockCipher)new KCTRBlockCipher((BlockCipher)new DSTU7624Engine(512))), 512);
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new DSTU7624Engine(128);
            }
          });
    }
  }
  
  public static class ECB128 extends BaseBlockCipher {
    public ECB128() {
      super((BlockCipher)new DSTU7624Engine(128));
    }
  }
  
  public static class ECB256 extends BaseBlockCipher {
    public ECB256() {
      super((BlockCipher)new DSTU7624Engine(256));
    }
  }
  
  public static class ECB512 extends BaseBlockCipher {
    public ECB512() {
      super((BlockCipher)new DSTU7624Engine(512));
    }
  }
  
  public static class ECB_128 extends BaseBlockCipher {
    public ECB_128() {
      super((BlockCipher)new DSTU7624Engine(128));
    }
  }
  
  public static class ECB_256 extends BaseBlockCipher {
    public ECB_256() {
      super((BlockCipher)new DSTU7624Engine(256));
    }
  }
  
  public static class ECB_512 extends BaseBlockCipher {
    public ECB_512() {
      super((BlockCipher)new DSTU7624Engine(512));
    }
  }
  
  public static class GCM128 extends BaseBlockCipher {
    public GCM128() {
      super((AEADBlockCipher)new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(128)));
    }
  }
  
  public static class GCM256 extends BaseBlockCipher {
    public GCM256() {
      super((AEADBlockCipher)new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(256)));
    }
  }
  
  public static class GCM512 extends BaseBlockCipher {
    public GCM512() {
      super((AEADBlockCipher)new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(512)));
    }
  }
  
  public static class GMAC extends BaseMac {
    public GMAC() {
      super((Mac)new KGMac(new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(128)), 128));
    }
  }
  
  public static class GMAC128 extends BaseMac {
    public GMAC128() {
      super((Mac)new KGMac(new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(128)), 128));
    }
  }
  
  public static class GMAC256 extends BaseMac {
    public GMAC256() {
      super((Mac)new KGMac(new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(256)), 256));
    }
  }
  
  public static class GMAC512 extends BaseMac {
    public GMAC512() {
      super((Mac)new KGMac(new KGCMBlockCipher((BlockCipher)new DSTU7624Engine(512)), 512));
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      this(256);
    }
    
    public KeyGen(int param1Int) {
      super("DSTU7624", param1Int, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGen128 extends KeyGen {
    public KeyGen128() {
      super(128);
    }
  }
  
  public static class KeyGen256 extends KeyGen {
    public KeyGen256() {
      super(256);
    }
  }
  
  public static class KeyGen512 extends KeyGen {
    public KeyGen512() {
      super(512);
    }
  }
  
  public static class Mappings extends SymmetricAlgorithmProvider {
    private static final String PREFIX = DSTU7624.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.DSTU7624", PREFIX + "$AlgParams128");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters", UAObjectIdentifiers.dstu7624cbc_128, PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters", UAObjectIdentifiers.dstu7624cbc_256, PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters", UAObjectIdentifiers.dstu7624cbc_512, PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.DSTU7624", PREFIX + "$AlgParamGen128");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator", UAObjectIdentifiers.dstu7624cbc_128, PREFIX + "$AlgParamGen128");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator", UAObjectIdentifiers.dstu7624cbc_256, PREFIX + "$AlgParamGen256");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator", UAObjectIdentifiers.dstu7624cbc_512, PREFIX + "$AlgParamGen512");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624", PREFIX + "$ECB_128");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-128", PREFIX + "$ECB_128");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-256", PREFIX + "$ECB_256");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-512", PREFIX + "$ECB_512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ecb_128, PREFIX + "$ECB128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ecb_256, PREFIX + "$ECB256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ecb_512, PREFIX + "$ECB512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cbc_128, PREFIX + "$CBC128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cbc_256, PREFIX + "$CBC256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cbc_512, PREFIX + "$CBC512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ofb_128, PREFIX + "$OFB128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ofb_256, PREFIX + "$OFB256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ofb_512, PREFIX + "$OFB512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cfb_128, PREFIX + "$CFB128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cfb_256, PREFIX + "$CFB256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624cfb_512, PREFIX + "$CFB512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ctr_128, PREFIX + "$CTR128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ctr_256, PREFIX + "$CTR256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ctr_512, PREFIX + "$CTR512");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ccm_128, PREFIX + "$CCM128");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ccm_256, PREFIX + "$CCM256");
      param1ConfigurableProvider.addAlgorithm("Cipher", UAObjectIdentifiers.dstu7624ccm_512, PREFIX + "$CCM512");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624KW", PREFIX + "$Wrap");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624WRAP", "DSTU7624KW");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-128KW", PREFIX + "$Wrap128");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher." + UAObjectIdentifiers.dstu7624kw_128.getId(), "DSTU7624-128KW");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-128WRAP", "DSTU7624-128KW");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-256KW", PREFIX + "$Wrap256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher." + UAObjectIdentifiers.dstu7624kw_256.getId(), "DSTU7624-256KW");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-256WRAP", "DSTU7624-256KW");
      param1ConfigurableProvider.addAlgorithm("Cipher.DSTU7624-512KW", PREFIX + "$Wrap512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher." + UAObjectIdentifiers.dstu7624kw_512.getId(), "DSTU7624-512KW");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-512WRAP", "DSTU7624-512KW");
      param1ConfigurableProvider.addAlgorithm("Mac.DSTU7624GMAC", PREFIX + "$GMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DSTU7624-128GMAC", PREFIX + "$GMAC128");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + UAObjectIdentifiers.dstu7624gmac_128.getId(), "DSTU7624-128GMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DSTU7624-256GMAC", PREFIX + "$GMAC256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + UAObjectIdentifiers.dstu7624gmac_256.getId(), "DSTU7624-256GMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.DSTU7624-512GMAC", PREFIX + "$GMAC512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + UAObjectIdentifiers.dstu7624gmac_512.getId(), "DSTU7624-512GMAC");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.DSTU7624", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624kw_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624kw_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624kw_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ecb_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ecb_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ecb_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cbc_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cbc_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cbc_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ofb_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ofb_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ofb_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cfb_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cfb_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624cfb_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ctr_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ctr_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ctr_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ccm_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ccm_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624ccm_512, PREFIX + "$KeyGen512");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624gmac_128, PREFIX + "$KeyGen128");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624gmac_256, PREFIX + "$KeyGen256");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", UAObjectIdentifiers.dstu7624gmac_512, PREFIX + "$KeyGen512");
    }
  }
  
  public static class OFB128 extends BaseBlockCipher {
    public OFB128() {
      super(new BufferedBlockCipher((BlockCipher)new OFBBlockCipher((BlockCipher)new DSTU7624Engine(128), 128)), 128);
    }
  }
  
  public static class OFB256 extends BaseBlockCipher {
    public OFB256() {
      super(new BufferedBlockCipher((BlockCipher)new OFBBlockCipher((BlockCipher)new DSTU7624Engine(256), 256)), 256);
    }
  }
  
  public static class OFB512 extends BaseBlockCipher {
    public OFB512() {
      super(new BufferedBlockCipher((BlockCipher)new OFBBlockCipher((BlockCipher)new DSTU7624Engine(512), 512)), 512);
    }
  }
  
  public static class Wrap extends BaseWrapCipher {
    public Wrap() {
      super((Wrapper)new DSTU7624WrapEngine(128));
    }
  }
  
  public static class Wrap128 extends BaseWrapCipher {
    public Wrap128() {
      super((Wrapper)new DSTU7624WrapEngine(128));
    }
  }
  
  public static class Wrap256 extends BaseWrapCipher {
    public Wrap256() {
      super((Wrapper)new DSTU7624WrapEngine(256));
    }
  }
  
  public static class Wrap512 extends BaseWrapCipher {
    public Wrap512() {
      super((Wrapper)new DSTU7624WrapEngine(512));
    }
  }
}
