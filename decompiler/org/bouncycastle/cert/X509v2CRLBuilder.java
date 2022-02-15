package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v2CRLBuilder {
  private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
  
  private ExtensionsGenerator extGenerator = new ExtensionsGenerator();
  
  public X509v2CRLBuilder(X500Name paramX500Name, Date paramDate) {
    this.tbsGen.setIssuer(paramX500Name);
    this.tbsGen.setThisUpdate(new Time(paramDate));
  }
  
  public X509v2CRLBuilder(X500Name paramX500Name, Date paramDate, Locale paramLocale) {
    this.tbsGen.setIssuer(paramX500Name);
    this.tbsGen.setThisUpdate(new Time(paramDate, paramLocale));
  }
  
  public X509v2CRLBuilder(X500Name paramX500Name, Time paramTime) {
    this.tbsGen.setIssuer(paramX500Name);
    this.tbsGen.setThisUpdate(paramTime);
  }
  
  public X509v2CRLBuilder setNextUpdate(Date paramDate) {
    return setNextUpdate(new Time(paramDate));
  }
  
  public X509v2CRLBuilder setNextUpdate(Date paramDate, Locale paramLocale) {
    return setNextUpdate(new Time(paramDate, paramLocale));
  }
  
  public X509v2CRLBuilder setNextUpdate(Time paramTime) {
    this.tbsGen.setNextUpdate(paramTime);
    return this;
  }
  
  public X509v2CRLBuilder addCRLEntry(BigInteger paramBigInteger, Date paramDate, int paramInt) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate), paramInt);
    return this;
  }
  
  public X509v2CRLBuilder addCRLEntry(BigInteger paramBigInteger, Date paramDate1, int paramInt, Date paramDate2) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate1), paramInt, new ASN1GeneralizedTime(paramDate2));
    return this;
  }
  
  public X509v2CRLBuilder addCRLEntry(BigInteger paramBigInteger, Date paramDate, Extensions paramExtensions) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate), paramExtensions);
    return this;
  }
  
  public X509v2CRLBuilder addCRL(X509CRLHolder paramX509CRLHolder) {
    TBSCertList tBSCertList = paramX509CRLHolder.toASN1Structure().getTBSCertList();
    if (tBSCertList != null) {
      Enumeration<ASN1Encodable> enumeration = tBSCertList.getRevokedCertificateEnumeration();
      while (enumeration.hasMoreElements())
        this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive())); 
    } 
    return this;
  }
  
  public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws CertIOException {
    CertUtils.addExtension(this.extGenerator, paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    return this;
  }
  
  public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) throws CertIOException {
    this.extGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramArrayOfbyte);
    return this;
  }
  
  public X509v2CRLBuilder addExtension(Extension paramExtension) throws CertIOException {
    this.extGenerator.addExtension(paramExtension);
    return this;
  }
  
  public X509CRLHolder build(ContentSigner paramContentSigner) {
    this.tbsGen.setSignature(paramContentSigner.getAlgorithmIdentifier());
    if (!this.extGenerator.isEmpty())
      this.tbsGen.setExtensions(this.extGenerator.generate()); 
    return CertUtils.generateFullCRL(paramContentSigner, this.tbsGen.generateTBSCertList());
  }
}
