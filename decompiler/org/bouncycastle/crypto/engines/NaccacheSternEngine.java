package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class NaccacheSternEngine implements AsymmetricBlockCipher {
  private boolean forEncryption;
  
  private NaccacheSternKeyParameters key;
  
  private Vector[] lookup = null;
  
  private boolean debug = false;
  
  private static BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static BigInteger ONE = BigInteger.valueOf(1L);
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forEncryption = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithRandom)
      paramCipherParameters = ((ParametersWithRandom)paramCipherParameters).getParameters(); 
    this.key = (NaccacheSternKeyParameters)paramCipherParameters;
    if (!this.forEncryption) {
      if (this.debug)
        System.out.println("Constructing lookup Array"); 
      NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters)this.key;
      Vector<BigInteger> vector = naccacheSternPrivateKeyParameters.getSmallPrimes();
      this.lookup = new Vector[vector.size()];
      for (byte b = 0; b < vector.size(); b++) {
        BigInteger bigInteger1 = vector.elementAt(b);
        int i = bigInteger1.intValue();
        this.lookup[b] = new Vector();
        this.lookup[b].addElement(ONE);
        if (this.debug)
          System.out.println("Constructing lookup ArrayList for " + i); 
        BigInteger bigInteger2 = ZERO;
        for (byte b1 = 1; b1 < i; b1++) {
          bigInteger2 = bigInteger2.add(naccacheSternPrivateKeyParameters.getPhi_n());
          BigInteger bigInteger = bigInteger2.divide(bigInteger1);
          this.lookup[b].addElement(naccacheSternPrivateKeyParameters.getG().modPow(bigInteger, naccacheSternPrivateKeyParameters.getModulus()));
        } 
      } 
    } 
  }
  
  public void setDebug(boolean paramBoolean) {
    this.debug = paramBoolean;
  }
  
  public int getInputBlockSize() {
    return this.forEncryption ? ((this.key.getLowerSigmaBound() + 7) / 8 - 1) : (this.key.getModulus().toByteArray()).length;
  }
  
  public int getOutputBlockSize() {
    return this.forEncryption ? (this.key.getModulus().toByteArray()).length : ((this.key.getLowerSigmaBound() + 7) / 8 - 1);
  }
  
  public byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte1;
    byte[] arrayOfByte2;
    if (this.key == null)
      throw new IllegalStateException("NaccacheStern engine not initialised"); 
    if (paramInt2 > getInputBlockSize() + 1)
      throw new DataLengthException("input too large for Naccache-Stern cipher.\n"); 
    if (!this.forEncryption && paramInt2 < getInputBlockSize())
      throw new InvalidCipherTextException("BlockLength does not match modulus for Naccache-Stern cipher.\n"); 
    if (paramInt1 != 0 || paramInt2 != paramArrayOfbyte.length) {
      arrayOfByte1 = new byte[paramInt2];
      System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, paramInt2);
    } else {
      arrayOfByte1 = paramArrayOfbyte;
    } 
    BigInteger bigInteger = new BigInteger(1, arrayOfByte1);
    if (this.debug)
      System.out.println("input as BigInteger: " + bigInteger); 
    if (this.forEncryption) {
      arrayOfByte2 = encrypt(bigInteger);
    } else {
      Vector<BigInteger> vector1 = new Vector();
      NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters)this.key;
      Vector<BigInteger> vector2 = naccacheSternPrivateKeyParameters.getSmallPrimes();
      for (byte b = 0; b < vector2.size(); b++) {
        BigInteger bigInteger2 = bigInteger.modPow(naccacheSternPrivateKeyParameters.getPhi_n().divide(vector2.elementAt(b)), naccacheSternPrivateKeyParameters.getModulus());
        Vector vector = this.lookup[b];
        if (this.lookup[b].size() != ((BigInteger)vector2.elementAt(b)).intValue()) {
          if (this.debug)
            System.out.println("Prime is " + vector2.elementAt(b) + ", lookup table has size " + vector.size()); 
          throw new InvalidCipherTextException("Error in lookup Array for " + ((BigInteger)vector2.elementAt(b)).intValue() + ": Size mismatch. Expected ArrayList with length " + ((BigInteger)vector2.elementAt(b)).intValue() + " but found ArrayList of length " + this.lookup[b].size());
        } 
        int i = vector.indexOf(bigInteger2);
        if (i == -1) {
          if (this.debug) {
            System.out.println("Actual prime is " + vector2.elementAt(b));
            System.out.println("Decrypted value is " + bigInteger2);
            System.out.println("LookupList for " + vector2.elementAt(b) + " with size " + this.lookup[b].size() + " is: ");
            for (byte b1 = 0; b1 < this.lookup[b].size(); b1++)
              System.out.println(this.lookup[b].elementAt(b1)); 
          } 
          throw new InvalidCipherTextException("Lookup failed");
        } 
        vector1.addElement(BigInteger.valueOf(i));
      } 
      BigInteger bigInteger1 = chineseRemainder(vector1, vector2);
      arrayOfByte2 = bigInteger1.toByteArray();
    } 
    return arrayOfByte2;
  }
  
  public byte[] encrypt(BigInteger paramBigInteger) {
    byte[] arrayOfByte1 = this.key.getModulus().toByteArray();
    Arrays.fill(arrayOfByte1, (byte)0);
    byte[] arrayOfByte2 = this.key.getG().modPow(paramBigInteger, this.key.getModulus()).toByteArray();
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte2.length, arrayOfByte2.length);
    if (this.debug)
      System.out.println("Encrypted value is:  " + new BigInteger(arrayOfByte1)); 
    return arrayOfByte1;
  }
  
  public byte[] addCryptedBlocks(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws InvalidCipherTextException {
    if (this.forEncryption) {
      if (paramArrayOfbyte1.length > getOutputBlockSize() || paramArrayOfbyte2.length > getOutputBlockSize())
        throw new InvalidCipherTextException("BlockLength too large for simple addition.\n"); 
    } else if (paramArrayOfbyte1.length > getInputBlockSize() || paramArrayOfbyte2.length > getInputBlockSize()) {
      throw new InvalidCipherTextException("BlockLength too large for simple addition.\n");
    } 
    BigInteger bigInteger1 = new BigInteger(1, paramArrayOfbyte1);
    BigInteger bigInteger2 = new BigInteger(1, paramArrayOfbyte2);
    BigInteger bigInteger3 = bigInteger1.multiply(bigInteger2);
    bigInteger3 = bigInteger3.mod(this.key.getModulus());
    if (this.debug) {
      System.out.println("c(m1) as BigInteger:....... " + bigInteger1);
      System.out.println("c(m2) as BigInteger:....... " + bigInteger2);
      System.out.println("c(m1)*c(m2)%n = c(m1+m2)%n: " + bigInteger3);
    } 
    byte[] arrayOfByte = this.key.getModulus().toByteArray();
    Arrays.fill(arrayOfByte, (byte)0);
    System.arraycopy(bigInteger3.toByteArray(), 0, arrayOfByte, arrayOfByte.length - (bigInteger3.toByteArray()).length, (bigInteger3.toByteArray()).length);
    return arrayOfByte;
  }
  
  public byte[] processData(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    if (this.debug)
      System.out.println(); 
    if (paramArrayOfbyte.length > getInputBlockSize()) {
      int i = getInputBlockSize();
      int j = getOutputBlockSize();
      if (this.debug) {
        System.out.println("Input blocksize is:  " + i + " bytes");
        System.out.println("Output blocksize is: " + j + " bytes");
        System.out.println("Data has length:.... " + paramArrayOfbyte.length + " bytes");
      } 
      int k = 0;
      int m = 0;
      byte[] arrayOfByte1 = new byte[(paramArrayOfbyte.length / i + 1) * j];
      while (k < paramArrayOfbyte.length) {
        byte[] arrayOfByte;
        if (k + i < paramArrayOfbyte.length) {
          arrayOfByte = processBlock(paramArrayOfbyte, k, i);
          k += i;
        } else {
          arrayOfByte = processBlock(paramArrayOfbyte, k, paramArrayOfbyte.length - k);
          k += paramArrayOfbyte.length - k;
        } 
        if (this.debug)
          System.out.println("new datapos is " + k); 
        if (arrayOfByte != null) {
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, m, arrayOfByte.length);
          m += arrayOfByte.length;
          continue;
        } 
        if (this.debug)
          System.out.println("cipher returned null"); 
        throw new InvalidCipherTextException("cipher returned null");
      } 
      byte[] arrayOfByte2 = new byte[m];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, m);
      if (this.debug)
        System.out.println("returning " + arrayOfByte2.length + " bytes"); 
      return arrayOfByte2;
    } 
    if (this.debug)
      System.out.println("data size is less then input block size, processing directly"); 
    return processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  private static BigInteger chineseRemainder(Vector<BigInteger> paramVector1, Vector<BigInteger> paramVector2) {
    BigInteger bigInteger1 = ZERO;
    BigInteger bigInteger2 = ONE;
    byte b;
    for (b = 0; b < paramVector2.size(); b++)
      bigInteger2 = bigInteger2.multiply(paramVector2.elementAt(b)); 
    for (b = 0; b < paramVector2.size(); b++) {
      BigInteger bigInteger3 = paramVector2.elementAt(b);
      BigInteger bigInteger4 = bigInteger2.divide(bigInteger3);
      BigInteger bigInteger5 = bigInteger4.modInverse(bigInteger3);
      BigInteger bigInteger6 = bigInteger4.multiply(bigInteger5);
      bigInteger6 = bigInteger6.multiply(paramVector1.elementAt(b));
      bigInteger1 = bigInteger1.add(bigInteger6);
    } 
    return bigInteger1.mod(bigInteger2);
  }
}
