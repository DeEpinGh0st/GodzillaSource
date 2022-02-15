package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.misc.IDEACBCPar;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class IDEA {
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for IDEA parameter generation.");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      byte[] arrayOfByte = new byte[8];
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(arrayOfByte);
      try {
        algorithmParameters = createParametersInstance("IDEA");
        algorithmParameters.init(new IvParameterSpec(arrayOfByte));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends BaseAlgorithmParameters {
    private byte[] iv;
    
    protected byte[] engineGetEncoded() throws IOException {
      return engineGetEncoded("ASN.1");
    }
    
    protected byte[] engineGetEncoded(String param1String) throws IOException {
      if (isASN1FormatString(param1String))
        return (new IDEACBCPar(engineGetEncoded("RAW"))).getEncoded(); 
      if (param1String.equals("RAW")) {
        byte[] arrayOfByte = new byte[this.iv.length];
        System.arraycopy(this.iv, 0, arrayOfByte, 0, this.iv.length);
        return arrayOfByte;
      } 
      return null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IvParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.iv); 
      throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof IvParameterSpec))
        throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object"); 
      this.iv = ((IvParameterSpec)param1AlgorithmParameterSpec).getIV();
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.iv = new byte[param1ArrayOfbyte.length];
      System.arraycopy(param1ArrayOfbyte, 0, this.iv, 0, this.iv.length);
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (param1String.equals("RAW")) {
        engineInit(param1ArrayOfbyte);
        return;
      } 
      if (param1String.equals("ASN.1")) {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(param1ArrayOfbyte);
        IDEACBCPar iDEACBCPar = new IDEACBCPar((ASN1Sequence)aSN1InputStream.readObject());
        engineInit(iDEACBCPar.getIV());
        return;
      } 
      throw new IOException("Unknown parameters format in IV parameters object");
    }
    
    protected String engineToString() {
      return "IDEA Parameters";
    }
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new IDEAEngine()), 64);
    }
  }
  
  public static class CFB8Mac extends BaseMac {
    public CFB8Mac() {
      super((org.bouncycastle.crypto.Mac)new CFBBlockCipherMac((BlockCipher)new IDEAEngine()));
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new IDEAEngine());
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      super("IDEA", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mac extends BaseMac {
    public Mac() {
      super((org.bouncycastle.crypto.Mac)new CBCBlockCipherMac((BlockCipher)new IDEAEngine()));
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = IDEA.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.IDEA", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.1.3.6.1.4.1.188.7.1.1.2", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.IDEA", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.1.3.6.1.4.1.188.7.1.1.2", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA-CBC", "PKCS12PBE");
      param1ConfigurableProvider.addAlgorithm("Cipher.IDEA", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Cipher", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, PREFIX + "$CBC");
      param1ConfigurableProvider.addAlgorithm("Cipher.PBEWITHSHAANDIDEA-CBC", PREFIX + "$PBEWithSHAAndIDEA");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.IDEA", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAANDIDEA-CBC", PREFIX + "$PBEWithSHAAndIDEAKeyGen");
      param1ConfigurableProvider.addAlgorithm("Mac.IDEAMAC", PREFIX + "$Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.IDEA", "IDEAMAC");
      param1ConfigurableProvider.addAlgorithm("Mac.IDEAMAC/CFB8", PREFIX + "$CFB8Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.IDEA/CFB8", "IDEAMAC/CFB8");
    }
  }
  
  public static class PBEWithSHAAndIDEA extends BaseBlockCipher {
    public PBEWithSHAAndIDEA() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new IDEAEngine()));
    }
  }
  
  public static class PBEWithSHAAndIDEAKeyGen extends PBESecretKeyFactory {
    public PBEWithSHAAndIDEAKeyGen() {
      super("PBEwithSHAandIDEA-CBC", null, true, 2, 1, 128, 64);
    }
  }
}
