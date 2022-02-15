package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class DSTU4145 {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dstu.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.DSTU-4145-2002", "DSTU4145");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.DSTU4145-3410", "DSTU4145");
      registerOid(param1ConfigurableProvider, UAObjectIdentifiers.dstu4145le, "DSTU4145", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameters(param1ConfigurableProvider, UAObjectIdentifiers.dstu4145le, "DSTU4145");
      registerOid(param1ConfigurableProvider, UAObjectIdentifiers.dstu4145be, "DSTU4145", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameters(param1ConfigurableProvider, UAObjectIdentifiers.dstu4145be, "DSTU4145");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145", "DSTU4145");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145-2002", "DSTU4145");
      param1ConfigurableProvider.addAlgorithm("Signature.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.DSTU-4145", "DSTU4145");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.DSTU-4145-2002", "DSTU4145");
      addSignatureAlgorithm(param1ConfigurableProvider, "GOST3411", "DSTU4145LE", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpiLe", UAObjectIdentifiers.dstu4145le);
      addSignatureAlgorithm(param1ConfigurableProvider, "GOST3411", "DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi", UAObjectIdentifiers.dstu4145be);
    }
  }
}
