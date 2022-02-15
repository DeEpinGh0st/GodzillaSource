package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v3CertificateBuilder {
  private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
  
  private ExtensionsGenerator extGenerator;
  
  public X509v3CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this(paramX500Name1, paramBigInteger, new Time(paramDate1), new Time(paramDate2), paramX500Name2, paramSubjectPublicKeyInfo);
  }
  
  public X509v3CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, Locale paramLocale, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this(paramX500Name1, paramBigInteger, new Time(paramDate1, paramLocale), new Time(paramDate2, paramLocale), paramX500Name2, paramSubjectPublicKeyInfo);
  }
  
  public X509v3CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Time paramTime1, Time paramTime2, X500Name paramX500Name2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.tbsGen.setSerialNumber(new ASN1Integer(paramBigInteger));
    this.tbsGen.setIssuer(paramX500Name1);
    this.tbsGen.setStartDate(paramTime1);
    this.tbsGen.setEndDate(paramTime2);
    this.tbsGen.setSubject(paramX500Name2);
    this.tbsGen.setSubjectPublicKeyInfo(paramSubjectPublicKeyInfo);
    this.extGenerator = new ExtensionsGenerator();
  }
  
  public X509v3CertificateBuilder setSubjectUniqueID(boolean[] paramArrayOfboolean) {
    this.tbsGen.setSubjectUniqueID(CertUtils.booleanToBitString(paramArrayOfboolean));
    return this;
  }
  
  public X509v3CertificateBuilder setIssuerUniqueID(boolean[] paramArrayOfboolean) {
    this.tbsGen.setIssuerUniqueID(CertUtils.booleanToBitString(paramArrayOfboolean));
    return this;
  }
  
  public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws CertIOException {
    CertUtils.addExtension(this.extGenerator, paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    return this;
  }
  
  public X509v3CertificateBuilder addExtension(Extension paramExtension) throws CertIOException {
    this.extGenerator.addExtension(paramExtension);
    return this;
  }
  
  public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) throws CertIOException {
    this.extGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramArrayOfbyte);
    return this;
  }
  
  public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, X509CertificateHolder paramX509CertificateHolder) {
    Certificate certificate = paramX509CertificateHolder.toASN1Structure();
    Extension extension = certificate.getTBSCertificate().getExtensions().getExtension(paramASN1ObjectIdentifier);
    if (extension == null)
      throw new NullPointerException("extension " + paramASN1ObjectIdentifier + " not present"); 
    this.extGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, extension.getExtnValue().getOctets());
    return this;
  }
  
  public X509CertificateHolder build(ContentSigner paramContentSigner) {
    this.tbsGen.setSignature(paramContentSigner.getAlgorithmIdentifier());
    if (!this.extGenerator.isEmpty())
      this.tbsGen.setExtensions(this.extGenerator.generate()); 
    return CertUtils.generateFullCert(paramContentSigner, this.tbsGen.generateTBSCertificate());
  }
}
