package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSalt;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class ISO9796d2PSSSigner implements SignerWithRecovery {
  public static final int TRAILER_IMPLICIT = 188;
  
  public static final int TRAILER_RIPEMD160 = 12748;
  
  public static final int TRAILER_RIPEMD128 = 13004;
  
  public static final int TRAILER_SHA1 = 13260;
  
  public static final int TRAILER_SHA256 = 13516;
  
  public static final int TRAILER_SHA512 = 13772;
  
  public static final int TRAILER_SHA384 = 14028;
  
  public static final int TRAILER_WHIRLPOOL = 14284;
  
  private Digest digest;
  
  private AsymmetricBlockCipher cipher;
  
  private SecureRandom random;
  
  private byte[] standardSalt;
  
  private int hLen;
  
  private int trailer;
  
  private int keyBits;
  
  private byte[] block;
  
  private byte[] mBuf;
  
  private int messageLength;
  
  private int saltLength;
  
  private boolean fullMessage;
  
  private byte[] recoveredMessage;
  
  private byte[] preSig;
  
  private byte[] preBlock;
  
  private int preMStart;
  
  private int preTLength;
  
  public ISO9796d2PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, int paramInt, boolean paramBoolean) {
    this.cipher = paramAsymmetricBlockCipher;
    this.digest = paramDigest;
    this.hLen = paramDigest.getDigestSize();
    this.saltLength = paramInt;
    if (paramBoolean) {
      this.trailer = 188;
    } else {
      Integer integer = ISOTrailers.getTrailer(paramDigest);
      if (integer != null) {
        this.trailer = integer.intValue();
      } else {
        throw new IllegalArgumentException("no valid trailer for digest: " + paramDigest.getAlgorithmName());
      } 
    } 
  }
  
  public ISO9796d2PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, int paramInt) {
    this(paramAsymmetricBlockCipher, paramDigest, paramInt, false);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    RSAKeyParameters rSAKeyParameters;
    int i = this.saltLength;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      rSAKeyParameters = (RSAKeyParameters)parametersWithRandom.getParameters();
      if (paramBoolean)
        this.random = parametersWithRandom.getRandom(); 
    } else if (paramCipherParameters instanceof ParametersWithSalt) {
      ParametersWithSalt parametersWithSalt = (ParametersWithSalt)paramCipherParameters;
      rSAKeyParameters = (RSAKeyParameters)parametersWithSalt.getParameters();
      this.standardSalt = parametersWithSalt.getSalt();
      i = this.standardSalt.length;
      if (this.standardSalt.length != this.saltLength)
        throw new IllegalArgumentException("Fixed salt is of wrong length"); 
    } else {
      rSAKeyParameters = (RSAKeyParameters)paramCipherParameters;
      if (paramBoolean)
        this.random = new SecureRandom(); 
    } 
    this.cipher.init(paramBoolean, (CipherParameters)rSAKeyParameters);
    this.keyBits = rSAKeyParameters.getModulus().bitLength();
    this.block = new byte[(this.keyBits + 7) / 8];
    if (this.trailer == 188) {
      this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - i - 1 - 1];
    } else {
      this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - i - 1 - 2];
    } 
    reset();
  }
  
  private boolean isSameAs(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    boolean bool = true;
    if (this.messageLength != paramArrayOfbyte2.length)
      bool = false; 
    for (byte b = 0; b != paramArrayOfbyte2.length; b++) {
      if (paramArrayOfbyte1[b] != paramArrayOfbyte2[b])
        bool = false; 
    } 
    return bool;
  }
  
  private void clearBlock(byte[] paramArrayOfbyte) {
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = 0; 
  }
  
  public void updateWithRecoveredMessage(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    byte b1;
    byte[] arrayOfByte1 = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    if (arrayOfByte1.length < (this.keyBits + 7) / 8) {
      byte[] arrayOfByte = new byte[(this.keyBits + 7) / 8];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, arrayOfByte.length - arrayOfByte1.length, arrayOfByte1.length);
      clearBlock(arrayOfByte1);
      arrayOfByte1 = arrayOfByte;
    } 
    if ((arrayOfByte1[arrayOfByte1.length - 1] & 0xFF ^ 0xBC) == 0) {
      b1 = 1;
    } else {
      int i = (arrayOfByte1[arrayOfByte1.length - 2] & 0xFF) << 8 | arrayOfByte1[arrayOfByte1.length - 1] & 0xFF;
      Integer integer = ISOTrailers.getTrailer(this.digest);
      if (integer != null) {
        if (i != integer.intValue())
          throw new IllegalStateException("signer initialised with wrong digest for trailer " + i); 
      } else {
        throw new IllegalArgumentException("unrecognised hash in signature");
      } 
      b1 = 2;
    } 
    byte[] arrayOfByte2 = new byte[this.hLen];
    this.digest.doFinal(arrayOfByte2, 0);
    byte[] arrayOfByte3 = maskGeneratorFunction1(arrayOfByte1, arrayOfByte1.length - this.hLen - b1, this.hLen, arrayOfByte1.length - this.hLen - b1);
    byte b2;
    for (b2 = 0; b2 != arrayOfByte3.length; b2++)
      arrayOfByte1[b2] = (byte)(arrayOfByte1[b2] ^ arrayOfByte3[b2]); 
    arrayOfByte1[0] = (byte)(arrayOfByte1[0] & Byte.MAX_VALUE);
    for (b2 = 0; b2 != arrayOfByte1.length && arrayOfByte1[b2] != 1; b2++);
    if (++b2 >= arrayOfByte1.length)
      clearBlock(arrayOfByte1); 
    this.fullMessage = (b2 > 1);
    this.recoveredMessage = new byte[arrayOfByte3.length - b2 - this.saltLength];
    System.arraycopy(arrayOfByte1, b2, this.recoveredMessage, 0, this.recoveredMessage.length);
    System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
    this.preSig = paramArrayOfbyte;
    this.preBlock = arrayOfByte1;
    this.preMStart = b2;
    this.preTLength = b1;
  }
  
  public void update(byte paramByte) {
    if (this.preSig == null && this.messageLength < this.mBuf.length) {
      this.mBuf[this.messageLength++] = paramByte;
    } else {
      this.digest.update(paramByte);
    } 
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.preSig == null)
      while (paramInt2 > 0 && this.messageLength < this.mBuf.length) {
        update(paramArrayOfbyte[paramInt1]);
        paramInt1++;
        paramInt2--;
      }  
    if (paramInt2 > 0)
      this.digest.update(paramArrayOfbyte, paramInt1, paramInt2); 
  }
  
  public void reset() {
    this.digest.reset();
    this.messageLength = 0;
    if (this.mBuf != null)
      clearBlock(this.mBuf); 
    if (this.recoveredMessage != null) {
      clearBlock(this.recoveredMessage);
      this.recoveredMessage = null;
    } 
    this.fullMessage = false;
    if (this.preSig != null) {
      this.preSig = null;
      clearBlock(this.preBlock);
      this.preBlock = null;
    } 
  }
  
  public byte[] generateSignature() throws CryptoException {
    byte[] arrayOfByte3;
    int i = this.digest.getDigestSize();
    byte[] arrayOfByte1 = new byte[i];
    this.digest.doFinal(arrayOfByte1, 0);
    byte[] arrayOfByte2 = new byte[8];
    LtoOSP((this.messageLength * 8), arrayOfByte2);
    this.digest.update(arrayOfByte2, 0, arrayOfByte2.length);
    this.digest.update(this.mBuf, 0, this.messageLength);
    this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    if (this.standardSalt != null) {
      arrayOfByte3 = this.standardSalt;
    } else {
      arrayOfByte3 = new byte[this.saltLength];
      this.random.nextBytes(arrayOfByte3);
    } 
    this.digest.update(arrayOfByte3, 0, arrayOfByte3.length);
    byte[] arrayOfByte4 = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte4, 0);
    byte b1 = 2;
    if (this.trailer == 188)
      b1 = 1; 
    int j = this.block.length - this.messageLength - arrayOfByte3.length - this.hLen - b1 - 1;
    this.block[j] = 1;
    System.arraycopy(this.mBuf, 0, this.block, j + 1, this.messageLength);
    System.arraycopy(arrayOfByte3, 0, this.block, j + 1 + this.messageLength, arrayOfByte3.length);
    byte[] arrayOfByte5 = maskGeneratorFunction1(arrayOfByte4, 0, arrayOfByte4.length, this.block.length - this.hLen - b1);
    for (byte b2 = 0; b2 != arrayOfByte5.length; b2++)
      this.block[b2] = (byte)(this.block[b2] ^ arrayOfByte5[b2]); 
    System.arraycopy(arrayOfByte4, 0, this.block, this.block.length - this.hLen - b1, this.hLen);
    if (this.trailer == 188) {
      this.block[this.block.length - 1] = -68;
    } else {
      this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
      this.block[this.block.length - 1] = (byte)this.trailer;
    } 
    this.block[0] = (byte)(this.block[0] & Byte.MAX_VALUE);
    byte[] arrayOfByte6 = this.cipher.processBlock(this.block, 0, this.block.length);
    this.recoveredMessage = new byte[this.messageLength];
    this.fullMessage = (this.messageLength <= this.mBuf.length);
    System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
    clearBlock(this.mBuf);
    clearBlock(this.block);
    this.messageLength = 0;
    return arrayOfByte6;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = new byte[this.hLen];
    this.digest.doFinal(arrayOfByte1, 0);
    int j = 0;
    if (this.preSig == null) {
      try {
        updateWithRecoveredMessage(paramArrayOfbyte);
      } catch (Exception exception) {
        return false;
      } 
    } else if (!Arrays.areEqual(this.preSig, paramArrayOfbyte)) {
      throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
    } 
    byte[] arrayOfByte2 = this.preBlock;
    j = this.preMStart;
    int i = this.preTLength;
    this.preSig = null;
    this.preBlock = null;
    byte[] arrayOfByte3 = new byte[8];
    LtoOSP((this.recoveredMessage.length * 8), arrayOfByte3);
    this.digest.update(arrayOfByte3, 0, arrayOfByte3.length);
    if (this.recoveredMessage.length != 0)
      this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length); 
    this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    if (this.standardSalt != null) {
      this.digest.update(this.standardSalt, 0, this.standardSalt.length);
    } else {
      this.digest.update(arrayOfByte2, j + this.recoveredMessage.length, this.saltLength);
    } 
    byte[] arrayOfByte4 = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte4, 0);
    int k = arrayOfByte2.length - i - arrayOfByte4.length;
    boolean bool = true;
    for (byte b = 0; b != arrayOfByte4.length; b++) {
      if (arrayOfByte4[b] != arrayOfByte2[k + b])
        bool = false; 
    } 
    clearBlock(arrayOfByte2);
    clearBlock(arrayOfByte4);
    if (!bool) {
      this.fullMessage = false;
      this.messageLength = 0;
      clearBlock(this.recoveredMessage);
      return false;
    } 
    if (this.messageLength != 0 && !isSameAs(this.mBuf, this.recoveredMessage)) {
      this.messageLength = 0;
      clearBlock(this.mBuf);
      return false;
    } 
    this.messageLength = 0;
    clearBlock(this.mBuf);
    return true;
  }
  
  public boolean hasFullMessage() {
    return this.fullMessage;
  }
  
  public byte[] getRecoveredMessage() {
    return this.recoveredMessage;
  }
  
  private void ItoOSP(int paramInt, byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = (byte)(paramInt >>> 24);
    paramArrayOfbyte[1] = (byte)(paramInt >>> 16);
    paramArrayOfbyte[2] = (byte)(paramInt >>> 8);
    paramArrayOfbyte[3] = (byte)(paramInt >>> 0);
  }
  
  private void LtoOSP(long paramLong, byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = (byte)(int)(paramLong >>> 56L);
    paramArrayOfbyte[1] = (byte)(int)(paramLong >>> 48L);
    paramArrayOfbyte[2] = (byte)(int)(paramLong >>> 40L);
    paramArrayOfbyte[3] = (byte)(int)(paramLong >>> 32L);
    paramArrayOfbyte[4] = (byte)(int)(paramLong >>> 24L);
    paramArrayOfbyte[5] = (byte)(int)(paramLong >>> 16L);
    paramArrayOfbyte[6] = (byte)(int)(paramLong >>> 8L);
    paramArrayOfbyte[7] = (byte)(int)(paramLong >>> 0L);
  }
  
  private byte[] maskGeneratorFunction1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte1 = new byte[paramInt3];
    byte[] arrayOfByte2 = new byte[this.hLen];
    byte[] arrayOfByte3 = new byte[4];
    byte b = 0;
    this.digest.reset();
    while (b < paramInt3 / this.hLen) {
      ItoOSP(b, arrayOfByte3);
      this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.digest.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.digest.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * this.hLen, this.hLen);
      b++;
    } 
    if (b * this.hLen < paramInt3) {
      ItoOSP(b, arrayOfByte3);
      this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.digest.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.digest.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * this.hLen, arrayOfByte1.length - b * this.hLen);
    } 
    return arrayOfByte1;
  }
}
