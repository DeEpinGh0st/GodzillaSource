package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class ISO9796d2Signer implements SignerWithRecovery {
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
  
  private int trailer;
  
  private int keyBits;
  
  private byte[] block;
  
  private byte[] mBuf;
  
  private int messageLength;
  
  private boolean fullMessage;
  
  private byte[] recoveredMessage;
  
  private byte[] preSig;
  
  private byte[] preBlock;
  
  public ISO9796d2Signer(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, boolean paramBoolean) {
    this.cipher = paramAsymmetricBlockCipher;
    this.digest = paramDigest;
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
  
  public ISO9796d2Signer(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest) {
    this(paramAsymmetricBlockCipher, paramDigest, false);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    RSAKeyParameters rSAKeyParameters = (RSAKeyParameters)paramCipherParameters;
    this.cipher.init(paramBoolean, (CipherParameters)rSAKeyParameters);
    this.keyBits = rSAKeyParameters.getModulus().bitLength();
    this.block = new byte[(this.keyBits + 7) / 8];
    if (this.trailer == 188) {
      this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 2];
    } else {
      this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 3];
    } 
    reset();
  }
  
  private boolean isSameAs(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    boolean bool = true;
    if (this.messageLength > this.mBuf.length) {
      if (this.mBuf.length > paramArrayOfbyte2.length)
        bool = false; 
      for (byte b = 0; b != this.mBuf.length; b++) {
        if (paramArrayOfbyte1[b] != paramArrayOfbyte2[b])
          bool = false; 
      } 
    } else {
      if (this.messageLength != paramArrayOfbyte2.length)
        bool = false; 
      for (byte b = 0; b != paramArrayOfbyte2.length; b++) {
        if (paramArrayOfbyte1[b] != paramArrayOfbyte2[b])
          bool = false; 
      } 
    } 
    return bool;
  }
  
  private void clearBlock(byte[] paramArrayOfbyte) {
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = 0; 
  }
  
  public void updateWithRecoveredMessage(byte[] paramArrayOfbyte) throws InvalidCipherTextException {
    byte[] arrayOfByte = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    if ((arrayOfByte[0] & 0xC0 ^ 0x40) != 0)
      throw new InvalidCipherTextException("malformed signature"); 
    if ((arrayOfByte[arrayOfByte.length - 1] & 0xF ^ 0xC) != 0)
      throw new InvalidCipherTextException("malformed signature"); 
    byte b1 = 0;
    if ((arrayOfByte[arrayOfByte.length - 1] & 0xFF ^ 0xBC) == 0) {
      b1 = 1;
    } else {
      int j = (arrayOfByte[arrayOfByte.length - 2] & 0xFF) << 8 | arrayOfByte[arrayOfByte.length - 1] & 0xFF;
      Integer integer = ISOTrailers.getTrailer(this.digest);
      if (integer != null) {
        if (j != integer.intValue())
          throw new IllegalStateException("signer initialised with wrong digest for trailer " + j); 
      } else {
        throw new IllegalArgumentException("unrecognised hash in signature");
      } 
      b1 = 2;
    } 
    byte b2 = 0;
    for (b2 = 0; b2 != arrayOfByte.length && (arrayOfByte[b2] & 0xF ^ 0xA) != 0; b2++);
    b2++;
    int i = arrayOfByte.length - b1 - this.digest.getDigestSize();
    if (i - b2 <= 0)
      throw new InvalidCipherTextException("malformed block"); 
    if ((arrayOfByte[0] & 0x20) == 0) {
      this.fullMessage = true;
      this.recoveredMessage = new byte[i - b2];
      System.arraycopy(arrayOfByte, b2, this.recoveredMessage, 0, this.recoveredMessage.length);
    } else {
      this.fullMessage = false;
      this.recoveredMessage = new byte[i - b2];
      System.arraycopy(arrayOfByte, b2, this.recoveredMessage, 0, this.recoveredMessage.length);
    } 
    this.preSig = paramArrayOfbyte;
    this.preBlock = arrayOfByte;
    this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
    this.messageLength = this.recoveredMessage.length;
    System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
    if (this.messageLength < this.mBuf.length)
      this.mBuf[this.messageLength] = paramByte; 
    this.messageLength++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (paramInt2 > 0 && this.messageLength < this.mBuf.length) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
    this.messageLength += paramInt2;
  }
  
  public void reset() {
    this.digest.reset();
    this.messageLength = 0;
    clearBlock(this.mBuf);
    if (this.recoveredMessage != null)
      clearBlock(this.recoveredMessage); 
    this.recoveredMessage = null;
    this.fullMessage = false;
    if (this.preSig != null) {
      this.preSig = null;
      clearBlock(this.preBlock);
      this.preBlock = null;
    } 
  }
  
  public byte[] generateSignature() throws CryptoException {
    int i = this.digest.getDigestSize();
    byte b1 = 0;
    int j = 0;
    if (this.trailer == 188) {
      b1 = 8;
      j = this.block.length - i - 1;
      this.digest.doFinal(this.block, j);
      this.block[this.block.length - 1] = -68;
    } else {
      b1 = 16;
      j = this.block.length - i - 2;
      this.digest.doFinal(this.block, j);
      this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
      this.block[this.block.length - 1] = (byte)this.trailer;
    } 
    byte b2 = 0;
    int k = (i + this.messageLength) * 8 + b1 + 4 - this.keyBits;
    if (k > 0) {
      int m = this.messageLength - (k + 7) / 8;
      b2 = 96;
      j -= m;
      System.arraycopy(this.mBuf, 0, this.block, j, m);
      this.recoveredMessage = new byte[m];
    } else {
      b2 = 64;
      j -= this.messageLength;
      System.arraycopy(this.mBuf, 0, this.block, j, this.messageLength);
      this.recoveredMessage = new byte[this.messageLength];
    } 
    if (j - 1 > 0) {
      for (int m = j - 1; m != 0; m--)
        this.block[m] = -69; 
      this.block[j - 1] = (byte)(this.block[j - 1] ^ 0x1);
      this.block[0] = 11;
      this.block[0] = (byte)(this.block[0] | b2);
    } else {
      this.block[0] = 10;
      this.block[0] = (byte)(this.block[0] | b2);
    } 
    byte[] arrayOfByte = this.cipher.processBlock(this.block, 0, this.block.length);
    this.fullMessage = ((b2 & 0x20) == 0);
    System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
    this.messageLength = 0;
    clearBlock(this.mBuf);
    clearBlock(this.block);
    return arrayOfByte;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = null;
    if (this.preSig == null) {
      try {
        arrayOfByte1 = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      } catch (Exception exception) {
        return false;
      } 
    } else {
      if (!Arrays.areEqual(this.preSig, paramArrayOfbyte))
        throw new IllegalStateException("updateWithRecoveredMessage called on different signature"); 
      arrayOfByte1 = this.preBlock;
      this.preSig = null;
      this.preBlock = null;
    } 
    if ((arrayOfByte1[0] & 0xC0 ^ 0x40) != 0)
      return returnFalse(arrayOfByte1); 
    if ((arrayOfByte1[arrayOfByte1.length - 1] & 0xF ^ 0xC) != 0)
      return returnFalse(arrayOfByte1); 
    byte b1 = 0;
    if ((arrayOfByte1[arrayOfByte1.length - 1] & 0xFF ^ 0xBC) == 0) {
      b1 = 1;
    } else {
      int j = (arrayOfByte1[arrayOfByte1.length - 2] & 0xFF) << 8 | arrayOfByte1[arrayOfByte1.length - 1] & 0xFF;
      Integer integer = ISOTrailers.getTrailer(this.digest);
      if (integer != null) {
        if (j != integer.intValue())
          throw new IllegalStateException("signer initialised with wrong digest for trailer " + j); 
      } else {
        throw new IllegalArgumentException("unrecognised hash in signature");
      } 
      b1 = 2;
    } 
    byte b2 = 0;
    for (b2 = 0; b2 != arrayOfByte1.length && (arrayOfByte1[b2] & 0xF ^ 0xA) != 0; b2++);
    b2++;
    byte[] arrayOfByte2 = new byte[this.digest.getDigestSize()];
    int i = arrayOfByte1.length - b1 - arrayOfByte2.length;
    if (i - b2 <= 0)
      return returnFalse(arrayOfByte1); 
    if ((arrayOfByte1[0] & 0x20) == 0) {
      this.fullMessage = true;
      if (this.messageLength > i - b2)
        return returnFalse(arrayOfByte1); 
      this.digest.reset();
      this.digest.update(arrayOfByte1, b2, i - b2);
      this.digest.doFinal(arrayOfByte2, 0);
      boolean bool = true;
      for (byte b = 0; b != arrayOfByte2.length; b++) {
        arrayOfByte1[i + b] = (byte)(arrayOfByte1[i + b] ^ arrayOfByte2[b]);
        if (arrayOfByte1[i + b] != 0)
          bool = false; 
      } 
      if (!bool)
        return returnFalse(arrayOfByte1); 
      this.recoveredMessage = new byte[i - b2];
      System.arraycopy(arrayOfByte1, b2, this.recoveredMessage, 0, this.recoveredMessage.length);
    } else {
      this.fullMessage = false;
      this.digest.doFinal(arrayOfByte2, 0);
      boolean bool = true;
      for (byte b = 0; b != arrayOfByte2.length; b++) {
        arrayOfByte1[i + b] = (byte)(arrayOfByte1[i + b] ^ arrayOfByte2[b]);
        if (arrayOfByte1[i + b] != 0)
          bool = false; 
      } 
      if (!bool)
        return returnFalse(arrayOfByte1); 
      this.recoveredMessage = new byte[i - b2];
      System.arraycopy(arrayOfByte1, b2, this.recoveredMessage, 0, this.recoveredMessage.length);
    } 
    if (this.messageLength != 0 && !isSameAs(this.mBuf, this.recoveredMessage))
      return returnFalse(arrayOfByte1); 
    clearBlock(this.mBuf);
    clearBlock(arrayOfByte1);
    this.messageLength = 0;
    return true;
  }
  
  private boolean returnFalse(byte[] paramArrayOfbyte) {
    this.messageLength = 0;
    clearBlock(this.mBuf);
    clearBlock(paramArrayOfbyte);
    return false;
  }
  
  public boolean hasFullMessage() {
    return this.fullMessage;
  }
  
  public byte[] getRecoveredMessage() {
    return this.recoveredMessage;
  }
}
