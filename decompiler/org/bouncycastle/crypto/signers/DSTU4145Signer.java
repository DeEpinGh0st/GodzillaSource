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
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;

public class DSTU4145Signer implements DSA {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private ECKeyParameters key;
  
  private SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.random = parametersWithRandom.getRandom();
        paramCipherParameters = parametersWithRandom.getParameters();
      } else {
        this.random = new SecureRandom();
      } 
      this.key = (ECKeyParameters)paramCipherParameters;
    } else {
      this.key = (ECKeyParameters)paramCipherParameters;
    } 
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    ECCurve eCCurve = eCDomainParameters.getCurve();
    ECFieldElement eCFieldElement = hash2FieldElement(eCCurve, paramArrayOfbyte);
    if (eCFieldElement.isZero())
      eCFieldElement = eCCurve.fromBigInteger(ONE); 
    BigInteger bigInteger1 = eCDomainParameters.getN();
    BigInteger bigInteger2 = ((ECPrivateKeyParameters)this.key).getD();
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    while (true) {
      BigInteger bigInteger = generateRandomInteger(bigInteger1, this.random);
      ECFieldElement eCFieldElement1 = eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger).normalize().getAffineXCoord();
      if (!eCFieldElement1.isZero()) {
        ECFieldElement eCFieldElement2 = eCFieldElement.multiply(eCFieldElement1);
        BigInteger bigInteger3 = fieldElement2Integer(bigInteger1, eCFieldElement2);
        if (bigInteger3.signum() != 0) {
          BigInteger bigInteger4 = bigInteger3.multiply(bigInteger2).add(bigInteger).mod(bigInteger1);
          if (bigInteger4.signum() != 0)
            return new BigInteger[] { bigInteger3, bigInteger4 }; 
        } 
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    if (paramBigInteger1.signum() <= 0 || paramBigInteger2.signum() <= 0)
      return false; 
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger = eCDomainParameters.getN();
    if (paramBigInteger1.compareTo(bigInteger) >= 0 || paramBigInteger2.compareTo(bigInteger) >= 0)
      return false; 
    ECCurve eCCurve = eCDomainParameters.getCurve();
    ECFieldElement eCFieldElement1 = hash2FieldElement(eCCurve, paramArrayOfbyte);
    if (eCFieldElement1.isZero())
      eCFieldElement1 = eCCurve.fromBigInteger(ONE); 
    ECPoint eCPoint = ECAlgorithms.sumOfTwoMultiplies(eCDomainParameters.getG(), paramBigInteger2, ((ECPublicKeyParameters)this.key).getQ(), paramBigInteger1).normalize();
    if (eCPoint.isInfinity())
      return false; 
    ECFieldElement eCFieldElement2 = eCFieldElement1.multiply(eCPoint.getAffineXCoord());
    return (fieldElement2Integer(bigInteger, eCFieldElement2).compareTo(paramBigInteger1) == 0);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
  
  private static BigInteger generateRandomInteger(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    return new BigInteger(paramBigInteger.bitLength() - 1, paramSecureRandom);
  }
  
  private static ECFieldElement hash2FieldElement(ECCurve paramECCurve, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = Arrays.reverse(paramArrayOfbyte);
    return paramECCurve.fromBigInteger(truncate(new BigInteger(1, arrayOfByte), paramECCurve.getFieldSize()));
  }
  
  private static BigInteger fieldElement2Integer(BigInteger paramBigInteger, ECFieldElement paramECFieldElement) {
    return truncate(paramECFieldElement.toBigInteger(), paramBigInteger.bitLength() - 1);
  }
  
  private static BigInteger truncate(BigInteger paramBigInteger, int paramInt) {
    if (paramBigInteger.bitLength() > paramInt)
      paramBigInteger = paramBigInteger.mod(ONE.shiftLeft(paramInt)); 
    return paramBigInteger;
  }
}
