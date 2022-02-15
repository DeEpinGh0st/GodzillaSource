package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class TimeStampTokenGenerator {
  public static final int R_SECONDS = 0;
  
  public static final int R_TENTHS_OF_SECONDS = 1;
  
  public static final int R_MICROSECONDS = 2;
  
  public static final int R_MILLISECONDS = 3;
  
  private int resolution = 0;
  
  private Locale locale = null;
  
  private int accuracySeconds = -1;
  
  private int accuracyMillis = -1;
  
  private int accuracyMicros = -1;
  
  boolean ordering = false;
  
  GeneralName tsa = null;
  
  private ASN1ObjectIdentifier tsaPolicyOID;
  
  private List certs = new ArrayList();
  
  private List crls = new ArrayList();
  
  private List attrCerts = new ArrayList();
  
  private Map otherRevoc = new HashMap<Object, Object>();
  
  private SignerInfoGenerator signerInfoGen;
  
  public TimeStampTokenGenerator(SignerInfoGenerator paramSignerInfoGenerator, DigestCalculator paramDigestCalculator, ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws IllegalArgumentException, TSPException {
    this(paramSignerInfoGenerator, paramDigestCalculator, paramASN1ObjectIdentifier, false);
  }
  
  public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGen, DigestCalculator paramDigestCalculator, ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean) throws IllegalArgumentException, TSPException {
    this.signerInfoGen = signerInfoGen;
    this.tsaPolicyOID = paramASN1ObjectIdentifier;
    if (!signerInfoGen.hasAssociatedCertificate())
      throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate"); 
    X509CertificateHolder x509CertificateHolder = signerInfoGen.getAssociatedCertificate();
    TSPUtil.validateCertificate(x509CertificateHolder);
    try {
      OutputStream outputStream = paramDigestCalculator.getOutputStream();
      outputStream.write(x509CertificateHolder.getEncoded());
      outputStream.close();
      if (paramDigestCalculator.getAlgorithmIdentifier().getAlgorithm().equals(OIWObjectIdentifiers.idSHA1)) {
        final ESSCertID essCertid = new ESSCertID(paramDigestCalculator.getDigest(), paramBoolean ? new IssuerSerial(new GeneralNames(new GeneralName(x509CertificateHolder.getIssuer())), x509CertificateHolder.getSerialNumber()) : null);
        this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() {
              public AttributeTable getAttributes(Map param1Map) throws CMSAttributeTableGenerationException {
                AttributeTable attributeTable = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(param1Map);
                return (attributeTable.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null) ? attributeTable.add(PKCSObjectIdentifiers.id_aa_signingCertificate, (ASN1Encodable)new SigningCertificate(essCertid)) : attributeTable;
              }
            }signerInfoGen.getUnsignedAttributeTableGenerator());
      } else {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(paramDigestCalculator.getAlgorithmIdentifier().getAlgorithm());
        final ESSCertIDv2 essCertid = new ESSCertIDv2(algorithmIdentifier, paramDigestCalculator.getDigest(), paramBoolean ? new IssuerSerial(new GeneralNames(new GeneralName(x509CertificateHolder.getIssuer())), new ASN1Integer(x509CertificateHolder.getSerialNumber())) : null);
        this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() {
              public AttributeTable getAttributes(Map param1Map) throws CMSAttributeTableGenerationException {
                AttributeTable attributeTable = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(param1Map);
                return (attributeTable.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null) ? attributeTable.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, (ASN1Encodable)new SigningCertificateV2(essCertid)) : attributeTable;
              }
            }signerInfoGen.getUnsignedAttributeTableGenerator());
      } 
    } catch (IOException iOException) {
      throw new TSPException("Exception processing certificate.", iOException);
    } 
  }
  
  public void addCertificates(Store paramStore) {
    this.certs.addAll(paramStore.getMatches(null));
  }
  
  public void addCRLs(Store paramStore) {
    this.crls.addAll(paramStore.getMatches(null));
  }
  
  public void addAttributeCertificates(Store paramStore) {
    this.attrCerts.addAll(paramStore.getMatches(null));
  }
  
  public void addOtherRevocationInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Store paramStore) {
    this.otherRevoc.put(paramASN1ObjectIdentifier, paramStore.getMatches(null));
  }
  
  public void setResolution(int paramInt) {
    this.resolution = paramInt;
  }
  
  public void setLocale(Locale paramLocale) {
    this.locale = paramLocale;
  }
  
  public void setAccuracySeconds(int paramInt) {
    this.accuracySeconds = paramInt;
  }
  
  public void setAccuracyMillis(int paramInt) {
    this.accuracyMillis = paramInt;
  }
  
  public void setAccuracyMicros(int paramInt) {
    this.accuracyMicros = paramInt;
  }
  
  public void setOrdering(boolean paramBoolean) {
    this.ordering = paramBoolean;
  }
  
  public void setTSA(GeneralName paramGeneralName) {
    this.tsa = paramGeneralName;
  }
  
  public TimeStampToken generate(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate) throws TSPException {
    return generate(paramTimeStampRequest, paramBigInteger, paramDate, null);
  }
  
  public TimeStampToken generate(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate, Extensions paramExtensions) throws TSPException {
    ASN1GeneralizedTime aSN1GeneralizedTime;
    ASN1ObjectIdentifier aSN1ObjectIdentifier1 = paramTimeStampRequest.getMessageImprintAlgOID();
    AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(aSN1ObjectIdentifier1, (ASN1Encodable)DERNull.INSTANCE);
    MessageImprint messageImprint = new MessageImprint(algorithmIdentifier, paramTimeStampRequest.getMessageImprintDigest());
    Accuracy accuracy = null;
    if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
      ASN1Integer aSN1Integer1 = null;
      if (this.accuracySeconds > 0)
        aSN1Integer1 = new ASN1Integer(this.accuracySeconds); 
      ASN1Integer aSN1Integer2 = null;
      if (this.accuracyMillis > 0)
        aSN1Integer2 = new ASN1Integer(this.accuracyMillis); 
      ASN1Integer aSN1Integer3 = null;
      if (this.accuracyMicros > 0)
        aSN1Integer3 = new ASN1Integer(this.accuracyMicros); 
      accuracy = new Accuracy(aSN1Integer1, aSN1Integer2, aSN1Integer3);
    } 
    ASN1Boolean aSN1Boolean = null;
    if (this.ordering)
      aSN1Boolean = ASN1Boolean.getInstance(this.ordering); 
    ASN1Integer aSN1Integer = null;
    if (paramTimeStampRequest.getNonce() != null)
      aSN1Integer = new ASN1Integer(paramTimeStampRequest.getNonce()); 
    ASN1ObjectIdentifier aSN1ObjectIdentifier2 = this.tsaPolicyOID;
    if (paramTimeStampRequest.getReqPolicy() != null)
      aSN1ObjectIdentifier2 = paramTimeStampRequest.getReqPolicy(); 
    Extensions extensions = paramTimeStampRequest.getExtensions();
    if (paramExtensions != null) {
      ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
      if (extensions != null) {
        Enumeration enumeration1 = extensions.oids();
        while (enumeration1.hasMoreElements())
          extensionsGenerator.addExtension(extensions.getExtension(ASN1ObjectIdentifier.getInstance(enumeration1.nextElement()))); 
      } 
      Enumeration enumeration = paramExtensions.oids();
      while (enumeration.hasMoreElements())
        extensionsGenerator.addExtension(paramExtensions.getExtension(ASN1ObjectIdentifier.getInstance(enumeration.nextElement()))); 
      extensions = extensionsGenerator.generate();
    } 
    if (this.resolution == 0) {
      aSN1GeneralizedTime = (this.locale == null) ? new ASN1GeneralizedTime(paramDate) : new ASN1GeneralizedTime(paramDate, this.locale);
    } else {
      aSN1GeneralizedTime = createGeneralizedTime(paramDate);
    } 
    TSTInfo tSTInfo = new TSTInfo(aSN1ObjectIdentifier2, messageImprint, new ASN1Integer(paramBigInteger), aSN1GeneralizedTime, accuracy, aSN1Boolean, aSN1Integer, this.tsa, extensions);
    try {
      CMSSignedDataGenerator cMSSignedDataGenerator = new CMSSignedDataGenerator();
      if (paramTimeStampRequest.getCertReq()) {
        cMSSignedDataGenerator.addCertificates((Store)new CollectionStore(this.certs));
        cMSSignedDataGenerator.addAttributeCertificates((Store)new CollectionStore(this.attrCerts));
      } 
      cMSSignedDataGenerator.addCRLs((Store)new CollectionStore(this.crls));
      if (!this.otherRevoc.isEmpty())
        for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.otherRevoc.keySet())
          cMSSignedDataGenerator.addOtherRevocationInfo(aSN1ObjectIdentifier, (Store)new CollectionStore((Collection)this.otherRevoc.get(aSN1ObjectIdentifier)));  
      cMSSignedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
      byte[] arrayOfByte = tSTInfo.getEncoded("DER");
      CMSSignedData cMSSignedData = cMSSignedDataGenerator.generate((CMSTypedData)new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, arrayOfByte), true);
      return new TimeStampToken(cMSSignedData);
    } catch (CMSException cMSException) {
      throw new TSPException("Error generating time-stamp token", cMSException);
    } catch (IOException iOException) {
      throw new TSPException("Exception encoding info", iOException);
    } 
  }
  
  private ASN1GeneralizedTime createGeneralizedTime(Date paramDate) throws TSPException {
    String str = "yyyyMMddHHmmss.SSS";
    SimpleDateFormat simpleDateFormat = (this.locale == null) ? new SimpleDateFormat(str) : new SimpleDateFormat(str, this.locale);
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    StringBuilder stringBuilder = new StringBuilder(simpleDateFormat.format(paramDate));
    int i = stringBuilder.indexOf(".");
    if (i < 0) {
      stringBuilder.append("Z");
      return new ASN1GeneralizedTime(stringBuilder.toString());
    } 
    switch (this.resolution) {
      case 1:
        if (stringBuilder.length() > i + 2)
          stringBuilder.delete(i + 2, stringBuilder.length()); 
        break;
      case 2:
        if (stringBuilder.length() > i + 3)
          stringBuilder.delete(i + 3, stringBuilder.length()); 
        break;
      case 3:
        break;
      default:
        throw new TSPException("unknown time-stamp resolution: " + this.resolution);
    } 
    while (stringBuilder.charAt(stringBuilder.length() - 1) == '0')
      stringBuilder.deleteCharAt(stringBuilder.length() - 1); 
    if (stringBuilder.length() - 1 == i)
      stringBuilder.deleteCharAt(stringBuilder.length() - 1); 
    stringBuilder.append("Z");
    return new ASN1GeneralizedTime(stringBuilder.toString());
  }
}
