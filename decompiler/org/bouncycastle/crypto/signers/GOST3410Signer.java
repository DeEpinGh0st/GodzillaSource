package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class GOST3410Signer implements DSA {
  GOST3410KeyParameters key;
  
  SecureRandom random;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.random = parametersWithRandom.getRandom();
        this.key = (GOST3410KeyParameters)parametersWithRandom.getParameters();
      } else {
        this.random = new SecureRandom();
        this.key = (GOST3410KeyParameters)paramCipherParameters;
      } 
    } else {
      this.key = (GOST3410KeyParameters)paramCipherParameters;
    } 
  }
  
  public BigInteger[] generateSignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[arrayOfByte.length - 1 - b]; 
    BigInteger bigInteger = new BigInteger(1, arrayOfByte);
    GOST3410Parameters gOST3410Parameters = this.key.getParameters();
    while (true) {
      BigInteger bigInteger1 = new BigInteger(gOST3410Parameters.getQ().bitLength(), this.random);
      if (bigInteger1.compareTo(gOST3410Parameters.getQ()) < 0) {
        BigInteger bigInteger2 = gOST3410Parameters.getA().modPow(bigInteger1, gOST3410Parameters.getP()).mod(gOST3410Parameters.getQ());
        BigInteger bigInteger3 = bigInteger1.multiply(bigInteger).add(((GOST3410PrivateKeyParameters)this.key).getX().multiply(bigInteger2)).mod(gOST3410Parameters.getQ());
        BigInteger[] arrayOfBigInteger = new BigInteger[2];
        arrayOfBigInteger[0] = bigInteger2;
        arrayOfBigInteger[1] = bigInteger3;
        return arrayOfBigInteger;
      } 
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[arrayOfByte.length - 1 - b]; 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    GOST3410Parameters gOST3410Parameters = this.key.getParameters();
    BigInteger bigInteger2 = BigInteger.valueOf(0L);
    if (bigInteger2.compareTo(paramBigInteger1) >= 0 || gOST3410Parameters.getQ().compareTo(paramBigInteger1) <= 0)
      return false; 
    if (bigInteger2.compareTo(paramBigInteger2) >= 0 || gOST3410Parameters.getQ().compareTo(paramBigInteger2) <= 0)
      return false; 
    BigInteger bigInteger3 = bigInteger1.modPow(gOST3410Parameters.getQ().subtract(new BigInteger("2")), gOST3410Parameters.getQ());
    BigInteger bigInteger4 = paramBigInteger2.multiply(bigInteger3).mod(gOST3410Parameters.getQ());
    BigInteger bigInteger5 = gOST3410Parameters.getQ().subtract(paramBigInteger1).multiply(bigInteger3).mod(gOST3410Parameters.getQ());
    bigInteger4 = gOST3410Parameters.getA().modPow(bigInteger4, gOST3410Parameters.getP());
    bigInteger5 = ((GOST3410PublicKeyParameters)this.key).getY().modPow(bigInteger5, gOST3410Parameters.getP());
    BigInteger bigInteger6 = bigInteger4.multiply(bigInteger5).mod(gOST3410Parameters.getP()).mod(gOST3410Parameters.getQ());
    return bigInteger6.equals(paramBigInteger1);
  }
}
