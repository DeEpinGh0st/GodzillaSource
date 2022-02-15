package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.CramerShoupKeyGenerationParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private CramerShoupKeyGenerationParameters param;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.param = (CramerShoupKeyGenerationParameters)paramKeyGenerationParameters;
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    CramerShoupParameters cramerShoupParameters = this.param.getParameters();
    CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters = generatePrivateKey(this.param.getRandom(), cramerShoupParameters);
    CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = calculatePublicKey(cramerShoupParameters, cramerShoupPrivateKeyParameters);
    cramerShoupPrivateKeyParameters.setPk(cramerShoupPublicKeyParameters);
    return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)cramerShoupPublicKeyParameters, (AsymmetricKeyParameter)cramerShoupPrivateKeyParameters);
  }
  
  private BigInteger generateRandomElement(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    return BigIntegers.createRandomInRange(ONE, paramBigInteger.subtract(ONE), paramSecureRandom);
  }
  
  private CramerShoupPrivateKeyParameters generatePrivateKey(SecureRandom paramSecureRandom, CramerShoupParameters paramCramerShoupParameters) {
    BigInteger bigInteger = paramCramerShoupParameters.getP();
    return new CramerShoupPrivateKeyParameters(paramCramerShoupParameters, generateRandomElement(bigInteger, paramSecureRandom), generateRandomElement(bigInteger, paramSecureRandom), generateRandomElement(bigInteger, paramSecureRandom), generateRandomElement(bigInteger, paramSecureRandom), generateRandomElement(bigInteger, paramSecureRandom));
  }
  
  private CramerShoupPublicKeyParameters calculatePublicKey(CramerShoupParameters paramCramerShoupParameters, CramerShoupPrivateKeyParameters paramCramerShoupPrivateKeyParameters) {
    BigInteger bigInteger1 = paramCramerShoupParameters.getG1();
    BigInteger bigInteger2 = paramCramerShoupParameters.getG2();
    BigInteger bigInteger3 = paramCramerShoupParameters.getP();
    BigInteger bigInteger4 = bigInteger1.modPow(paramCramerShoupPrivateKeyParameters.getX1(), bigInteger3).multiply(bigInteger2.modPow(paramCramerShoupPrivateKeyParameters.getX2(), bigInteger3));
    BigInteger bigInteger5 = bigInteger1.modPow(paramCramerShoupPrivateKeyParameters.getY1(), bigInteger3).multiply(bigInteger2.modPow(paramCramerShoupPrivateKeyParameters.getY2(), bigInteger3));
    BigInteger bigInteger6 = bigInteger1.modPow(paramCramerShoupPrivateKeyParameters.getZ(), bigInteger3);
    return new CramerShoupPublicKeyParameters(paramCramerShoupParameters, bigInteger4, bigInteger5, bigInteger6);
  }
}
