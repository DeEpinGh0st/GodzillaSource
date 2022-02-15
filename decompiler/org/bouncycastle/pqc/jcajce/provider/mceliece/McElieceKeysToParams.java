package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;

public class McElieceKeysToParams {
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCMcEliecePublicKey) {
      BCMcEliecePublicKey bCMcEliecePublicKey = (BCMcEliecePublicKey)paramPublicKey;
      return bCMcEliecePublicKey.getKeyParams();
    } 
    throw new InvalidKeyException("can't identify McEliece public key: " + paramPublicKey.getClass().getName());
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCMcEliecePrivateKey) {
      BCMcEliecePrivateKey bCMcEliecePrivateKey = (BCMcEliecePrivateKey)paramPrivateKey;
      return (AsymmetricKeyParameter)new McEliecePrivateKeyParameters(bCMcEliecePrivateKey.getN(), bCMcEliecePrivateKey.getK(), bCMcEliecePrivateKey.getField(), bCMcEliecePrivateKey.getGoppaPoly(), bCMcEliecePrivateKey.getP1(), bCMcEliecePrivateKey.getP2(), bCMcEliecePrivateKey.getSInv());
    } 
    throw new InvalidKeyException("can't identify McEliece private key.");
  }
}
