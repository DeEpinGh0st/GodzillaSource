package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;

public class SignatureSpi extends SignatureSpi {
  private Digest digest;
  
  private RainbowSigner signer;
  
  private SecureRandom random;
  
  protected SignatureSpi(Digest paramDigest, RainbowSigner paramRainbowSigner) {
    this.digest = paramDigest;
    this.signer = paramRainbowSigner;
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter = RainbowKeysToParams.generatePublicKeyParameter(paramPublicKey);
    this.digest.reset();
    this.signer.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.random = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    ParametersWithRandom parametersWithRandom;
    AsymmetricKeyParameter asymmetricKeyParameter = RainbowKeysToParams.generatePrivateKeyParameter(paramPrivateKey);
    if (this.random != null)
      parametersWithRandom = new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, this.random); 
    this.digest.reset();
    this.signer.init(true, (CipherParameters)parametersWithRandom);
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
  
  public static class withSha224 extends SignatureSpi {
    public withSha224() {
      super((Digest)new SHA224Digest(), new RainbowSigner());
    }
  }
  
  public static class withSha256 extends SignatureSpi {
    public withSha256() {
      super((Digest)new SHA256Digest(), new RainbowSigner());
    }
  }
  
  public static class withSha384 extends SignatureSpi {
    public withSha384() {
      super((Digest)new SHA384Digest(), new RainbowSigner());
    }
  }
  
  public static class withSha512 extends SignatureSpi {
    public withSha512() {
      super((Digest)new SHA512Digest(), new RainbowSigner());
    }
  }
}
