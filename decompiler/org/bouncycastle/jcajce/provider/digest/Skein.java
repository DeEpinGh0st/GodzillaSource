package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Skein {
  public static class DigestSkein1024 extends BCMessageDigest implements Cloneable {
    public DigestSkein1024(int param1Int) {
      super((Digest)new SkeinDigest(1024, param1Int));
    }
    
    public Object clone() throws CloneNotSupportedException {
      BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
      bCMessageDigest.digest = (Digest)new SkeinDigest((SkeinDigest)this.digest);
      return bCMessageDigest;
    }
  }
  
  public static class DigestSkein256 extends BCMessageDigest implements Cloneable {
    public DigestSkein256(int param1Int) {
      super((Digest)new SkeinDigest(256, param1Int));
    }
    
    public Object clone() throws CloneNotSupportedException {
      BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
      bCMessageDigest.digest = (Digest)new SkeinDigest((SkeinDigest)this.digest);
      return bCMessageDigest;
    }
  }
  
  public static class DigestSkein512 extends BCMessageDigest implements Cloneable {
    public DigestSkein512(int param1Int) {
      super((Digest)new SkeinDigest(512, param1Int));
    }
    
    public Object clone() throws CloneNotSupportedException {
      BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
      bCMessageDigest.digest = (Digest)new SkeinDigest((SkeinDigest)this.digest);
      return bCMessageDigest;
    }
  }
  
  public static class Digest_1024_1024 extends DigestSkein1024 {
    public Digest_1024_1024() {
      super(1024);
    }
  }
  
  public static class Digest_1024_384 extends DigestSkein1024 {
    public Digest_1024_384() {
      super(384);
    }
  }
  
  public static class Digest_1024_512 extends DigestSkein1024 {
    public Digest_1024_512() {
      super(512);
    }
  }
  
  public static class Digest_256_128 extends DigestSkein256 {
    public Digest_256_128() {
      super(128);
    }
  }
  
  public static class Digest_256_160 extends DigestSkein256 {
    public Digest_256_160() {
      super(160);
    }
  }
  
  public static class Digest_256_224 extends DigestSkein256 {
    public Digest_256_224() {
      super(224);
    }
  }
  
  public static class Digest_256_256 extends DigestSkein256 {
    public Digest_256_256() {
      super(256);
    }
  }
  
  public static class Digest_512_128 extends DigestSkein512 {
    public Digest_512_128() {
      super(128);
    }
  }
  
  public static class Digest_512_160 extends DigestSkein512 {
    public Digest_512_160() {
      super(160);
    }
  }
  
  public static class Digest_512_224 extends DigestSkein512 {
    public Digest_512_224() {
      super(224);
    }
  }
  
  public static class Digest_512_256 extends DigestSkein512 {
    public Digest_512_256() {
      super(256);
    }
  }
  
  public static class Digest_512_384 extends DigestSkein512 {
    public Digest_512_384() {
      super(384);
    }
  }
  
  public static class Digest_512_512 extends DigestSkein512 {
    public Digest_512_512() {
      super(512);
    }
  }
  
  public static class HMacKeyGenerator_1024_1024 extends BaseKeyGenerator {
    public HMacKeyGenerator_1024_1024() {
      super("HMACSkein-1024-1024", 1024, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_1024_384 extends BaseKeyGenerator {
    public HMacKeyGenerator_1024_384() {
      super("HMACSkein-1024-384", 384, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_1024_512 extends BaseKeyGenerator {
    public HMacKeyGenerator_1024_512() {
      super("HMACSkein-1024-512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_256_128 extends BaseKeyGenerator {
    public HMacKeyGenerator_256_128() {
      super("HMACSkein-256-128", 128, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_256_160 extends BaseKeyGenerator {
    public HMacKeyGenerator_256_160() {
      super("HMACSkein-256-160", 160, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_256_224 extends BaseKeyGenerator {
    public HMacKeyGenerator_256_224() {
      super("HMACSkein-256-224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_256_256 extends BaseKeyGenerator {
    public HMacKeyGenerator_256_256() {
      super("HMACSkein-256-256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_128 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_128() {
      super("HMACSkein-512-128", 128, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_160 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_160() {
      super("HMACSkein-512-160", 160, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_224 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_224() {
      super("HMACSkein-512-224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_256 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_256() {
      super("HMACSkein-512-256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_384 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_384() {
      super("HMACSkein-512-384", 384, new CipherKeyGenerator());
    }
  }
  
  public static class HMacKeyGenerator_512_512 extends BaseKeyGenerator {
    public HMacKeyGenerator_512_512() {
      super("HMACSkein-512-512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class HashMac_1024_1024 extends BaseMac {
    public HashMac_1024_1024() {
      super((Mac)new HMac((Digest)new SkeinDigest(1024, 1024)));
    }
  }
  
  public static class HashMac_1024_384 extends BaseMac {
    public HashMac_1024_384() {
      super((Mac)new HMac((Digest)new SkeinDigest(1024, 384)));
    }
  }
  
  public static class HashMac_1024_512 extends BaseMac {
    public HashMac_1024_512() {
      super((Mac)new HMac((Digest)new SkeinDigest(1024, 512)));
    }
  }
  
  public static class HashMac_256_128 extends BaseMac {
    public HashMac_256_128() {
      super((Mac)new HMac((Digest)new SkeinDigest(256, 128)));
    }
  }
  
  public static class HashMac_256_160 extends BaseMac {
    public HashMac_256_160() {
      super((Mac)new HMac((Digest)new SkeinDigest(256, 160)));
    }
  }
  
  public static class HashMac_256_224 extends BaseMac {
    public HashMac_256_224() {
      super((Mac)new HMac((Digest)new SkeinDigest(256, 224)));
    }
  }
  
  public static class HashMac_256_256 extends BaseMac {
    public HashMac_256_256() {
      super((Mac)new HMac((Digest)new SkeinDigest(256, 256)));
    }
  }
  
  public static class HashMac_512_128 extends BaseMac {
    public HashMac_512_128() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 128)));
    }
  }
  
  public static class HashMac_512_160 extends BaseMac {
    public HashMac_512_160() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 160)));
    }
  }
  
  public static class HashMac_512_224 extends BaseMac {
    public HashMac_512_224() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 224)));
    }
  }
  
  public static class HashMac_512_256 extends BaseMac {
    public HashMac_512_256() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 256)));
    }
  }
  
  public static class HashMac_512_384 extends BaseMac {
    public HashMac_512_384() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 384)));
    }
  }
  
  public static class HashMac_512_512 extends BaseMac {
    public HashMac_512_512() {
      super((Mac)new HMac((Digest)new SkeinDigest(512, 512)));
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = Skein.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-256-128", PREFIX + "$Digest_256_128");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-256-160", PREFIX + "$Digest_256_160");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-256-224", PREFIX + "$Digest_256_224");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-256-256", PREFIX + "$Digest_256_256");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-128", PREFIX + "$Digest_512_128");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-160", PREFIX + "$Digest_512_160");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-224", PREFIX + "$Digest_512_224");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-256", PREFIX + "$Digest_512_256");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-384", PREFIX + "$Digest_512_384");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-512-512", PREFIX + "$Digest_512_512");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-1024-384", PREFIX + "$Digest_1024_384");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-1024-512", PREFIX + "$Digest_1024_512");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.Skein-1024-1024", PREFIX + "$Digest_1024_1024");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-256-128", PREFIX + "$HashMac_256_128", PREFIX + "$HMacKeyGenerator_256_128");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-256-160", PREFIX + "$HashMac_256_160", PREFIX + "$HMacKeyGenerator_256_160");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-256-224", PREFIX + "$HashMac_256_224", PREFIX + "$HMacKeyGenerator_256_224");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-256-256", PREFIX + "$HashMac_256_256", PREFIX + "$HMacKeyGenerator_256_256");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-128", PREFIX + "$HashMac_512_128", PREFIX + "$HMacKeyGenerator_512_128");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-160", PREFIX + "$HashMac_512_160", PREFIX + "$HMacKeyGenerator_512_160");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-224", PREFIX + "$HashMac_512_224", PREFIX + "$HMacKeyGenerator_512_224");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-256", PREFIX + "$HashMac_512_256", PREFIX + "$HMacKeyGenerator_512_256");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-384", PREFIX + "$HashMac_512_384", PREFIX + "$HMacKeyGenerator_512_384");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-512-512", PREFIX + "$HashMac_512_512", PREFIX + "$HMacKeyGenerator_512_512");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-1024-384", PREFIX + "$HashMac_1024_384", PREFIX + "$HMacKeyGenerator_1024_384");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-1024-512", PREFIX + "$HashMac_1024_512", PREFIX + "$HMacKeyGenerator_1024_512");
      addHMACAlgorithm(param1ConfigurableProvider, "Skein-1024-1024", PREFIX + "$HashMac_1024_1024", PREFIX + "$HMacKeyGenerator_1024_1024");
      addSkeinMacAlgorithm(param1ConfigurableProvider, 256, 128);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 256, 160);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 256, 224);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 256, 256);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 128);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 160);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 224);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 256);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 384);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 512, 512);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 1024, 384);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 1024, 512);
      addSkeinMacAlgorithm(param1ConfigurableProvider, 1024, 1024);
    }
    
    private void addSkeinMacAlgorithm(ConfigurableProvider param1ConfigurableProvider, int param1Int1, int param1Int2) {
      String str1 = "Skein-MAC-" + param1Int1 + "-" + param1Int2;
      String str2 = PREFIX + "$SkeinMac_" + param1Int1 + "_" + param1Int2;
      String str3 = PREFIX + "$SkeinMacKeyGenerator_" + param1Int1 + "_" + param1Int2;
      param1ConfigurableProvider.addAlgorithm("Mac." + str1, str2);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Mac.Skein-MAC" + param1Int1 + "/" + param1Int2, str1);
      param1ConfigurableProvider.addAlgorithm("KeyGenerator." + str1, str3);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.Skein-MAC" + param1Int1 + "/" + param1Int2, str1);
    }
  }
  
  public static class SkeinMacKeyGenerator_1024_1024 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_1024_1024() {
      super("Skein-MAC-1024-1024", 1024, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_1024_384 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_1024_384() {
      super("Skein-MAC-1024-384", 384, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_1024_512 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_1024_512() {
      super("Skein-MAC-1024-512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_256_128 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_256_128() {
      super("Skein-MAC-256-128", 128, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_256_160 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_256_160() {
      super("Skein-MAC-256-160", 160, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_256_224 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_256_224() {
      super("Skein-MAC-256-224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_256_256 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_256_256() {
      super("Skein-MAC-256-256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_128 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_128() {
      super("Skein-MAC-512-128", 128, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_160 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_160() {
      super("Skein-MAC-512-160", 160, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_224 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_224() {
      super("Skein-MAC-512-224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_256 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_256() {
      super("Skein-MAC-512-256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_384 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_384() {
      super("Skein-MAC-512-384", 384, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMacKeyGenerator_512_512 extends BaseKeyGenerator {
    public SkeinMacKeyGenerator_512_512() {
      super("Skein-MAC-512-512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class SkeinMac_1024_1024 extends BaseMac {
    public SkeinMac_1024_1024() {
      super((Mac)new SkeinMac(1024, 1024));
    }
  }
  
  public static class SkeinMac_1024_384 extends BaseMac {
    public SkeinMac_1024_384() {
      super((Mac)new SkeinMac(1024, 384));
    }
  }
  
  public static class SkeinMac_1024_512 extends BaseMac {
    public SkeinMac_1024_512() {
      super((Mac)new SkeinMac(1024, 512));
    }
  }
  
  public static class SkeinMac_256_128 extends BaseMac {
    public SkeinMac_256_128() {
      super((Mac)new SkeinMac(256, 128));
    }
  }
  
  public static class SkeinMac_256_160 extends BaseMac {
    public SkeinMac_256_160() {
      super((Mac)new SkeinMac(256, 160));
    }
  }
  
  public static class SkeinMac_256_224 extends BaseMac {
    public SkeinMac_256_224() {
      super((Mac)new SkeinMac(256, 224));
    }
  }
  
  public static class SkeinMac_256_256 extends BaseMac {
    public SkeinMac_256_256() {
      super((Mac)new SkeinMac(256, 256));
    }
  }
  
  public static class SkeinMac_512_128 extends BaseMac {
    public SkeinMac_512_128() {
      super((Mac)new SkeinMac(512, 128));
    }
  }
  
  public static class SkeinMac_512_160 extends BaseMac {
    public SkeinMac_512_160() {
      super((Mac)new SkeinMac(512, 160));
    }
  }
  
  public static class SkeinMac_512_224 extends BaseMac {
    public SkeinMac_512_224() {
      super((Mac)new SkeinMac(512, 224));
    }
  }
  
  public static class SkeinMac_512_256 extends BaseMac {
    public SkeinMac_512_256() {
      super((Mac)new SkeinMac(512, 256));
    }
  }
  
  public static class SkeinMac_512_384 extends BaseMac {
    public SkeinMac_512_384() {
      super((Mac)new SkeinMac(512, 384));
    }
  }
  
  public static class SkeinMac_512_512 extends BaseMac {
    public SkeinMac_512_512() {
      super((Mac)new SkeinMac(512, 512));
    }
  }
}
