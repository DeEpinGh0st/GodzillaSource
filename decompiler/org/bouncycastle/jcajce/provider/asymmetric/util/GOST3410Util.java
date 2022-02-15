package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class GOST3410Util {
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof GOST3410PublicKey) {
      GOST3410PublicKey gOST3410PublicKey = (GOST3410PublicKey)paramPublicKey;
      GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec = gOST3410PublicKey.getParameters().getPublicKeyParameters();
      return (AsymmetricKeyParameter)new GOST3410PublicKeyParameters(gOST3410PublicKey.getY(), new GOST3410Parameters(gOST3410PublicKeyParameterSetSpec.getP(), gOST3410PublicKeyParameterSetSpec.getQ(), gOST3410PublicKeyParameterSetSpec.getA()));
    } 
    throw new InvalidKeyException("can't identify GOST3410 public key: " + paramPublicKey.getClass().getName());
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof GOST3410PrivateKey) {
      GOST3410PrivateKey gOST3410PrivateKey = (GOST3410PrivateKey)paramPrivateKey;
      GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec = gOST3410PrivateKey.getParameters().getPublicKeyParameters();
      return (AsymmetricKeyParameter)new GOST3410PrivateKeyParameters(gOST3410PrivateKey.getX(), new GOST3410Parameters(gOST3410PublicKeyParameterSetSpec.getP(), gOST3410PublicKeyParameterSetSpec.getQ(), gOST3410PublicKeyParameterSetSpec.getA()));
    } 
    throw new InvalidKeyException("can't identify GOST3410 private key.");
  }
}
