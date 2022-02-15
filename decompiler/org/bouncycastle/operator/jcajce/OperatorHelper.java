package org.bouncycastle.operator.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Integers;

class OperatorHelper {
  private static final Map oids = new HashMap<Object, Object>();
  
  private static final Map asymmetricWrapperAlgNames = new HashMap<Object, Object>();
  
  private static final Map symmetricWrapperAlgNames = new HashMap<Object, Object>();
  
  private static final Map symmetricKeyAlgNames = new HashMap<Object, Object>();
  
  private static final Map symmetricWrapperKeySizes = new HashMap<Object, Object>();
  
  private JcaJceHelper helper;
  
  OperatorHelper(JcaJceHelper paramJcaJceHelper) {
    this.helper = paramJcaJceHelper;
  }
  
  String getWrappingAlgorithmName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (String)symmetricWrapperAlgNames.get(paramASN1ObjectIdentifier);
  }
  
  int getKeySizeInBits(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return ((Integer)symmetricWrapperKeySizes.get(paramASN1ObjectIdentifier)).intValue();
  }
  
  Cipher createAsymmetricWrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Map paramMap) throws OperatorCreationException {
    try {
      String str = null;
      if (!paramMap.isEmpty())
        str = (String)paramMap.get(paramASN1ObjectIdentifier); 
      if (str == null)
        str = (String)asymmetricWrapperAlgNames.get(paramASN1ObjectIdentifier); 
      if (str != null)
        try {
          return this.helper.createCipher(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          if (str.equals("RSA/ECB/PKCS1Padding"))
            try {
              return this.helper.createCipher("RSA/NONE/PKCS1Padding");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {} 
        }  
      return this.helper.createCipher(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  Cipher createSymmetricWrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws OperatorCreationException {
    try {
      String str = (String)symmetricWrapperAlgNames.get(paramASN1ObjectIdentifier);
      if (str != null)
        try {
          return this.helper.createCipher(str);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
      return this.helper.createCipher(paramASN1ObjectIdentifier.getId());
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException {
    AlgorithmParameters algorithmParameters;
    if (paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption))
      return null; 
    try {
      algorithmParameters = this.helper.createAlgorithmParameters(paramAlgorithmIdentifier.getAlgorithm().getId());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return null;
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new OperatorCreationException("cannot create algorithm parameters: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } 
    try {
      algorithmParameters.init(paramAlgorithmIdentifier.getParameters().toASN1Primitive().getEncoded());
    } catch (IOException iOException) {
      throw new OperatorCreationException("cannot initialise algorithm parameters: " + iOException.getMessage(), iOException);
    } 
    return algorithmParameters;
  }
  
  MessageDigest createDigest(AlgorithmIdentifier paramAlgorithmIdentifier) throws GeneralSecurityException {
    MessageDigest messageDigest;
    try {
      messageDigest = this.helper.createDigest(MessageDigestUtils.getDigestName(paramAlgorithmIdentifier.getAlgorithm()));
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      if (oids.get(paramAlgorithmIdentifier.getAlgorithm()) != null) {
        String str = (String)oids.get(paramAlgorithmIdentifier.getAlgorithm());
        messageDigest = this.helper.createDigest(str);
      } else {
        throw noSuchAlgorithmException;
      } 
    } 
    return messageDigest;
  }
  
  Signature createSignature(AlgorithmIdentifier paramAlgorithmIdentifier) throws GeneralSecurityException {
    Signature signature;
    try {
      signature = this.helper.createSignature(getSignatureName(paramAlgorithmIdentifier));
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      if (oids.get(paramAlgorithmIdentifier.getAlgorithm()) != null) {
        String str = (String)oids.get(paramAlgorithmIdentifier.getAlgorithm());
        signature = this.helper.createSignature(str);
      } else {
        throw noSuchAlgorithmException;
      } 
    } 
    if (paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramAlgorithmIdentifier.getParameters());
      if (notDefaultPSSParams(aSN1Sequence))
        try {
          AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters("PSS");
          algorithmParameters.init(aSN1Sequence.getEncoded());
          signature.setParameter(algorithmParameters.getParameterSpec((Class)PSSParameterSpec.class));
        } catch (IOException iOException) {
          throw new GeneralSecurityException("unable to process PSS parameters: " + iOException.getMessage());
        }  
    } 
    return signature;
  }
  
  public Signature createRawSignature(AlgorithmIdentifier paramAlgorithmIdentifier) {
    Signature signature;
    try {
      String str = getSignatureName(paramAlgorithmIdentifier);
      str = "NONE" + str.substring(str.indexOf("WITH"));
      signature = this.helper.createSignature(str);
      if (paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
        AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(str);
        AlgorithmParametersUtils.loadParameters(algorithmParameters, paramAlgorithmIdentifier.getParameters());
        PSSParameterSpec pSSParameterSpec = algorithmParameters.<PSSParameterSpec>getParameterSpec(PSSParameterSpec.class);
        signature.setParameter(pSSParameterSpec);
      } 
    } catch (Exception exception) {
      return null;
    } 
    return signature;
  }
  
  private static String getSignatureName(AlgorithmIdentifier paramAlgorithmIdentifier) {
    ASN1Encodable aSN1Encodable = paramAlgorithmIdentifier.getParameters();
    if (aSN1Encodable != null && !DERNull.INSTANCE.equals(aSN1Encodable) && paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
      RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Encodable);
      return getDigestName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()) + "WITHRSAANDMGF1";
    } 
    return oids.containsKey(paramAlgorithmIdentifier.getAlgorithm()) ? (String)oids.get(paramAlgorithmIdentifier.getAlgorithm()) : paramAlgorithmIdentifier.getAlgorithm().getId();
  }
  
  private static String getDigestName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = MessageDigestUtils.getDigestName(paramASN1ObjectIdentifier);
    int i = str.indexOf('-');
    return (i > 0 && !str.startsWith("SHA3")) ? (str.substring(0, i) + str.substring(i + 1)) : MessageDigestUtils.getDigestName(paramASN1ObjectIdentifier);
  }
  
  public X509Certificate convertCertificate(X509CertificateHolder paramX509CertificateHolder) throws CertificateException {
    try {
      CertificateFactory certificateFactory = this.helper.createCertificateFactory("X.509");
      return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(paramX509CertificateHolder.getEncoded()));
    } catch (IOException iOException) {
      throw new OpCertificateException("cannot get encoded form of certificate: " + iOException.getMessage(), iOException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new OpCertificateException("cannot find factory provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } 
  }
  
  public PublicKey convertPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws OperatorCreationException {
    try {
      KeyFactory keyFactory = this.helper.createKeyFactory(paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId());
      return keyFactory.generatePublic(new X509EncodedKeySpec(paramSubjectPublicKeyInfo.getEncoded()));
    } catch (IOException iOException) {
      throw new OperatorCreationException("cannot get encoded form of key: " + iOException.getMessage(), iOException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new OperatorCreationException("cannot create key factory: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new OperatorCreationException("cannot find factory provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new OperatorCreationException("cannot create key factory: " + invalidKeySpecException.getMessage(), invalidKeySpecException);
    } 
  }
  
  String getKeyAlgorithmName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)symmetricKeyAlgNames.get(paramASN1ObjectIdentifier);
    return (str != null) ? str : paramASN1ObjectIdentifier.getId();
  }
  
  private boolean notDefaultPSSParams(ASN1Sequence paramASN1Sequence) throws GeneralSecurityException {
    if (paramASN1Sequence == null || paramASN1Sequence.size() == 0)
      return false; 
    RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(paramASN1Sequence);
    if (!rSASSAPSSparams.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1))
      return true; 
    if (!rSASSAPSSparams.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(rSASSAPSSparams.getMaskGenAlgorithm().getParameters())))
      return true; 
    MessageDigest messageDigest = createDigest(rSASSAPSSparams.getHashAlgorithm());
    return (rSASSAPSSparams.getSaltLength().intValue() != messageDigest.getDigestLength());
  }
  
  static {
    oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
    oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
    oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
    oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
    oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
    oids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
    oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
    oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
    oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
    oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
    oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
    oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
    oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
    oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
    oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
    oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
    oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
    oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
    oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
    oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
    oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
    oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
    oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
    oids.put(OIWObjectIdentifiers.idSHA1, "SHA-1");
    oids.put(NISTObjectIdentifiers.id_sha224, "SHA-224");
    oids.put(NISTObjectIdentifiers.id_sha256, "SHA-256");
    oids.put(NISTObjectIdentifiers.id_sha384, "SHA-384");
    oids.put(NISTObjectIdentifiers.id_sha512, "SHA-512");
    oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
    oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
    oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
    asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
    asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
    symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
    symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
    symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
    symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
    symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
    symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
    symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
    symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
    symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
    symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
    symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
    symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
    symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
    symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
    symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
    symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
    symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
    symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
    symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
    symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
    symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
    symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
    symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
    symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
    symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
  }
  
  private static class OpCertificateException extends CertificateException {
    private Throwable cause;
    
    public OpCertificateException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
