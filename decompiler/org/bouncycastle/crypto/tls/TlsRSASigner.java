package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.GenericSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;

public class TlsRSASigner extends AbstractTlsSigner {
  public byte[] generateRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte) throws CryptoException {
    Signer signer = makeSigner(paramSignatureAndHashAlgorithm, true, true, (CipherParameters)new ParametersWithRandom((CipherParameters)paramAsymmetricKeyParameter, this.context.getSecureRandom()));
    signer.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    return signer.generateSignature();
  }
  
  public boolean verifyRawSignature(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, byte[] paramArrayOfbyte1, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte2) throws CryptoException {
    Signer signer = makeSigner(paramSignatureAndHashAlgorithm, true, false, (CipherParameters)paramAsymmetricKeyParameter);
    signer.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    return signer.verifySignature(paramArrayOfbyte1);
  }
  
  public Signer createSigner(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return makeSigner(paramSignatureAndHashAlgorithm, false, true, (CipherParameters)new ParametersWithRandom((CipherParameters)paramAsymmetricKeyParameter, this.context.getSecureRandom()));
  }
  
  public Signer createVerifyer(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return makeSigner(paramSignatureAndHashAlgorithm, false, false, (CipherParameters)paramAsymmetricKeyParameter);
  }
  
  public boolean isValidPublicKey(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.RSAKeyParameters && !paramAsymmetricKeyParameter.isPrivate());
  }
  
  protected Signer makeSigner(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, boolean paramBoolean1, boolean paramBoolean2, CipherParameters paramCipherParameters) {
    Digest digest;
    GenericSigner genericSigner;
    if (((paramSignatureAndHashAlgorithm != null)) != TlsUtils.isTLSv12(this.context))
      throw new IllegalStateException(); 
    if (paramSignatureAndHashAlgorithm != null && paramSignatureAndHashAlgorithm.getSignature() != 1)
      throw new IllegalStateException(); 
    if (paramBoolean1) {
      NullDigest nullDigest = new NullDigest();
    } else if (paramSignatureAndHashAlgorithm == null) {
      digest = new CombinedHash();
    } else {
      digest = TlsUtils.createHash(paramSignatureAndHashAlgorithm.getHash());
    } 
    if (paramSignatureAndHashAlgorithm != null) {
      RSADigestSigner rSADigestSigner = new RSADigestSigner(digest, TlsUtils.getOIDForHashAlgorithm(paramSignatureAndHashAlgorithm.getHash()));
    } else {
      genericSigner = new GenericSigner(createRSAImpl(), digest);
    } 
    genericSigner.init(paramBoolean2, paramCipherParameters);
    return (Signer)genericSigner;
  }
  
  protected AsymmetricBlockCipher createRSAImpl() {
    return (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine());
  }
}
