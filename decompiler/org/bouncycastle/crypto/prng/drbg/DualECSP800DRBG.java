package org.bouncycastle.crypto.prng.drbg;

import java.math.BigInteger;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class DualECSP800DRBG implements SP80090DRBG {
  private static final BigInteger p256_Px = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16);
  
  private static final BigInteger p256_Py = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16);
  
  private static final BigInteger p256_Qx = new BigInteger("c97445f45cdef9f0d3e05e1e585fc297235b82b5be8ff3efca67c59852018192", 16);
  
  private static final BigInteger p256_Qy = new BigInteger("b28ef557ba31dfcbdd21ac46e2a91e3c304f44cb87058ada2cb815151e610046", 16);
  
  private static final BigInteger p384_Px = new BigInteger("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16);
  
  private static final BigInteger p384_Py = new BigInteger("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16);
  
  private static final BigInteger p384_Qx = new BigInteger("8e722de3125bddb05580164bfe20b8b432216a62926c57502ceede31c47816edd1e89769124179d0b695106428815065", 16);
  
  private static final BigInteger p384_Qy = new BigInteger("023b1660dd701d0839fd45eec36f9ee7b32e13b315dc02610aa1b636e346df671f790f84c5e09b05674dbb7e45c803dd", 16);
  
  private static final BigInteger p521_Px = new BigInteger("c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16);
  
  private static final BigInteger p521_Py = new BigInteger("11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16);
  
  private static final BigInteger p521_Qx = new BigInteger("1b9fa3e518d683c6b65763694ac8efbaec6fab44f2276171a42726507dd08add4c3b3f4c1ebc5b1222ddba077f722943b24c3edfa0f85fe24d0c8c01591f0be6f63", 16);
  
  private static final BigInteger p521_Qy = new BigInteger("1f3bdba585295d9a1110d1df1f9430ef8442c5018976ff3437ef91b81dc0b8132c8d5c39c32d0e004a3092b7d327c0e7a4d26d2c7b69b58f9066652911e457779de", 16);
  
  private static final DualECPoints[] nistPoints = new DualECPoints[3];
  
  private static final long RESEED_MAX = 2147483648L;
  
  private static final int MAX_ADDITIONAL_INPUT = 4096;
  
  private static final int MAX_ENTROPY_LENGTH = 4096;
  
  private static final int MAX_PERSONALIZATION_STRING = 4096;
  
  private Digest _digest;
  
  private long _reseedCounter;
  
  private EntropySource _entropySource;
  
  private int _securityStrength;
  
  private int _seedlen;
  
  private int _outlen;
  
  private ECCurve.Fp _curve;
  
  private ECPoint _P;
  
  private ECPoint _Q;
  
  private byte[] _s;
  
  private int _sLength;
  
  private ECMultiplier _fixedPointMultiplier = (ECMultiplier)new FixedPointCombMultiplier();
  
  public DualECSP800DRBG(Digest paramDigest, int paramInt, EntropySource paramEntropySource, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(nistPoints, paramDigest, paramInt, paramEntropySource, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public DualECSP800DRBG(DualECPoints[] paramArrayOfDualECPoints, Digest paramDigest, int paramInt, EntropySource paramEntropySource, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this._digest = paramDigest;
    this._entropySource = paramEntropySource;
    this._securityStrength = paramInt;
    if (Utils.isTooLarge(paramArrayOfbyte1, 512))
      throw new IllegalArgumentException("Personalization string too large"); 
    if (paramEntropySource.entropySize() < paramInt || paramEntropySource.entropySize() > 4096)
      throw new IllegalArgumentException("EntropySource must provide between " + paramInt + " and " + 'က' + " bits"); 
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, paramArrayOfbyte2, paramArrayOfbyte1);
    for (byte b = 0; b != paramArrayOfDualECPoints.length; b++) {
      if (paramInt <= paramArrayOfDualECPoints[b].getSecurityStrength()) {
        if (Utils.getMaxSecurityStrength(paramDigest) < paramArrayOfDualECPoints[b].getSecurityStrength())
          throw new IllegalArgumentException("Requested security strength is not supported by digest"); 
        this._seedlen = paramArrayOfDualECPoints[b].getSeedLen();
        this._outlen = paramArrayOfDualECPoints[b].getMaxOutlen() / 8;
        this._P = paramArrayOfDualECPoints[b].getP();
        this._Q = paramArrayOfDualECPoints[b].getQ();
        break;
      } 
    } 
    if (this._P == null)
      throw new IllegalArgumentException("security strength cannot be greater than 256 bits"); 
    this._s = Utils.hash_df(this._digest, arrayOfByte2, this._seedlen);
    this._sLength = this._s.length;
    this._reseedCounter = 0L;
  }
  
  public int getBlockSize() {
    return this._outlen * 8;
  }
  
  public int generate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean) {
    BigInteger bigInteger;
    int i = paramArrayOfbyte1.length * 8;
    int j = paramArrayOfbyte1.length / this._outlen;
    if (Utils.isTooLarge(paramArrayOfbyte2, 512))
      throw new IllegalArgumentException("Additional input too large"); 
    if (this._reseedCounter + j > 2147483648L)
      return -1; 
    if (paramBoolean) {
      reseed(paramArrayOfbyte2);
      paramArrayOfbyte2 = null;
    } 
    if (paramArrayOfbyte2 != null) {
      paramArrayOfbyte2 = Utils.hash_df(this._digest, paramArrayOfbyte2, this._seedlen);
      bigInteger = new BigInteger(1, xor(this._s, paramArrayOfbyte2));
    } else {
      bigInteger = new BigInteger(1, this._s);
    } 
    Arrays.fill(paramArrayOfbyte1, (byte)0);
    int k = 0;
    for (byte b = 0; b < j; b++) {
      bigInteger = getScalarMultipleXCoord(this._P, bigInteger);
      byte[] arrayOfByte = getScalarMultipleXCoord(this._Q, bigInteger).toByteArray();
      if (arrayOfByte.length > this._outlen) {
        System.arraycopy(arrayOfByte, arrayOfByte.length - this._outlen, paramArrayOfbyte1, k, this._outlen);
      } else {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte1, k + this._outlen - arrayOfByte.length, arrayOfByte.length);
      } 
      k += this._outlen;
      this._reseedCounter++;
    } 
    if (k < paramArrayOfbyte1.length) {
      bigInteger = getScalarMultipleXCoord(this._P, bigInteger);
      byte[] arrayOfByte = getScalarMultipleXCoord(this._Q, bigInteger).toByteArray();
      int m = paramArrayOfbyte1.length - k;
      if (arrayOfByte.length > this._outlen) {
        System.arraycopy(arrayOfByte, arrayOfByte.length - this._outlen, paramArrayOfbyte1, k, m);
      } else {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte1, k + this._outlen - arrayOfByte.length, m);
      } 
      this._reseedCounter++;
    } 
    this._s = BigIntegers.asUnsignedByteArray(this._sLength, getScalarMultipleXCoord(this._P, bigInteger));
    return i;
  }
  
  public void reseed(byte[] paramArrayOfbyte) {
    if (Utils.isTooLarge(paramArrayOfbyte, 512))
      throw new IllegalArgumentException("Additional input string too large"); 
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(pad8(this._s, this._seedlen), arrayOfByte1, paramArrayOfbyte);
    this._s = Utils.hash_df(this._digest, arrayOfByte2, this._seedlen);
    this._reseedCounter = 0L;
  }
  
  private byte[] getEntropy() {
    byte[] arrayOfByte = this._entropySource.getEntropy();
    if (arrayOfByte.length < (this._securityStrength + 7) / 8)
      throw new IllegalStateException("Insufficient entropy provided by entropy source"); 
    return arrayOfByte;
  }
  
  private byte[] xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte2 == null)
      return paramArrayOfbyte1; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]); 
    return arrayOfByte;
  }
  
  private byte[] pad8(byte[] paramArrayOfbyte, int paramInt) {
    if (paramInt % 8 == 0)
      return paramArrayOfbyte; 
    int i = 8 - paramInt % 8;
    int j = 0;
    for (int k = paramArrayOfbyte.length - 1; k >= 0; k--) {
      int m = paramArrayOfbyte[k] & 0xFF;
      paramArrayOfbyte[k] = (byte)(m << i | j >> 8 - i);
      j = m;
    } 
    return paramArrayOfbyte;
  }
  
  private BigInteger getScalarMultipleXCoord(ECPoint paramECPoint, BigInteger paramBigInteger) {
    return this._fixedPointMultiplier.multiply(paramECPoint, paramBigInteger).normalize().getAffineXCoord().toBigInteger();
  }
  
  static {
    ECCurve.Fp fp = (ECCurve.Fp)NISTNamedCurves.getByName("P-256").getCurve();
    nistPoints[0] = new DualECPoints(128, fp.createPoint(p256_Px, p256_Py), fp.createPoint(p256_Qx, p256_Qy), 1);
    fp = (ECCurve.Fp)NISTNamedCurves.getByName("P-384").getCurve();
    nistPoints[1] = new DualECPoints(192, fp.createPoint(p384_Px, p384_Py), fp.createPoint(p384_Qx, p384_Qy), 1);
    fp = (ECCurve.Fp)NISTNamedCurves.getByName("P-521").getCurve();
    nistPoints[2] = new DualECPoints(256, fp.createPoint(p521_Px, p521_Py), fp.createPoint(p521_Qx, p521_Qy), 1);
  }
}
