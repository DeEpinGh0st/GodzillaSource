package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;

public class NH {
  private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.newhope.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyAgreement.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.KeyAgreementSpi");
      NHKeyFactorySpi nHKeyFactorySpi = new NHKeyFactorySpi();
      registerOid(param1ConfigurableProvider, PQCObjectIdentifiers.newHope, "NH", (AsymmetricKeyInfoConverter)nHKeyFactorySpi);
    }
  }
}
