package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;

public class ElGamalUtil {
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof ElGamalPublicKey) {
      ElGamalPublicKey elGamalPublicKey = (ElGamalPublicKey)paramPublicKey;
      return (AsymmetricKeyParameter)new ElGamalPublicKeyParameters(elGamalPublicKey.getY(), new ElGamalParameters(elGamalPublicKey.getParameters().getP(), elGamalPublicKey.getParameters().getG()));
    } 
    if (paramPublicKey instanceof DHPublicKey) {
      DHPublicKey dHPublicKey = (DHPublicKey)paramPublicKey;
      return (AsymmetricKeyParameter)new ElGamalPublicKeyParameters(dHPublicKey.getY(), new ElGamalParameters(dHPublicKey.getParams().getP(), dHPublicKey.getParams().getG()));
    } 
    throw new InvalidKeyException("can't identify public key for El Gamal.");
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof ElGamalPrivateKey) {
      ElGamalPrivateKey elGamalPrivateKey = (ElGamalPrivateKey)paramPrivateKey;
      return (AsymmetricKeyParameter)new ElGamalPrivateKeyParameters(elGamalPrivateKey.getX(), new ElGamalParameters(elGamalPrivateKey.getParameters().getP(), elGamalPrivateKey.getParameters().getG()));
    } 
    if (paramPrivateKey instanceof DHPrivateKey) {
      DHPrivateKey dHPrivateKey = (DHPrivateKey)paramPrivateKey;
      return (AsymmetricKeyParameter)new ElGamalPrivateKeyParameters(dHPrivateKey.getX(), new ElGamalParameters(dHPrivateKey.getParams().getP(), dHPrivateKey.getParams().getG()));
    } 
    throw new InvalidKeyException("can't identify private key for El Gamal.");
  }
}
