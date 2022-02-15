package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;

public class ECNRSigner implements DSA {
  private boolean forSigning;
  
  private ECKeyParameters key;
  
  private SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forSigning = paramBoolean;
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
    if (!this.forSigning)
      throw new IllegalStateException("not initialised for signing"); 
    BigInteger bigInteger1 = ((ECPrivateKeyParameters)this.key).getParameters().getN();
    int i = bigInteger1.bitLength();
    BigInteger bigInteger2 = new BigInteger(1, paramArrayOfbyte);
    int j = bigInteger2.bitLength();
    ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
    if (j > i)
      throw new DataLengthException("input too large for ECNR key."); 
    BigInteger bigInteger3 = null;
    BigInteger bigInteger4 = null;
    while (true) {
      ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
      eCKeyPairGenerator.init((KeyGenerationParameters)new ECKeyGenerationParameters(eCPrivateKeyParameters.getParameters(), this.random));
      AsymmetricCipherKeyPair asymmetricCipherKeyPair = eCKeyPairGenerator.generateKeyPair();
      ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
      BigInteger bigInteger = eCPublicKeyParameters.getQ().getAffineXCoord().toBigInteger();
      bigInteger3 = bigInteger.add(bigInteger2).mod(bigInteger1);
      if (!bigInteger3.equals(ECConstants.ZERO)) {
        BigInteger bigInteger5 = eCPrivateKeyParameters.getD();
        BigInteger bigInteger6 = ((ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate()).getD();
        bigInteger4 = bigInteger6.subtract(bigInteger3.multiply(bigInteger5)).mod(bigInteger1);
        BigInteger[] arrayOfBigInteger = new BigInteger[2];
        arrayOfBigInteger[0] = bigInteger3;
        arrayOfBigInteger[1] = bigInteger4;
        return arrayOfBigInteger;
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    if (this.forSigning)
      throw new IllegalStateException("not initialised for verifying"); 
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)this.key;
    BigInteger bigInteger1 = eCPublicKeyParameters.getParameters().getN();
    int i = bigInteger1.bitLength();
    BigInteger bigInteger2 = new BigInteger(1, paramArrayOfbyte);
    int j = bigInteger2.bitLength();
    if (j > i)
      throw new DataLengthException("input too large for ECNR key."); 
    if (paramBigInteger1.compareTo(ECConstants.ONE) < 0 || paramBigInteger1.compareTo(bigInteger1) >= 0)
      return false; 
    if (paramBigInteger2.compareTo(ECConstants.ZERO) < 0 || paramBigInteger2.compareTo(bigInteger1) >= 0)
      return false; 
    ECPoint eCPoint1 = eCPublicKeyParameters.getParameters().getG();
    ECPoint eCPoint2 = eCPublicKeyParameters.getQ();
    ECPoint eCPoint3 = ECAlgorithms.sumOfTwoMultiplies(eCPoint1, paramBigInteger2, eCPoint2, paramBigInteger1).normalize();
    if (eCPoint3.isInfinity())
      return false; 
    BigInteger bigInteger3 = eCPoint3.getAffineXCoord().toBigInteger();
    BigInteger bigInteger4 = paramBigInteger1.subtract(bigInteger3).mod(bigInteger1);
    return bigInteger4.equals(bigInteger2);
  }
}
