package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;

class CMSUtils {
  private static final Set mqvAlgs = new HashSet();
  
  private static final Set ecAlgs = new HashSet();
  
  private static final Set gostAlgs = new HashSet();
  
  static boolean isMQV(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return mqvAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isEC(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return ecAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isGOST(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return gostAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isRFC2631(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_ESDH) || paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH));
  }
  
  static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate paramX509Certificate) throws CertificateEncodingException {
    Certificate certificate = Certificate.getInstance(paramX509Certificate.getEncoded());
    return new IssuerAndSerialNumber(certificate.getIssuer(), paramX509Certificate.getSerialNumber());
  }
  
  static byte[] getSubjectKeyId(X509Certificate paramX509Certificate) {
    byte[] arrayOfByte = paramX509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
    return (arrayOfByte != null) ? ASN1OctetString.getInstance(ASN1OctetString.getInstance(arrayOfByte).getOctets()).getOctets() : null;
  }
  
  static EnvelopedDataHelper createContentHelper(Provider paramProvider) {
    return (paramProvider != null) ? new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  }
  
  static EnvelopedDataHelper createContentHelper(String paramString) {
    return (paramString != null) ? new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  }
  
  static ASN1Encodable extractParameters(AlgorithmParameters paramAlgorithmParameters) throws CMSException {
    try {
      return AlgorithmParametersUtils.extractParameters(paramAlgorithmParameters);
    } catch (IOException iOException) {
      throw new CMSException("cannot extract parameters: " + iOException.getMessage(), iOException);
    } 
  }
  
  static void loadParameters(AlgorithmParameters paramAlgorithmParameters, ASN1Encodable paramASN1Encodable) throws CMSException {
    try {
      AlgorithmParametersUtils.loadParameters(paramAlgorithmParameters, paramASN1Encodable);
    } catch (IOException iOException) {
      throw new CMSException("error encoding algorithm parameters.", iOException);
    } 
  }
  
  static {
    mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
    ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
    ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
    gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
    gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
  }
}
