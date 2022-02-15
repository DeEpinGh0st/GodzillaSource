package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class GenericSigner implements Signer {
  private final AsymmetricBlockCipher engine;
  
  private final Digest digest;
  
  private boolean forSigning;
  
  public GenericSigner(AsymmetricBlockCipher paramAsymmetricBlockCipher, Digest paramDigest) {
    this.engine = paramAsymmetricBlockCipher;
    this.digest = paramDigest;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    AsymmetricKeyParameter asymmetricKeyParameter;
    this.forSigning = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)paramCipherParameters).getParameters();
    } else {
      asymmetricKeyParameter = (AsymmetricKeyParameter)paramCipherParameters;
    } 
    if (paramBoolean && !asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("signing requires private key"); 
    if (!paramBoolean && asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("verification requires public key"); 
    reset();
    this.engine.init(paramBoolean, paramCipherParameters);
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] generateSignature() throws CryptoException, DataLengthException {
    if (!this.forSigning)
      throw new IllegalStateException("GenericSigner not initialised for signature generation."); 
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    return this.engine.processBlock(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    if (this.forSigning)
      throw new IllegalStateException("GenericSigner not initialised for verification"); 
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      byte[] arrayOfByte1 = this.engine.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      if (arrayOfByte1.length < arrayOfByte.length) {
        byte[] arrayOfByte2 = new byte[arrayOfByte.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte2.length - arrayOfByte1.length, arrayOfByte1.length);
        arrayOfByte1 = arrayOfByte2;
      } 
      return Arrays.constantTimeAreEqual(arrayOfByte1, arrayOfByte);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public void reset() {
    this.digest.reset();
  }
}
