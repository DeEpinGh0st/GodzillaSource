package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class ECGOST {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ecgost.";
  
  private static final String PREFIX_GOST_2012 = "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyFactory.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2001", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410", "ECGOST3410");
      registerOid(param1ConfigurableProvider, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOid(param1ConfigurableProvider, CryptoProObjectIdentifiers.gostR3410_2001DH, "ECGOST3410", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameters(param1ConfigurableProvider, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST-3410", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2001", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Signature.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST-3410", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2001", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("KeyAgreement.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyAgreementSpi$ECVKO");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement.GOST-3410-2001", "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH, "ECGOST3410");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.AlgorithmParametersSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410-2001", "ECGOST3410");
      addSignatureAlgorithm(param1ConfigurableProvider, "GOST3411", "ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
      param1ConfigurableProvider.addAlgorithm("KeyFactory.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2012", "ECGOST3410-2012");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410-2012", "ECGOST3410-2012");
      registerOid(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOid(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, "ECGOST3410-2012", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameters(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012");
      registerOid(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOid(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, "ECGOST3410-2012", (AsymmetricKeyInfoConverter)new KeyFactorySpi());
      registerOidAlgorithmParameters(param1ConfigurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST3410-2012", "ECGOST3410-2012");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2012", "ECGOST3410-2012");
      param1ConfigurableProvider.addAlgorithm("Signature.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-256", "ECGOST3410-2012-256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-256", "ECGOST3410-2012-256");
      addSignatureAlgorithm(param1ConfigurableProvider, "GOST3411-2012-256", "ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
      param1ConfigurableProvider.addAlgorithm("Signature.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-512", "ECGOST3410-2012-512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-512", "ECGOST3410-2012-512");
      addSignatureAlgorithm(param1ConfigurableProvider, "GOST3411-2012-512", "ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
      param1ConfigurableProvider.addAlgorithm("KeyAgreement.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO256");
      param1ConfigurableProvider.addAlgorithm("KeyAgreement.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, "ECGOST3410-2012-256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, "ECGOST3410-2012-512");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
    }
  }
}
