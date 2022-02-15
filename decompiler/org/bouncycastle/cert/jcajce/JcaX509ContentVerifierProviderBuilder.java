package org.bouncycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class JcaX509ContentVerifierProviderBuilder implements X509ContentVerifierProviderBuilder {
  private JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();
  
  public JcaX509ContentVerifierProviderBuilder setProvider(Provider paramProvider) {
    this.builder.setProvider(paramProvider);
    return this;
  }
  
  public JcaX509ContentVerifierProviderBuilder setProvider(String paramString) {
    this.builder.setProvider(paramString);
    return this;
  }
  
  public ContentVerifierProvider build(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws OperatorCreationException {
    return this.builder.build(paramSubjectPublicKeyInfo);
  }
  
  public ContentVerifierProvider build(X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException {
    try {
      return this.builder.build(paramX509CertificateHolder);
    } catch (CertificateException certificateException) {
      throw new OperatorCreationException("Unable to process certificate: " + certificateException.getMessage(), certificateException);
    } 
  }
}
