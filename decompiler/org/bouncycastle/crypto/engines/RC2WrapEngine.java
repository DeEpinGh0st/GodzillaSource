package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class RC2WrapEngine implements Wrapper {
  private CBCBlockCipher engine;
  
  private CipherParameters param;
  
  private ParametersWithIV paramPlusIV;
  
  private byte[] iv;
  
  private boolean forWrapping;
  
  private SecureRandom sr;
  
  private static final byte[] IV2 = new byte[] { 74, -35, -94, 44, 121, -24, 33, 5 };
  
  Digest sha1 = DigestFactory.createSHA1();
  
  byte[] digest = new byte[20];
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forWrapping = paramBoolean;
    this.engine = new CBCBlockCipher(new RC2Engine());
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.sr = parametersWithRandom.getRandom();
      paramCipherParameters = parametersWithRandom.getParameters();
    } else {
      this.sr = new SecureRandom();
    } 
    if (paramCipherParameters instanceof ParametersWithIV) {
      this.paramPlusIV = (ParametersWithIV)paramCipherParameters;
      this.iv = this.paramPlusIV.getIV();
      this.param = this.paramPlusIV.getParameters();
      if (this.forWrapping) {
        if (this.iv == null || this.iv.length != 8)
          throw new IllegalArgumentException("IV is not 8 octets"); 
      } else {
        throw new IllegalArgumentException("You should not supply an IV for unwrapping");
      } 
    } else {
      this.param = paramCipherParameters;
      if (this.forWrapping) {
        this.iv = new byte[8];
        this.sr.nextBytes(this.iv);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
      } 
    } 
  }
  
  public String getAlgorithmName() {
    return "RC2";
  }
  
  public byte[] wrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (!this.forWrapping)
      throw new IllegalStateException("Not initialized for wrapping"); 
    int i = paramInt2 + 1;
    if (i % 8 != 0)
      i += 8 - i % 8; 
    byte[] arrayOfByte1 = new byte[i];
    arrayOfByte1[0] = (byte)paramInt2;
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 1, paramInt2);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length - paramInt2 - 1];
    if (arrayOfByte2.length > 0) {
      this.sr.nextBytes(arrayOfByte2);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramInt2 + 1, arrayOfByte2.length);
    } 
    byte[] arrayOfByte3 = calculateCMSKeyChecksum(arrayOfByte1);
    byte[] arrayOfByte4 = new byte[arrayOfByte1.length + arrayOfByte3.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte4, 0, arrayOfByte1.length);
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, arrayOfByte1.length, arrayOfByte3.length);
    byte[] arrayOfByte5 = new byte[arrayOfByte4.length];
    System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte4.length);
    int j = arrayOfByte4.length / this.engine.getBlockSize();
    int k = arrayOfByte4.length % this.engine.getBlockSize();
    if (k != 0)
      throw new IllegalStateException("Not multiple of block length"); 
    this.engine.init(true, (CipherParameters)this.paramPlusIV);
    for (byte b1 = 0; b1 < j; b1++) {
      int m = b1 * this.engine.getBlockSize();
      this.engine.processBlock(arrayOfByte5, m, arrayOfByte5, m);
    } 
    byte[] arrayOfByte6 = new byte[this.iv.length + arrayOfByte5.length];
    System.arraycopy(this.iv, 0, arrayOfByte6, 0, this.iv.length);
    System.arraycopy(arrayOfByte5, 0, arrayOfByte6, this.iv.length, arrayOfByte5.length);
    byte[] arrayOfByte7 = new byte[arrayOfByte6.length];
    for (byte b2 = 0; b2 < arrayOfByte6.length; b2++)
      arrayOfByte7[b2] = arrayOfByte6[arrayOfByte6.length - b2 + 1]; 
    ParametersWithIV parametersWithIV = new ParametersWithIV(this.param, IV2);
    this.engine.init(true, (CipherParameters)parametersWithIV);
    for (byte b3 = 0; b3 < j + 1; b3++) {
      int m = b3 * this.engine.getBlockSize();
      this.engine.processBlock(arrayOfByte7, m, arrayOfByte7, m);
    } 
    return arrayOfByte7;
  }
  
  public byte[] unwrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    if (this.forWrapping)
      throw new IllegalStateException("Not set for unwrapping"); 
    if (paramArrayOfbyte == null)
      throw new InvalidCipherTextException("Null pointer as ciphertext"); 
    if (paramInt2 % this.engine.getBlockSize() != 0)
      throw new InvalidCipherTextException("Ciphertext not multiple of " + this.engine.getBlockSize()); 
    ParametersWithIV parametersWithIV = new ParametersWithIV(this.param, IV2);
    this.engine.init(false, (CipherParameters)parametersWithIV);
    byte[] arrayOfByte1 = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, paramInt2);
    for (byte b1 = 0; b1 < arrayOfByte1.length / this.engine.getBlockSize(); b1++) {
      int i = b1 * this.engine.getBlockSize();
      this.engine.processBlock(arrayOfByte1, i, arrayOfByte1, i);
    } 
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    for (byte b2 = 0; b2 < arrayOfByte1.length; b2++)
      arrayOfByte2[b2] = arrayOfByte1[arrayOfByte1.length - b2 + 1]; 
    this.iv = new byte[8];
    byte[] arrayOfByte3 = new byte[arrayOfByte2.length - 8];
    System.arraycopy(arrayOfByte2, 0, this.iv, 0, 8);
    System.arraycopy(arrayOfByte2, 8, arrayOfByte3, 0, arrayOfByte2.length - 8);
    this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
    this.engine.init(false, (CipherParameters)this.paramPlusIV);
    byte[] arrayOfByte4 = new byte[arrayOfByte3.length];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, arrayOfByte3.length);
    for (byte b3 = 0; b3 < arrayOfByte4.length / this.engine.getBlockSize(); b3++) {
      int i = b3 * this.engine.getBlockSize();
      this.engine.processBlock(arrayOfByte4, i, arrayOfByte4, i);
    } 
    byte[] arrayOfByte5 = new byte[arrayOfByte4.length - 8];
    byte[] arrayOfByte6 = new byte[8];
    System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte4.length - 8);
    System.arraycopy(arrayOfByte4, arrayOfByte4.length - 8, arrayOfByte6, 0, 8);
    if (!checkCMSKeyChecksum(arrayOfByte5, arrayOfByte6))
      throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted"); 
    if (arrayOfByte5.length - (arrayOfByte5[0] & 0xFF) + 1 > 7)
      throw new InvalidCipherTextException("too many pad bytes (" + (arrayOfByte5.length - (arrayOfByte5[0] & 0xFF) + 1) + ")"); 
    byte[] arrayOfByte7 = new byte[arrayOfByte5[0]];
    System.arraycopy(arrayOfByte5, 1, arrayOfByte7, 0, arrayOfByte7.length);
    return arrayOfByte7;
  }
  
  private byte[] calculateCMSKeyChecksum(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[8];
    this.sha1.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    this.sha1.doFinal(this.digest, 0);
    System.arraycopy(this.digest, 0, arrayOfByte, 0, 8);
    return arrayOfByte;
  }
  
  private boolean checkCMSKeyChecksum(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return Arrays.constantTimeAreEqual(calculateCMSKeyChecksum(paramArrayOfbyte1), paramArrayOfbyte2);
  }
}
