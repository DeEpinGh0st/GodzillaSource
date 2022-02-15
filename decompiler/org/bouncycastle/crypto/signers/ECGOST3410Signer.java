package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECGOST3410Signer implements DSA {
  ECKeyParameters key;
  
  SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.random = parametersWithRandom.getRandom();
        this.key = (ECKeyParameters)parametersWithRandom.getParameters();
      } else {
        this.random = new SecureRandom();
        this.key = (ECKeyParameters)paramCipherParameters;
      } 
    } else {
      this.key = (ECKeyParameters)paramCipherParameters;
    } 
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[arrayOfByte.length - 1 - b]; 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger2 = eCDomainParameters.getN();
    BigInteger bigInteger3 = ((ECPrivateKeyParameters)this.key).getD();
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    while (true) {
      BigInteger bigInteger = new BigInteger(bigInteger2.bitLength(), this.random);
      if (!bigInteger.equals(ECConstants.ZERO)) {
        ECPoint eCPoint = eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger).normalize();
        BigInteger bigInteger4 = eCPoint.getAffineXCoord().toBigInteger().mod(bigInteger2);
        if (!bigInteger4.equals(ECConstants.ZERO)) {
          BigInteger bigInteger5 = bigInteger.multiply(bigInteger1).add(bigInteger3.multiply(bigInteger4)).mod(bigInteger2);
          if (!bigInteger5.equals(ECConstants.ZERO))
            return new BigInteger[] { bigInteger4, bigInteger5 }; 
        } 
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[arrayOfByte.length - 1 - b]; 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = this.key.getParameters().getN();
    if (paramBigInteger1.compareTo(ECConstants.ONE) < 0 || paramBigInteger1.compareTo(bigInteger2) >= 0)
      return false; 
    if (paramBigInteger2.compareTo(ECConstants.ONE) < 0 || paramBigInteger2.compareTo(bigInteger2) >= 0)
      return false; 
    BigInteger bigInteger3 = bigInteger1.modInverse(bigInteger2);
    BigInteger bigInteger4 = paramBigInteger2.multiply(bigInteger3).mod(bigInteger2);
    BigInteger bigInteger5 = bigInteger2.subtract(paramBigInteger1).multiply(bigInteger3).mod(bigInteger2);
    ECPoint eCPoint1 = this.key.getParameters().getG();
    ECPoint eCPoint2 = ((ECPublicKeyParameters)this.key).getQ();
    ECPoint eCPoint3 = ECAlgorithms.sumOfTwoMultiplies(eCPoint1, bigInteger4, eCPoint2, bigInteger5).normalize();
    if (eCPoint3.isInfinity())
      return false; 
    BigInteger bigInteger6 = eCPoint3.getAffineXCoord().toBigInteger().mod(bigInteger2);
    return bigInteger6.equals(paramBigInteger1);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
}
