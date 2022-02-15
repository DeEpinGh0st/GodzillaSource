package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindedEngine implements AsymmetricBlockCipher {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private RSACoreEngine core = new RSACoreEngine();
  
  private RSAKeyParameters key;
  
  private SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.core.init(paramBoolean, paramCipherParameters);
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.key = (RSAKeyParameters)parametersWithRandom.getParameters();
      this.random = parametersWithRandom.getRandom();
    } else {
      this.key = (RSAKeyParameters)paramCipherParameters;
      this.random = new SecureRandom();
    } 
  }
  
  public int getInputBlockSize() {
    return this.core.getInputBlockSize();
  }
  
  public int getOutputBlockSize() {
    return this.core.getOutputBlockSize();
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    BigInteger bigInteger2;
    if (this.key == null)
      throw new IllegalStateException("RSA engine not initialised"); 
    BigInteger bigInteger1 = this.core.convertInput(paramArrayOfbyte, paramInt1, paramInt2);
    if (this.key instanceof RSAPrivateCrtKeyParameters) {
      RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)this.key;
      BigInteger bigInteger = rSAPrivateCrtKeyParameters.getPublicExponent();
      if (bigInteger != null) {
        BigInteger bigInteger3 = rSAPrivateCrtKeyParameters.getModulus();
        BigInteger bigInteger4 = BigIntegers.createRandomInRange(ONE, bigInteger3.subtract(ONE), this.random);
        BigInteger bigInteger5 = bigInteger4.modPow(bigInteger, bigInteger3).multiply(bigInteger1).mod(bigInteger3);
        BigInteger bigInteger6 = this.core.processBlock(bigInteger5);
        BigInteger bigInteger7 = bigInteger4.modInverse(bigInteger3);
        bigInteger2 = bigInteger6.multiply(bigInteger7).mod(bigInteger3);
        if (!bigInteger1.equals(bigInteger2.modPow(bigInteger, bigInteger3)))
          throw new IllegalStateException("RSA engine faulty decryption/signing detected"); 
      } else {
        bigInteger2 = this.core.processBlock(bigInteger1);
      } 
    } else {
      bigInteger2 = this.core.processBlock(bigInteger1);
    } 
    return this.core.convertOutput(bigInteger2);
  }
}
