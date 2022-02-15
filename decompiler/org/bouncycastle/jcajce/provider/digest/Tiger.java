package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class Tiger {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new TigerDigest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new TigerDigest((TigerDigest)this.digest);
      return digest;
    }
  }
  
  public static class HashMac extends BaseMac {
    public HashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new TigerDigest()));
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("HMACTIGER", 192, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = Tiger.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.TIGER", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Tiger", PREFIX + "$Digest");
      addHMACAlgorithm(param1ConfigurableProvider, "TIGER", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      addHMACAlias(param1ConfigurableProvider, "TIGER", IANAObjectIdentifiers.hmacTIGER);
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACTIGER", PREFIX + "$PBEWithMacKeyFactory");
    }
  }
  
  public static class PBEWithHashMac extends BaseMac {
    public PBEWithHashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new TigerDigest()), 2, 3, 192);
    }
  }
  
  public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
    public PBEWithMacKeyFactory() {
      super("PBEwithHmacTiger", null, false, 2, 3, 192, 0);
    }
  }
  
  public static class TigerHmac extends BaseMac {
    public TigerHmac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new TigerDigest()));
    }
  }
}
