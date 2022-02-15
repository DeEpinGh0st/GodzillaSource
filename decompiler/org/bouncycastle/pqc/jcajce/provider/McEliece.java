package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;

public class McEliece {
  private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.mceliece.";
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.McElieceKobaraImai", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.McEliecePointcheval", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.McElieceFujisaki", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.McEliece", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.McEliece-CCA2", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.McElieceKobaraImai", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.McEliecePointcheval", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.McElieceFujisaki", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.McEliece", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.McEliece-CCA2", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory." + PQCObjectIdentifiers.mcElieceCca2, "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyFactory." + PQCObjectIdentifiers.mcEliece, "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("Cipher.McEliece", "org.bouncycastle.pqc.jcajce.provider.mceliece.McEliecePKCSCipherSpi$McEliecePKCS");
      param1ConfigurableProvider.addAlgorithm("Cipher.McEliecePointcheval", "org.bouncycastle.pqc.jcajce.provider.mceliece.McEliecePointchevalCipherSpi$McEliecePointcheval");
      param1ConfigurableProvider.addAlgorithm("Cipher.McElieceKobaraImai", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKobaraImaiCipherSpi$McElieceKobaraImai");
      param1ConfigurableProvider.addAlgorithm("Cipher.McElieceFujisaki", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceFujisakiCipherSpi$McElieceFujisaki");
    }
  }
}
