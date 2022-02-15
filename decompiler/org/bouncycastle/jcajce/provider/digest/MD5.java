package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class MD5 {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new MD5Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new MD5Digest((MD5Digest)this.digest);
      return digest;
    }
  }
  
  public static class HashMac extends BaseMac {
    public HashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new MD5Digest()));
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("HMACMD5", 128, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = MD5.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.MD5", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md5, "MD5");
      addHMACAlgorithm(param1ConfigurableProvider, "MD5", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      addHMACAlias(param1ConfigurableProvider, "MD5", IANAObjectIdentifiers.hmacMD5);
    }
  }
}
