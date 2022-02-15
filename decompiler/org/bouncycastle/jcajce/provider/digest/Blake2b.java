package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class Blake2b {
  public static class Blake2b160 extends BCMessageDigest implements Cloneable {
    public Blake2b160() {
      super((Digest)new Blake2bDigest(160));
    }
    
    public Object clone() throws CloneNotSupportedException {
      Blake2b160 blake2b160 = (Blake2b160)super.clone();
      blake2b160.digest = (Digest)new Blake2bDigest((Blake2bDigest)this.digest);
      return blake2b160;
    }
  }
  
  public static class Blake2b256 extends BCMessageDigest implements Cloneable {
    public Blake2b256() {
      super((Digest)new Blake2bDigest(256));
    }
    
    public Object clone() throws CloneNotSupportedException {
      Blake2b256 blake2b256 = (Blake2b256)super.clone();
      blake2b256.digest = (Digest)new Blake2bDigest((Blake2bDigest)this.digest);
      return blake2b256;
    }
  }
  
  public static class Blake2b384 extends BCMessageDigest implements Cloneable {
    public Blake2b384() {
      super((Digest)new Blake2bDigest(384));
    }
    
    public Object clone() throws CloneNotSupportedException {
      Blake2b384 blake2b384 = (Blake2b384)super.clone();
      blake2b384.digest = (Digest)new Blake2bDigest((Blake2bDigest)this.digest);
      return blake2b384;
    }
  }
  
  public static class Blake2b512 extends BCMessageDigest implements Cloneable {
    public Blake2b512() {
      super((Digest)new Blake2bDigest(512));
    }
    
    public Object clone() throws CloneNotSupportedException {
      Blake2b512 blake2b512 = (Blake2b512)super.clone();
      blake2b512.digest = (Digest)new Blake2bDigest((Blake2bDigest)this.digest);
      return blake2b512;
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = Blake2b.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.BLAKE2B-512", PREFIX + "$Blake2b512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b512, "BLAKE2B-512");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.BLAKE2B-384", PREFIX + "$Blake2b384");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b384, "BLAKE2B-384");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.BLAKE2B-256", PREFIX + "$Blake2b256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b256, "BLAKE2B-256");
      param1ConfigurableProvider.addAlgorithm("MessageDigest.BLAKE2B-160", PREFIX + "$Blake2b160");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b160, "BLAKE2B-160");
    }
  }
}
