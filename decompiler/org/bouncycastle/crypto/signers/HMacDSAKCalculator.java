package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class HMacDSAKCalculator implements DSAKCalculator {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private final HMac hMac;
  
  private final byte[] K;
  
  private final byte[] V;
  
  private BigInteger n;
  
  public HMacDSAKCalculator(Digest paramDigest) {
    this.hMac = new HMac(paramDigest);
    this.V = new byte[this.hMac.getMacSize()];
    this.K = new byte[this.hMac.getMacSize()];
  }
  
  public boolean isDeterministic() {
    return true;
  }
  
  public void init(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    throw new IllegalStateException("Operation not supported");
  }
  
  public void init(BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
    this.n = paramBigInteger1;
    Arrays.fill(this.V, (byte)1);
    Arrays.fill(this.K, (byte)0);
    byte[] arrayOfByte1 = new byte[(paramBigInteger1.bitLength() + 7) / 8];
    byte[] arrayOfByte2 = BigIntegers.asUnsignedByteArray(paramBigInteger2);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte2.length, arrayOfByte2.length);
    byte[] arrayOfByte3 = new byte[(paramBigInteger1.bitLength() + 7) / 8];
    BigInteger bigInteger = bitsToInt(paramArrayOfbyte);
    if (bigInteger.compareTo(paramBigInteger1) >= 0)
      bigInteger = bigInteger.subtract(paramBigInteger1); 
    byte[] arrayOfByte4 = BigIntegers.asUnsignedByteArray(bigInteger);
    System.arraycopy(arrayOfByte4, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte4.length, arrayOfByte4.length);
    this.hMac.init((CipherParameters)new KeyParameter(this.K));
    this.hMac.update(this.V, 0, this.V.length);
    this.hMac.update((byte)0);
    this.hMac.update(arrayOfByte1, 0, arrayOfByte1.length);
    this.hMac.update(arrayOfByte3, 0, arrayOfByte3.length);
    this.hMac.doFinal(this.K, 0);
    this.hMac.init((CipherParameters)new KeyParameter(this.K));
    this.hMac.update(this.V, 0, this.V.length);
    this.hMac.doFinal(this.V, 0);
    this.hMac.update(this.V, 0, this.V.length);
    this.hMac.update((byte)1);
    this.hMac.update(arrayOfByte1, 0, arrayOfByte1.length);
    this.hMac.update(arrayOfByte3, 0, arrayOfByte3.length);
    this.hMac.doFinal(this.K, 0);
    this.hMac.init((CipherParameters)new KeyParameter(this.K));
    this.hMac.update(this.V, 0, this.V.length);
    this.hMac.doFinal(this.V, 0);
  }
  
  public BigInteger nextK() {
    byte[] arrayOfByte = new byte[(this.n.bitLength() + 7) / 8];
    while (true) {
      for (int i = 0; i < arrayOfByte.length; i += j) {
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        int j = Math.min(arrayOfByte.length - i, this.V.length);
        System.arraycopy(this.V, 0, arrayOfByte, i, j);
      } 
      BigInteger bigInteger = bitsToInt(arrayOfByte);
      if (bigInteger.compareTo(ZERO) > 0 && bigInteger.compareTo(this.n) < 0)
        return bigInteger; 
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.update((byte)0);
      this.hMac.doFinal(this.K, 0);
      this.hMac.init((CipherParameters)new KeyParameter(this.K));
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.doFinal(this.V, 0);
    } 
  }
  
  private BigInteger bitsToInt(byte[] paramArrayOfbyte) {
    BigInteger bigInteger = new BigInteger(1, paramArrayOfbyte);
    if (paramArrayOfbyte.length * 8 > this.n.bitLength())
      bigInteger = bigInteger.shiftRight(paramArrayOfbyte.length * 8 - this.n.bitLength()); 
    return bigInteger;
  }
}
