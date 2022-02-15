package org.bouncycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.x509.X509StreamParserSpi;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509CertParser extends X509StreamParserSpi {
  private static final PEMUtil PEM_PARSER = new PEMUtil("CERTIFICATE");
  
  private ASN1Set sData = null;
  
  private int sDataObjectCount = 0;
  
  private InputStream currentStream = null;
  
  private Certificate readDERCertificate(InputStream paramInputStream) throws IOException, CertificateParsingException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramInputStream);
    ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
    if (aSN1Sequence.size() > 1 && aSN1Sequence.getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier && aSN1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
      this.sData = (new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(1), true))).getCertificates();
      return getCertificate();
    } 
    return new X509CertificateObject(Certificate.getInstance(aSN1Sequence));
  }
  
  private Certificate getCertificate() throws CertificateParsingException {
    if (this.sData != null)
      while (this.sDataObjectCount < this.sData.size()) {
        ASN1Encodable aSN1Encodable = this.sData.getObjectAt(this.sDataObjectCount++);
        if (aSN1Encodable instanceof ASN1Sequence)
          return new X509CertificateObject(Certificate.getInstance(aSN1Encodable)); 
      }  
    return null;
  }
  
  private Certificate readPEMCertificate(InputStream paramInputStream) throws IOException, CertificateParsingException {
    ASN1Sequence aSN1Sequence = PEM_PARSER.readPEMObject(paramInputStream);
    return (aSN1Sequence != null) ? new X509CertificateObject(Certificate.getInstance(aSN1Sequence)) : null;
  }
  
  public void engineInit(InputStream paramInputStream) {
    this.currentStream = paramInputStream;
    this.sData = null;
    this.sDataObjectCount = 0;
    if (!this.currentStream.markSupported())
      this.currentStream = new BufferedInputStream(this.currentStream); 
  }
  
  public Object engineRead() throws StreamParsingException {
    try {
      if (this.sData != null) {
        if (this.sDataObjectCount != this.sData.size())
          return getCertificate(); 
        this.sData = null;
        this.sDataObjectCount = 0;
        return null;
      } 
      this.currentStream.mark(10);
      int i = this.currentStream.read();
      if (i == -1)
        return null; 
      if (i != 48) {
        this.currentStream.reset();
        return readPEMCertificate(this.currentStream);
      } 
      this.currentStream.reset();
      return readDERCertificate(this.currentStream);
    } catch (Exception exception) {
      throw new StreamParsingException(exception.toString(), exception);
    } 
  }
  
  public Collection engineReadAll() throws StreamParsingException {
    ArrayList<Certificate> arrayList = new ArrayList();
    Certificate certificate;
    while ((certificate = (Certificate)engineRead()) != null)
      arrayList.add(certificate); 
    return arrayList;
  }
}
