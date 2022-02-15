package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Keccak {
  public static class Digest224 extends DigestKeccak {
    public Digest224() {
      super(224);
    }
  }
  
  public static class Digest256 extends DigestKeccak {
    public Digest256() {
      super(256);
    }
  }
  
  public static class Digest288 extends DigestKeccak {
    public Digest288() {
      super(288);
    }
  }
  
  public static class Digest384 extends DigestKeccak {
    public Digest384() {
      super(384);
    }
  }
  
  public static class Digest512 extends DigestKeccak {
    public Digest512() {
      super(512);
    }
  }
  
  public static class DigestKeccak extends BCMessageDigest implements Cloneable {
    public DigestKeccak(int param1Int) {
      super((Digest)new KeccakDigest(param1Int));
    }
    
    public Object clone() throws CloneNotSupportedException {
      BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
      bCMessageDigest.digest = (Digest)new KeccakDigest((KeccakDigest)this.digest);
      return bCMessageDigest;
    }
  }
  
  public static class HashMac224 extends BaseMac {
    public HashMac224() {
      super((Mac)new HMac((Digest)new KeccakDigest(224)));
    }
  }
  
  public static class HashMac256 extends BaseMac {
    public HashMac256() {
      super((Mac)new HMac((Digest)new KeccakDigest(256)));
    }
  }
  
  public static class HashMac288 extends BaseMac {
    public HashMac288() {
      super((Mac)new HMac((Digest)new KeccakDigest(288)));
    }
  }
  
  public static class HashMac384 extends BaseMac {
    public HashMac384() {
      super((Mac)new HMac((Digest)new KeccakDigest(384)));
    }
  }
  
  public static class HashMac512 extends BaseMac {
    public HashMac512() {
      super((Mac)new HMac((Digest)new KeccakDigest(512)));
    }
  }
  
  public static class KeyGenerator224 extends BaseKeyGenerator {
    public KeyGenerator224() {
      super("HMACKECCAK224", 224, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator256 extends BaseKeyGenerator {
    public KeyGenerator256() {
      super("HMACKECCAK256", 256, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator288 extends BaseKeyGenerator {
    public KeyGenerator288() {
      super("HMACKECCAK288", 288, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator384 extends BaseKeyGenerator {
    public KeyGenerator384() {
      super("HMACKECCAK384", 384, new CipherKeyGenerator());
    }
  }
  
  public static class KeyGenerator512 extends BaseKeyGenerator {
    public KeyGenerator512() {
      super("HMACKECCAK512", 512, new CipherKeyGenerator());
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = Keccak.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.KECCAK-224", PREFIX + "$Digest224");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.KECCAK-288", PREFIX + "$Digest288");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.KECCAK-256", PREFIX + "$Digest256");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.KECCAK-384", PREFIX + "$Digest384");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.KECCAK-512", PREFIX + "$Digest512");
      addHMACAlgorithm(param1ConfigurableProvider, "KECCAK224", PREFIX + "$HashMac224", PREFIX + "$KeyGenerator224");
      addHMACAlgorithm(param1ConfigurableProvider, "KECCAK256", PREFIX + "$HashMac256", PREFIX + "$KeyGenerator256");
      addHMACAlgorithm(param1ConfigurableProvider, "KECCAK288", PREFIX + "$HashMac288", PREFIX + "$KeyGenerator288");
      addHMACAlgorithm(param1ConfigurableProvider, "KECCAK384", PREFIX + "$HashMac384", PREFIX + "$KeyGenerator384");
      addHMACAlgorithm(param1ConfigurableProvider, "KECCAK512", PREFIX + "$HashMac512", PREFIX + "$KeyGenerator512");
    }
  }
}
