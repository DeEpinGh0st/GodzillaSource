package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class PSSSignatureSpi extends SignatureSpi {
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  private AlgorithmParameters engineParams;
  
  private PSSParameterSpec paramSpec;
  
  private PSSParameterSpec originalSpec;
  
  private AsymmetricBlockCipher signer;
  
  private Digest contentDigest;
  
  private Digest mgfDigest;
  
  private int saltLength;
  
  private byte trailer;
  
  private boolean isRaw;
  
  private PSSSigner pss;
  
  private byte getTrailer(int paramInt) {
    if (paramInt == 1)
      return -68; 
    throw new IllegalArgumentException("unknown trailer field");
  }
  
  private void setupContentDigest() {
    if (this.isRaw) {
      this.contentDigest = new NullPssDigest(this.mgfDigest);
    } else {
      this.contentDigest = this.mgfDigest;
    } 
  }
  
  protected PSSSignatureSpi(AsymmetricBlockCipher paramAsymmetricBlockCipher, PSSParameterSpec paramPSSParameterSpec) {
    this(paramAsymmetricBlockCipher, paramPSSParameterSpec, false);
  }
  
  protected PSSSignatureSpi(AsymmetricBlockCipher paramAsymmetricBlockCipher, PSSParameterSpec paramPSSParameterSpec, boolean paramBoolean) {
    this.signer = paramAsymmetricBlockCipher;
    this.originalSpec = paramPSSParameterSpec;
    if (paramPSSParameterSpec == null) {
      this.paramSpec = PSSParameterSpec.DEFAULT;
    } else {
      this.paramSpec = paramPSSParameterSpec;
    } 
    this.mgfDigest = DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm());
    this.saltLength = this.paramSpec.getSaltLength();
    this.trailer = getTrailer(this.paramSpec.getTrailerField());
    this.isRaw = paramBoolean;
    setupContentDigest();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (!(paramPublicKey instanceof RSAPublicKey))
      throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance"); 
    this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
    this.pss.init(false, (CipherParameters)RSAUtil.generatePublicKeyParameter((RSAPublicKey)paramPublicKey));
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    if (!(paramPrivateKey instanceof RSAPrivateKey))
      throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance"); 
    this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
    this.pss.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)paramPrivateKey), paramSecureRandom));
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (!(paramPrivateKey instanceof RSAPrivateKey))
      throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance"); 
    this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
    this.pss.init(true, (CipherParameters)RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)paramPrivateKey));
  }
  
  protected void engineUpdate(byte paramByte) throws SignatureException {
    this.pss.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    this.pss.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected byte[] engineSign() throws SignatureException {
    try {
      return this.pss.generateSignature();
    } catch (CryptoException cryptoException) {
      throw new SignatureException(cryptoException.getMessage());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    return this.pss.verifySignature(paramArrayOfbyte);
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec instanceof PSSParameterSpec) {
      PSSParameterSpec pSSParameterSpec = (PSSParameterSpec)paramAlgorithmParameterSpec;
      if (this.originalSpec != null && !DigestFactory.isSameDigest(this.originalSpec.getDigestAlgorithm(), pSSParameterSpec.getDigestAlgorithm()))
        throw new InvalidAlgorithmParameterException("parameter must be using " + this.originalSpec.getDigestAlgorithm()); 
      if (!pSSParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !pSSParameterSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId()))
        throw new InvalidAlgorithmParameterException("unknown mask generation function specified"); 
      if (!(pSSParameterSpec.getMGFParameters() instanceof MGF1ParameterSpec))
        throw new InvalidAlgorithmParameterException("unknown MGF parameters"); 
      MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec)pSSParameterSpec.getMGFParameters();
      if (!DigestFactory.isSameDigest(mGF1ParameterSpec.getDigestAlgorithm(), pSSParameterSpec.getDigestAlgorithm()))
        throw new InvalidAlgorithmParameterException("digest algorithm for MGF should be the same as for PSS parameters."); 
      Digest digest = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
      if (digest == null)
        throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mGF1ParameterSpec.getDigestAlgorithm()); 
      this.engineParams = null;
      this.paramSpec = pSSParameterSpec;
      this.mgfDigest = digest;
      this.saltLength = this.paramSpec.getSaltLength();
      this.trailer = getTrailer(this.paramSpec.getTrailerField());
      setupContentDigest();
    } else {
      throw new InvalidAlgorithmParameterException("Only PSSParameterSpec supported");
    } 
  }
  
  protected AlgorithmParameters engineGetParameters() {
    if (this.engineParams == null && this.paramSpec != null)
      try {
        this.engineParams = this.helper.createAlgorithmParameters("PSS");
        this.engineParams.init(this.paramSpec);
      } catch (Exception exception) {
        throw new RuntimeException(exception.toString());
      }  
    return this.engineParams;
  }
  
  protected void engineSetParameter(String paramString, Object paramObject) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  protected Object engineGetParameter(String paramString) {
    throw new UnsupportedOperationException("engineGetParameter unsupported");
  }
  
  private class NullPssDigest implements Digest {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    
    private Digest baseDigest;
    
    private boolean oddTime = true;
    
    public NullPssDigest(Digest param1Digest) {
      this.baseDigest = param1Digest;
    }
    
    public String getAlgorithmName() {
      return "NULL";
    }
    
    public int getDigestSize() {
      return this.baseDigest.getDigestSize();
    }
    
    public void update(byte param1Byte) {
      this.bOut.write(param1Byte);
    }
    
    public void update(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      this.bOut.write(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public int doFinal(byte[] param1ArrayOfbyte, int param1Int) {
      byte[] arrayOfByte = this.bOut.toByteArray();
      if (this.oddTime) {
        System.arraycopy(arrayOfByte, 0, param1ArrayOfbyte, param1Int, arrayOfByte.length);
      } else {
        this.baseDigest.update(arrayOfByte, 0, arrayOfByte.length);
        this.baseDigest.doFinal(param1ArrayOfbyte, param1Int);
      } 
      reset();
      this.oddTime = !this.oddTime;
      return arrayOfByte.length;
    }
    
    public void reset() {
      this.bOut.reset();
      this.baseDigest.reset();
    }
    
    public int getByteLength() {
      return 0;
    }
  }
  
  public static class PSSwithRSA extends PSSSignatureSpi {
    public PSSwithRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), null);
    }
  }
  
  public static class SHA1withRSA extends PSSSignatureSpi {
    public SHA1withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), PSSParameterSpec.DEFAULT);
    }
  }
  
  public static class SHA224withRSA extends PSSSignatureSpi {
    public SHA224withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), 28, 1));
    }
  }
  
  public static class SHA256withRSA extends PSSSignatureSpi {
    public SHA256withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
    }
  }
  
  public static class SHA384withRSA extends PSSSignatureSpi {
    public SHA384withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
    }
  }
  
  public static class SHA3_224withRSA extends PSSSignatureSpi {
    public SHA3_224withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), 28, 1));
    }
  }
  
  public static class SHA3_256withRSA extends PSSSignatureSpi {
    public SHA3_256withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1));
    }
  }
  
  public static class SHA3_384withRSA extends PSSSignatureSpi {
    public SHA3_384withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), 48, 1));
    }
  }
  
  public static class SHA3_512withRSA extends PSSSignatureSpi {
    public SHA3_512withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), 64, 1));
    }
  }
  
  public static class SHA512_224withRSA extends PSSSignatureSpi {
    public SHA512_224withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "MGF1", new MGF1ParameterSpec("SHA-512(224)"), 28, 1));
    }
  }
  
  public static class SHA512_256withRSA extends PSSSignatureSpi {
    public SHA512_256withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "MGF1", new MGF1ParameterSpec("SHA-512(256)"), 32, 1));
    }
  }
  
  public static class SHA512withRSA extends PSSSignatureSpi {
    public SHA512withRSA() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1));
    }
  }
  
  public static class nonePSS extends PSSSignatureSpi {
    public nonePSS() {
      super((AsymmetricBlockCipher)new RSABlindedEngine(), null, true);
    }
  }
}
