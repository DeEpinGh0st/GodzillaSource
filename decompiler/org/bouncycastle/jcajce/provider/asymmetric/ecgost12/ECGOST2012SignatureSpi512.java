package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410_2012Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECGOST2012SignatureSpi512 extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
  private Digest digest = (Digest)new GOST3411_2012_512Digest();
  
  private DSA signer = (DSA)new ECGOST3410_2012Signer();
  
  private int size = 128;
  
  private int halfSize = 64;
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter;
    if (paramPublicKey instanceof org.bouncycastle.jce.interfaces.ECPublicKey) {
      asymmetricKeyParameter = generatePublicKeyParameter(paramPublicKey);
    } else {
      try {
        byte[] arrayOfByte = paramPublicKey.getEncoded();
        paramPublicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(arrayOfByte));
        asymmetricKeyParameter = ECUtil.generatePublicKeyParameter(paramPublicKey);
      } catch (Exception exception) {
        throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
      } 
    } 
    this.digest.reset();
    this.signer.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter;
    if (paramPrivateKey instanceof org.bouncycastle.jce.interfaces.ECKey) {
      asymmetricKeyParameter = ECUtil.generatePrivateKeyParameter(paramPrivateKey);
    } else {
      throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
    } 
    this.digest.reset();
    if (this.appRandom != null) {
      this.signer.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, this.appRandom));
    } else {
      this.signer.init(true, (CipherParameters)asymmetricKeyParameter);
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
      byte[] arrayOfByte1 = new byte[this.size];
      BigInteger[] arrayOfBigInteger = this.signer.generateSignature(arrayOfByte);
      byte[] arrayOfByte2 = arrayOfBigInteger[0].toByteArray();
      byte[] arrayOfByte3 = arrayOfBigInteger[1].toByteArray();
      if (arrayOfByte3[0] != 0) {
        System.arraycopy(arrayOfByte3, 0, arrayOfByte1, this.halfSize - arrayOfByte3.length, arrayOfByte3.length);
      } else {
        System.arraycopy(arrayOfByte3, 1, arrayOfByte1, this.halfSize - arrayOfByte3.length - 1, arrayOfByte3.length - 1);
      } 
      if (arrayOfByte2[0] != 0) {
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, this.size - arrayOfByte2.length, arrayOfByte2.length);
      } else {
        System.arraycopy(arrayOfByte2, 1, arrayOfByte1, this.size - arrayOfByte2.length - 1, arrayOfByte2.length - 1);
      } 
      return arrayOfByte1;
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    BigInteger[] arrayOfBigInteger;
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      byte[] arrayOfByte1 = new byte[this.halfSize];
      byte[] arrayOfByte2 = new byte[this.halfSize];
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte2, 0, this.halfSize);
      System.arraycopy(paramArrayOfbyte, this.halfSize, arrayOfByte1, 0, this.halfSize);
      arrayOfBigInteger = new BigInteger[2];
      arrayOfBigInteger[0] = new BigInteger(1, arrayOfByte1);
      arrayOfBigInteger[1] = new BigInteger(1, arrayOfByte2);
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
  
  static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    return (paramPublicKey instanceof BCECGOST3410_2012PublicKey) ? (AsymmetricKeyParameter)((BCECGOST3410_2012PublicKey)paramPublicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(paramPublicKey);
  }
}
