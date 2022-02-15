package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class GOST {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.gost.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-94", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-94", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParametersSpi");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParameterGeneratorSpi");
      registerOid(param1ConfigurableProvider, CryptoProObjectIdentifiers.gostR3410_94, "GOST3410", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameterGenerator(param1ConfigurableProvider, CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Signature.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.SignatureSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-94", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411withGOST3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411WITHGOST3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411WithGOST3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.GOST-3410", "GOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410", "GOST3410");
    }
  }
}
