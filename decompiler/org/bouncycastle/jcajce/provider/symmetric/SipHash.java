package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class SipHash {
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("SipHash", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mac24 extends BaseMac {
    public Mac24() {
      super((Mac)new org.bouncycastle.crypto.macs.SipHash());
    }
  }
  
  public static class Mac48 extends BaseMac {
    public Mac48() {
      super((Mac)new org.bouncycastle.crypto.macs.SipHash(4, 8));
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = SipHash.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Mac.SIPHASH-2-4", PREFIX + "$Mac24");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.SIPHASH", "SIPHASH-2-4");
      param1ConfigurableProvider.addAlgorithm("Mac.SIPHASH-4-8", PREFIX + "$Mac48");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.SIPHASH", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-2-4", "SIPHASH");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-4-8", "SIPHASH");
    }
  }
}
