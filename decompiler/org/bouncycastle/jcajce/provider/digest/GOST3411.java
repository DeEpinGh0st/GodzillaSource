package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class GOST3411 {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new GOST3411Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new GOST3411Digest((GOST3411Digest)this.digest);
      return digest;
    }
  }
  
  public static class Digest2012_256 extends BCMessageDigest implements Cloneable {
    public Digest2012_256() {
      super((org.bouncycastle.crypto.Digest)new GOST3411_2012_256Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest2012_256 digest2012_256 = (Digest2012_256)super.clone();
      digest2012_256.digest = (org.bouncycastle.crypto.Digest)new GOST3411_2012_256Digest((GOST3411_2012_256Digest)this.digest);
      return digest2012_256;
    }
  }
  
  public static class Digest2012_512 extends BCMessageDigest implements Cloneable {
    public Digest2012_512() {
      super((org.bouncycastle.crypto.Digest)new GOST3411_2012_512Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest2012_512 digest2012_512 = (Digest2012_512)super.clone();
      digest2012_512.digest = (org.bouncycastle.crypto.Digest)new GOST3411_2012_512Digest((GOST3411_2012_512Digest)this.digest);
      return digest2012_512;
    }
  }
  
  public static class HashMac extends BaseMac {
    public HashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new GOST3411Digest()));
    }
  }
  
  public static class HashMac2012_256 extends BaseMac {
    public HashMac2012_256() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new GOST3411_2012_256Digest()));
    }
  }
  
  public static class HashMac2012_512 extends BaseMac {
    public HashMac2012_512() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new GOST3411_2012_512Digest()));
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("HMACGOST3411", 256, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator2012_256 extends BaseKeyGenerator {
    public KeyGenerator2012_256() {
      super("HMACGOST3411", 256, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator2012_512 extends BaseKeyGenerator {
    public KeyGenerator2012_512() {
      super("HMACGOST3411", 512, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = GOST3411.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.GOST3411", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST", "GOST3411");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411", "GOST3411");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + CryptoProObjectIdentifiers.gostR3411, "GOST3411");
      addHMACAlgorithm(param1ConfigurableProvider, "GOST3411", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      addHMACAlias(param1ConfigurableProvider, "GOST3411", CryptoProObjectIdentifiers.gostR3411);
      param1ConfigurableProvider.addAlgorithm("MessageDigest.GOST3411-2012-256", PREFIX + "$Digest2012_256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-2012-256", "GOST3411-2012-256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411-2012-256", "GOST3411-2012-256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
      addHMACAlgorithm(param1ConfigurableProvider, "GOST3411-2012-256", PREFIX + "$HashMac2012_256", PREFIX + "$KeyGenerator2012_256");
      addHMACAlias(param1ConfigurableProvider, "GOST3411-2012-256", RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_256);
      param1ConfigurableProvider.addAlgorithm("MessageDigest.GOST3411-2012-512", PREFIX + "$Digest2012_512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-2012-512", "GOST3411-2012-512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411-2012-512", "GOST3411-2012-512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
      addHMACAlgorithm(param1ConfigurableProvider, "GOST3411-2012-512", PREFIX + "$HashMac2012_512", PREFIX + "$KeyGenerator2012_512");
      addHMACAlias(param1ConfigurableProvider, "GOST3411-2012-512", RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_512);
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACGOST3411", PREFIX + "$PBEWithMacKeyFactory");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + CryptoProObjectIdentifiers.gostR3411, "PBEWITHHMACGOST3411");
    }
  }
  
  public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
    public PBEWithMacKeyFactory() {
      super("PBEwithHmacGOST3411", null, false, 2, 6, 256, 0);
    }
  }
}
