package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class CAST5 {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[8];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("CAST5");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends BaseAlgorithmParameters {
    private byte[] iv;
    
    private int keyLength = 128;
    
    protected byte[] engineGetEncoded() {
      byte[] arrayOfByte = new byte[this.iv.length];
      System.arraycopy(this.iv, 0, arrayOfByte, 0, this.iv.length);
      return arrayOfByte;
    }
    
    protected byte[] engineGetEncoded(String param1String) throws IOException {
      return isASN1FormatString(param1String) ? (new CAST5CBCParameters(engineGetEncoded(), this.keyLength)).getEncoded() : (param1String.equals("RAW") ? engineGetEncoded() : null);
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IvParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.iv); 
      throw new InvalidParameterSpecException("unknown parameter spec passed to CAST5 parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (param1AlgorithmParameterSpec instanceof IvParameterSpec) {
        this.iv = ((IvParameterSpec)param1AlgorithmParameterSpec).getIV();
      } else {
        throw new InvalidParameterSpecException("IvParameterSpec required to initialise a CAST5 parameters algorithm parameters object");
      } 
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.iv = new byte[param1ArrayOfbyte.length];
      System.arraycopy(param1ArrayOfbyte, 0, this.iv, 0, this.iv.length);
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (isASN1FormatString(param1String)) {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(param1ArrayOfbyte);
        CAST5CBCParameters cAST5CBCParameters = CAST5CBCParameters.getInstance(aSN1InputStream.readObject());
        this.keyLength = cAST5CBCParameters.getKeyLength();
        this.iv = cAST5CBCParameters.getIV();
        return;
      } 
      if (param1String.equals("RAW")) {
        engineInit(param1ArrayOfbyte);
        return;
      } 
      throw new IOException("Unknown parameters format in IV parameters object");
    }
    
    protected String engineToString() {
      return "CAST5 Parameters";
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new CAST5Engine()), 64);
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new CAST5Engine());
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("CAST5", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = CAST5.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.CAST5", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113533.7.66.10", "CAST5");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.CAST5", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "CAST5");
      param1ConfigurableProvider.addAlgorithm("Cipher.CAST5", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", MiscObjectIdentifiers.cast5CBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.CAST5", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cast5CBC, "CAST5");
    }
  }
}
