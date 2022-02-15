package org.bouncycastle.jcajce.provider.keystore;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class BC {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.keystore.bc.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyStore.BKS", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Std");
      param1ConfigurableProvider.addAlgorithm("KeyStore.BKS-V1", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Version1");
      param1ConfigurableProvider.addAlgorithm("KeyStore.BouncyCastle", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$BouncyCastleStore");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyStore.UBER", "BouncyCastle");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyStore.BOUNCYCASTLE", "BouncyCastle");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyStore.bouncycastle", "BouncyCastle");
    }
  }
}
