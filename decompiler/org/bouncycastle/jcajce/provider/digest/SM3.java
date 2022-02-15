package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class SM3 {
  public static class Digest extends BCMessageDigest implements Cloneable {
    public Digest() {
      super((org.bouncycastle.crypto.Digest)new SM3Digest());
    }
    
    public Object clone() throws CloneNotSupportedException {
      Digest digest = (Digest)super.clone();
      digest.digest = (org.bouncycastle.crypto.Digest)new SM3Digest((SM3Digest)this.digest);
      return digest;
    }
  }
  
  public static class Mappings extends DigestAlgorithmProvider {
    private static final String PREFIX = SM3.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("MessageDigest.SM3", PREFIX + "$Digest");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SM3", "SM3");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.MessageDigest.1.2.156.197.1.401", "SM3");
    }
  }
}
