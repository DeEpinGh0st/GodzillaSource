package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class X509 {
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.X.509", "org.bouncycastle.jcajce.provider.asymmetric.x509.KeyFactory");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.X509", "X.509");
      param1ConfigurableProvider.addAlgorithm("CertificateFactory.X.509", "org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.CertificateFactory.X509", "X.509");
    }
  }
}
