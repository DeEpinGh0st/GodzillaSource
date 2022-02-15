package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class SymmetricAlgorithmProvider extends AlgorithmProvider {
  protected void addCMacAlgorithm(ConfigurableProvider paramConfigurableProvider, String paramString1, String paramString2, String paramString3) {
    paramConfigurableProvider.addAlgorithm("Mac." + paramString1 + "-CMAC", paramString2);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + paramString1 + "CMAC", paramString1 + "-CMAC");
    paramConfigurableProvider.addAlgorithm("KeyGenerator." + paramString1 + "-CMAC", paramString3);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + paramString1 + "CMAC", paramString1 + "-CMAC");
  }
  
  protected void addGMacAlgorithm(ConfigurableProvider paramConfigurableProvider, String paramString1, String paramString2, String paramString3) {
    paramConfigurableProvider.addAlgorithm("Mac." + paramString1 + "-GMAC", paramString2);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.Mac." + paramString1 + "GMAC", paramString1 + "-GMAC");
    paramConfigurableProvider.addAlgorithm("KeyGenerator." + paramString1 + "-GMAC", paramString3);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + paramString1 + "GMAC", paramString1 + "-GMAC");
  }
  
  protected void addPoly1305Algorithm(ConfigurableProvider paramConfigurableProvider, String paramString1, String paramString2, String paramString3) {
    paramConfigurableProvider.addAlgorithm("Mac.POLY1305-" + paramString1, paramString2);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.Mac.POLY1305" + paramString1, "POLY1305-" + paramString1);
    paramConfigurableProvider.addAlgorithm("KeyGenerator.POLY1305-" + paramString1, paramString3);
    paramConfigurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.POLY1305" + paramString1, "POLY1305-" + paramString1);
  }
}
