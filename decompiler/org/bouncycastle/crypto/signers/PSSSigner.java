package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class PSSSigner implements Signer {
  public static final byte TRAILER_IMPLICIT = -68;
  
  private Digest contentDigest;
  
  private Digest mgfDigest;
  
  private AsymmetricBlockCipher cipher;
  
  private SecureRandom random;
  
  private int hLen;
  
  private int mgfhLen;
  
  private boolean sSet;
  
  private int sLen;
  
  private int emBits;
  
  private byte[] salt;
  
  private byte[] mDash;
  
  private byte[] block;
  
  private byte trailer;
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, int paramInt) {
    this(paramAsymmetricBlockCipher, paramDigest, paramInt, (byte)-68);
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest1, Digest paramDigest2, int paramInt) {
    this(paramAsymmetricBlockCipher, paramDigest1, paramDigest2, paramInt, (byte)-68);
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, int paramInt, byte paramByte) {
    this(paramAsymmetricBlockCipher, paramDigest, paramDigest, paramInt, paramByte);
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest1, Digest paramDigest2, int paramInt, byte paramByte) {
    this.cipher = paramAsymmetricBlockCipher;
    this.contentDigest = paramDigest1;
    this.mgfDigest = paramDigest2;
    this.hLen = paramDigest1.getDigestSize();
    this.mgfhLen = paramDigest2.getDigestSize();
    this.sSet = false;
    this.sLen = paramInt;
    this.salt = new byte[paramInt];
    this.mDash = new byte[8 + paramInt + this.hLen];
    this.trailer = paramByte;
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, byte[] paramArrayOfbyte) {
    this(paramAsymmetricBlockCipher, paramDigest, paramDigest, paramArrayOfbyte, (byte)-68);
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest1, Digest paramDigest2, byte[] paramArrayOfbyte) {
    this(paramAsymmetricBlockCipher, paramDigest1, paramDigest2, paramArrayOfbyte, (byte)-68);
  }
  
  public PSSSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest1, Digest paramDigest2, byte[] paramArrayOfbyte, byte paramByte) {
    this.cipher = paramAsymmetricBlockCipher;
    this.contentDigest = paramDigest1;
    this.mgfDigest = paramDigest2;
    this.hLen = paramDigest1.getDigestSize();
    this.mgfhLen = paramDigest2.getDigestSize();
    this.sSet = true;
    this.sLen = paramArrayOfbyte.length;
    this.salt = paramArrayOfbyte;
    this.mDash = new byte[8 + this.sLen + this.hLen];
    this.trailer = paramByte;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    CipherParameters cipherParameters;
    RSAKeyParameters rSAKeyParameters;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      cipherParameters = parametersWithRandom.getParameters();
      this.random = parametersWithRandom.getRandom();
    } else {
      cipherParameters = paramCipherParameters;
      if (paramBoolean)
        this.random = new SecureRandom(); 
    } 
    if (cipherParameters instanceof RSABlindingParameters) {
      rSAKeyParameters = ((RSABlindingParameters)cipherParameters).getPublicKey();
      this.cipher.init(paramBoolean, paramCipherParameters);
    } else {
      rSAKeyParameters = (RSAKeyParameters)cipherParameters;
      this.cipher.init(paramBoolean, cipherParameters);
    } 
    this.emBits = rSAKeyParameters.getModulus().bitLength() - 1;
    if (this.emBits < 8 * this.hLen + 8 * this.sLen + 9)
      throw new IllegalArgumentException("key too small for specified hash and salt lengths"); 
    this.block = new byte[(this.emBits + 7) / 8];
    reset();
  }
  
  private void clearBlock(byte[] paramArrayOfbyte) {
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = 0; 
  }
  
  public void update(byte paramByte) {
    this.contentDigest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.contentDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void reset() {
    this.contentDigest.reset();
  }
  
  public byte[] generateSignature() throws CryptoException, DataLengthException {
    this.contentDigest.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);
    if (this.sLen != 0) {
      if (!this.sSet)
        this.random.nextBytes(this.salt); 
      System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
    } 
    byte[] arrayOfByte1 = new byte[this.hLen];
    this.contentDigest.update(this.mDash, 0, this.mDash.length);
    this.contentDigest.doFinal(arrayOfByte1, 0);
    this.block[this.block.length - this.sLen - 1 - this.hLen - 1] = 1;
    System.arraycopy(this.salt, 0, this.block, this.block.length - this.sLen - this.hLen - 1, this.sLen);
    byte[] arrayOfByte2 = maskGeneratorFunction1(arrayOfByte1, 0, arrayOfByte1.length, this.block.length - this.hLen - 1);
    for (byte b = 0; b != arrayOfByte2.length; b++)
      this.block[b] = (byte)(this.block[b] ^ arrayOfByte2[b]); 
    this.block[0] = (byte)(this.block[0] & 255 >> this.block.length * 8 - this.emBits);
    System.arraycopy(arrayOfByte1, 0, this.block, this.block.length - this.hLen - 1, this.hLen);
    this.block[this.block.length - 1] = this.trailer;
    byte[] arrayOfByte3 = this.cipher.processBlock(this.block, 0, this.block.length);
    clearBlock(this.block);
    return arrayOfByte3;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    this.contentDigest.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);
    try {
      byte[] arrayOfByte1 = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      System.arraycopy(arrayOfByte1, 0, this.block, this.block.length - arrayOfByte1.length, arrayOfByte1.length);
    } catch (Exception exception) {
      return false;
    } 
    if (this.block[this.block.length - 1] != this.trailer) {
      clearBlock(this.block);
      return false;
    } 
    byte[] arrayOfByte = maskGeneratorFunction1(this.block, this.block.length - this.hLen - 1, this.hLen, this.block.length - this.hLen - 1);
    int i;
    for (i = 0; i != arrayOfByte.length; i++)
      this.block[i] = (byte)(this.block[i] ^ arrayOfByte[i]); 
    this.block[0] = (byte)(this.block[0] & 255 >> this.block.length * 8 - this.emBits);
    for (i = 0; i != this.block.length - this.hLen - this.sLen - 2; i++) {
      if (this.block[i] != 0) {
        clearBlock(this.block);
        return false;
      } 
    } 
    if (this.block[this.block.length - this.hLen - this.sLen - 2] != 1) {
      clearBlock(this.block);
      return false;
    } 
    if (this.sSet) {
      System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
    } else {
      System.arraycopy(this.block, this.block.length - this.sLen - this.hLen - 1, this.mDash, this.mDash.length - this.sLen, this.sLen);
    } 
    this.contentDigest.update(this.mDash, 0, this.mDash.length);
    this.contentDigest.doFinal(this.mDash, this.mDash.length - this.hLen);
    i = this.block.length - this.hLen - 1;
    for (int j = this.mDash.length - this.hLen; j != this.mDash.length; j++) {
      if ((this.block[i] ^ this.mDash[j]) != 0) {
        clearBlock(this.mDash);
        clearBlock(this.block);
        return false;
      } 
      i++;
    } 
    clearBlock(this.mDash);
    clearBlock(this.block);
    return true;
  }
  
  private void ItoOSP(int paramInt, byte[] paramArrayOfbyte) {
    paramArrayOfbyte[0] = (byte)(paramInt >>> 24);
    paramArrayOfbyte[1] = (byte)(paramInt >>> 16);
    paramArrayOfbyte[2] = (byte)(paramInt >>> 8);
    paramArrayOfbyte[3] = (byte)(paramInt >>> 0);
  }
  
  private byte[] maskGeneratorFunction1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte1 = new byte[paramInt3];
    byte[] arrayOfByte2 = new byte[this.mgfhLen];
    byte[] arrayOfByte3 = new byte[4];
    byte b = 0;
    this.mgfDigest.reset();
    while (b < paramInt3 / this.mgfhLen) {
      ItoOSP(b, arrayOfByte3);
      this.mgfDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.mgfDigest.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.mgfDigest.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * this.mgfhLen, this.mgfhLen);
      b++;
    } 
    if (b * this.mgfhLen < paramInt3) {
      ItoOSP(b, arrayOfByte3);
      this.mgfDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
      this.mgfDigest.update(arrayOfByte3, 0, arrayOfByte3.length);
      this.mgfDigest.doFinal(arrayOfByte2, 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, b * this.mgfhLen, arrayOfByte1.length - b * this.mgfhLen);
    } 
    return arrayOfByte1;
  }
}
