package org.bouncycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.x509.X509CertificatePair;
import org.bouncycastle.x509.X509StreamParserSpi;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509CertPairParser extends X509StreamParserSpi {
  private InputStream currentStream = null;
  
  private X509CertificatePair readDERCrossCertificatePair(InputStream paramInputStream) throws IOException, CertificateParsingException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramInputStream);
    ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
    CertificatePair certificatePair = CertificatePair.getInstance(aSN1Sequence);
    return new X509CertificatePair(certificatePair);
  }
  
  public void engineInit(InputStream paramInputStream) {
    this.currentStream = paramInputStream;
    if (!this.currentStream.markSupported())
      this.currentStream = new BufferedInputStream(this.currentStream); 
  }
  
  public Object engineRead() throws StreamParsingException {
    try {
      this.currentStream.mark(10);
      int i = this.currentStream.read();
      if (i == -1)
        return null; 
      this.currentStream.reset();
      return readDERCrossCertificatePair(this.currentStream);
    } catch (Exception exception) {
      throw new StreamParsingException(exception.toString(), exception);
    } 
  }
  
  public Collection engineReadAll() throws StreamParsingException {
    ArrayList<X509CertificatePair> arrayList = new ArrayList();
    X509CertificatePair x509CertificatePair;
    while ((x509CertificatePair = (X509CertificatePair)engineRead()) != null)
      arrayList.add(x509CertificatePair); 
    return arrayList;
  }
}
