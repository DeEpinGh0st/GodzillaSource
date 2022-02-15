package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class Shacal2 {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for Shacal2 parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[32];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("Shacal2");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "Shacal2 IV";
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new Shacal2Engine()), 256);
    }
  }
  
  public static class CMAC extends BaseMac {
    public CMAC() {
      super((Mac)new CMac((BlockCipher)new Shacal2Engine()));
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super(new BlockCipherProvider() {
            public BlockCipher get() {
              return (BlockCipher)new Shacal2Engine();
            }
          });
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("SHACAL-2", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends SymmetricAlgorithmProvider {
    private static final String PREFIX = Shacal2.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Mac.Shacal-2CMAC", PREFIX + "$CMAC");
      param1ConfigurableProvider.addAlgorithm("Cipher.Shacal2", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher.SHACAL-2", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.Shacal2", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.Shacal2", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.Shacal2", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.SHACAL-2", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.SHACAL-2", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.SHACAL-2", PREFIX + "$AlgParams");
    }
  }
}
