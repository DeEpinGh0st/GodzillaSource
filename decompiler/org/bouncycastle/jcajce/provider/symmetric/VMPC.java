package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class VMPC {
  public static class Base extends BaseStreamCipher {
    public Base() {
      super((StreamCipher)new VMPCEngine(), 16);
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("VMPC", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mac extends BaseMac {
    public Mac() {
      super((org.bouncycastle.crypto.Mac)new VMPCMac());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = VMPC.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.VMPC", PREFIX + "$Base");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.VMPC", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("Mac.VMPCMAC", PREFIX + "$Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.VMPC", "VMPCMAC");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.VMPC-MAC", "VMPCMAC");
    }
  }
}
