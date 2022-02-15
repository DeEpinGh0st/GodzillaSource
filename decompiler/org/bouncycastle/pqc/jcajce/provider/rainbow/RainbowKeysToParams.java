package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;

public class RainbowKeysToParams {
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCRainbowPublicKey) {
      BCRainbowPublicKey bCRainbowPublicKey = (BCRainbowPublicKey)paramPublicKey;
      return (AsymmetricKeyParameter)new RainbowPublicKeyParameters(bCRainbowPublicKey.getDocLength(), bCRainbowPublicKey.getCoeffQuadratic(), bCRainbowPublicKey.getCoeffSingular(), bCRainbowPublicKey.getCoeffScalar());
    } 
    throw new InvalidKeyException("can't identify Rainbow public key: " + paramPublicKey.getClass().getName());
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCRainbowPrivateKey) {
      BCRainbowPrivateKey bCRainbowPrivateKey = (BCRainbowPrivateKey)paramPrivateKey;
      return (AsymmetricKeyParameter)new RainbowPrivateKeyParameters(bCRainbowPrivateKey.getInvA1(), bCRainbowPrivateKey.getB1(), bCRainbowPrivateKey.getInvA2(), bCRainbowPrivateKey.getB2(), bCRainbowPrivateKey.getVi(), bCRainbowPrivateKey.getLayers());
    } 
    throw new InvalidKeyException("can't identify Rainbow private key.");
  }
}
