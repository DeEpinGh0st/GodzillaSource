package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSADigestSigner;

public abstract class TlsDSASigner extends AbstractTlsSigner {
  public byte[] generateRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte) throws CryptoException {
    Signer signer = makeSigner(paramSignatureAndHashAlgorithm, true, true, (CipherParameters)new ParametersWithRandom((CipherParameters)paramAsymmetricKeyParameter, this.context.getSecureRandom()));
    if (paramSignatureAndHashAlgorithm == null) {
      signer.update(paramArrayOfbyte, 16, 20);
    } else {
      signer.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    } 
    return signer.generateSignature();
  }
  
  public boolean verifyRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, byte[] paramArrayOfbyte1, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte2) throws CryptoException {
    Signer signer = makeSigner(paramSignatureAndHashAlgorithm, true, false, (CipherParameters)paramAsymmetricKeyParameter);
    if (paramSignatureAndHashAlgorithm == null) {
      signer.update(paramArrayOfbyte2, 16, 20);
    } else {
      signer.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    } 
    return signer.verifySignature(paramArrayOfbyte1);
  }
  
  public Signer createSigner(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return makeSigner(paramSignatureAndHashAlgorithm, false, true, (CipherParameters)paramAsymmetricKeyParameter);
  }
  
  public Signer createVerifyer(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return makeSigner(paramSignatureAndHashAlgorithm, false, false, (CipherParameters)paramAsymmetricKeyParameter);
  }
  
  protected CipherParameters makeInitParameters(boolean paramBoolean, CipherParameters paramCipherParameters) {
    return paramCipherParameters;
  }
  
  protected Signer makeSigner(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, boolean paramBoolean1, boolean paramBoolean2, CipherParameters paramCipherParameters) {
    if (((paramSignatureAndHashAlgorithm != null)) != TlsUtils.isTLSv12(this.context))
      throw new IllegalStateException(); 
    if (paramSignatureAndHashAlgorithm != null && paramSignatureAndHashAlgorithm.getSignature() != getSignatureAlgorithm())
      throw new IllegalStateException(); 
    boolean bool = (paramSignatureAndHashAlgorithm == null) ? true : paramSignatureAndHashAlgorithm.getHash();
    Digest digest = (Digest)(paramBoolean1 ? new NullDigest() : TlsUtils.createHash(bool));
    DSADigestSigner dSADigestSigner = new DSADigestSigner(createDSAImpl(bool), digest);
    dSADigestSigner.init(paramBoolean2, makeInitParameters(paramBoolean2, paramCipherParameters));
    return (Signer)dSADigestSigner;
  }
  
  protected abstract short getSignatureAlgorithm();
  
  protected abstract DSA createDSAImpl(short paramShort);
}
