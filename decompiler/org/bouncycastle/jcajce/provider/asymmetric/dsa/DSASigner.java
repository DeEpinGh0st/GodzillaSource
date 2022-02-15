package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
  private Digest digest;
  
  private DSA signer;
  
  private SecureRandom random;
  
  protected DSASigner(Digest paramDigest, DSA paramDSA) {
    this.digest = paramDigest;
    this.signer = paramDSA;
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter = DSAUtil.generatePublicKeyParameter(paramPublicKey);
    this.digest.reset();
    this.signer.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.random = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    ParametersWithRandom parametersWithRandom;
    AsymmetricKeyParameter asymmetricKeyParameter = DSAUtil.generatePrivateKeyParameter(paramPrivateKey);
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
      BigInteger[] arrayOfBigInteger = this.signer.generateSignature(arrayOfByte);
      return derEncode(arrayOfBigInteger[0], arrayOfBigInteger[1]);
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    BigInteger[] arrayOfBigInteger;
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      arrayOfBigInteger = derDecode(paramArrayOfbyte);
    } catch (Exception exception) {
      throw new SignatureException("error decoding signature bytes.");
    } 
    return this.signer.verifySignature(arrayOfByte, arrayOfBigInteger[0], arrayOfBigInteger[1]);
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
  
  private byte[] derEncode(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws IOException {
    ASN1Integer[] arrayOfASN1Integer = { new ASN1Integer(paramBigInteger1), new ASN1Integer(paramBigInteger2) };
    return (new DERSequence((ASN1Encodable[])arrayOfASN1Integer)).getEncoded("DER");
  }
  
  private BigInteger[] derDecode(byte[] paramArrayOfbyte) throws IOException {
    ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(paramArrayOfbyte);
    if (aSN1Sequence.size() != 2)
      throw new IOException("malformed signature"); 
    if (!Arrays.areEqual(paramArrayOfbyte, aSN1Sequence.getEncoded("DER")))
      throw new IOException("malformed signature"); 
    return new BigInteger[] { ((ASN1Integer)aSN1Sequence.getObjectAt(0)).getValue(), ((ASN1Integer)aSN1Sequence.getObjectAt(1)).getValue() };
  }
  
  public static class detDSA extends DSASigner {
    public detDSA() {
      super(DigestFactory.createSHA1(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA1())));
    }
  }
  
  public static class detDSA224 extends DSASigner {
    public detDSA224() {
      super(DigestFactory.createSHA224(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA224())));
    }
  }
  
  public static class detDSA256 extends DSASigner {
    public detDSA256() {
      super(DigestFactory.createSHA256(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA256())));
    }
  }
  
  public static class detDSA384 extends DSASigner {
    public detDSA384() {
      super(DigestFactory.createSHA384(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA384())));
    }
  }
  
  public static class detDSA512 extends DSASigner {
    public detDSA512() {
      super(DigestFactory.createSHA512(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA512())));
    }
  }
  
  public static class detDSASha3_224 extends DSASigner {
    public detDSASha3_224() {
      super(DigestFactory.createSHA3_224(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
    }
  }
  
  public static class detDSASha3_256 extends DSASigner {
    public detDSASha3_256() {
      super(DigestFactory.createSHA3_256(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
    }
  }
  
  public static class detDSASha3_384 extends DSASigner {
    public detDSASha3_384() {
      super(DigestFactory.createSHA3_384(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
    }
  }
  
  public static class detDSASha3_512 extends DSASigner {
    public detDSASha3_512() {
      super(DigestFactory.createSHA3_512(), (DSA)new org.bouncycastle.crypto.signers.DSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
    }
  }
  
  public static class dsa224 extends DSASigner {
    public dsa224() {
      super(DigestFactory.createSHA224(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsa256 extends DSASigner {
    public dsa256() {
      super(DigestFactory.createSHA256(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsa384 extends DSASigner {
    public dsa384() {
      super(DigestFactory.createSHA384(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsa512 extends DSASigner {
    public dsa512() {
      super(DigestFactory.createSHA512(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsaSha3_224 extends DSASigner {
    public dsaSha3_224() {
      super(DigestFactory.createSHA3_224(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsaSha3_256 extends DSASigner {
    public dsaSha3_256() {
      super(DigestFactory.createSHA3_256(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsaSha3_384 extends DSASigner {
    public dsaSha3_384() {
      super(DigestFactory.createSHA3_384(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class dsaSha3_512 extends DSASigner {
    public dsaSha3_512() {
      super(DigestFactory.createSHA3_512(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class noneDSA extends DSASigner {
    public noneDSA() {
      super((Digest)new NullDigest(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
  
  public static class stdDSA extends DSASigner {
    public stdDSA() {
      super(DigestFactory.createSHA1(), (DSA)new org.bouncycastle.crypto.signers.DSASigner());
    }
  }
}
