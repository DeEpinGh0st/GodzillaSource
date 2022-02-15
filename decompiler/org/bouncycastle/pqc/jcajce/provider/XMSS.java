package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;

public class XMSS {
  private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.xmss.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyPairGeneratorSpi");
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA256", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256", BCObjectIdentifiers.xmss_with_SHA256);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHAKE128", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128", BCObjectIdentifiers.xmss_with_SHAKE128);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA512", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512", BCObjectIdentifiers.xmss_with_SHA512);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHAKE256", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256", BCObjectIdentifiers.xmss_with_SHAKE256);
      param1ConfigurableProvider.addAlgorithm("KeyFactory.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyPairGeneratorSpi");
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA256", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256", BCObjectIdentifiers.xmss_mt_with_SHA256);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHAKE128", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128", BCObjectIdentifiers.xmss_mt_with_SHAKE128);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA512", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512", BCObjectIdentifiers.xmss_mt_with_SHA512);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHAKE256", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256", BCObjectIdentifiers.xmss_mt_with_SHAKE256);
      registerOid(param1ConfigurableProvider, PQCObjectIdentifiers.xmss, "XMSS", (AsymmetricKeyInfoConverter)new XMSSKeyFactorySpi());
      registerOid(param1ConfigurableProvider, PQCObjectIdentifiers.xmss_mt, "XMSSMT", (AsymmetricKeyInfoConverter)new XMSSMTKeyFactorySpi());
    }
  }
}
