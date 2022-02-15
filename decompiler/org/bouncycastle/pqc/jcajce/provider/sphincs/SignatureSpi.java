package org.bouncycastle.pqc.jcajce.provider.sphincs;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;

public class SignatureSpi extends SignatureSpi {
  private Digest digest;
  
  private SPHINCS256Signer signer;
  
  private SecureRandom random;
  
  protected SignatureSpi(Digest paramDigest, SPHINCS256Signer paramSPHINCS256Signer) {
    this.digest = paramDigest;
    this.signer = paramSPHINCS256Signer;
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCSphincs256PublicKey) {
      CipherParameters cipherParameters = ((BCSphincs256PublicKey)paramPublicKey).getKeyParams();
      this.digest.reset();
      this.signer.init(false, cipherParameters);
    } else {
      throw new InvalidKeyException("unknown public key passed to SPHINCS-256");
    } 
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.random = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCSphincs256PrivateKey) {
      ParametersWithRandom parametersWithRandom;
      CipherParameters cipherParameters = ((BCSphincs256PrivateKey)paramPrivateKey).getKeyParams();
      if (this.random != null)
        parametersWithRandom = new ParametersWithRandom(cipherParameters, this.random); 
      this.digest.reset();
      this.signer.init(true, (CipherParameters)parametersWithRandom);
    } else {
      throw new InvalidKeyException("unknown private key passed to SPHINCS-256");
    } 
  }
  
  protected void engineUpdate(byte paramByte) throws SignatureException {
    this.digest.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected byte[] engineSign() throws SignatureException {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      return this.signer.generateSignature(arrayOfByte);
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    return this.signer.verifySignature(arrayOfByte, paramArrayOfbyte);
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  protected void engineSetParameter(String paramString, Object paramObject) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  protected Object engineGetParameter(String paramString) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  public static class withSha3_512 extends SignatureSpi {
    public withSha3_512() {
      super((Digest)new SHA3Digest(512), new SPHINCS256Signer((Digest)new SHA3Digest(256), (Digest)new SHA3Digest(512)));
    }
  }
  
  public static class withSha512 extends SignatureSpi {
    public withSha512() {
      super((Digest)new SHA512Digest(), new SPHINCS256Signer((Digest)new SHA512tDigest(256), (Digest)new SHA512Digest()));
    }
  }
}
