package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SM2KeyExchange {
  private final Digest digest;
  
  private byte[] userID;
  
  private ECPrivateKeyParameters staticKey;
  
  private ECPoint staticPubPoint;
  
  private ECPoint ephemeralPubPoint;
  
  private ECDomainParameters ecParams;
  
  private int curveLength;
  
  private int w;
  
  private ECPrivateKeyParameters ephemeralKey;
  
  private boolean initiator;
  
  public SM2KeyExchange() {
    this((Digest)new SM3Digest());
  }
  
  public SM2KeyExchange(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(CipherParameters paramCipherParameters) {
    SM2KeyExchangePrivateParameters sM2KeyExchangePrivateParameters;
    if (paramCipherParameters instanceof ParametersWithID) {
      sM2KeyExchangePrivateParameters = (SM2KeyExchangePrivateParameters)((ParametersWithID)paramCipherParameters).getParameters();
      this.userID = ((ParametersWithID)paramCipherParameters).getID();
    } else {
      sM2KeyExchangePrivateParameters = (SM2KeyExchangePrivateParameters)paramCipherParameters;
      this.userID = new byte[0];
    } 
    this.initiator = sM2KeyExchangePrivateParameters.isInitiator();
    this.staticKey = sM2KeyExchangePrivateParameters.getStaticPrivateKey();
    this.ephemeralKey = sM2KeyExchangePrivateParameters.getEphemeralPrivateKey();
    this.ecParams = this.staticKey.getParameters();
    this.staticPubPoint = sM2KeyExchangePrivateParameters.getStaticPublicPoint();
    this.ephemeralPubPoint = sM2KeyExchangePrivateParameters.getEphemeralPublicPoint();
    this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
    this.w = this.ecParams.getCurve().getFieldSize() / 2 - 1;
  }
  
  public int getFieldSize() {
    return (this.staticKey.getParameters().getCurve().getFieldSize() + 7) / 8;
  }
  
  public byte[] calculateKey(int paramInt, CipherParameters paramCipherParameters) {
    SM2KeyExchangePublicParameters sM2KeyExchangePublicParameters;
    byte[] arrayOfByte1;
    byte[] arrayOfByte4;
    if (paramCipherParameters instanceof ParametersWithID) {
      sM2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)((ParametersWithID)paramCipherParameters).getParameters();
      arrayOfByte1 = ((ParametersWithID)paramCipherParameters).getID();
    } else {
      sM2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)paramCipherParameters;
      arrayOfByte1 = new byte[0];
    } 
    byte[] arrayOfByte2 = getZ(this.digest, this.userID, this.staticPubPoint);
    byte[] arrayOfByte3 = getZ(this.digest, arrayOfByte1, sM2KeyExchangePublicParameters.getStaticPublicKey().getQ());
    ECPoint eCPoint = calculateU(sM2KeyExchangePublicParameters);
    if (this.initiator) {
      arrayOfByte4 = kdf(eCPoint, arrayOfByte2, arrayOfByte3, paramInt);
    } else {
      arrayOfByte4 = kdf(eCPoint, arrayOfByte3, arrayOfByte2, paramInt);
    } 
    return arrayOfByte4;
  }
  
  public byte[][] calculateKeyWithConfirmation(int paramInt, byte[] paramArrayOfbyte, CipherParameters paramCipherParameters) {
    SM2KeyExchangePublicParameters sM2KeyExchangePublicParameters;
    byte[] arrayOfByte1;
    if (paramCipherParameters instanceof ParametersWithID) {
      sM2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)((ParametersWithID)paramCipherParameters).getParameters();
      arrayOfByte1 = ((ParametersWithID)paramCipherParameters).getID();
    } else {
      sM2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)paramCipherParameters;
      arrayOfByte1 = new byte[0];
    } 
    if (this.initiator && paramArrayOfbyte == null)
      throw new IllegalArgumentException("if initiating, confirmationTag must be set"); 
    byte[] arrayOfByte2 = getZ(this.digest, this.userID, this.staticPubPoint);
    byte[] arrayOfByte3 = getZ(this.digest, arrayOfByte1, sM2KeyExchangePublicParameters.getStaticPublicKey().getQ());
    ECPoint eCPoint = calculateU(sM2KeyExchangePublicParameters);
    if (this.initiator) {
      byte[] arrayOfByte6 = kdf(eCPoint, arrayOfByte2, arrayOfByte3, paramInt);
      byte[] arrayOfByte7 = calculateInnerHash(this.digest, eCPoint, arrayOfByte2, arrayOfByte3, this.ephemeralPubPoint, sM2KeyExchangePublicParameters.getEphemeralPublicKey().getQ());
      byte[] arrayOfByte8 = S1(this.digest, eCPoint, arrayOfByte7);
      if (!Arrays.constantTimeAreEqual(arrayOfByte8, paramArrayOfbyte))
        throw new IllegalStateException("confirmation tag mismatch"); 
      return new byte[][] { arrayOfByte6, S2(this.digest, eCPoint, arrayOfByte7) };
    } 
    byte[] arrayOfByte4 = kdf(eCPoint, arrayOfByte3, arrayOfByte2, paramInt);
    byte[] arrayOfByte5 = calculateInnerHash(this.digest, eCPoint, arrayOfByte3, arrayOfByte2, sM2KeyExchangePublicParameters.getEphemeralPublicKey().getQ(), this.ephemeralPubPoint);
    return new byte[][] { arrayOfByte4, S1(this.digest, eCPoint, arrayOfByte5), S2(this.digest, eCPoint, arrayOfByte5) };
  }
  
  private ECPoint calculateU(SM2KeyExchangePublicParameters paramSM2KeyExchangePublicParameters) {
    BigInteger bigInteger1 = reduce(this.ephemeralPubPoint.getAffineXCoord().toBigInteger());
    BigInteger bigInteger2 = this.staticKey.getD().add(bigInteger1.multiply(this.ephemeralKey.getD())).mod(this.ecParams.getN());
    BigInteger bigInteger3 = reduce(paramSM2KeyExchangePublicParameters.getEphemeralPublicKey().getQ().getAffineXCoord().toBigInteger());
    ECPoint eCPoint1 = paramSM2KeyExchangePublicParameters.getEphemeralPublicKey().getQ().multiply(bigInteger3).normalize();
    ECPoint eCPoint2 = paramSM2KeyExchangePublicParameters.getStaticPublicKey().getQ().add(eCPoint1).normalize();
    return eCPoint2.multiply(this.ecParams.getH().multiply(bigInteger2)).normalize();
  }
  
  private byte[] kdf(ECPoint paramECPoint, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    byte b1 = 1;
    int i = this.digest.getDigestSize() * 8;
    byte[] arrayOfByte1 = new byte[this.digest.getDigestSize()];
    byte[] arrayOfByte2 = new byte[(paramInt + 7) / 8];
    int j = 0;
    for (byte b2 = 1; b2 <= (paramInt + i - 1) / i; b2++) {
      addFieldElement(this.digest, paramECPoint.getAffineXCoord());
      addFieldElement(this.digest, paramECPoint.getAffineYCoord());
      this.digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      this.digest.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
      this.digest.update((byte)(b1 >> 24));
      this.digest.update((byte)(b1 >> 16));
      this.digest.update((byte)(b1 >> 8));
      this.digest.update((byte)b1);
      this.digest.doFinal(arrayOfByte1, 0);
      if (j + arrayOfByte1.length < arrayOfByte2.length) {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, j, arrayOfByte1.length);
      } else {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, j, arrayOfByte2.length - j);
      } 
      j += arrayOfByte1.length;
      b1++;
    } 
    return arrayOfByte2;
  }
  
  private BigInteger reduce(BigInteger paramBigInteger) {
    return paramBigInteger.and(BigInteger.valueOf(1L).shiftLeft(this.w).subtract(BigInteger.valueOf(1L))).setBit(this.w);
  }
  
  private byte[] S1(Digest paramDigest, ECPoint paramECPoint, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.update((byte)2);
    addFieldElement(paramDigest, paramECPoint.getAffineYCoord());
    paramDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    paramDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private byte[] calculateInnerHash(Digest paramDigest, ECPoint paramECPoint1, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, ECPoint paramECPoint2, ECPoint paramECPoint3) {
    addFieldElement(paramDigest, paramECPoint1.getAffineXCoord());
    paramDigest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    paramDigest.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    addFieldElement(paramDigest, paramECPoint2.getAffineXCoord());
    addFieldElement(paramDigest, paramECPoint2.getAffineYCoord());
    addFieldElement(paramDigest, paramECPoint3.getAffineXCoord());
    addFieldElement(paramDigest, paramECPoint3.getAffineYCoord());
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private byte[] S2(Digest paramDigest, ECPoint paramECPoint, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.update((byte)3);
    addFieldElement(paramDigest, paramECPoint.getAffineYCoord());
    paramDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    paramDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  private byte[] getZ(Digest paramDigest, byte[] paramArrayOfbyte, ECPoint paramECPoint) {
    addUserID(paramDigest, paramArrayOfbyte);
    addFieldElement(paramDigest, this.ecParams.getCurve().getA());
    addFieldElement(paramDigest, this.ecParams.getCurve().getB());
    addFieldElement(paramDigest, this.ecParams.getG().getAffineXCoord());
    addFieldElement(paramDigest, this.ecParams.getG().getAffineYCoord());
    addFieldElement(paramDigest, paramECPoint.getAffineXCoord());
    addFieldElement(paramDigest, paramECPoint.getAffineYCoord());
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
}
