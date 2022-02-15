package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class RSABlindingParameters implements CipherParameters {
  private RSAKeyParameters publicKey;
  
  private BigInteger blindingFactor;
  
  public RSABlindingParameters(RSAKeyParameters paramRSAKeyParameters, BigInteger paramBigInteger) {
    if (paramRSAKeyParameters instanceof RSAPrivateCrtKeyParameters)
      throw new IllegalArgumentException("RSA parameters should be for a public key"); 
    this.publicKey = paramRSAKeyParameters;
    this.blindingFactor = paramBigInteger;
  }
  
  public RSAKeyParameters getPublicKey() {
    return this.publicKey;
  }
  
  public BigInteger getBlindingFactor() {
    return this.blindingFactor;
  }
}
