package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Memoable;

public class GMSSStateAwareSigner implements StateAwareMessageSigner {
  private final GMSSSigner gmssSigner;
  
  private GMSSPrivateKeyParameters key;
  
  public GMSSStateAwareSigner(Digest paramDigest) {
    if (!(paramDigest instanceof Memoable))
      throw new IllegalArgumentException("digest must implement Memoable"); 
    final Memoable dig = ((Memoable)paramDigest).copy();
    this.gmssSigner = new GMSSSigner(new GMSSDigestProvider() {
          public Digest get() {
            return (Digest)dig.copy();
          }
        });
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean)
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.key = (GMSSPrivateKeyParameters)parametersWithRandom.getParameters();
      } else {
        this.key = (GMSSPrivateKeyParameters)paramCipherParameters;
      }  
    this.gmssSigner.init(paramBoolean, paramCipherParameters);
  }
  
  public byte[] generateSignature(byte[] paramArrayOfbyte) {
    if (this.key == null)
      throw new IllegalStateException("signing key no longer usable"); 
    byte[] arrayOfByte = this.gmssSigner.generateSignature(paramArrayOfbyte);
    this.key = this.key.nextKey();
    return arrayOfByte;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return this.gmssSigner.verifySignature(paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public AsymmetricKeyParameter getUpdatedPrivateKey() {
    GMSSPrivateKeyParameters gMSSPrivateKeyParameters = this.key;
    this.key = null;
    return gMSSPrivateKeyParameters;
  }
}
