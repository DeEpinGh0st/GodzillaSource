package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class DigestSignatureSpi extends SignatureSpi {
  private Digest digest;
  
  private AsymmetricBlockCipher cipher;
  
  private AlgorithmIdentifier algId;
  
  protected DigestSignatureSpi(Digest paramDigest, AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.digest = paramDigest;
    this.cipher = paramAsymmetricBlockCipher;
    this.algId = null;
  }
  
  protected DigestSignatureSpi(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Digest paramDigest, AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.digest = paramDigest;
    this.cipher = paramAsymmetricBlockCipher;
    this.algId = new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (!(paramPublicKey instanceof RSAPublicKey))
      throw new InvalidKeyException("Supplied key (" + getType(paramPublicKey) + ") is not a RSAPublicKey instance"); 
    RSAKeyParameters rSAKeyParameters = RSAUtil.generatePublicKeyParameter((RSAPublicKey)paramPublicKey);
    this.digest.reset();
    this.cipher.init(false, (CipherParameters)rSAKeyParameters);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (!(paramPrivateKey instanceof RSAPrivateKey))
      throw new InvalidKeyException("Supplied key (" + getType(paramPrivateKey) + ") is not a RSAPrivateKey instance"); 
    RSAKeyParameters rSAKeyParameters = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)paramPrivateKey);
    this.digest.reset();
    this.cipher.init(true, (CipherParameters)rSAKeyParameters);
  }
  
  private String getType(Object paramObject) {
    return (paramObject == null) ? null : paramObject.getClass().getName();
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
      byte[] arrayOfByte1 = derEncode(arrayOfByte);
      return this.cipher.processBlock(arrayOfByte1, 0, arrayOfByte1.length);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SignatureException("key too small for signature type");
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte3;
    byte[] arrayOfByte1 = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte1, 0);
    try {
      arrayOfByte2 = this.cipher.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      arrayOfByte3 = derEncode(arrayOfByte1);
    } catch (Exception exception) {
      return false;
    } 
    if (arrayOfByte2.length == arrayOfByte3.length)
      return Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte3); 
    if (arrayOfByte2.length == arrayOfByte3.length - 2) {
      arrayOfByte3[1] = (byte)(arrayOfByte3[1] - 2);
      arrayOfByte3[3] = (byte)(arrayOfByte3[3] - 2);
      int i = 4 + arrayOfByte3[3];
      int j = i + 2;
      int k = 0;
      byte b;
      for (b = 0; b < arrayOfByte3.length - j; b++)
        k |= arrayOfByte2[i + b] ^ arrayOfByte3[j + b]; 
      for (b = 0; b < i; b++)
        k |= arrayOfByte2[b] ^ arrayOfByte3[b]; 
      return (k == 0);
    } 
    Arrays.constantTimeAreEqual(arrayOfByte3, arrayOfByte3);
    return false;
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  protected void engineSetParameter(String paramString, Object paramObject) {
    throw new UnsupportedOperationException("engineSetParameter unsupported");
  }
  
  protected Object engineGetParameter(String paramString) {
    return null;
  }
  
  protected AlgorithmParameters engineGetParameters() {
    return null;
  }
  
  private byte[] derEncode(byte[] paramArrayOfbyte) throws IOException {
    if (this.algId == null)
      return paramArrayOfbyte; 
    DigestInfo digestInfo = new DigestInfo(this.algId, paramArrayOfbyte);
    return digestInfo.getEncoded("DER");
  }
  
  public static class MD2 extends DigestSignatureSpi {
    public MD2() {
      super(PKCSObjectIdentifiers.md2, (Digest)new MD2Digest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class MD4 extends DigestSignatureSpi {
    public MD4() {
      super(PKCSObjectIdentifiers.md4, (Digest)new MD4Digest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class MD5 extends DigestSignatureSpi {
    public MD5() {
      super(PKCSObjectIdentifiers.md5, DigestFactory.createMD5(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class RIPEMD128 extends DigestSignatureSpi {
    public RIPEMD128() {
      super(TeleTrusTObjectIdentifiers.ripemd128, (Digest)new RIPEMD128Digest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class RIPEMD160 extends DigestSignatureSpi {
    public RIPEMD160() {
      super(TeleTrusTObjectIdentifiers.ripemd160, (Digest)new RIPEMD160Digest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class RIPEMD256 extends DigestSignatureSpi {
    public RIPEMD256() {
      super(TeleTrusTObjectIdentifiers.ripemd256, (Digest)new RIPEMD256Digest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA1 extends DigestSignatureSpi {
    public SHA1() {
      super(OIWObjectIdentifiers.idSHA1, DigestFactory.createSHA1(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA224 extends DigestSignatureSpi {
    public SHA224() {
      super(NISTObjectIdentifiers.id_sha224, DigestFactory.createSHA224(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA256 extends DigestSignatureSpi {
    public SHA256() {
      super(NISTObjectIdentifiers.id_sha256, DigestFactory.createSHA256(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA384 extends DigestSignatureSpi {
    public SHA384() {
      super(NISTObjectIdentifiers.id_sha384, DigestFactory.createSHA384(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA3_224 extends DigestSignatureSpi {
    public SHA3_224() {
      super(NISTObjectIdentifiers.id_sha3_224, DigestFactory.createSHA3_224(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA3_256 extends DigestSignatureSpi {
    public SHA3_256() {
      super(NISTObjectIdentifiers.id_sha3_256, DigestFactory.createSHA3_256(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA3_384 extends DigestSignatureSpi {
    public SHA3_384() {
      super(NISTObjectIdentifiers.id_sha3_384, DigestFactory.createSHA3_384(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA3_512 extends DigestSignatureSpi {
    public SHA3_512() {
      super(NISTObjectIdentifiers.id_sha3_512, DigestFactory.createSHA3_512(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA512 extends DigestSignatureSpi {
    public SHA512() {
      super(NISTObjectIdentifiers.id_sha512, DigestFactory.createSHA512(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA512_224 extends DigestSignatureSpi {
    public SHA512_224() {
      super(NISTObjectIdentifiers.id_sha512_224, DigestFactory.createSHA512_224(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class SHA512_256 extends DigestSignatureSpi {
    public SHA512_256() {
      super(NISTObjectIdentifiers.id_sha512_256, DigestFactory.createSHA512_256(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
  
  public static class noneRSA extends DigestSignatureSpi {
    public noneRSA() {
      super((Digest)new NullDigest(), (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine()));
    }
  }
}
