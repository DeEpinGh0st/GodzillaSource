package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ElGamalKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

public class ElGamalEngine implements AsymmetricBlockCipher {
  private ElGamalKeyParameters key;
  
  private SecureRandom random;
  
  private boolean forEncryption;
  
  private int bitSize;
  
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.key = (ElGamalKeyParameters)parametersWithRandom.getParameters();
      this.random = parametersWithRandom.getRandom();
    } else {
      this.key = (ElGamalKeyParameters)paramCipherParameters;
      this.random = new SecureRandom();
    } 
    this.forEncryption = paramBoolean;
    BigInteger bigInteger = this.key.getParameters().getP();
    this.bitSize = bigInteger.bitLength();
    if (paramBoolean) {
      if (!(this.key instanceof ElGamalPublicKeyParameters))
        throw new IllegalArgumentException("ElGamalPublicKeyParameters are required for encryption."); 
    } else if (!(this.key instanceof ElGamalPrivateKeyParameters)) {
      throw new IllegalArgumentException("ElGamalPrivateKeyParameters are required for decryption.");
    } 
  }
  
  public int getInputBlockSize() {
    return this.forEncryption ? ((this.bitSize - 1) / 8) : (2 * (this.bitSize + 7) / 8);
  }
  
  public int getOutputBlockSize() {
    return this.forEncryption ? (2 * (this.bitSize + 7) / 8) : ((this.bitSize - 1) / 8);
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte;
    if (this.key == null)
      throw new IllegalStateException("ElGamal engine not initialised"); 
    int i = this.forEncryption ? ((this.bitSize - 1 + 7) / 8) : getInputBlockSize();
    if (paramInt2 > i)
      throw new DataLengthException("input too large for ElGamal cipher.\n"); 
    BigInteger bigInteger1 = this.key.getParameters().getP();
    if (this.key instanceof ElGamalPrivateKeyParameters) {
      arrayOfByte = new byte[paramInt2 / 2];
      byte[] arrayOfByte1 = new byte[paramInt2 / 2];
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, arrayOfByte.length);
      System.arraycopy(paramArrayOfbyte, paramInt1 + arrayOfByte.length, arrayOfByte1, 0, arrayOfByte1.length);
      BigInteger bigInteger4 = new BigInteger(1, arrayOfByte);
      BigInteger bigInteger5 = new BigInteger(1, arrayOfByte1);
      ElGamalPrivateKeyParameters elGamalPrivateKeyParameters = (ElGamalPrivateKeyParameters)this.key;
      BigInteger bigInteger6 = bigInteger4.modPow(bigInteger1.subtract(ONE).subtract(elGamalPrivateKeyParameters.getX()), bigInteger1).multiply(bigInteger5).mod(bigInteger1);
      return BigIntegers.asUnsignedByteArray(bigInteger6);
    } 
    if (paramInt1 != 0 || paramInt2 != paramArrayOfbyte.length) {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    } else {
      arrayOfByte = paramArrayOfbyte;
    } 
    BigInteger bigInteger2 = new BigInteger(1, arrayOfByte);
    if (bigInteger2.compareTo(bigInteger1) >= 0)
      throw new DataLengthException("input too large for ElGamal cipher.\n"); 
    ElGamalPublicKeyParameters elGamalPublicKeyParameters = (ElGamalPublicKeyParameters)this.key;
    int j = bigInteger1.bitLength();
    BigInteger bigInteger3 = new BigInteger(j, this.random);
    while (true) {
      if (bigInteger3.equals(ZERO) || bigInteger3.compareTo(bigInteger1.subtract(TWO)) > 0) {
        bigInteger3 = new BigInteger(j, this.random);
        continue;
      } 
      BigInteger bigInteger4 = this.key.getParameters().getG();
      BigInteger bigInteger5 = bigInteger4.modPow(bigInteger3, bigInteger1);
      BigInteger bigInteger6 = bigInteger2.multiply(elGamalPublicKeyParameters.getY().modPow(bigInteger3, bigInteger1)).mod(bigInteger1);
      byte[] arrayOfByte1 = bigInteger5.toByteArray();
      byte[] arrayOfByte2 = bigInteger6.toByteArray();
      byte[] arrayOfByte3 = new byte[getOutputBlockSize()];
      if (arrayOfByte1.length > arrayOfByte3.length / 2) {
        System.arraycopy(arrayOfByte1, 1, arrayOfByte3, arrayOfByte3.length / 2 - arrayOfByte1.length - 1, arrayOfByte1.length - 1);
      } else {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, arrayOfByte3.length / 2 - arrayOfByte1.length, arrayOfByte1.length);
      } 
      if (arrayOfByte2.length > arrayOfByte3.length / 2) {
        System.arraycopy(arrayOfByte2, 1, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length - 1, arrayOfByte2.length - 1);
      } else {
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
      } 
      return arrayOfByte3;
    } 
  }
}
