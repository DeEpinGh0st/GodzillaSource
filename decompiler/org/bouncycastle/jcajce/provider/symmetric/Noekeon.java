package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class Noekeon {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for Noekeon parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[16];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("Noekeon");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Noekeon IV";
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new NoekeonEngine();
            }
          });
    }
  }
  
  public static class GMAC extends BaseMac {
    public GMAC() {
      super((Mac)new GMac(new GCMBlockCipher((BlockCipher)new NoekeonEngine())));
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("Noekeon", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends SymmetricAlgorithmProvider {
    private static final String PREFIX = Noekeon.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.NOEKEON", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.NOEKEON", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Cipher.NOEKEON", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.NOEKEON", PREFIX + "$KeyGen");
      addGMacAlgorithm(param1ConfigurableProvider, "NOEKEON", PREFIX + "$GMAC", PREFIX + "$KeyGen");
      addPoly1305Algorithm(param1ConfigurableProvider, "NOEKEON", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
    }
  }
  
  public static class Poly1305 extends BaseMac {
    public Poly1305() {
      super((Mac)new org.bouncycastle.crypto.macs.Poly1305((BlockCipher)new NoekeonEngine()));
    }
  }
  
  public static class Poly1305KeyGen extends BaseKeyGenerator {
    public Poly1305KeyGen() {
      super("Poly1305-Noekeon", 256, (CipherKeyGenerator)new Poly1305KeyGenerator());
    }
  }
}
