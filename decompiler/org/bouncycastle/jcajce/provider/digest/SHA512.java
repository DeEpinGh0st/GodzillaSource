package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class SHA512 {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new SHA512Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new SHA512Digest((SHA512Digest)this.digest);
      return digest;
    }
  }
  
  public static class DigestT extends BCMessageDigest implements Cloneable {
    public DigestT(int param1Int) {
      super((org.bouncycastle.crypto.Digest)new SHA512tDigest(param1Int));
    }
    
    public Object clone() throws CloneNotSupportedException {
      DigestT digestT = (DigestT)super.clone();
      digestT.digest = (org.bouncycastle.crypto.Digest)new SHA512tDigest((SHA512tDigest)this.digest);
      return digestT;
    }
  }
  
  public static class DigestT224 extends DigestT {
    public DigestT224() {
      super(224);
    }
  }
  
  public static class DigestT256 extends DigestT {
    public DigestT256() {
      super(256);
    }
  }
  
  public static class HashMac extends BaseMac {
    public HashMac() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new SHA512Digest()));
    }
  }
  
  public static class HashMacT224 extends BaseMac {
    public HashMacT224() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new SHA512tDigest(224)));
    }
  }
  
  public static class HashMacT256 extends BaseMac {
    public HashMacT256() {
      super((Mac)new HMac((org.bouncycastle.crypto.Digest)new SHA512tDigest(256)));
    }
  }
  
  public static class KeyGenerator extends BaseKeyGenerator {
    public KeyGenerator() {
      super("HMACSHA512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGeneratorT224 extends BaseKeyGenerator {
    public KeyGeneratorT224() {
      super("HMACSHA512/224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGeneratorT256 extends BaseKeyGenerator {
    public KeyGeneratorT256() {
      super("HMACSHA512/256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = SHA512.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.SHA-512", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512", "SHA-512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512, "SHA-512");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.SHA-512/224", PREFIX + "$DigestT224");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512/224", "SHA-512/224");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_224, "SHA-512/224");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.SHA-512/256", PREFIX + "$DigestT256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512256", "SHA-512/256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_256, "SHA-512/256");
      param1ConfigurableProvider.addAlgorithm("Mac.OLDHMACSHA512", PREFIX + "$OldSHA512");
      param1ConfigurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA512", PREFIX + "$HashMac");
      addHMACAlgorithm(param1ConfigurableProvider, "SHA512", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      addHMACAlias(param1ConfigurableProvider, "SHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
      addHMACAlgorithm(param1ConfigurableProvider, "SHA512/224", PREFIX + "$HashMacT224", PREFIX + "$KeyGeneratorT224");
      addHMACAlgorithm(param1ConfigurableProvider, "SHA512/256", PREFIX + "$HashMacT256", PREFIX + "$KeyGeneratorT256");
    }
  }
  
  public static class OldSHA512 extends BaseMac {
    public OldSHA512() {
      super((Mac)new OldHMac((org.bouncycastle.crypto.Digest)new SHA512Digest()));
    }
  }
}
