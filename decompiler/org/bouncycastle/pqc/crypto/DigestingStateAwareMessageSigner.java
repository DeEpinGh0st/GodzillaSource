package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DigestingStateAwareMessageSigner extends DigestingMessageSigner {
  private final StateAwareMessageSigner signer;
  
  public DigestingStateAwareMessageSigner(StateAwareMessageSigner paramStateAwareMessageSigner, Digest paramDigest) {
    super(paramStateAwareMessageSigner, paramDigest);
    this.signer = paramStateAwareMessageSigner;
  }
  
  public AsymmetricKeyParameter getUpdatedPrivateKey() {
    return this.signer.getUpdatedPrivateKey();
  }
}
