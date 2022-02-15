package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v2AttributeCertificateBuilder {
  private V2AttributeCertificateInfoGenerator acInfoGen = new V2AttributeCertificateInfoGenerator();
  
  private ExtensionsGenerator extGenerator = new ExtensionsGenerator();
  
  public X509v2AttributeCertificateBuilder(AttributeCertificateHolder paramAttributeCertificateHolder, AttributeCertificateIssuer paramAttributeCertificateIssuer, BigInteger paramBigInteger, Date paramDate1, Date paramDate2) {
    this.acInfoGen.setHolder(paramAttributeCertificateHolder.holder);
    this.acInfoGen.setIssuer(AttCertIssuer.getInstance(paramAttributeCertificateIssuer.form));
    this.acInfoGen.setSerialNumber(new ASN1Integer(paramBigInteger));
    this.acInfoGen.setStartDate(new ASN1GeneralizedTime(paramDate1));
    this.acInfoGen.setEndDate(new ASN1GeneralizedTime(paramDate2));
  }
  
  public X509v2AttributeCertificateBuilder(AttributeCertificateHolder paramAttributeCertificateHolder, AttributeCertificateIssuer paramAttributeCertificateIssuer, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, Locale paramLocale) {
    this.acInfoGen.setHolder(paramAttributeCertificateHolder.holder);
    this.acInfoGen.setIssuer(AttCertIssuer.getInstance(paramAttributeCertificateIssuer.form));
    this.acInfoGen.setSerialNumber(new ASN1Integer(paramBigInteger));
    this.acInfoGen.setStartDate(new ASN1GeneralizedTime(paramDate1, paramLocale));
    this.acInfoGen.setEndDate(new ASN1GeneralizedTime(paramDate2, paramLocale));
  }
  
  public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.acInfoGen.addAttribute(new Attribute(paramASN1ObjectIdentifier, (ASN1Set)new DERSet(paramASN1Encodable)));
    return this;
  }
  
  public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable[] paramArrayOfASN1Encodable) {
    this.acInfoGen.addAttribute(new Attribute(paramASN1ObjectIdentifier, (ASN1Set)new DERSet(paramArrayOfASN1Encodable)));
    return this;
  }
  
  public void setIssuerUniqueId(boolean[] paramArrayOfboolean) {
    this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(paramArrayOfboolean));
  }
  
  public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws CertIOException {
    CertUtils.addExtension(this.extGenerator, paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    return this;
  }
  
  public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) throws CertIOException {
    this.extGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramArrayOfbyte);
    return this;
  }
  
  public X509v2AttributeCertificateBuilder addExtension(Extension paramExtension) throws CertIOException {
    this.extGenerator.addExtension(paramExtension);
    return this;
  }
  
  public X509AttributeCertificateHolder build(ContentSigner paramContentSigner) {
    this.acInfoGen.setSignature(paramContentSigner.getAlgorithmIdentifier());
    if (!this.extGenerator.isEmpty())
      this.acInfoGen.setExtensions(this.extGenerator.generate()); 
    return CertUtils.generateFullAttrCert(paramContentSigner, this.acInfoGen.generateAttributeCertificateInfo());
  }
}
