package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class X931Signer implements Signer {
  public static final int TRAILER_IMPLICIT = 188;
  
  public static final int TRAILER_RIPEMD160 = 12748;
  
  public static final int TRAILER_RIPEMD128 = 13004;
  
  public static final int TRAILER_SHA1 = 13260;
  
  public static final int TRAILER_SHA256 = 13516;
  
  public static final int TRAILER_SHA512 = 13772;
  
  public static final int TRAILER_SHA384 = 14028;
  
  public static final int TRAILER_WHIRLPOOL = 14284;
  
  public static final int TRAILER_SHA224 = 14540;
  
  private Digest digest;
  
  private AsymmetricBlockCipher cipher;
  
  private RSAKeyParameters kParam;
  
  private int trailer;
  
  private int keyBits;
  
  private byte[] block;
  
  public X931Signer(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest, boolean paramBoolean) {
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
  
  public X931Signer(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest) {
    this(paramAsymmetricBlockCipher, paramDigest, false);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.kParam = (RSAKeyParameters)paramCipherParameters;
    this.cipher.init(paramBoolean, (CipherParameters)this.kParam);
    this.keyBits = this.kParam.getModulus().bitLength();
    this.block = new byte[(this.keyBits + 7) / 8];
    reset();
  }
  
  private void clearBlock(byte[] paramArrayOfbyte) {
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      paramArrayOfbyte[b] = 0; 
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void reset() {
    this.digest.reset();
  }
  
  public byte[] generateSignature() throws CryptoException {
    createSignatureBlock();
    BigInteger bigInteger = new BigInteger(1, this.cipher.processBlock(this.block, 0, this.block.length));
    clearBlock(this.block);
    bigInteger = bigInteger.min(this.kParam.getModulus().subtract(bigInteger));
    return BigIntegers.asUnsignedByteArray((this.kParam.getModulus().bitLength() + 7) / 8, bigInteger);
  }
  
  private void createSignatureBlock() {
    int j;
    int i = this.digest.getDigestSize();
    if (this.trailer == 188) {
      j = this.block.length - i - 1;
      this.digest.doFinal(this.block, j);
      this.block[this.block.length - 1] = -68;
    } else {
      j = this.block.length - i - 2;
      this.digest.doFinal(this.block, j);
      this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
      this.block[this.block.length - 1] = (byte)this.trailer;
    } 
    this.block[0] = 107;
    for (int k = j - 2; k != 0; k--)
      this.block[k] = -69; 
    this.block[j - 1] = -70;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    BigInteger bigInteger2;
    try {
      this.block = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    } catch (Exception exception) {
      return false;
    } 
    BigInteger bigInteger1 = new BigInteger(1, this.block);
    if ((bigInteger1.intValue() & 0xF) == 12) {
      bigInteger2 = bigInteger1;
    } else {
      bigInteger1 = this.kParam.getModulus().subtract(bigInteger1);
      if ((bigInteger1.intValue() & 0xF) == 12) {
        bigInteger2 = bigInteger1;
      } else {
        return false;
      } 
    } 
    createSignatureBlock();
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(this.block.length, bigInteger2);
    boolean bool = Arrays.constantTimeAreEqual(this.block, arrayOfByte);
    clearBlock(this.block);
    clearBlock(arrayOfByte);
    return bool;
  }
}
