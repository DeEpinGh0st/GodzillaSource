package org.bouncycastle.crypto.encodings;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class ISO9796d1Encoding implements AsymmetricBlockCipher {
  private static final BigInteger SIXTEEN = BigInteger.valueOf(16L);
  
  private static final BigInteger SIX = BigInteger.valueOf(6L);
  
  private static byte[] shadows = new byte[] { 
      14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 
      11, 6, 7, 10, 12, 1 };
  
  private static byte[] inverse = new byte[] { 
      8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 
      13, 10, 14, 9, 0, 7 };
  
  private AsymmetricBlockCipher engine;
  
  private boolean forEncryption;
  
  private int bitSize;
  
  private int padBits = 0;
  
  private BigInteger modulus;
  
  public ISO9796d1Encoding(AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.engine = paramAsymmetricBlockCipher;
  }
  
  public AsymmetricBlockCipher getUnderlyingCipher() {
    return this.engine;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    RSAKeyParameters rSAKeyParameters = null;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      rSAKeyParameters = (RSAKeyParameters)parametersWithRandom.getParameters();
    } else {
      rSAKeyParameters = (RSAKeyParameters)paramCipherParameters;
    } 
    this.engine.init(paramBoolean, paramCipherParameters);
    this.modulus = rSAKeyParameters.getModulus();
    this.bitSize = this.modulus.bitLength();
    this.forEncryption = paramBoolean;
  }
  
  public int getInputBlockSize() {
    int i = this.engine.getInputBlockSize();
    return this.forEncryption ? ((i + 1) / 2) : i;
  }
  
  public int getOutputBlockSize() {
    int i = this.engine.getOutputBlockSize();
    return this.forEncryption ? i : ((i + 1) / 2);
  }
  
  public void setPadBits(int paramInt) {
    if (paramInt > 7)
      throw new IllegalArgumentException("padBits > 7"); 
    this.padBits = paramInt;
  }
  
  public int getPadBits() {
    return this.padBits;
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    return this.forEncryption ? encodeBlock(paramArrayOfbyte, paramInt1, paramInt2) : decodeBlock(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  private byte[] encodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte = new byte[(this.bitSize + 7) / 8];
    int i = this.padBits + 1;
    int j = paramInt2;
    int k = (this.bitSize + 13) / 16;
    int m;
    for (m = 0; m < k; m += j) {
      if (m > k - j) {
        System.arraycopy(paramArrayOfbyte, paramInt1 + paramInt2 - k - m, arrayOfByte, arrayOfByte.length - k, k - m);
      } else {
        System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, arrayOfByte.length - m + j, j);
      } 
    } 
    for (m = arrayOfByte.length - 2 * k; m != arrayOfByte.length; m += 2) {
      byte b1 = arrayOfByte[arrayOfByte.length - k + m / 2];
      arrayOfByte[m] = (byte)(shadows[(b1 & 0xFF) >>> 4] << 4 | shadows[b1 & 0xF]);
      arrayOfByte[m + 1] = b1;
    } 
    arrayOfByte[arrayOfByte.length - 2 * j] = (byte)(arrayOfByte[arrayOfByte.length - 2 * j] ^ i);
    arrayOfByte[arrayOfByte.length - 1] = (byte)(arrayOfByte[arrayOfByte.length - 1] << 4 | 0x6);
    m = 8 - (this.bitSize - 1) % 8;
    byte b = 0;
    if (m != 8) {
      arrayOfByte[0] = (byte)(arrayOfByte[0] & 255 >>> m);
      arrayOfByte[0] = (byte)(arrayOfByte[0] | 128 >>> m);
    } else {
      arrayOfByte[0] = 0;
      arrayOfByte[1] = (byte)(arrayOfByte[1] | 0x80);
      b = 1;
    } 
    return this.engine.processBlock(arrayOfByte, b, arrayOfByte.length - b);
  }
  
  private byte[] decodeBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    BigInteger bigInteger2;
    byte[] arrayOfByte1 = this.engine.processBlock(paramArrayOfbyte, paramInt1, paramInt2);
    int i = 1;
    int j = (this.bitSize + 13) / 16;
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte1);
    if (bigInteger1.mod(SIXTEEN).equals(SIX)) {
      bigInteger2 = bigInteger1;
    } else if (this.modulus.subtract(bigInteger1).mod(SIXTEEN).equals(SIX)) {
      bigInteger2 = this.modulus.subtract(bigInteger1);
    } else {
      throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
    } 
    arrayOfByte1 = convertOutputDecryptOnly(bigInteger2);
    if ((arrayOfByte1[arrayOfByte1.length - 1] & 0xF) != 6)
      throw new InvalidCipherTextException("invalid forcing byte in block"); 
    arrayOfByte1[arrayOfByte1.length - 1] = (byte)((arrayOfByte1[arrayOfByte1.length - 1] & 0xFF) >>> 4 | inverse[(arrayOfByte1[arrayOfByte1.length - 2] & 0xFF) >> 4] << 4);
    arrayOfByte1[0] = (byte)(shadows[(arrayOfByte1[1] & 0xFF) >>> 4] << 4 | shadows[arrayOfByte1[1] & 0xF]);
    boolean bool = false;
    int k = 0;
    for (int m = arrayOfByte1.length - 1; m >= arrayOfByte1.length - 2 * j; m -= 2) {
      int n = shadows[(arrayOfByte1[m] & 0xFF) >>> 4] << 4 | shadows[arrayOfByte1[m] & 0xF];
      if (((arrayOfByte1[m - 1] ^ n) & 0xFF) != 0)
        if (!bool) {
          bool = true;
          i = (arrayOfByte1[m - 1] ^ n) & 0xFF;
          k = m - 1;
        } else {
          throw new InvalidCipherTextException("invalid tsums in block");
        }  
    } 
    arrayOfByte1[k] = 0;
    byte[] arrayOfByte2 = new byte[(arrayOfByte1.length - k) / 2];
    for (byte b = 0; b < arrayOfByte2.length; b++)
      arrayOfByte2[b] = arrayOfByte1[2 * b + k + 1]; 
    this.padBits = i - 1;
    return arrayOfByte2;
  }
  
  private static byte[] convertOutputDecryptOnly(BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
}
