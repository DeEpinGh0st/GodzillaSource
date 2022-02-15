package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class SHA1 {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new SHA1Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new SHA1Digest((SHA1Digest)this.digest);
      return digest;
    }
  }
  
  public static class HashMac extends BaseMac {
    public HashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new SHA1Digest()));
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("HMACSHA1", 160, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = SHA1.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.SHA-1", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA1", "SHA-1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA", "SHA-1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + OIWObjectIdentifiers.idSHA1, "SHA-1");
      addHMACAlgorithm(param1ConfigurableProvider, "SHA1", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      addHMACAlias(param1ConfigurableProvider, "SHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
      addHMACAlias(param1ConfigurableProvider, "SHA1", IANAObjectIdentifiers.hmacSHA1);
      param1ConfigurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA", PREFIX + "$SHA1Mac");
      param1ConfigurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA1", PREFIX + "$SHA1Mac");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHHMACSHA", "PBEWITHHMACSHA1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA");
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACSHA1", PREFIX + "$PBEWithMacKeyFactory");
    }
  }
  
  public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
    public PBEWithMacKeyFactory() {
      super("PBEwithHmacSHA", null, false, 2, 1, 160, 0);
    }
  }
  
  public static class SHA1Mac extends BaseMac {
    public SHA1Mac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new SHA1Digest()));
    }
  }
}
