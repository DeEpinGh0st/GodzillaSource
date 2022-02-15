package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;

public class SM2Signer implements DSA, ECConstants {
  private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
  
  private byte[] userID;
  
  private int curveLength;
  
  private ECDomainParameters ecParams;
  
  private ECPoint pubPoint;
  
  private ECKeyParameters ecKey;
  
  private SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    CipherParameters cipherParameters;
    if (paramCipherParameters instanceof ParametersWithID) {
      cipherParameters = ((ParametersWithID)paramCipherParameters).getParameters();
      this.userID = ((ParametersWithID)paramCipherParameters).getID();
    } else {
      cipherParameters = paramCipherParameters;
      this.userID = new byte[0];
    } 
    if (paramBoolean) {
      if (cipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
        this.ecKey = (ECKeyParameters)parametersWithRandom.getParameters();
        this.ecParams = this.ecKey.getParameters();
        this.kCalculator.init(this.ecParams.getN(), parametersWithRandom.getRandom());
      } else {
        this.ecKey = (ECKeyParameters)cipherParameters;
        this.ecParams = this.ecKey.getParameters();
        this.kCalculator.init(this.ecParams.getN(), new SecureRandom());
      } 
      this.pubPoint = this.ecParams.getG().multiply(((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
    } else {
      this.ecKey = (ECKeyParameters)cipherParameters;
      this.ecParams = this.ecKey.getParameters();
      this.pubPoint = ((ECPublicKeyParameters)this.ecKey).getQ();
    } 
    this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    SM3Digest sM3Digest = new SM3Digest();
    byte[] arrayOfByte1 = getZ((Digest)sM3Digest);
    sM3Digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    sM3Digest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte2 = new byte[sM3Digest.getDigestSize()];
    sM3Digest.doFinal(arrayOfByte2, 0);
    BigInteger bigInteger1 = this.ecParams.getN();
    BigInteger bigInteger2 = calculateE(arrayOfByte2);
    BigInteger bigInteger3 = ((ECPrivateKeyParameters)this.ecKey).getD();
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    while (true) {
      BigInteger bigInteger5 = this.kCalculator.nextK();
      ECPoint eCPoint = eCMultiplier.multiply(this.ecParams.getG(), bigInteger5).normalize();
      BigInteger bigInteger4 = bigInteger2.add(eCPoint.getAffineXCoord().toBigInteger()).mod(bigInteger1);
      if (!bigInteger4.equals(ZERO) && !bigInteger4.add(bigInteger5).equals(bigInteger1)) {
        BigInteger bigInteger7 = bigInteger3.add(ONE).modInverse(bigInteger1);
        BigInteger bigInteger6 = bigInteger5.subtract(bigInteger4.multiply(bigInteger3)).mod(bigInteger1);
        bigInteger6 = bigInteger7.multiply(bigInteger6).mod(bigInteger1);
        if (!bigInteger6.equals(ZERO))
          return new BigInteger[] { bigInteger4, bigInteger6 }; 
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = this.ecParams.getN();
    if (paramBigInteger1.compareTo(ONE) < 0 || paramBigInteger1.compareTo(bigInteger1) >= 0)
      return false; 
    if (paramBigInteger2.compareTo(ONE) < 0 || paramBigInteger2.compareTo(bigInteger1) >= 0)
      return false; 
    ECPoint eCPoint1 = ((ECPublicKeyParameters)this.ecKey).getQ();
    SM3Digest sM3Digest = new SM3Digest();
    byte[] arrayOfByte1 = getZ((Digest)sM3Digest);
    sM3Digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    sM3Digest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte2 = new byte[sM3Digest.getDigestSize()];
    sM3Digest.doFinal(arrayOfByte2, 0);
    BigInteger bigInteger2 = calculateE(arrayOfByte2);
    BigInteger bigInteger3 = paramBigInteger1.add(paramBigInteger2).mod(bigInteger1);
    if (bigInteger3.equals(ZERO))
      return false; 
    ECPoint eCPoint2 = this.ecParams.getG().multiply(paramBigInteger2);
    eCPoint2 = eCPoint2.add(eCPoint1.multiply(bigInteger3)).normalize();
    return paramBigInteger1.equals(bigInteger2.add(eCPoint2.getAffineXCoord().toBigInteger()).mod(bigInteger1));
  }
  
  private byte[] getZ(Digest paramDigest) {
    addUserID(paramDigest, this.userID);
    addFieldElement(paramDigest, this.ecParams.getCurve().getA());
    addFieldElement(paramDigest, this.ecParams.getCurve().getB());
    addFieldElement(paramDigest, this.ecParams.getG().getAffineXCoord());
    addFieldElement(paramDigest, this.ecParams.getG().getAffineYCoord());
    addFieldElement(paramDigest, this.pubPoint.getAffineXCoord());
    addFieldElement(paramDigest, this.pubPoint.getAffineYCoord());
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private void addUserID(Digest paramDigest, byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length * 8;
    paramDigest.update((byte)(i >> 8 & 0xFF));
    paramDigest.update((byte)(i & 0xFF));
    paramDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  private void addFieldElement(Digest paramDigest, ECFieldElement paramECFieldElement) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(this.curveLength, paramECFieldElement.toBigInteger());
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
  
  protected BigInteger calculateE(byte[] paramArrayOfbyte) {
    return new BigInteger(1, paramArrayOfbyte);
  }
}
