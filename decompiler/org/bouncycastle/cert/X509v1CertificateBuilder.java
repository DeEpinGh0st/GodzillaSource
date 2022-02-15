package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v1CertificateBuilder {
  private V1TBSCertificateGenerator tbsGen;
  
  public X509v1CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this(paramX500Name1, paramBigInteger, new Time(paramDate1), new Time(paramDate2), paramX500Name2, paramSubjectPublicKeyInfo);
  }
  
  public X509v1CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, Locale paramLocale, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this(paramX500Name1, paramBigInteger, new Time(paramDate1, paramLocale), new Time(paramDate2, paramLocale), paramX500Name2, paramSubjectPublicKeyInfo);
  }
  
  public X509v1CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Time paramTime1, Time paramTime2, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    if (paramX500Name1 == null)
      throw new IllegalArgumentException("issuer must not be null"); 
    if (paramSubjectPublicKeyInfo == null)
      throw new IllegalArgumentException("publicKeyInfo must not be null"); 
    this.tbsGen = new V1TBSCertificateGenerator();
    this.tbsGen.setSerialNumber(new ASN1Integer(paramBigInteger));
    this.tbsGen.setIssuer(paramX500Name1);
    this.tbsGen.setStartDate(paramTime1);
    this.tbsGen.setEndDate(paramTime2);
    this.tbsGen.setSubject(paramX500Name2);
    this.tbsGen.setSubjectPublicKeyInfo(paramSubjectPublicKeyInfo);
  }
  
  public X509CertificateHolder build(ContentSigner paramContentSigner) {
    this.tbsGen.setSignature(paramContentSigner.getAlgorithmIdentifier());
    return CertUtils.generateFullCert(paramContentSigner, this.tbsGen.generateTBSCertificate());
  }
}
