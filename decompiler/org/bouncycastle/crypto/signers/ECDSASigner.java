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
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECDSASigner implements ECConstants, DSA {
  private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
  
  private ECKeyParameters key;
  
  private SecureRandom random;
  
  public ECDSASigner() {}
  
  public ECDSASigner(DSAKCalculator paramDSAKCalculator) {}
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    SecureRandom secureRandom = null;
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.key = (ECKeyParameters)parametersWithRandom.getParameters();
        secureRandom = parametersWithRandom.getRandom();
      } else {
        this.key = (ECKeyParameters)paramCipherParameters;
      } 
    } else {
      this.key = (ECKeyParameters)paramCipherParameters;
    } 
    this.random = initSecureRandom((paramBoolean && !this.kCalculator.isDeterministic()), secureRandom);
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger1 = eCDomainParameters.getN();
    BigInteger bigInteger2 = calculateE(bigInteger1, paramArrayOfbyte);
    BigInteger bigInteger3 = ((ECPrivateKeyParameters)this.key).getD();
    if (this.kCalculator.isDeterministic()) {
      this.kCalculator.init(bigInteger1, bigInteger3, paramArrayOfbyte);
    } else {
      this.kCalculator.init(bigInteger1, this.random);
    } 
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    while (true) {
      BigInteger bigInteger5 = this.kCalculator.nextK();
      ECPoint eCPoint = eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger5).normalize();
      BigInteger bigInteger4 = eCPoint.getAffineXCoord().toBigInteger().mod(bigInteger1);
      if (!bigInteger4.equals(ZERO)) {
        BigInteger bigInteger = bigInteger5.modInverse(bigInteger1).multiply(bigInteger2.add(bigInteger3.multiply(bigInteger4))).mod(bigInteger1);
        if (!bigInteger.equals(ZERO))
          return new BigInteger[] { bigInteger4, bigInteger }; 
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger1 = eCDomainParameters.getN();
    BigInteger bigInteger2 = calculateE(bigInteger1, paramArrayOfbyte);
    if (paramBigInteger1.compareTo(ONE) < 0 || paramBigInteger1.compareTo(bigInteger1) >= 0)
      return false; 
    if (paramBigInteger2.compareTo(ONE) < 0 || paramBigInteger2.compareTo(bigInteger1) >= 0)
      return false; 
    BigInteger bigInteger3 = paramBigInteger2.modInverse(bigInteger1);
    BigInteger bigInteger4 = bigInteger2.multiply(bigInteger3).mod(bigInteger1);
    BigInteger bigInteger5 = paramBigInteger1.multiply(bigInteger3).mod(bigInteger1);
    ECPoint eCPoint1 = eCDomainParameters.getG();
    ECPoint eCPoint2 = ((ECPublicKeyParameters)this.key).getQ();
    ECPoint eCPoint3 = ECAlgorithms.sumOfTwoMultiplies(eCPoint1, bigInteger4, eCPoint2, bigInteger5);
    if (eCPoint3.isInfinity())
      return false; 
    ECCurve eCCurve = eCPoint3.getCurve();
    if (eCCurve != null) {
      BigInteger bigInteger = eCCurve.getCofactor();
      if (bigInteger != null && bigInteger.compareTo(EIGHT) <= 0) {
        ECFieldElement eCFieldElement = getDenominator(eCCurve.getCoordinateSystem(), eCPoint3);
        if (eCFieldElement != null && !eCFieldElement.isZero()) {
          ECFieldElement eCFieldElement1 = eCPoint3.getXCoord();
          while (eCCurve.isValidFieldElement(paramBigInteger1)) {
            ECFieldElement eCFieldElement2 = eCCurve.fromBigInteger(paramBigInteger1).multiply(eCFieldElement);
            if (eCFieldElement2.equals(eCFieldElement1))
              return true; 
            paramBigInteger1 = paramBigInteger1.add(bigInteger1);
          } 
          return false;
        } 
      } 
    } 
    BigInteger bigInteger6 = eCPoint3.normalize().getAffineXCoord().toBigInteger().mod(bigInteger1);
    return bigInteger6.equals(paramBigInteger1);
  }
  
  protected BigInteger calculateE(BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    int i = paramBigInteger.bitLength();
    int j = paramArrayOfbyte.length * 8;
    BigInteger bigInteger = new BigInteger(1, paramArrayOfbyte);
    if (i < j)
      bigInteger = bigInteger.shiftRight(j - i); 
    return bigInteger;
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
  
  protected ECFieldElement getDenominator(int paramInt, ECPoint paramECPoint) {
    switch (paramInt) {
      case 1:
      case 6:
      case 7:
        return paramECPoint.getZCoord(0);
      case 2:
      case 3:
      case 4:
        return paramECPoint.getZCoord(0).square();
    } 
    return null;
  }
  
  protected SecureRandom initSecureRandom(boolean paramBoolean, SecureRandom paramSecureRandom) {
    return !paramBoolean ? null : ((paramSecureRandom != null) ? paramSecureRandom : new SecureRandom());
  }
}
