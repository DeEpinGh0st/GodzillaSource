package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.CryptoProWrapEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST28147WrapEngine;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;

public final class GOST28147 {
  private static Map<ASN1ObjectIdentifier, String> oidMappings = new HashMap<ASN1ObjectIdentifier, String>();
  
  private static Map<String, ASN1ObjectIdentifier> nameMappings = new HashMap<String, ASN1ObjectIdentifier>();
  
  static {
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_TestParamSet, "E-TEST");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
    nameMappings.put("E-A", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet);
    nameMappings.put("E-B", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet);
    nameMappings.put("E-C", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet);
    nameMappings.put("E-D", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet);
  }
  
  public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
    byte[] iv = new byte[8];
    
    byte[] sBox = GOST28147Engine.getSBox("E-A");
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      if (param1AlgorithmParameterSpec instanceof GOST28147ParameterSpec) {
        this.sBox = ((GOST28147ParameterSpec)param1AlgorithmParameterSpec).getSBox();
      } else {
        throw new InvalidAlgorithmParameterException("parameter spec not supported");
      } 
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters algorithmParameters;
      if (this.random == null)
        this.random = new SecureRandom(); 
      this.random.nextBytes(this.iv);
      try {
        algorithmParameters = createParametersInstance("GOST28147");
        algorithmParameters.init((AlgorithmParameterSpec)new GOST28147ParameterSpec(this.sBox, this.iv));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
      return algorithmParameters;
    }
  }
  
  public static class AlgParams extends BaseAlgParams {
    private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
    
    private byte[] iv;
    
    protected byte[] localGetEncoded() throws IOException {
      return (new GOST28147Parameters(this.iv, this.sBox)).getEncoded();
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IvParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.iv); 
      if (param1Class == GOST28147ParameterSpec.class || param1Class == AlgorithmParameterSpec.class)
        return (AlgorithmParameterSpec)new GOST28147ParameterSpec(this.sBox, this.iv); 
      throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + param1Class.getName());
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (param1AlgorithmParameterSpec instanceof IvParameterSpec) {
        this.iv = ((IvParameterSpec)param1AlgorithmParameterSpec).getIV();
      } else if (param1AlgorithmParameterSpec instanceof GOST28147ParameterSpec) {
        this.iv = ((GOST28147ParameterSpec)param1AlgorithmParameterSpec).getIV();
        try {
          this.sBox = getSBoxOID(((GOST28147ParameterSpec)param1AlgorithmParameterSpec).getSBox());
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new InvalidParameterSpecException(illegalArgumentException.getMessage());
        } 
      } else {
        throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
      } 
    }
    
    protected void localInit(byte[] param1ArrayOfbyte) throws IOException {
      ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(param1ArrayOfbyte);
      if (aSN1Primitive instanceof ASN1OctetString) {
        this.iv = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
      } else if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Sequence) {
        GOST28147Parameters gOST28147Parameters = GOST28147Parameters.getInstance(aSN1Primitive);
        this.sBox = gOST28147Parameters.getEncryptionParamSet();
        this.iv = gOST28147Parameters.getIV();
      } else {
        throw new IOException("Unable to recognize parameters");
      } 
    }
    
    protected String engineToString() {
      return "GOST 28147 IV Parameters";
    }
  }
  
  public static abstract class BaseAlgParams extends BaseAlgorithmParameters {
    private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
    
    private byte[] iv;
    
    protected final void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      engineInit(param1ArrayOfbyte, "ASN.1");
    }
    
    protected final byte[] engineGetEncoded() throws IOException {
      return engineGetEncoded("ASN.1");
    }
    
    protected final byte[] engineGetEncoded(String param1String) throws IOException {
      if (isASN1FormatString(param1String))
        return localGetEncoded(); 
      throw new IOException("Unknown parameter format: " + param1String);
    }
    
    protected final void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (param1ArrayOfbyte == null)
        throw new NullPointerException("Encoded parameters cannot be null"); 
      if (isASN1FormatString(param1String)) {
        try {
          localInit(param1ArrayOfbyte);
        } catch (IOException iOException) {
          throw iOException;
        } catch (Exception exception) {
          throw new IOException("Parameter parsing failed: " + exception.getMessage());
        } 
      } else {
        throw new IOException("Unknown parameter format: " + param1String);
      } 
    }
    
    protected byte[] localGetEncoded() throws IOException {
      return (new GOST28147Parameters(this.iv, this.sBox)).getEncoded();
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IvParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == IvParameterSpec.class)
        return new IvParameterSpec(this.iv); 
      if (param1Class == GOST28147ParameterSpec.class || param1Class == AlgorithmParameterSpec.class)
        return (AlgorithmParameterSpec)new GOST28147ParameterSpec(this.sBox, this.iv); 
      throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + param1Class.getName());
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (param1AlgorithmParameterSpec instanceof IvParameterSpec) {
        this.iv = ((IvParameterSpec)param1AlgorithmParameterSpec).getIV();
      } else if (param1AlgorithmParameterSpec instanceof GOST28147ParameterSpec) {
        this.iv = ((GOST28147ParameterSpec)param1AlgorithmParameterSpec).getIV();
        try {
          this.sBox = getSBoxOID(((GOST28147ParameterSpec)param1AlgorithmParameterSpec).getSBox());
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new InvalidParameterSpecException(illegalArgumentException.getMessage());
        } 
      } else {
        throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
      } 
    }
    
    protected static ASN1ObjectIdentifier getSBoxOID(String param1String) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)GOST28147.nameMappings.get(param1String);
      if (aSN1ObjectIdentifier == null)
        throw new IllegalArgumentException("Unknown SBOX name: " + param1String); 
      return aSN1ObjectIdentifier;
    }
    
    protected static ASN1ObjectIdentifier getSBoxOID(byte[] param1ArrayOfbyte) {
      return getSBoxOID(GOST28147Engine.getSBoxName(param1ArrayOfbyte));
    }
    
    abstract void localInit(byte[] param1ArrayOfbyte) throws IOException;
  }
  
  public static class CBC extends BaseBlockCipher {
    public CBC() {
      super((BlockCipher)new CBCBlockCipher((BlockCipher)new GOST28147Engine()), 64);
    }
  }
  
  public static class CryptoProWrap extends BaseWrapCipher {
    public CryptoProWrap() {
      super((Wrapper)new CryptoProWrapEngine());
    }
  }
  
  public static class ECB extends BaseBlockCipher {
    public ECB() {
      super((BlockCipher)new GOST28147Engine());
    }
  }
  
  public static class GCFB extends BaseBlockCipher {
    public GCFB() {
      super(new BufferedBlockCipher((BlockCipher)new GCFBBlockCipher((BlockCipher)new GOST28147Engine())), 64);
    }
  }
  
  public static class GostWrap extends BaseWrapCipher {
    public GostWrap() {
      super((Wrapper)new GOST28147WrapEngine());
    }
  }
  
  public static class KeyGen extends BaseKeyGenerator {
    public KeyGen() {
      this(256);
    }
    
    public KeyGen(int param1Int) {
      super("GOST28147", param1Int, new CipherKeyGenerator());
    }
  }
  
  public static class Mac extends BaseMac {
    public Mac() {
      super((org.bouncycastle.crypto.Mac)new GOST28147Mac());
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = GOST28147.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("Cipher.GOST28147", PREFIX + "$ECB");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST", "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST-28147", "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.gostR28147_gcfb, PREFIX + "$GCFB");
      param1ConfigurableProvider.addAlgorithm("KeyGenerator.GOST28147", PREFIX + "$KeyGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST", "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST-28147", "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.GOST28147", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.GOST28147", PREFIX + "$AlgParamGen");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
      param1ConfigurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap, PREFIX + "$CryptoProWrap");
      param1ConfigurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap, PREFIX + "$GostWrap");
      param1ConfigurableProvider.addAlgorithm("Mac.GOST28147MAC", PREFIX + "$Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.GOST28147", "GOST28147MAC");
    }
  }
}
