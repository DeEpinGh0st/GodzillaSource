package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;

public class SPHINCS {
  private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.sphincs.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyPairGeneratorSpi");
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA512", "SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha512", PQCObjectIdentifiers.sphincs256_with_SHA512);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA3-512", "SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha3_512", PQCObjectIdentifiers.sphincs256_with_SHA3_512);
      Sphincs256KeyFactorySpi sphincs256KeyFactorySpi = new Sphincs256KeyFactorySpi();
      registerOid(param1ConfigurableProvider, PQCObjectIdentifiers.sphincs256, "SPHINCS256", (AsymmetricKeyInfoConverter)sphincs256KeyFactorySpi);
      registerOidAlgorithmParameters(param1ConfigurableProvider, PQCObjectIdentifiers.sphincs256, "SPHINCS256");
    }
  }
}
