package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

public class DSTU4145KeyPairGenerator extends ECKeyPairGenerator {
  public AsymmetricCipherKeyPair generateKeyPair() {
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = super.generateKeyPair();
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    eCPublicKeyParameters = new ECPublicKeyParameters(eCPublicKeyParameters.getQ().negate(), eCPublicKeyParameters.getParameters());
    return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)eCPublicKeyParameters, (AsymmetricKeyParameter)eCPrivateKeyParameters);
  }
}
