package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.RC532Engine;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class RC5 {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for RC5 parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[8];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("RC5");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends IvAlgorithmParameters {
    protected String engineToString() {
      return "RC5 IV";
    }
  }
  
  public static class CBC32 extends BaseBlockCipher {
    public CBC32() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new RC532Engine()), 64);
    }
  }
  
  public static class CFB8Mac32 extends BaseMac {
    public CFB8Mac32() {
      super((Mac)new CFBBlockCipherMac((BlockCipher)new RC532Engine()));
    }
  }
  
  public static class ECB32 extends BaseBlockCipher {
    public ECB32() {
      super((BlockCipher)new RC532Engine());
    }
  }
  
  public static class ECB64 extends BaseBlockCipher {
    public ECB64() {
      super((BlockCipher)new RC564Engine());
    }
  }
  
  public static class KeyGen32 extends BaseKeyGenerator {
    public KeyGen32() {
      super("RC5", 128, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGen64 extends BaseKeyGenerator {
    public KeyGen64() {
      super("RC5-64", 256, new CipherKeyGenerator());
    }
  }
  
  public static class Mac32 extends BaseMac {
    public Mac32() {
      super((Mac)new CBCBlockCipherMac((BlockCipher)new RC532Engine()));
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = RC5.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.RC5", PREFIX + "$ECB32");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RC5-32", "RC5");
      param1ConfigurableProvider.addAlgorithm("Cipher.RC5-64", PREFIX + "$ECB64");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.RC5", PREFIX + "$KeyGen32");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.RC5-32", "RC5");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.RC5-64", PREFIX + "$KeyGen64");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.RC5", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.RC5-64", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Mac.RC5MAC", PREFIX + "$Mac32");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.RC5", "RC5MAC");
      param1ConfigurableProvider.addAlgorithm("Mac.RC5MAC/CFB8", PREFIX + "$CFB8Mac32");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.RC5/CFB8", "RC5MAC/CFB8");
    }
  }
}
