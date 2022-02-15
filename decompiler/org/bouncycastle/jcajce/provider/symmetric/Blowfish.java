package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Blowfish {
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Blowfish IV";
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new BlowfishEngine()), 64);
    }
  }
  
  public static class CMAC extends BaseMac {
    public CMAC() {
      super((Mac)new CMac((BlockCipher)new BlowfishEngine()));
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new BlowfishEngine());
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Blowfish", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = Blowfish.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Mac.BLOWFISHCMAC", PREFIX + "$CMAC");
      param1ConfigurableProvider.addAlgorithm("Cipher.BLOWFISH", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.BLOWFISH", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, "BLOWFISH");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.BLOWFISH", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, "BLOWFISH");
    }
  }
}
