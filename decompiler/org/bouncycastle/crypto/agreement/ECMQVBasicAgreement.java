package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Properties;

public class ECMQVBasicAgreement implements BasicAgreement {
  MQVPrivateParameters privParams;
  
  public void init(CipherParameters paramCipherParameters) {
    this.privParams = (MQVPrivateParameters)paramCipherParameters;
  }
  
  public int getFieldSize() {
    return (this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
  }
  
  public BigInteger calculateAgreement(CipherParameters paramCipherParameters) {
    if (Properties.isOverrideSet("org.bouncycastle.ec.disable_mqv"))
      throw new IllegalStateException("ECMQV explicitly disabled"); 
    MQVPublicParameters mQVPublicParameters = (MQVPublicParameters)paramCipherParameters;
    ECPrivateKeyParameters eCPrivateKeyParameters = this.privParams.getStaticPrivateKey();
    ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
    if (!eCDomainParameters.equals(mQVPublicParameters.getStaticPublicKey().getParameters()))
      throw new IllegalStateException("ECMQV public key components have wrong domain parameters"); 
    ECPoint eCPoint = calculateMqvAgreement(eCDomainParameters, eCPrivateKeyParameters, this.privParams.getEphemeralPrivateKey(), this.privParams.getEphemeralPublicKey(), mQVPublicParameters.getStaticPublicKey(), mQVPublicParameters.getEphemeralPublicKey()).normalize();
    if (eCPoint.isInfinity())
      throw new IllegalStateException("Infinity is not a valid agreement value for MQV"); 
    return eCPoint.getAffineXCoord().toBigInteger();
  }
  
  private ECPoint calculateMqvAgreement(ECDomainParameters paramECDomainParameters, ECPrivateKeyParameters paramECPrivateKeyParameters1, ECPrivateKeyParameters paramECPrivateKeyParameters2, ECPublicKeyParameters paramECPublicKeyParameters1, ECPublicKeyParameters paramECPublicKeyParameters2, ECPublicKeyParameters paramECPublicKeyParameters3) {
    BigInteger bigInteger1 = paramECDomainParameters.getN();
    int i = (bigInteger1.bitLength() + 1) / 2;
    BigInteger bigInteger2 = ECConstants.ONE.shiftLeft(i);
    ECCurve eCCurve = paramECDomainParameters.getCurve();
    ECPoint[] arrayOfECPoint = { ECAlgorithms.importPoint(eCCurve, paramECPublicKeyParameters1.getQ()), ECAlgorithms.importPoint(eCCurve, paramECPublicKeyParameters2.getQ()), ECAlgorithms.importPoint(eCCurve, paramECPublicKeyParameters3.getQ()) };
    eCCurve.normalizeAll(arrayOfECPoint);
    ECPoint eCPoint1 = arrayOfECPoint[0];
    ECPoint eCPoint2 = arrayOfECPoint[1];
    ECPoint eCPoint3 = arrayOfECPoint[2];
    BigInteger bigInteger3 = eCPoint1.getAffineXCoord().toBigInteger();
    BigInteger bigInteger4 = bigInteger3.mod(bigInteger2);
    BigInteger bigInteger5 = bigInteger4.setBit(i);
    BigInteger bigInteger6 = paramECPrivateKeyParameters1.getD().multiply(bigInteger5).add(paramECPrivateKeyParameters2.getD()).mod(bigInteger1);
    BigInteger bigInteger7 = eCPoint3.getAffineXCoord().toBigInteger();
    BigInteger bigInteger8 = bigInteger7.mod(bigInteger2);
    BigInteger bigInteger9 = bigInteger8.setBit(i);
    BigInteger bigInteger10 = paramECDomainParameters.getH().multiply(bigInteger6).mod(bigInteger1);
    return ECAlgorithms.sumOfTwoMultiplies(eCPoint2, bigInteger9.multiply(bigInteger10).mod(bigInteger1), eCPoint3, bigInteger10);
  }
}
