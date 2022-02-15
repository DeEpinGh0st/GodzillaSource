package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.KeyEncapsulation;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSAKeyEncapsulation implements KeyEncapsulation {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private DerivationFunction kdf;
  
  private SecureRandom rnd;
  
  private RSAKeyParameters key;
  
  public RSAKeyEncapsulation(DerivationFunction paramDerivationFunction, SecureRandom paramSecureRandom) {
    this.kdf = paramDerivationFunction;
    this.rnd = paramSecureRandom;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof RSAKeyParameters))
      throw new IllegalArgumentException("RSA key required"); 
    this.key = (RSAKeyParameters)paramCipherParameters;
  }
  
  public CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalArgumentException {
    if (this.key.isPrivate())
      throw new IllegalArgumentException("Public key required for encryption"); 
    BigInteger bigInteger1 = this.key.getModulus();
    BigInteger bigInteger2 = this.key.getExponent();
    BigInteger bigInteger3 = BigIntegers.createRandomInRange(ZERO, bigInteger1.subtract(ONE), this.rnd);
    BigInteger bigInteger4 = bigInteger3.modPow(bigInteger2, bigInteger1);
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray((bigInteger1.bitLength() + 7) / 8, bigInteger4);
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, arrayOfByte.length);
    return (CipherParameters)generateKey(bigInteger1, bigInteger3, paramInt2);
  }
  
  public CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt) {
    return encrypt(paramArrayOfbyte, 0, paramInt);
  }
  
  public CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IllegalArgumentException {
    if (!this.key.isPrivate())
      throw new IllegalArgumentException("Private key required for decryption"); 
    BigInteger bigInteger1 = this.key.getModulus();
    BigInteger bigInteger2 = this.key.getExponent();
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, arrayOfByte.length);
    BigInteger bigInteger3 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger4 = bigInteger3.modPow(bigInteger2, bigInteger1);
    return (CipherParameters)generateKey(bigInteger1, bigInteger4, paramInt3);
  }
  
  public CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt) {
    return decrypt(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramInt);
  }
  
  protected KeyParameter generateKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    byte[] arrayOfByte1 = BigIntegers.asUnsignedByteArray((paramBigInteger1.bitLength() + 7) / 8, paramBigInteger2);
    this.kdf.init((DerivationParameters)new KDFParameters(arrayOfByte1, null));
    byte[] arrayOfByte2 = new byte[paramInt];
    this.kdf.generateBytes(arrayOfByte2, 0, arrayOfByte2.length);
    return new KeyParameter(arrayOfByte2);
  }
}
