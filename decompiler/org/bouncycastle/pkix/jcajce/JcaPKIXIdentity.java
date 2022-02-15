package org.bouncycastle.pkix.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.pkix.PKIXIdentity;

public class JcaPKIXIdentity extends PKIXIdentity {
  private final PrivateKey privKey;
  
  private final X509Certificate[] certs;
  
  private static PrivateKeyInfo getPrivateKeyInfo(PrivateKey paramPrivateKey) {
    try {
      return PrivateKeyInfo.getInstance(paramPrivateKey.getEncoded());
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private static X509CertificateHolder[] getCertificates(X509Certificate[] paramArrayOfX509Certificate) {
    X509CertificateHolder[] arrayOfX509CertificateHolder = new X509CertificateHolder[paramArrayOfX509Certificate.length];
    try {
      for (byte b = 0; b != arrayOfX509CertificateHolder.length; b++)
        arrayOfX509CertificateHolder[b] = (X509CertificateHolder)new JcaX509CertificateHolder(paramArrayOfX509Certificate[b]); 
      return arrayOfX509CertificateHolder;
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new IllegalArgumentException("Unable to process certificates: " + certificateEncodingException.getMessage());
    } 
  }
  
  public JcaPKIXIdentity(PrivateKey paramPrivateKey, X509Certificate[] paramArrayOfX509Certificate) {
    super(getPrivateKeyInfo(paramPrivateKey), getCertificates(paramArrayOfX509Certificate));
    this.privKey = paramPrivateKey;
    this.certs = new X509Certificate[paramArrayOfX509Certificate.length];
    System.arraycopy(paramArrayOfX509Certificate, 0, this.certs, 0, paramArrayOfX509Certificate.length);
  }
  
  public PrivateKey getPrivateKey() {
    return this.privKey;
  }
  
  public X509Certificate getX509Certificate() {
    return this.certs[0];
  }
}
