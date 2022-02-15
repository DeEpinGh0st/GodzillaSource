package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECVKOAgreement {
  private final Digest digest;
  
  private ECPrivateKeyParameters key;
  
  private BigInteger ukm;
  
  public ECVKOAgreement(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(CipherParameters paramCipherParameters) {
    ParametersWithUKM parametersWithUKM = (ParametersWithUKM)paramCipherParameters;
    this.key = (ECPrivateKeyParameters)parametersWithUKM.getParameters();
    this.ukm = toInteger(parametersWithUKM.getUKM());
  }
  
  public int getFieldSize() {
    return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
  }
  
  public byte[] calculateAgreement(CipherParameters paramCipherParameters) {
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)paramCipherParameters;
    ECDomainParameters eCDomainParameters = eCPublicKeyParameters.getParameters();
    if (!eCDomainParameters.equals(this.key.getParameters()))
      throw new IllegalStateException("ECVKO public key has wrong domain parameters"); 
    BigInteger bigInteger = eCDomainParameters.getH().multiply(this.ukm).multiply(this.key.getD()).mod(eCDomainParameters.getN());
    ECPoint eCPoint = eCPublicKeyParameters.getQ().multiply(bigInteger).normalize();
    if (eCPoint.isInfinity())
      throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO"); 
    return fromPoint(eCPoint.normalize());
  }
  
  private static BigInteger toInteger(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[paramArrayOfbyte.length - b - 1]; 
    return new BigInteger(1, arrayOfByte);
  }
  
  private byte[] fromPoint(ECPoint paramECPoint) {
    byte b1;
    BigInteger bigInteger1 = paramECPoint.getAffineXCoord().toBigInteger();
    BigInteger bigInteger2 = paramECPoint.getAffineYCoord().toBigInteger();
    if ((bigInteger1.toByteArray()).length > 33) {
      b1 = 64;
    } else {
      b1 = 32;
    } 
    byte[] arrayOfByte1 = new byte[2 * b1];
    byte[] arrayOfByte2 = BigIntegers.asUnsignedByteArray(b1, bigInteger1);
    byte[] arrayOfByte3 = BigIntegers.asUnsignedByteArray(b1, bigInteger2);
    byte b2;
    for (b2 = 0; b2 != b1; b2++)
      arrayOfByte1[b2] = arrayOfByte2[b1 - b2 - 1]; 
    for (b2 = 0; b2 != b1; b2++)
      arrayOfByte1[b1 + b2] = arrayOfByte3[b1 - b2 - 1]; 
    this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte4 = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte4, 0);
    return arrayOfByte4;
  }
}
