package org.bouncycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509StreamParserSpi;
import org.bouncycastle.x509.X509V2AttributeCertificate;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509AttrCertParser extends X509StreamParserSpi {
  private static final PEMUtil PEM_PARSER = new PEMUtil("ATTRIBUTE CERTIFICATE");
  
  private ASN1Set sData = null;
  
  private int sDataObjectCount = 0;
  
  private InputStream currentStream = null;
  
  private X509AttributeCertificate readDERCertificate(InputStream paramInputStream) throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramInputStream);
    ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
    if (aSN1Sequence.size() > 1 && aSN1Sequence.getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier && aSN1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
      this.sData = (new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(1), true))).getCertificates();
      return getCertificate();
    } 
    return (X509AttributeCertificate)new X509V2AttributeCertificate(aSN1Sequence.getEncoded());
  }
  
  private X509AttributeCertificate getCertificate() throws IOException {
    if (this.sData != null)
      while (this.sDataObjectCount < this.sData.size()) {
        ASN1Encodable aSN1Encodable = this.sData.getObjectAt(this.sDataObjectCount++);
        if (aSN1Encodable instanceof ASN1TaggedObject && ((ASN1TaggedObject)aSN1Encodable).getTagNo() == 2)
          return (X509AttributeCertificate)new X509V2AttributeCertificate(ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Encodable, false).getEncoded()); 
      }  
    return null;
  }
  
  private X509AttributeCertificate readPEMCertificate(InputStream paramInputStream) throws IOException {
    ASN1Sequence aSN1Sequence = PEM_PARSER.readPEMObject(paramInputStream);
    return (X509AttributeCertificate)((aSN1Sequence != null) ? new X509V2AttributeCertificate(aSN1Sequence.getEncoded()) : null);
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
    ArrayList<X509AttributeCertificate> arrayList = new ArrayList();
    X509AttributeCertificate x509AttributeCertificate;
    while ((x509AttributeCertificate = (X509AttributeCertificate)engineRead()) != null)
      arrayList.add(x509AttributeCertificate); 
    return arrayList;
  }
}
