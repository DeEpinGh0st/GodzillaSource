package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Rijndael {
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Rijndael IV";
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new RijndaelEngine();
            }
          });
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Rijndael", 192, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = Rijndael.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.RIJNDAEL", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.RIJNDAEL", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.RIJNDAEL", PREFIX + "$AlgParams");
    }
  }
}
