package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.util.Integers;

public class PBEPBKDF2 {
  private static final Map prfCodes = new HashMap<Object, Object>();
  
  static {
    prfCodes.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(6));
    prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(1));
    prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(4));
    prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(7));
    prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(8));
    prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(9));
    prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(11));
    prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(10));
    prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(12));
    prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(13));
  }
  
  public static class AlgParams extends BaseAlgorithmParameters {
    PBKDF2Params params;
    
    protected byte[] engineGetEncoded() {
      try {
        return this.params.getEncoded("DER");
      } catch (IOException iOException) {
        throw new RuntimeException("Oooops! " + iOException.toString());
      } 
    }
    
    protected byte[] engineGetEncoded(String param1String) {
      return isASN1FormatString(param1String) ? engineGetEncoded() : null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<PBEParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == PBEParameterSpec.class)
        return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue()); 
      throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF2 PBE parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF2 PBE parameters algorithm parameters object"); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      this.params = new PBKDF2Params(pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.params = PBKDF2Params.getInstance(ASN1Primitive.fromByteArray(param1ArrayOfbyte));
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (isASN1FormatString(param1String)) {
        engineInit(param1ArrayOfbyte);
        return;
      } 
      throw new IOException("Unknown parameters format in PBKDF2 parameters object");
    }
    
    protected String engineToString() {
      return "PBKDF2 Parameters";
    }
  }
  
  public static class BasePBKDF2 extends BaseSecretKeyFactory {
    private int scheme;
    
    private int defaultDigest;
    
    public BasePBKDF2(String param1String, int param1Int) {
      this(param1String, param1Int, 1);
    }
    
    public BasePBKDF2(String param1String, int param1Int1, int param1Int2) {
      super(param1String, PKCSObjectIdentifiers.id_PBKDF2);
      this.scheme = param1Int1;
      this.defaultDigest = param1Int2;
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof PBEKeySpec) {
        PBEKeySpec pBEKeySpec = (PBEKeySpec)param1KeySpec;
        if (pBEKeySpec.getSalt() == null)
          return (SecretKey)new PBKDF2Key(((PBEKeySpec)param1KeySpec).getPassword(), (this.scheme == 1) ? (CharToByteConverter)PasswordConverter.ASCII : (CharToByteConverter)PasswordConverter.UTF8); 
        if (pBEKeySpec.getIterationCount() <= 0)
          throw new InvalidKeySpecException("positive iteration count required: " + pBEKeySpec.getIterationCount()); 
        if (pBEKeySpec.getKeyLength() <= 0)
          throw new InvalidKeySpecException("positive key length required: " + pBEKeySpec.getKeyLength()); 
        if ((pBEKeySpec.getPassword()).length == 0)
          throw new IllegalArgumentException("password empty"); 
        if (pBEKeySpec instanceof PBKDF2KeySpec) {
          PBKDF2KeySpec pBKDF2KeySpec = (PBKDF2KeySpec)pBEKeySpec;
          int k = getDigestCode(pBKDF2KeySpec.getPrf().getAlgorithm());
          int m = pBEKeySpec.getKeyLength();
          byte b1 = -1;
          CipherParameters cipherParameters1 = PBE.Util.makePBEMacParameters(pBEKeySpec, this.scheme, k, m);
          return (SecretKey)new BCPBEKey(this.algName, this.algOid, this.scheme, k, m, b1, pBEKeySpec, cipherParameters1);
        } 
        int i = this.defaultDigest;
        int j = pBEKeySpec.getKeyLength();
        byte b = -1;
        CipherParameters cipherParameters = PBE.Util.makePBEMacParameters(pBEKeySpec, this.scheme, i, j);
        return (SecretKey)new BCPBEKey(this.algName, this.algOid, this.scheme, i, j, b, pBEKeySpec, cipherParameters);
      } 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
    
    private int getDigestCode(ASN1ObjectIdentifier param1ASN1ObjectIdentifier) throws InvalidKeySpecException {
      Integer integer = (Integer)PBEPBKDF2.prfCodes.get(param1ASN1ObjectIdentifier);
      if (integer != null)
        return integer.intValue(); 
      throw new InvalidKeySpecException("Invalid KeySpec: unknown PRF algorithm " + param1ASN1ObjectIdentifier);
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = PBEPBKDF2.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.PBKDF2", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2", PREFIX + "$PBKDF2withUTF8");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1", "PBKDF2");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1ANDUTF8", "PBKDF2");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHASCII", PREFIX + "$PBKDF2with8BIT");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITH8BIT", "PBKDF2WITHASCII");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1AND8BIT", "PBKDF2WITHASCII");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA224", PREFIX + "$PBKDF2withSHA224");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA256", PREFIX + "$PBKDF2withSHA256");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA384", PREFIX + "$PBKDF2withSHA384");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512", PREFIX + "$PBKDF2withSHA512");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-224", PREFIX + "$PBKDF2withSHA3_224");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-256", PREFIX + "$PBKDF2withSHA3_256");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-384", PREFIX + "$PBKDF2withSHA3_384");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-512", PREFIX + "$PBKDF2withSHA3_512");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACGOST3411", PREFIX + "$PBKDF2withGOST3411");
    }
  }
  
  public static class PBKDF2with8BIT extends BasePBKDF2 {
    public PBKDF2with8BIT() {
      super("PBKDF2", 1);
    }
  }
  
  public static class PBKDF2withGOST3411 extends BasePBKDF2 {
    public PBKDF2withGOST3411() {
      super("PBKDF2", 5, 6);
    }
  }
  
  public static class PBKDF2withSHA224 extends BasePBKDF2 {
    public PBKDF2withSHA224() {
      super("PBKDF2", 5, 7);
    }
  }
  
  public static class PBKDF2withSHA256 extends BasePBKDF2 {
    public PBKDF2withSHA256() {
      super("PBKDF2", 5, 4);
    }
  }
  
  public static class PBKDF2withSHA384 extends BasePBKDF2 {
    public PBKDF2withSHA384() {
      super("PBKDF2", 5, 8);
    }
  }
  
  public static class PBKDF2withSHA3_224 extends BasePBKDF2 {
    public PBKDF2withSHA3_224() {
      super("PBKDF2", 5, 10);
    }
  }
  
  public static class PBKDF2withSHA3_256 extends BasePBKDF2 {
    public PBKDF2withSHA3_256() {
      super("PBKDF2", 5, 11);
    }
  }
  
  public static class PBKDF2withSHA3_384 extends BasePBKDF2 {
    public PBKDF2withSHA3_384() {
      super("PBKDF2", 5, 12);
    }
  }
  
  public static class PBKDF2withSHA3_512 extends BasePBKDF2 {
    public PBKDF2withSHA3_512() {
      super("PBKDF2", 5, 13);
    }
  }
  
  public static class PBKDF2withSHA512 extends BasePBKDF2 {
    public PBKDF2withSHA512() {
      super("PBKDF2", 5, 9);
    }
  }
  
  public static class PBKDF2withUTF8 extends BasePBKDF2 {
    public PBKDF2withUTF8() {
      super("PBKDF2", 5);
    }
  }
}
