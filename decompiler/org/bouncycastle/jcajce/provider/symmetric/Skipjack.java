package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Skipjack {
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Skipjack IV";
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new SkipjackEngine());
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Skipjack", 80, new CipherKeyGenerator());
    }
  }
  
  public static class Mac extends BaseMac {
    public Mac() {
      super((org.bouncycastle.crypto.Mac)new CBCBlockCipherMac((BlockCipher)new SkipjackEngine()));
    }
  }
  
  public static class MacCFB8 extends BaseMac {
    public MacCFB8() {
      super((org.bouncycastle.crypto.Mac)new CFBBlockCipherMac((BlockCipher)new SkipjackEngine()));
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = Skipjack.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.SKIPJACK", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.SKIPJACK", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.SKIPJACK", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Mac.SKIPJACKMAC", PREFIX + "$Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.SKIPJACK", "SKIPJACKMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.SKIPJACKMAC/CFB8", PREFIX + "$MacCFB8");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.SKIPJACK/CFB8", "SKIPJACKMAC/CFB8");
    }
  }
}
