package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DigestingMessageSigner implements Signer {
  private final Digest messDigest;
  
  private final MessageSigner messSigner;
  
  private boolean forSigning;
  
  public DigestingMessageSigner(MessageSigner paramMessageSigner, Digest paramDigest) {
    this.messSigner = paramMessageSigner;
    this.messDigest = paramDigest;
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
      throw new IllegalArgumentException("Signing Requires Private Key."); 
    if (!paramBoolean && asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("Verification Requires Public Key."); 
    reset();
    this.messSigner.init(paramBoolean, paramCipherParameters);
  }
  
  public byte[] generateSignature() {
    if (!this.forSigning)
      throw new IllegalStateException("DigestingMessageSigner not initialised for signature generation."); 
    byte[] arrayOfByte = new byte[this.messDigest.getDigestSize()];
    this.messDigest.doFinal(arrayOfByte, 0);
    return this.messSigner.generateSignature(arrayOfByte);
  }
  
  public void update(byte paramByte) {
    this.messDigest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.messDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void reset() {
    this.messDigest.reset();
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    if (this.forSigning)
      throw new IllegalStateException("DigestingMessageSigner not initialised for verification"); 
    byte[] arrayOfByte = new byte[this.messDigest.getDigestSize()];
    this.messDigest.doFinal(arrayOfByte, 0);
    return this.messSigner.verifySignature(arrayOfByte, paramArrayOfbyte);
  }
}
