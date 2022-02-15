package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class Poly1305 {
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Poly1305", 256, (CipherKeyGenerator)new Poly1305KeyGenerator());
    }
  }
  
  public static class Mac extends BaseMac {
    public Mac() {
      super((org.bouncycastle.crypto.Mac)new org.bouncycastle.crypto.macs.Poly1305());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = Poly1305.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Mac.POLY1305", PREFIX + "$Mac");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.POLY1305", PREFIX + "$KeyGen");
    }
  }
}
