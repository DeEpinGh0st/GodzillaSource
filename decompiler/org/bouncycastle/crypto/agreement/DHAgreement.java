package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DHAgreement {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private DHPrivateKeyParameters key;
  
  private DHParameters dhParams;
  
  private BigInteger privateValue;
  
  private SecureRandom random;
  
  public void init(CipherParameters paramCipherParameters) {
    AsymmetricKeyParameter asymmetricKeyParameter;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.random = parametersWithRandom.getRandom();
      asymmetricKeyParameter = (AsymmetricKeyParameter)parametersWithRandom.getParameters();
    } else {
      this.random = new SecureRandom();
      asymmetricKeyParameter = (AsymmetricKeyParameter)paramCipherParameters;
    } 
    if (!(asymmetricKeyParameter instanceof DHPrivateKeyParameters))
      throw new IllegalArgumentException("DHEngine expects DHPrivateKeyParameters"); 
    this.key = (DHPrivateKeyParameters)asymmetricKeyParameter;
    this.dhParams = this.key.getParameters();
  }
  
  public BigInteger calculateMessage() {
    DHKeyPairGenerator dHKeyPairGenerator = new DHKeyPairGenerator();
    dHKeyPairGenerator.init((KeyGenerationParameters)new DHKeyGenerationParameters(this.random, this.dhParams));
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = dHKeyPairGenerator.generateKeyPair();
    this.privateValue = ((DHPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate()).getX();
    return ((DHPublicKeyParameters)asymmetricCipherKeyPair.getPublic()).getY();
  }
  
  public BigInteger calculateAgreement(DHPublicKeyParameters paramDHPublicKeyParameters, BigInteger paramBigInteger) {
    if (!paramDHPublicKeyParameters.getParameters().equals(this.dhParams))
      throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters."); 
    BigInteger bigInteger1 = this.dhParams.getP();
    BigInteger bigInteger2 = paramDHPublicKeyParameters.getY();
    if (bigInteger2 == null || bigInteger2.compareTo(ONE) <= 0 || bigInteger2.compareTo(bigInteger1.subtract(ONE)) >= 0)
      throw new IllegalArgumentException("Diffie-Hellman public key is weak"); 
    BigInteger bigInteger3 = bigInteger2.modPow(this.privateValue, bigInteger1);
    if (bigInteger3.equals(ONE))
      throw new IllegalStateException("Shared key can't be 1"); 
    return paramBigInteger.modPow(this.key.getX(), bigInteger1).multiply(bigInteger3).mod(bigInteger1);
  }
}
