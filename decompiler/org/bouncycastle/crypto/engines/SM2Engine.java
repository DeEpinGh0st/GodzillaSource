package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SM2Engine {
  private final Digest digest;
  
  private boolean forEncryption;
  
  private ECKeyParameters ecKey;
  
  private ECDomainParameters ecParams;
  
  private int curveLength;
  
  private SecureRandom random;
  
  public SM2Engine() {
    this((Digest)new SM3Digest());
  }
  
  public SM2Engine(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forEncryption = paramBoolean;
    if (paramBoolean) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.ecKey = (ECKeyParameters)parametersWithRandom.getParameters();
      this.ecParams = this.ecKey.getParameters();
      ECPoint eCPoint = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(this.ecParams.getH());
      if (eCPoint.isInfinity())
        throw new IllegalArgumentException("invalid key: [h]Q at infinity"); 
      this.random = parametersWithRandom.getRandom();
    } else {
      this.ecKey = (ECKeyParameters)paramCipherParameters;
      this.ecParams = this.ecKey.getParameters();
    } 
    this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    return this.forEncryption ? encrypt(paramArrayOfbyte, paramInt1, paramInt2) : decrypt(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  private byte[] encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, arrayOfByte.length);
    while (true) {
      BigInteger bigInteger = nextK();
      ECPoint eCPoint2 = this.ecParams.getG().multiply(bigInteger).normalize();
      byte[] arrayOfByte1 = eCPoint2.getEncoded(false);
      ECPoint eCPoint1 = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(bigInteger).normalize();
      kdf(this.digest, eCPoint1, arrayOfByte);
      if (!notEncrypted(arrayOfByte, paramArrayOfbyte, paramInt1)) {
        byte[] arrayOfByte2 = new byte[this.digest.getDigestSize()];
        addFieldElement(this.digest, eCPoint1.getAffineXCoord());
        this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
        addFieldElement(this.digest, eCPoint1.getAffineYCoord());
        this.digest.doFinal(arrayOfByte2, 0);
        return Arrays.concatenate(arrayOfByte1, arrayOfByte, arrayOfByte2);
      } 
    } 
  }
  
  private byte[] decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte1 = new byte[this.curveLength * 2 + 1];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, arrayOfByte1.length);
    ECPoint eCPoint1 = this.ecParams.getCurve().decodePoint(arrayOfByte1);
    ECPoint eCPoint2 = eCPoint1.multiply(this.ecParams.getH());
    if (eCPoint2.isInfinity())
      throw new InvalidCipherTextException("[h]C1 at infinity"); 
    eCPoint1 = eCPoint1.multiply(((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
    byte[] arrayOfByte2 = new byte[paramInt2 - arrayOfByte1.length - this.digest.getDigestSize()];
    System.arraycopy(paramArrayOfbyte, paramInt1 + arrayOfByte1.length, arrayOfByte2, 0, arrayOfByte2.length);
    kdf(this.digest, eCPoint1, arrayOfByte2);
    byte[] arrayOfByte3 = new byte[this.digest.getDigestSize()];
    addFieldElement(this.digest, eCPoint1.getAffineXCoord());
    this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
    addFieldElement(this.digest, eCPoint1.getAffineYCoord());
    this.digest.doFinal(arrayOfByte3, 0);
    int i = 0;
    for (byte b = 0; b != arrayOfByte3.length; b++)
      i |= arrayOfByte3[b] ^ paramArrayOfbyte[arrayOfByte1.length + arrayOfByte2.length + b]; 
    clearBlock(arrayOfByte1);
    clearBlock(arrayOfByte3);
    if (i != 0) {
      clearBlock(arrayOfByte2);
      throw new InvalidCipherTextException("invalid cipher text");
    } 
    return arrayOfByte2;
  }
  
  private boolean notEncrypted(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    for (byte b = 0; b != paramArrayOfbyte1.length; b++) {
      if (paramArrayOfbyte1[b] != paramArrayOfbyte2[paramInt])
        return false; 
    } 
    return true;
  }
  
  private void kdf(Digest paramDigest, ECPoint paramECPoint, byte[] paramArrayOfbyte) {
    byte b1 = 1;
    int i = paramDigest.getDigestSize();
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    int j = 0;
    for (byte b2 = 1; b2 <= (paramArrayOfbyte.length + i - 1) / i; b2++) {
      addFieldElement(paramDigest, paramECPoint.getAffineXCoord());
      addFieldElement(paramDigest, paramECPoint.getAffineYCoord());
      paramDigest.update((byte)(b1 >> 24));
      paramDigest.update((byte)(b1 >> 16));
      paramDigest.update((byte)(b1 >> 8));
      paramDigest.update((byte)b1);
      paramDigest.doFinal(arrayOfByte, 0);
      if (j + arrayOfByte.length < paramArrayOfbyte.length) {
        xor(paramArrayOfbyte, arrayOfByte, j, arrayOfByte.length);
      } else {
        xor(paramArrayOfbyte, arrayOfByte, j, paramArrayOfbyte.length - j);
      } 
      j += arrayOfByte.length;
      b1++;
    } 
  }
  
  private void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
    for (int i = 0; i != paramInt2; i++)
      paramArrayOfbyte1[paramInt1 + i] = (byte)(paramArrayOfbyte1[paramInt1 + i] ^ paramArrayOfbyte2[i]); 
  }
  
  private BigInteger nextK() {
    int i = this.ecParams.getN().bitLength();
    while (true) {
      BigInteger bigInteger = new BigInteger(i, this.random);
      if (!bigInteger.equals(ECConstants.ZERO) && bigInteger.compareTo(this.ecParams.getN()) < 0)
        return bigInteger; 
    } 
  }
  
  private void addFieldElement(Digest paramDigest, ECFieldElement paramECFieldElement) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(this.curveLength, paramECFieldElement.toBigInteger());
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
  }
  
  private void clearBlock(byte[] paramArrayOfbyte) {
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = 0; 
  }
}
