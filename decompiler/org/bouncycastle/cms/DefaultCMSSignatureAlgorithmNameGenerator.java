package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public class DefaultCMSSignatureAlgorithmNameGenerator implements CMSSignatureAlgorithmNameGenerator {
  private final Map encryptionAlgs = new HashMap<Object, Object>();
  
  private final Map digestAlgs = new HashMap<Object, Object>();
  
  private void addEntries(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString1, String paramString2) {
    this.digestAlgs.put(paramASN1ObjectIdentifier, paramString1);
    this.encryptionAlgs.put(paramASN1ObjectIdentifier, paramString2);
  }
  
  public DefaultCMSSignatureAlgorithmNameGenerator() {
    addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
    addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
    addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
    addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
    addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
    addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
    addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
    addEntries(OIWObjectIdentifiers.md5WithRSA, "MD5", "RSA");
    addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", "RSA");
    addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", "RSA");
    addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
    addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5", "RSA");
    addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", "RSA");
    addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
    addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
    addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", "RSA");
    addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
    addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128", "RSA");
    addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160", "RSA");
    addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256", "RSA");
    addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
    addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
    addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
    addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
    addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
    addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
    addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
    addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
    addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
    addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
    addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
    addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", "RSA");
    addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
    addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
    addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1", "PLAIN-ECDSA");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224", "PLAIN-ECDSA");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256", "PLAIN-ECDSA");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384", "PLAIN-ECDSA");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512", "PLAIN-ECDSA");
    addEntries(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160", "PLAIN-ECDSA");
    this.encryptionAlgs.put(X9ObjectIdentifiers.id_dsa, "DSA");
    this.encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
    this.encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
    this.encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa, "RSA");
    this.encryptionAlgs.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
    this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
    this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
    this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
    this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
    this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
    this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
    this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
    this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
    this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
    this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
    this.encryptionAlgs.put(GMObjectIdentifiers.sm2sign_with_sm3, "SM2");
    this.digestAlgs.put(PKCSObjectIdentifiers.md2, "MD2");
    this.digestAlgs.put(PKCSObjectIdentifiers.md4, "MD4");
    this.digestAlgs.put(PKCSObjectIdentifiers.md5, "MD5");
    this.digestAlgs.put(OIWObjectIdentifiers.idSHA1, "SHA1");
    this.digestAlgs.put(NISTObjectIdentifiers.id_sha224, "SHA224");
    this.digestAlgs.put(NISTObjectIdentifiers.id_sha256, "SHA256");
    this.digestAlgs.put(NISTObjectIdentifiers.id_sha384, "SHA384");
    this.digestAlgs.put(NISTObjectIdentifiers.id_sha512, "SHA512");
    this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
    this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
    this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
    this.digestAlgs.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
    this.digestAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.2.1"), "GOST3411");
    this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
    this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
    this.digestAlgs.put(GMObjectIdentifiers.sm3, "SM3");
  }
  
  private String getDigestAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)this.digestAlgs.get(paramASN1ObjectIdentifier);
    return (str != null) ? str : paramASN1ObjectIdentifier.getId();
  }
  
  private String getEncryptionAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)this.encryptionAlgs.get(paramASN1ObjectIdentifier);
    return (str != null) ? str : paramASN1ObjectIdentifier.getId();
  }
  
  protected void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.encryptionAlgs.put(paramASN1ObjectIdentifier, paramString);
  }
  
  protected void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.digestAlgs.put(paramASN1ObjectIdentifier, paramString);
  }
  
  public String getSignatureName(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    String str = getDigestAlgName(paramAlgorithmIdentifier2.getAlgorithm());
    return !str.equals(paramAlgorithmIdentifier2.getAlgorithm().getId()) ? (str + "with" + getEncryptionAlgName(paramAlgorithmIdentifier2.getAlgorithm())) : (getDigestAlgName(paramAlgorithmIdentifier1.getAlgorithm()) + "with" + getEncryptionAlgName(paramAlgorithmIdentifier2.getAlgorithm()));
  }
}
