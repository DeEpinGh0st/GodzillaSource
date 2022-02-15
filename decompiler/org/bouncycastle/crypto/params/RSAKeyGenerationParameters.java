package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class RSAKeyGenerationParameters extends KeyGenerationParameters {
  private BigInteger publicExponent;
  
  private int certainty;
  
  public RSAKeyGenerationParameters(BigInteger paramBigInteger, SecureRandom paramSecureRandom, int paramInt1, int paramInt2) {
    super(paramSecureRandom, paramInt1);
    if (paramInt1 < 12)
      throw new IllegalArgumentException("key strength too small"); 
    if (!paramBigInteger.testBit(0))
      throw new IllegalArgumentException("public exponent cannot be even"); 
    this.publicExponent = paramBigInteger;
    this.certainty = paramInt2;
  }
  
  public BigInteger getPublicExponent() {
    return this.publicExponent;
  }
  
  public int getCertainty() {
    return this.certainty;
  }
}
