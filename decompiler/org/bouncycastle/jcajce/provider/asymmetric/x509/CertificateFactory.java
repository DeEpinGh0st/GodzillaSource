package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.io.Streams;

public class CertificateFactory extends CertificateFactorySpi {
  private final JcaJceHelper bcHelper = (JcaJceHelper)new BCJcaJceHelper();
  
  private static final PEMUtil PEM_CERT_PARSER = new PEMUtil("CERTIFICATE");
  
  private static final PEMUtil PEM_CRL_PARSER = new PEMUtil("CRL");
  
  private static final PEMUtil PEM_PKCS7_PARSER = new PEMUtil("PKCS7");
  
  private ASN1Set sData = null;
  
  private int sDataObjectCount = 0;
  
  private InputStream currentStream = null;
  
  private ASN1Set sCrlData = null;
  
  private int sCrlDataObjectCount = 0;
  
  private InputStream currentCrlStream = null;
  
  private Certificate readDERCertificate(ASN1InputStream paramASN1InputStream) throws IOException, CertificateParsingException {
    return getCertificate(ASN1Sequence.getInstance(paramASN1InputStream.readObject()));
  }
  
  private Certificate readPEMCertificate(InputStream paramInputStream) throws IOException, CertificateParsingException {
    return getCertificate(PEM_CERT_PARSER.readPEMObject(paramInputStream));
  }
  
  private Certificate getCertificate(ASN1Sequence paramASN1Sequence) throws CertificateParsingException {
    if (paramASN1Sequence == null)
      return null; 
    if (paramASN1Sequence.size() > 1 && paramASN1Sequence.getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier && paramASN1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
      this.sData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true)).getCertificates();
      return getCertificate();
    } 
    return new X509CertificateObject(this.bcHelper, Certificate.getInstance(paramASN1Sequence));
  }
  
  private Certificate getCertificate() throws CertificateParsingException {
    if (this.sData != null)
      while (this.sDataObjectCount < this.sData.size()) {
        ASN1Encodable aSN1Encodable = this.sData.getObjectAt(this.sDataObjectCount++);
        if (aSN1Encodable instanceof ASN1Sequence)
          return new X509CertificateObject(this.bcHelper, Certificate.getInstance(aSN1Encodable)); 
      }  
    return null;
  }
  
  protected CRL createCRL(CertificateList paramCertificateList) throws CRLException {
    return new X509CRLObject(this.bcHelper, paramCertificateList);
  }
  
  private CRL readPEMCRL(InputStream paramInputStream) throws IOException, CRLException {
    return getCRL(PEM_CRL_PARSER.readPEMObject(paramInputStream));
  }
  
  private CRL readDERCRL(ASN1InputStream paramASN1InputStream) throws IOException, CRLException {
    return getCRL(ASN1Sequence.getInstance(paramASN1InputStream.readObject()));
  }
  
  private CRL getCRL(ASN1Sequence paramASN1Sequence) throws CRLException {
    if (paramASN1Sequence == null)
      return null; 
    if (paramASN1Sequence.size() > 1 && paramASN1Sequence.getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier && paramASN1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
      this.sCrlData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true)).getCRLs();
      return getCRL();
    } 
    return createCRL(CertificateList.getInstance(paramASN1Sequence));
  }
  
  private CRL getCRL() throws CRLException {
    return (this.sCrlData == null || this.sCrlDataObjectCount >= this.sCrlData.size()) ? null : createCRL(CertificateList.getInstance(this.sCrlData.getObjectAt(this.sCrlDataObjectCount++)));
  }
  
  public Certificate engineGenerateCertificate(InputStream paramInputStream) throws CertificateException {
    if (this.currentStream == null) {
      this.currentStream = paramInputStream;
      this.sData = null;
      this.sDataObjectCount = 0;
    } else if (this.currentStream != paramInputStream) {
      this.currentStream = paramInputStream;
      this.sData = null;
      this.sDataObjectCount = 0;
    } 
    try {
      InputStream inputStream;
      if (this.sData != null) {
        if (this.sDataObjectCount != this.sData.size())
          return getCertificate(); 
        this.sData = null;
        this.sDataObjectCount = 0;
        return null;
      } 
      if (paramInputStream.markSupported()) {
        inputStream = paramInputStream;
      } else {
        inputStream = new ByteArrayInputStream(Streams.readAll(paramInputStream));
      } 
      inputStream.mark(1);
      int i = inputStream.read();
      if (i == -1)
        return null; 
      inputStream.reset();
      return (i != 48) ? readPEMCertificate(inputStream) : readDERCertificate(new ASN1InputStream(inputStream));
    } catch (Exception exception) {
      throw new ExCertificateException("parsing issue: " + exception.getMessage(), exception);
    } 
  }
  
  public Collection engineGenerateCertificates(InputStream paramInputStream) throws CertificateException {
    BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
    ArrayList<Certificate> arrayList = new ArrayList();
    Certificate certificate;
    while ((certificate = engineGenerateCertificate(bufferedInputStream)) != null)
      arrayList.add(certificate); 
    return arrayList;
  }
  
  public CRL engineGenerateCRL(InputStream paramInputStream) throws CRLException {
    if (this.currentCrlStream == null) {
      this.currentCrlStream = paramInputStream;
      this.sCrlData = null;
      this.sCrlDataObjectCount = 0;
    } else if (this.currentCrlStream != paramInputStream) {
      this.currentCrlStream = paramInputStream;
      this.sCrlData = null;
      this.sCrlDataObjectCount = 0;
    } 
    try {
      InputStream inputStream;
      if (this.sCrlData != null) {
        if (this.sCrlDataObjectCount != this.sCrlData.size())
          return getCRL(); 
        this.sCrlData = null;
        this.sCrlDataObjectCount = 0;
        return null;
      } 
      if (paramInputStream.markSupported()) {
        inputStream = paramInputStream;
      } else {
        inputStream = new ByteArrayInputStream(Streams.readAll(paramInputStream));
      } 
      inputStream.mark(1);
      int i = inputStream.read();
      if (i == -1)
        return null; 
      inputStream.reset();
      return (i != 48) ? readPEMCRL(inputStream) : readDERCRL(new ASN1InputStream(inputStream, true));
    } catch (CRLException cRLException) {
      throw cRLException;
    } catch (Exception exception) {
      throw new CRLException(exception.toString());
    } 
  }
  
  public Collection engineGenerateCRLs(InputStream paramInputStream) throws CRLException {
    ArrayList<CRL> arrayList = new ArrayList();
    BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
    CRL cRL;
    while ((cRL = engineGenerateCRL(bufferedInputStream)) != null)
      arrayList.add(cRL); 
    return arrayList;
  }
  
  public Iterator engineGetCertPathEncodings() {
    return PKIXCertPath.certPathEncodings.iterator();
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream) throws CertificateException {
    return engineGenerateCertPath(paramInputStream, "PkiPath");
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString) throws CertificateException {
    return new PKIXCertPath(paramInputStream, paramString);
  }
  
  public CertPath engineGenerateCertPath(List paramList) throws CertificateException {
    for (Object object : paramList) {
      if (object != null && !(object instanceof java.security.cert.X509Certificate))
        throw new CertificateException("list contains non X509Certificate object while creating CertPath\n" + object.toString()); 
    } 
    return new PKIXCertPath(paramList);
  }
  
  private class ExCertificateException extends CertificateException {
    private Throwable cause;
    
    public ExCertificateException(Throwable param1Throwable) {
      this.cause = param1Throwable;
    }
    
    public ExCertificateException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
