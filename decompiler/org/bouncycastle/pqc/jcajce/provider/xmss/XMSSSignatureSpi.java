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
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSSigner;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;

public class XMSSSignatureSpi extends Signature implements StateAwareSignature {
  private Digest digest;
  
  private XMSSSigner signer;
  
  private SecureRandom random;
  
  private ASN1ObjectIdentifier treeDigest;
  
  protected XMSSSignatureSpi(String paramString) {
    super(paramString);
  }
  
  protected XMSSSignatureSpi(String paramString, Digest paramDigest, XMSSSigner paramXMSSSigner) {
    super(paramString);
    this.digest = paramDigest;
    this.signer = paramXMSSSigner;
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCXMSSPublicKey) {
      CipherParameters cipherParameters = ((BCXMSSPublicKey)paramPublicKey).getKeyParams();
      this.treeDigest = null;
      this.digest.reset();
      this.signer.init(false, cipherParameters);
    } else {
      throw new InvalidKeyException("unknown public key passed to XMSS");
    } 
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.random = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof BCXMSSPrivateKey) {
      ParametersWithRandom parametersWithRandom;
      CipherParameters cipherParameters = ((BCXMSSPrivateKey)paramPrivateKey).getKeyParams();
      this.treeDigest = ((BCXMSSPrivateKey)paramPrivateKey).getTreeDigestOID();
      if (this.random != null)
        parametersWithRandom = new ParametersWithRandom(cipherParameters, this.random); 
      this.digest.reset();
      this.signer.init(true, (CipherParameters)parametersWithRandom);
    } else {
      throw new InvalidKeyException("unknown private key passed to XMSS");
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
    BCXMSSPrivateKey bCXMSSPrivateKey = new BCXMSSPrivateKey(this.treeDigest, (XMSSPrivateKeyParameters)this.signer.getUpdatedPrivateKey());
    this.treeDigest = null;
    return bCXMSSPrivateKey;
  }
  
  public static class withSha256 extends XMSSSignatureSpi {
    public withSha256() {
      super("SHA256withXMSS", (Digest)new SHA256Digest(), new XMSSSigner());
    }
  }
  
  public static class withSha512 extends XMSSSignatureSpi {
    public withSha512() {
      super("SHA512withXMSS", (Digest)new SHA512Digest(), new XMSSSigner());
    }
  }
  
  public static class withShake128 extends XMSSSignatureSpi {
    public withShake128() {
      super("SHAKE128withXMSSMT", (Digest)new SHAKEDigest(128), new XMSSSigner());
    }
  }
  
  public static class withShake256 extends XMSSSignatureSpi {
    public withShake256() {
      super("SHAKE256withXMSS", (Digest)new SHAKEDigest(256), new XMSSSigner());
    }
  }
}
