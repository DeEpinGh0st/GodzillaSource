package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DSASigner implements DSA {
  private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
  
  private DSAKeyParameters key;
  
  private SecureRandom random;
  
  public DSASigner() {}
  
  public DSASigner(DSAKCalculator paramDSAKCalculator) {}
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    SecureRandom secureRandom = null;
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.key = (DSAKeyParameters)parametersWithRandom.getParameters();
        secureRandom = parametersWithRandom.getRandom();
      } else {
        this.key = (DSAKeyParameters)paramCipherParameters;
      } 
    } else {
      this.key = (DSAKeyParameters)paramCipherParameters;
    } 
    this.random = initSecureRandom((paramBoolean && !this.kCalculator.isDeterministic()), secureRandom);
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    DSAParameters dSAParameters = this.key.getParameters();
    BigInteger bigInteger1 = dSAParameters.getQ();
    BigInteger bigInteger2 = calculateE(bigInteger1, paramArrayOfbyte);
    BigInteger bigInteger3 = ((DSAPrivateKeyParameters)this.key).getX();
    if (this.kCalculator.isDeterministic()) {
      this.kCalculator.init(bigInteger1, bigInteger3, paramArrayOfbyte);
    } else {
      this.kCalculator.init(bigInteger1, this.random);
    } 
    BigInteger bigInteger4 = this.kCalculator.nextK();
    BigInteger bigInteger5 = dSAParameters.getG().modPow(bigInteger4.add(getRandomizer(bigInteger1, this.random)), dSAParameters.getP()).mod(bigInteger1);
    bigInteger4 = bigInteger4.modInverse(bigInteger1).multiply(bigInteger2.add(bigInteger3.multiply(bigInteger5)));
    BigInteger bigInteger6 = bigInteger4.mod(bigInteger1);
    return new BigInteger[] { bigInteger5, bigInteger6 };
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    DSAParameters dSAParameters = this.key.getParameters();
    BigInteger bigInteger1 = dSAParameters.getQ();
    BigInteger bigInteger2 = calculateE(bigInteger1, paramArrayOfbyte);
    BigInteger bigInteger3 = BigInteger.valueOf(0L);
    if (bigInteger3.compareTo(paramBigInteger1) >= 0 || bigInteger1.compareTo(paramBigInteger1) <= 0)
      return false; 
    if (bigInteger3.compareTo(paramBigInteger2) >= 0 || bigInteger1.compareTo(paramBigInteger2) <= 0)
      return false; 
    BigInteger bigInteger4 = paramBigInteger2.modInverse(bigInteger1);
    BigInteger bigInteger5 = bigInteger2.multiply(bigInteger4).mod(bigInteger1);
    BigInteger bigInteger6 = paramBigInteger1.multiply(bigInteger4).mod(bigInteger1);
    BigInteger bigInteger7 = dSAParameters.getP();
    bigInteger5 = dSAParameters.getG().modPow(bigInteger5, bigInteger7);
    bigInteger6 = ((DSAPublicKeyParameters)this.key).getY().modPow(bigInteger6, bigInteger7);
    BigInteger bigInteger8 = bigInteger5.multiply(bigInteger6).mod(bigInteger7).mod(bigInteger1);
    return bigInteger8.equals(paramBigInteger1);
  }
  
  private BigInteger calculateE(BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    if (paramBigInteger.bitLength() >= paramArrayOfbyte.length * 8)
      return new BigInteger(1, paramArrayOfbyte); 
    byte[] arrayOfByte = new byte[paramBigInteger.bitLength() / 8];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, arrayOfByte.length);
    return new BigInteger(1, arrayOfByte);
  }
  
  protected SecureRandom initSecureRandom(boolean paramBoolean, SecureRandom paramSecureRandom) {
    return !paramBoolean ? null : ((paramSecureRandom != null) ? paramSecureRandom : new SecureRandom());
  }
  
  private BigInteger getRandomizer(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    byte b = 7;
    return (new BigInteger(b, (paramSecureRandom != null) ? paramSecureRandom : new SecureRandom())).add(BigInteger.valueOf(128L)).multiply(paramBigInteger);
  }
}
