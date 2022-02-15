package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

public class DSAParametersGenerator {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  private Digest digest;
  
  private int L;
  
  private int N;
  
  private int certainty;
  
  private int iterations;
  
  private SecureRandom random;
  
  private boolean use186_3;
  
  private int usageIndex;
  
  public DSAParametersGenerator() {
    this(DigestFactory.createSHA1());
  }
  
  public DSAParametersGenerator(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    this.L = paramInt1;
    this.N = getDefaultN(paramInt1);
    this.certainty = paramInt2;
    this.iterations = Math.max(getMinimumIterations(this.L), (paramInt2 + 1) / 2);
    this.random = paramSecureRandom;
    this.use186_3 = false;
    this.usageIndex = -1;
  }
  
  public void init(DSAParameterGenerationParameters paramDSAParameterGenerationParameters) {
    int i = paramDSAParameterGenerationParameters.getL();
    int j = paramDSAParameterGenerationParameters.getN();
    if (i < 1024 || i > 3072 || i % 1024 != 0)
      throw new IllegalArgumentException("L values must be between 1024 and 3072 and a multiple of 1024"); 
    if (i == 1024 && j != 160)
      throw new IllegalArgumentException("N must be 160 for L = 1024"); 
    if (i == 2048 && j != 224 && j != 256)
      throw new IllegalArgumentException("N must be 224 or 256 for L = 2048"); 
    if (i == 3072 && j != 256)
      throw new IllegalArgumentException("N must be 256 for L = 3072"); 
    if (this.digest.getDigestSize() * 8 < j)
      throw new IllegalStateException("Digest output size too small for value of N"); 
    this.L = i;
    this.N = j;
    this.certainty = paramDSAParameterGenerationParameters.getCertainty();
    this.iterations = Math.max(getMinimumIterations(i), (this.certainty + 1) / 2);
    this.random = paramDSAParameterGenerationParameters.getRandom();
    this.use186_3 = true;
    this.usageIndex = paramDSAParameterGenerationParameters.getUsageIndex();
  }
  
  public DSAParameters generateParameters() {
    return this.use186_3 ? generateParameters_FIPS186_3() : generateParameters_FIPS186_2();
  }
  
  private DSAParameters generateParameters_FIPS186_2() {
    byte[] arrayOfByte1 = new byte[20];
    byte[] arrayOfByte2 = new byte[20];
    byte[] arrayOfByte3 = new byte[20];
    byte[] arrayOfByte4 = new byte[20];
    int i = (this.L - 1) / 160;
    byte[] arrayOfByte5 = new byte[this.L / 8];
    if (!(this.digest instanceof org.bouncycastle.crypto.digests.SHA1Digest))
      throw new IllegalStateException("can only use SHA-1 for generating FIPS 186-2 parameters"); 
    while (true) {
      this.random.nextBytes(arrayOfByte1);
      hash(this.digest, arrayOfByte1, arrayOfByte2, 0);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
      inc(arrayOfByte3);
      hash(this.digest, arrayOfByte3, arrayOfByte3, 0);
      for (byte b1 = 0; b1 != arrayOfByte4.length; b1++)
        arrayOfByte4[b1] = (byte)(arrayOfByte2[b1] ^ arrayOfByte3[b1]); 
      arrayOfByte4[0] = (byte)(arrayOfByte4[0] | Byte.MIN_VALUE);
      arrayOfByte4[19] = (byte)(arrayOfByte4[19] | 0x1);
      BigInteger bigInteger = new BigInteger(1, arrayOfByte4);
      if (!isProbablePrime(bigInteger))
        continue; 
      byte[] arrayOfByte = Arrays.clone(arrayOfByte1);
      inc(arrayOfByte);
      for (byte b2 = 0; b2 < 'က'; b2++) {
        int j;
        for (j = 1; j <= i; j++) {
          inc(arrayOfByte);
          hash(this.digest, arrayOfByte, arrayOfByte5, arrayOfByte5.length - j * arrayOfByte2.length);
        } 
        j = arrayOfByte5.length - i * arrayOfByte2.length;
        inc(arrayOfByte);
        hash(this.digest, arrayOfByte, arrayOfByte2, 0);
        System.arraycopy(arrayOfByte2, arrayOfByte2.length - j, arrayOfByte5, 0, j);
        arrayOfByte5[0] = (byte)(arrayOfByte5[0] | Byte.MIN_VALUE);
        BigInteger bigInteger1 = new BigInteger(1, arrayOfByte5);
        BigInteger bigInteger2 = bigInteger1.mod(bigInteger.shiftLeft(1));
        BigInteger bigInteger3 = bigInteger1.subtract(bigInteger2.subtract(ONE));
        if (bigInteger3.bitLength() == this.L && isProbablePrime(bigInteger3)) {
          BigInteger bigInteger4 = calculateGenerator_FIPS186_2(bigInteger3, bigInteger, this.random);
          return new DSAParameters(bigInteger3, bigInteger, bigInteger4, new DSAValidationParameters(arrayOfByte1, b2));
        } 
      } 
    } 
  }
  
  private static BigInteger calculateGenerator_FIPS186_2(BigInteger paramBigInteger1, BigInteger paramBigInteger2, SecureRandom paramSecureRandom) {
    BigInteger bigInteger1 = paramBigInteger1.subtract(ONE).divide(paramBigInteger2);
    BigInteger bigInteger2 = paramBigInteger1.subtract(TWO);
    while (true) {
      BigInteger bigInteger3 = BigIntegers.createRandomInRange(TWO, bigInteger2, paramSecureRandom);
      BigInteger bigInteger4 = bigInteger3.modPow(bigInteger1, paramBigInteger1);
      if (bigInteger4.bitLength() > 1)
        return bigInteger4; 
    } 
  }
  
  private DSAParameters generateParameters_FIPS186_3() {
    Digest digest = this.digest;
    int i = digest.getDigestSize() * 8;
    int j = this.N;
    byte[] arrayOfByte1 = new byte[j / 8];
    int k = (this.L - 1) / i;
    int m = (this.L - 1) % i;
    byte[] arrayOfByte2 = new byte[this.L / 8];
    byte[] arrayOfByte3 = new byte[digest.getDigestSize()];
    while (true) {
      this.random.nextBytes(arrayOfByte1);
      hash(digest, arrayOfByte1, arrayOfByte3, 0);
      BigInteger bigInteger1 = (new BigInteger(1, arrayOfByte3)).mod(ONE.shiftLeft(this.N - 1));
      BigInteger bigInteger2 = bigInteger1.setBit(0).setBit(this.N - 1);
      if (!isProbablePrime(bigInteger2))
        continue; 
      byte[] arrayOfByte = Arrays.clone(arrayOfByte1);
      int n = 4 * this.L;
      for (byte b = 0; b < n; b++) {
        int i1;
        for (i1 = 1; i1 <= k; i1++) {
          inc(arrayOfByte);
          hash(digest, arrayOfByte, arrayOfByte2, arrayOfByte2.length - i1 * arrayOfByte3.length);
        } 
        i1 = arrayOfByte2.length - k * arrayOfByte3.length;
        inc(arrayOfByte);
        hash(digest, arrayOfByte, arrayOfByte3, 0);
        System.arraycopy(arrayOfByte3, arrayOfByte3.length - i1, arrayOfByte2, 0, i1);
        arrayOfByte2[0] = (byte)(arrayOfByte2[0] | Byte.MIN_VALUE);
        BigInteger bigInteger3 = new BigInteger(1, arrayOfByte2);
        BigInteger bigInteger4 = bigInteger3.mod(bigInteger2.shiftLeft(1));
        BigInteger bigInteger5 = bigInteger3.subtract(bigInteger4.subtract(ONE));
        if (bigInteger5.bitLength() == this.L && isProbablePrime(bigInteger5)) {
          if (this.usageIndex >= 0) {
            BigInteger bigInteger6 = calculateGenerator_FIPS186_3_Verifiable(digest, bigInteger5, bigInteger2, arrayOfByte1, this.usageIndex);
            if (bigInteger6 != null)
              return new DSAParameters(bigInteger5, bigInteger2, bigInteger6, new DSAValidationParameters(arrayOfByte1, b, this.usageIndex)); 
          } 
          BigInteger bigInteger = calculateGenerator_FIPS186_3_Unverifiable(bigInteger5, bigInteger2, this.random);
          return new DSAParameters(bigInteger5, bigInteger2, bigInteger, new DSAValidationParameters(arrayOfByte1, b));
        } 
      } 
    } 
  }
  
  private boolean isProbablePrime(BigInteger paramBigInteger) {
    return paramBigInteger.isProbablePrime(this.certainty);
  }
  
  private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(BigInteger paramBigInteger1, BigInteger paramBigInteger2, SecureRandom paramSecureRandom) {
    return calculateGenerator_FIPS186_2(paramBigInteger1, paramBigInteger2, paramSecureRandom);
  }
  
  private static BigInteger calculateGenerator_FIPS186_3_Verifiable(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte, int paramInt) {
    BigInteger bigInteger = paramBigInteger1.subtract(ONE).divide(paramBigInteger2);
    byte[] arrayOfByte1 = Hex.decode("6767656E");
    byte[] arrayOfByte2 = new byte[paramArrayOfbyte.length + arrayOfByte1.length + 1 + 2];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte2, 0, paramArrayOfbyte.length);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, paramArrayOfbyte.length, arrayOfByte1.length);
    arrayOfByte2[arrayOfByte2.length - 3] = (byte)paramInt;
    byte[] arrayOfByte3 = new byte[paramDigest.getDigestSize()];
    for (byte b = 1; b < 65536; b++) {
      inc(arrayOfByte2);
      hash(paramDigest, arrayOfByte2, arrayOfByte3, 0);
      BigInteger bigInteger1 = new BigInteger(1, arrayOfByte3);
      BigInteger bigInteger2 = bigInteger1.modPow(bigInteger, paramBigInteger1);
      if (bigInteger2.compareTo(TWO) >= 0)
        return bigInteger2; 
    } 
    return null;
  }
  
  private static void hash(Digest paramDigest, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    paramDigest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    paramDigest.doFinal(paramArrayOfbyte2, paramInt);
  }
  
  private static int getDefaultN(int paramInt) {
    return (paramInt > 1024) ? 256 : 160;
  }
  
  private static int getMinimumIterations(int paramInt) {
    return (paramInt <= 1024) ? 40 : (48 + 8 * (paramInt - 1) / 1024);
  }
  
  private static void inc(byte[] paramArrayOfbyte) {
    for (int i = paramArrayOfbyte.length - 1; i >= 0; i--) {
      byte b = (byte)(paramArrayOfbyte[i] + 1 & 0xFF);
      paramArrayOfbyte[i] = b;
      if (b != 0)
        break; 
    } 
  }
}
