package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.KeyEncapsulation;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECIESKeyEncapsulation implements KeyEncapsulation {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private DerivationFunction kdf;
  
  private SecureRandom rnd;
  
  private ECKeyParameters key;
  
  private boolean CofactorMode;
  
  private boolean OldCofactorMode;
  
  private boolean SingleHashMode;
  
  public ECIESKeyEncapsulation(DerivationFunction paramDerivationFunction, SecureRandom paramSecureRandom) {
    this.kdf = paramDerivationFunction;
    this.rnd = paramSecureRandom;
    this.CofactorMode = false;
    this.OldCofactorMode = false;
    this.SingleHashMode = false;
  }
  
  public ECIESKeyEncapsulation(DerivationFunction paramDerivationFunction, SecureRandom paramSecureRandom, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    this.kdf = paramDerivationFunction;
    this.rnd = paramSecureRandom;
    this.CofactorMode = paramBoolean1;
    this.OldCofactorMode = paramBoolean2;
    this.SingleHashMode = paramBoolean3;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof ECKeyParameters))
      throw new IllegalArgumentException("EC key required"); 
    this.key = (ECKeyParameters)paramCipherParameters;
  }
  
  public CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalArgumentException {
    if (!(this.key instanceof ECPublicKeyParameters))
      throw new IllegalArgumentException("Public key required for encryption"); 
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)this.key;
    ECDomainParameters eCDomainParameters = eCPublicKeyParameters.getParameters();
    ECCurve eCCurve = eCDomainParameters.getCurve();
    BigInteger bigInteger1 = eCDomainParameters.getN();
    BigInteger bigInteger2 = eCDomainParameters.getH();
    BigInteger bigInteger3 = BigIntegers.createRandomInRange(ONE, bigInteger1, this.rnd);
    BigInteger bigInteger4 = this.CofactorMode ? bigInteger3.multiply(bigInteger2).mod(bigInteger1) : bigInteger3;
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    ECPoint[] arrayOfECPoint = { eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger3), eCPublicKeyParameters.getQ().multiply(bigInteger4) };
    eCCurve.normalizeAll(arrayOfECPoint);
    ECPoint eCPoint1 = arrayOfECPoint[0];
    ECPoint eCPoint2 = arrayOfECPoint[1];
    byte[] arrayOfByte1 = eCPoint1.getEncoded(false);
    System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1, arrayOfByte1.length);
    byte[] arrayOfByte2 = eCPoint2.getAffineXCoord().getEncoded();
    return (CipherParameters)deriveKey(paramInt2, arrayOfByte1, arrayOfByte2);
  }
  
  public CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt) {
    return encrypt(paramArrayOfbyte, 0, paramInt);
  }
  
  public CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IllegalArgumentException {
    if (!(this.key instanceof ECPrivateKeyParameters))
      throw new IllegalArgumentException("Private key required for encryption"); 
    ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
    ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
    ECCurve eCCurve = eCDomainParameters.getCurve();
    BigInteger bigInteger1 = eCDomainParameters.getN();
    BigInteger bigInteger2 = eCDomainParameters.getH();
    byte[] arrayOfByte1 = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, paramInt2);
    ECPoint eCPoint1 = eCCurve.decodePoint(arrayOfByte1);
    ECPoint eCPoint2 = eCPoint1;
    if (this.CofactorMode || this.OldCofactorMode)
      eCPoint2 = eCPoint2.multiply(bigInteger2); 
    BigInteger bigInteger3 = eCPrivateKeyParameters.getD();
    if (this.CofactorMode)
      bigInteger3 = bigInteger3.multiply(bigInteger2.modInverse(bigInteger1)).mod(bigInteger1); 
    ECPoint eCPoint3 = eCPoint2.multiply(bigInteger3).normalize();
    byte[] arrayOfByte2 = eCPoint3.getAffineXCoord().getEncoded();
    return (CipherParameters)deriveKey(paramInt3, arrayOfByte1, arrayOfByte2);
  }
  
  public CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt) {
    return decrypt(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramInt);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
  
  protected KeyParameter deriveKey(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = paramArrayOfbyte2;
    if (!this.SingleHashMode) {
      arrayOfByte = Arrays.concatenate(paramArrayOfbyte1, paramArrayOfbyte2);
      Arrays.fill(paramArrayOfbyte2, (byte)0);
    } 
    try {
      this.kdf.init((DerivationParameters)new KDFParameters(arrayOfByte, null));
      byte[] arrayOfByte1 = new byte[paramInt];
      this.kdf.generateBytes(arrayOfByte1, 0, arrayOfByte1.length);
      return new KeyParameter(arrayOfByte1);
    } finally {
      Arrays.fill(arrayOfByte, (byte)0);
    } 
  }
}
