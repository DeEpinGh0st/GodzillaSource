package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public final class Twofish {
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Twofish IV";
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new TwofishEngine();
            }
          });
    }
  }
  
  public static class GMAC extends BaseMac {
    public GMAC() {
      super((Mac)new GMac(new GCMBlockCipher((BlockCipher)new TwofishEngine())));
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Twofish", 256, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends SymmetricAlgorithmProvider {
    private static final String PREFIX = Twofish.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.Twofish", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.Twofish", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.Twofish", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDTWOFISH", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDTWOFISH-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAANDTWOFISH-CBC", PREFIX + "$PBEWithSHA");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAANDTWOFISH-CBC", PREFIX + "$PBEWithSHAKeyFactory");
      addGMacAlgorithm(param1ConfigurableProvider, "Twofish", PREFIX + "$GMAC", PREFIX + "$KeyGen");
      addPoly1305Algorithm(param1ConfigurableProvider, "Twofish", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
    }
  }
  
  public static class PBEWithSHA extends BaseBlockCipher {
    public PBEWithSHA() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new TwofishEngine()), 2, 1, 256, 16);
    }
  }
  
  public static class PBEWithSHAKeyFactory extends PBESecretKeyFactory {
    public PBEWithSHAKeyFactory() {
      super("PBEwithSHAandTwofish-CBC", null, true, 2, 1, 256, 128);
    }
  }
  
  public static class Poly1305 extends BaseMac {
    public Poly1305() {
      super((Mac)new org.bouncycastle.crypto.macs.Poly1305((BlockCipher)new TwofishEngine()));
    }
  }
  
  public static class Poly1305KeyGen extends BaseKeyGenerator {
    public Poly1305KeyGen() {
      super("Poly1305-Twofish", 256, (CipherKeyGenerator)new Poly1305KeyGenerator());
    }
  }
}
