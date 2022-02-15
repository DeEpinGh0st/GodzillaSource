package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class RSABlindingEngine implements AsymmetricBlockCipher {
  private RSACoreEngine core = new RSACoreEngine();
  
  private RSAKeyParameters key;
  
  private BigInteger blindingFactor;
  
  private boolean forEncryption;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    RSABlindingParameters rSABlindingParameters;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      rSABlindingParameters = (RSABlindingParameters)parametersWithRandom.getParameters();
    } else {
      rSABlindingParameters = (RSABlindingParameters)paramCipherParameters;
    } 
    this.core.init(paramBoolean, (CipherParameters)rSABlindingParameters.getPublicKey());
    this.forEncryption = paramBoolean;
    this.key = rSABlindingParameters.getPublicKey();
    this.blindingFactor = rSABlindingParameters.getBlindingFactor();
  }
  
  public int getInputBlockSize() {
    return this.core.getInputBlockSize();
  }
  
  public int getOutputBlockSize() {
    return this.core.getOutputBlockSize();
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    BigInteger bigInteger = this.core.convertInput(paramArrayOfbyte, paramInt1, paramInt2);
    if (this.forEncryption) {
      bigInteger = blindMessage(bigInteger);
    } else {
      bigInteger = unblindMessage(bigInteger);
    } 
    return this.core.convertOutput(bigInteger);
  }
  
  private BigInteger blindMessage(BigInteger paramBigInteger) {
    null = this.blindingFactor;
    null = paramBigInteger.multiply(null.modPow(this.key.getExponent(), this.key.getModulus()));
    return null.mod(this.key.getModulus());
  }
  
  private BigInteger unblindMessage(BigInteger paramBigInteger) {
    BigInteger bigInteger1 = this.key.getModulus();
    null = paramBigInteger;
    BigInteger bigInteger2 = this.blindingFactor.modInverse(bigInteger1);
    null = null.multiply(bigInteger2);
    return null.mod(bigInteger1);
  }
}
