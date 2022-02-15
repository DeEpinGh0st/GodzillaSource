package org.bouncycastle.jce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Strings;

public class PKCS10CertificationRequest extends CertificationRequest {
  private static Hashtable algorithms = new Hashtable<Object, Object>();
  
  private static Hashtable params = new Hashtable<Object, Object>();
  
  private static Hashtable keyAlgorithms = new Hashtable<Object, Object>();
  
  private static Hashtable oids = new Hashtable<Object, Object>();
  
  private static Set noParams = new HashSet();
  
  private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt) {
    return new RSASSAPSSparams(paramAlgorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)paramAlgorithmIdentifier), new ASN1Integer(paramInt), new ASN1Integer(1L));
  }
  
  private static ASN1Sequence toDERSequence(byte[] paramArrayOfbyte) {
    try {
      ASN1InputStream aSN1InputStream = new ASN1InputStream(paramArrayOfbyte);
      return (ASN1Sequence)aSN1InputStream.readObject();
    } catch (Exception exception) {
      throw new IllegalArgumentException("badly encoded request");
    } 
  }
  
  public PKCS10CertificationRequest(byte[] paramArrayOfbyte) {
    super(toDERSequence(paramArrayOfbyte));
  }
  
  public PKCS10CertificationRequest(ASN1Sequence paramASN1Sequence) {
    super(paramASN1Sequence);
  }
  
  public PKCS10CertificationRequest(String paramString, X509Name paramX509Name, PublicKey paramPublicKey, ASN1Set paramASN1Set, PrivateKey paramPrivateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    this(paramString, paramX509Name, paramPublicKey, paramASN1Set, paramPrivateKey, "BC");
  }
  
  private static X509Name convertName(X500Principal paramX500Principal) {
    try {
      return new X509Principal(paramX500Principal.getEncoded());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("can't convert name");
    } 
  }
  
  public PKCS10CertificationRequest(String paramString, X500Principal paramX500Principal, PublicKey paramPublicKey, ASN1Set paramASN1Set, PrivateKey paramPrivateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    this(paramString, convertName(paramX500Principal), paramPublicKey, paramASN1Set, paramPrivateKey, "BC");
  }
  
  public PKCS10CertificationRequest(String paramString1, X500Principal paramX500Principal, PublicKey paramPublicKey, ASN1Set paramASN1Set, PrivateKey paramPrivateKey, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    this(paramString1, convertName(paramX500Principal), paramPublicKey, paramASN1Set, paramPrivateKey, paramString2);
  }
  
  public PKCS10CertificationRequest(String paramString1, X509Name paramX509Name, PublicKey paramPublicKey, ASN1Set paramASN1Set, PrivateKey paramPrivateKey, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    Signature signature;
    String str = Strings.toUpperCase(paramString1);
    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)algorithms.get(str);
    if (aSN1ObjectIdentifier == null)
      try {
        aSN1ObjectIdentifier = new ASN1ObjectIdentifier(str);
      } catch (Exception exception) {
        throw new IllegalArgumentException("Unknown signature type requested");
      }  
    if (paramX509Name == null)
      throw new IllegalArgumentException("subject must not be null"); 
    if (paramPublicKey == null)
      throw new IllegalArgumentException("public key must not be null"); 
    if (noParams.contains(aSN1ObjectIdentifier)) {
      this.sigAlgId = new AlgorithmIdentifier(aSN1ObjectIdentifier);
    } else if (params.containsKey(str)) {
      this.sigAlgId = new AlgorithmIdentifier(aSN1ObjectIdentifier, (ASN1Encodable)params.get(str));
    } else {
      this.sigAlgId = new AlgorithmIdentifier(aSN1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
    } 
    try {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(paramPublicKey.getEncoded());
      this.reqInfo = new CertificationRequestInfo(paramX509Name, SubjectPublicKeyInfo.getInstance(aSN1Sequence), paramASN1Set);
    } catch (IOException iOException) {
      throw new IllegalArgumentException("can't encode public key");
    } 
    if (paramString2 == null) {
      signature = Signature.getInstance(paramString1);
    } else {
      signature = Signature.getInstance(paramString1, paramString2);
    } 
    signature.initSign(paramPrivateKey);
    try {
      signature.update(this.reqInfo.getEncoded("DER"));
    } catch (Exception exception) {
      throw new IllegalArgumentException("exception encoding TBS cert request - " + exception);
    } 
    this.sigBits = new DERBitString(signature.sign());
  }
  
  public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
    return getPublicKey("BC");
  }
  
  public PublicKey getPublicKey(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
    SubjectPublicKeyInfo subjectPublicKeyInfo = this.reqInfo.getSubjectPublicKeyInfo();
    try {
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((new DERBitString((ASN1Encodable)subjectPublicKeyInfo)).getOctets());
      AlgorithmIdentifier algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
      try {
        return (paramString == null) ? KeyFactory.getInstance(algorithmIdentifier.getAlgorithm().getId()).generatePublic(x509EncodedKeySpec) : KeyFactory.getInstance(algorithmIdentifier.getAlgorithm().getId(), paramString).generatePublic(x509EncodedKeySpec);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        if (keyAlgorithms.get(algorithmIdentifier.getAlgorithm()) != null) {
          String str = (String)keyAlgorithms.get(algorithmIdentifier.getAlgorithm());
          return (paramString == null) ? KeyFactory.getInstance(str).generatePublic(x509EncodedKeySpec) : KeyFactory.getInstance(str, paramString).generatePublic(x509EncodedKeySpec);
        } 
        throw noSuchAlgorithmException;
      } 
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("error decoding public key");
    } catch (IOException iOException) {
      throw new InvalidKeyException("error decoding public key");
    } 
  }
  
  public boolean verify() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    return verify("BC");
  }
  
  public boolean verify(String paramString) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    return verify(getPublicKey(paramString), paramString);
  }
  
  public boolean verify(PublicKey paramPublicKey, String paramString) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
    Signature signature;
    try {
      if (paramString == null) {
        signature = Signature.getInstance(getSignatureName(this.sigAlgId));
      } else {
        signature = Signature.getInstance(getSignatureName(this.sigAlgId), paramString);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      if (oids.get(this.sigAlgId.getAlgorithm()) != null) {
        String str = (String)oids.get(this.sigAlgId.getAlgorithm());
        if (paramString == null) {
          signature = Signature.getInstance(str);
        } else {
          signature = Signature.getInstance(str, paramString);
        } 
      } else {
        throw noSuchAlgorithmException;
      } 
    } 
    setSignatureParameters(signature, this.sigAlgId.getParameters());
    signature.initVerify(paramPublicKey);
    try {
      signature.update(this.reqInfo.getEncoded("DER"));
    } catch (Exception exception) {
      throw new SignatureException("exception encoding TBS cert request - " + exception);
    } 
    return signature.verify(this.sigBits.getOctets());
  }
  
  public byte[] getEncoded() {
    try {
      return getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
  }
  
  private void setSignatureParameters(Signature paramSignature, ASN1Encodable paramASN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    if (paramASN1Encodable != null && !DERNull.INSTANCE.equals(paramASN1Encodable)) {
      AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(paramSignature.getAlgorithm(), paramSignature.getProvider());
      try {
        algorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
      } catch (IOException iOException) {
        throw new SignatureException("IOException decoding parameters: " + iOException.getMessage());
      } 
      if (paramSignature.getAlgorithm().endsWith("MGF1"))
        try {
          paramSignature.setParameter(algorithmParameters.getParameterSpec((Class)PSSParameterSpec.class));
        } catch (GeneralSecurityException generalSecurityException) {
          throw new SignatureException("Exception extracting parameters: " + generalSecurityException.getMessage());
        }  
    } 
  }
  
  static String getSignatureName(AlgorithmIdentifier paramAlgorithmIdentifier) {
    ASN1Encodable aSN1Encodable = paramAlgorithmIdentifier.getParameters();
    if (aSN1Encodable != null && !DERNull.INSTANCE.equals(aSN1Encodable) && paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
      RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Encodable);
      return getDigestAlgName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
    } 
    return paramAlgorithmIdentifier.getAlgorithm().getId();
  }
  
  private static String getDigestAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return PKCSObjectIdentifiers.md5.equals(paramASN1ObjectIdentifier) ? "MD5" : (OIWObjectIdentifiers.idSHA1.equals(paramASN1ObjectIdentifier) ? "SHA1" : (NISTObjectIdentifiers.id_sha224.equals(paramASN1ObjectIdentifier) ? "SHA224" : (NISTObjectIdentifiers.id_sha256.equals(paramASN1ObjectIdentifier) ? "SHA256" : (NISTObjectIdentifiers.id_sha384.equals(paramASN1ObjectIdentifier) ? "SHA384" : (NISTObjectIdentifiers.id_sha512.equals(paramASN1ObjectIdentifier) ? "SHA512" : (TeleTrusTObjectIdentifiers.ripemd128.equals(paramASN1ObjectIdentifier) ? "RIPEMD128" : (TeleTrusTObjectIdentifiers.ripemd160.equals(paramASN1ObjectIdentifier) ? "RIPEMD160" : (TeleTrusTObjectIdentifiers.ripemd256.equals(paramASN1ObjectIdentifier) ? "RIPEMD256" : (CryptoProObjectIdentifiers.gostR3411.equals(paramASN1ObjectIdentifier) ? "GOST3411" : paramASN1ObjectIdentifier.getId())))))))));
  }
  
  static {
    algorithms.put("MD2WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
    algorithms.put("MD2WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
    algorithms.put("MD5WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
    algorithms.put("MD5WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
    algorithms.put("RSAWITHMD5", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
    algorithms.put("SHA1WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
    algorithms.put("SHA1WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
    algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
    algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
    algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
    algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
    algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
    algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
    algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
    algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
    algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("RSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
    algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
    algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
    algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
    algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
    algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
    algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
    algorithms.put("SHA1WITHDSA", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
    algorithms.put("DSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
    algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
    algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
    algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
    algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
    algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
    algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
    algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
    algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
    algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
    algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
    algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    algorithms.put("GOST3410WITHGOST3411", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
    oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
    oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
    oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
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
    keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
    keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
    noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
    noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    AlgorithmIdentifier algorithmIdentifier1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA1WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier1, 20));
    AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA224WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier2, 28));
    AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA256WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier3, 32));
    AlgorithmIdentifier algorithmIdentifier4 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA384WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier4, 48));
    AlgorithmIdentifier algorithmIdentifier5 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA512WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier5, 64));
  }
}
