package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class McElieceCCA2KeysToParams {
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCMcElieceCCA2PublicKey) {
      BCMcElieceCCA2PublicKey bCMcElieceCCA2PublicKey = (BCMcElieceCCA2PublicKey)paramPublicKey;
      return bCMcElieceCCA2PublicKey.getKeyParams();
    } 
    throw new InvalidKeyException("can't identify McElieceCCA2 public key: " + paramPublicKey.getClass().getName());
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCMcElieceCCA2PrivateKey) {
      BCMcElieceCCA2PrivateKey bCMcElieceCCA2PrivateKey = (BCMcElieceCCA2PrivateKey)paramPrivateKey;
      return bCMcElieceCCA2PrivateKey.getKeyParams();
    } 
    throw new InvalidKeyException("can't identify McElieceCCA2 private key.");
  }
}
