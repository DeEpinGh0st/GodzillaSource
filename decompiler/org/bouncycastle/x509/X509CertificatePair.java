package org.bouncycastle.x509;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.X509CertificateObject;

public class X509CertificatePair {
  private final JcaJceHelper bcHelper = (JcaJceHelper)new BCJcaJceHelper();
  
  private X509Certificate forward;
  
  private X509Certificate reverse;
  
  public X509CertificatePair(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
    this.forward = paramX509Certificate1;
    this.reverse = paramX509Certificate2;
  }
  
  public X509CertificatePair(CertificatePair paramCertificatePair) throws CertificateParsingException {
    if (paramCertificatePair.getForward() != null)
      this.forward = (X509Certificate)new X509CertificateObject(paramCertificatePair.getForward()); 
    if (paramCertificatePair.getReverse() != null)
      this.reverse = (X509Certificate)new X509CertificateObject(paramCertificatePair.getReverse()); 
  }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    Certificate certificate1 = null;
    Certificate certificate2 = null;
    try {
      if (this.forward != null) {
        certificate1 = Certificate.getInstance((new ASN1InputStream(this.forward.getEncoded())).readObject());
        if (certificate1 == null)
          throw new CertificateEncodingException("unable to get encoding for forward"); 
      } 
      if (this.reverse != null) {
        certificate2 = Certificate.getInstance((new ASN1InputStream(this.reverse.getEncoded())).readObject());
        if (certificate2 == null)
          throw new CertificateEncodingException("unable to get encoding for reverse"); 
      } 
      return (new CertificatePair(certificate1, certificate2)).getEncoded("DER");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ExtCertificateEncodingException(illegalArgumentException.toString(), illegalArgumentException);
    } catch (IOException iOException) {
      throw new ExtCertificateEncodingException(iOException.toString(), iOException);
    } 
  }
  
  public X509Certificate getForward() {
    return this.forward;
  }
  
  public X509Certificate getReverse() {
    return this.reverse;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof X509CertificatePair))
      return false; 
    X509CertificatePair x509CertificatePair = (X509CertificatePair)paramObject;
    boolean bool1 = true;
    boolean bool2 = true;
    if (this.forward != null) {
      bool2 = this.forward.equals(x509CertificatePair.forward);
    } else if (x509CertificatePair.forward != null) {
      bool2 = false;
    } 
    if (this.reverse != null) {
      bool1 = this.reverse.equals(x509CertificatePair.reverse);
    } else if (x509CertificatePair.reverse != null) {
      bool1 = false;
    } 
    return (bool2 && bool1);
  }
  
  public int hashCode() {
    int i = -1;
    if (this.forward != null)
      i ^= this.forward.hashCode(); 
    if (this.reverse != null) {
      i *= 17;
      i ^= this.reverse.hashCode();
    } 
    return i;
  }
}
