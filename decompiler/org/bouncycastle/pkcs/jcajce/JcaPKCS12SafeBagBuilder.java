package org.bouncycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCSIOException;

public class JcaPKCS12SafeBagBuilder extends PKCS12SafeBagBuilder {
  public JcaPKCS12SafeBagBuilder(X509Certificate paramX509Certificate) throws IOException {
    super(convertCert(paramX509Certificate));
  }
  
  private static Certificate convertCert(X509Certificate paramX509Certificate) throws IOException {
    try {
      return Certificate.getInstance(paramX509Certificate.getEncoded());
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new PKCSIOException("cannot encode certificate: " + certificateEncodingException.getMessage(), certificateEncodingException);
    } 
  }
  
  public JcaPKCS12SafeBagBuilder(PrivateKey paramPrivateKey, OutputEncryptor paramOutputEncryptor) {
    super(PrivateKeyInfo.getInstance(paramPrivateKey.getEncoded()), paramOutputEncryptor);
  }
  
  public JcaPKCS12SafeBagBuilder(PrivateKey paramPrivateKey) {
    super(PrivateKeyInfo.getInstance(paramPrivateKey.getEncoded()));
  }
}
