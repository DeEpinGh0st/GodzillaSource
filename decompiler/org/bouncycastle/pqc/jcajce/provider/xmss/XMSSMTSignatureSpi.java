package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;

public class XMSSMTSignatureSpi extends Signature implements StateAwareSignature {
  private Digest digest;
  
  private XMSSMTSigner signer;
  
  private ASN1ObjectIdentifier treeDigest;
  
  private SecureRandom random;
  
  protected XMSSMTSignatureSpi(String paramString) {
    super(paramString);
  }
  
  protected XMSSMTSignatureSpi(String paramString, Digest paramDigest, XMSSMTSigner paramXMSSMTSigner) {
    super(paramString);
    this.digest = paramDigest;
    this.signer = paramXMSSMTSigner;
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCXMSSMTPublicKey) {
      CipherParameters cipherParameters = ((BCXMSSMTPublicKey)paramPublicKey).getKeyParams();
      this.treeDigest = null;
      this.digest.reset();
      this.signer.init(false, cipherParameters);
    } else {
      throw new InvalidKeyException("unknown public key passed to XMSSMT");
    } 
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.random = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCXMSSMTPrivateKey) {
      ParametersWithRandom parametersWithRandom;
      CipherParameters cipherParameters = ((BCXMSSMTPrivateKey)paramPrivateKey).getKeyParams();
      this.treeDigest = ((BCXMSSMTPrivateKey)paramPrivateKey).getTreeDigestOID();
      if (this.random != null)
        parametersWithRandom = new ParametersWithRandom(cipherParameters, this.random); 
      this.digest.reset();
      this.signer.init(true, (CipherParameters)parametersWithRandom);
    } else {
      throw new InvalidKeyException("unknown private key passed to XMSSMT");
    } 
  }
  
  protected void engineUpdate(byte paramByte) throws SignatureException {
    this.digest.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected byte[] engineSign() throws SignatureException {
    byte[] arrayOfByte = DigestUtil.getDigestResult(this.digest);
    try {
      return this.signer.generateSignature(arrayOfByte);
    } catch (Exception exception) {
      if (exception instanceof IllegalStateException)
        throw new SignatureException(exception.getMessage()); 
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    byte[] arrayOfByte = DigestUtil.getDigestResult(this.digest);
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
  
  public PrivateKey getUpdatedPrivateKey() {
    if (this.treeDigest == null)
      throw new IllegalStateException("signature object not in a signing state"); 
    BCXMSSMTPrivateKey bCXMSSMTPrivateKey = new BCXMSSMTPrivateKey(this.treeDigest, (XMSSMTPrivateKeyParameters)this.signer.getUpdatedPrivateKey());
    this.treeDigest = null;
    return bCXMSSMTPrivateKey;
  }
  
  public static class withSha256 extends XMSSMTSignatureSpi {
    public withSha256() {
      super("SHA256withXMSSMT", (Digest)new SHA256Digest(), new XMSSMTSigner());
    }
  }
  
  public static class withSha512 extends XMSSMTSignatureSpi {
    public withSha512() {
      super("SHA512withXMSSMT", (Digest)new SHA512Digest(), new XMSSMTSigner());
    }
  }
  
  public static class withShake128 extends XMSSMTSignatureSpi {
    public withShake128() {
      super("SHAKE128withXMSSMT", (Digest)new SHAKEDigest(128), new XMSSMTSigner());
    }
  }
  
  public static class withShake256 extends XMSSMTSignatureSpi {
    public withShake256() {
      super("SHAKE256withXMSSMT", (Digest)new SHAKEDigest(256), new XMSSMTSigner());
    }
  }
}
