package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class DSA {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dsa.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParametersSpi");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameterGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParameterGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Signature.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$stdDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.NONEWITHDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$noneDSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.RAWDSA", "NONEWITHDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.DETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA1WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA224WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA256WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA384WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA512WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
      param1ConfigurableProvider.addAlgorithm("Signature.DDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA1WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA224WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA256WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA384WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA512WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA3-224WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_224");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA3-256WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_256");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA3-384WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_384");
      param1ConfigurableProvider.addAlgorithm("Signature.SHA3-512WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_512");
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa224", NISTObjectIdentifiers.dsa_with_sha224);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa256", NISTObjectIdentifiers.dsa_with_sha256);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa384", NISTObjectIdentifiers.dsa_with_sha384);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa512", NISTObjectIdentifiers.dsa_with_sha512);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA3-224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_224", NISTObjectIdentifiers.id_dsa_with_sha3_224);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA3-256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_256", NISTObjectIdentifiers.id_dsa_with_sha3_256);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA3-384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_384", NISTObjectIdentifiers.id_dsa_with_sha3_384);
      addSignatureAlgorithm(param1ConfigurableProvider, "SHA3-512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_512", NISTObjectIdentifiers.id_dsa_with_sha3_512);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.SHA/DSA", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1withDSA", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1WITHDSA", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.1", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.3", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.DSAwithSHA1", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.DSAWITHSHA1", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1WithDSA", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.DSAWithSHA1", "DSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.1.2.840.10040.4.3", "DSA");
      KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
      for (byte b = 0; b != DSAUtil.dsaOids.length; b++) {
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + DSAUtil.dsaOids[b], "DSA");
        registerOid(param1ConfigurableProvider, DSAUtil.dsaOids[b], "DSA", (AsymmetricKeyInfoConverter)keyFactorySpi);
        registerOidAlgorithmParameterGenerator(param1ConfigurableProvider, DSAUtil.dsaOids[b], "DSA");
      } 
    }
  }
}
