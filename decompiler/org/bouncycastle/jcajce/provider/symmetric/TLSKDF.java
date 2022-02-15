package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.TLSKeyMaterialSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class TLSKDF {
  private static byte[] PRF_legacy(TLSKeyMaterialSpec paramTLSKeyMaterialSpec) {
    HMac hMac1 = new HMac(DigestFactory.createMD5());
    HMac hMac2 = new HMac(DigestFactory.createSHA1());
    byte[] arrayOfByte1 = Strings.toByteArray(paramTLSKeyMaterialSpec.getLabel());
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, paramTLSKeyMaterialSpec.getSeed());
    byte[] arrayOfByte3 = paramTLSKeyMaterialSpec.getSecret();
    int i = (arrayOfByte3.length + 1) / 2;
    byte[] arrayOfByte4 = new byte[i];
    byte[] arrayOfByte5 = new byte[i];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, i);
    System.arraycopy(arrayOfByte3, arrayOfByte3.length - i, arrayOfByte5, 0, i);
    int j = paramTLSKeyMaterialSpec.getLength();
    byte[] arrayOfByte6 = new byte[j];
    byte[] arrayOfByte7 = new byte[j];
    hmac_hash((Mac)hMac1, arrayOfByte4, arrayOfByte2, arrayOfByte6);
    hmac_hash((Mac)hMac2, arrayOfByte5, arrayOfByte2, arrayOfByte7);
    for (byte b = 0; b < j; b++)
      arrayOfByte6[b] = (byte)(arrayOfByte6[b] ^ arrayOfByte7[b]); 
    return arrayOfByte6;
  }
  
  private static void hmac_hash(Mac paramMac, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    paramMac.init((CipherParameters)new KeyParameter(paramArrayOfbyte1));
    byte[] arrayOfByte1 = paramArrayOfbyte2;
    int i = paramMac.getMacSize();
    int j = (paramArrayOfbyte3.length + i - 1) / i;
    byte[] arrayOfByte2 = new byte[paramMac.getMacSize()];
    byte[] arrayOfByte3 = new byte[paramMac.getMacSize()];
    for (byte b = 0; b < j; b++) {
      paramMac.update(arrayOfByte1, 0, arrayOfByte1.length);
      paramMac.doFinal(arrayOfByte2, 0);
      arrayOfByte1 = arrayOfByte2;
      paramMac.update(arrayOfByte1, 0, arrayOfByte1.length);
      paramMac.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
      paramMac.doFinal(arrayOfByte3, 0);
      System.arraycopy(arrayOfByte3, 0, paramArrayOfbyte3, i * b, Math.min(i, paramArrayOfbyte3.length - i * b));
    } 
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = TLSKDF.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.TLS10KDF", PREFIX + "$TLS10");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.TLS11KDF", PREFIX + "$TLS11");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA256KDF", PREFIX + "$TLS12withSHA256");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA384KDF", PREFIX + "$TLS12withSHA384");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA512KDF", PREFIX + "$TLS12withSHA512");
    }
  }
  
  public static final class TLS10 extends TLSKeyMaterialFactory {
    public TLS10() {
      super("TLS10KDF");
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof TLSKeyMaterialSpec)
        return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec)param1KeySpec), this.algName); 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
  }
  
  public static final class TLS11 extends TLSKeyMaterialFactory {
    public TLS11() {
      super("TLS11KDF");
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof TLSKeyMaterialSpec)
        return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec)param1KeySpec), this.algName); 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
  }
  
  public static class TLS12 extends TLSKeyMaterialFactory {
    private final Mac prf;
    
    protected TLS12(String param1String, Mac param1Mac) {
      super(param1String);
      this.prf = param1Mac;
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof TLSKeyMaterialSpec)
        return new SecretKeySpec(PRF((TLSKeyMaterialSpec)param1KeySpec, this.prf), this.algName); 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
    
    private byte[] PRF(TLSKeyMaterialSpec param1TLSKeyMaterialSpec, Mac param1Mac) {
      byte[] arrayOfByte1 = Strings.toByteArray(param1TLSKeyMaterialSpec.getLabel());
      byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, param1TLSKeyMaterialSpec.getSeed());
      byte[] arrayOfByte3 = param1TLSKeyMaterialSpec.getSecret();
      byte[] arrayOfByte4 = new byte[param1TLSKeyMaterialSpec.getLength()];
      TLSKDF.hmac_hash(param1Mac, arrayOfByte3, arrayOfByte2, arrayOfByte4);
      return arrayOfByte4;
    }
  }
  
  public static final class TLS12withSHA256 extends TLS12 {
    public TLS12withSHA256() {
      super("TLS12withSHA256KDF", (Mac)new HMac((Digest)new SHA256Digest()));
    }
  }
  
  public static final class TLS12withSHA384 extends TLS12 {
    public TLS12withSHA384() {
      super("TLS12withSHA384KDF", (Mac)new HMac((Digest)new SHA384Digest()));
    }
  }
  
  public static final class TLS12withSHA512 extends TLS12 {
    public TLS12withSHA512() {
      super("TLS12withSHA512KDF", (Mac)new HMac((Digest)new SHA512Digest()));
    }
  }
  
  public static class TLSKeyMaterialFactory extends BaseSecretKeyFactory {
    protected TLSKeyMaterialFactory(String param1String) {
      super(param1String, null);
    }
  }
}
