package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;

public class JcaX509CertificateConverter {
  private CertHelper helper = new DefaultCertHelper();
  
  public JcaX509CertificateConverter() {
    this.helper = new DefaultCertHelper();
  }
  
  public JcaX509CertificateConverter setProvider(Provider paramProvider) {
    this.helper = new ProviderCertHelper(paramProvider);
    return this;
  }
  
  public JcaX509CertificateConverter setProvider(String paramString) {
    this.helper = new NamedCertHelper(paramString);
    return this;
  }
  
  public X509Certificate getCertificate(X509CertificateHolder paramX509CertificateHolder) throws CertificateException {
    try {
      CertificateFactory certificateFactory = this.helper.getCertificateFactory("X.509");
      return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(paramX509CertificateHolder.getEncoded()));
    } catch (IOException iOException) {
      throw new ExCertificateParsingException("exception parsing certificate: " + iOException.getMessage(), iOException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new ExCertificateException("cannot find required provider:" + noSuchProviderException.getMessage(), noSuchProviderException);
    } 
  }
  
  private class ExCertificateException extends CertificateException {
    private Throwable cause;
    
    public ExCertificateException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
  
  private class ExCertificateParsingException extends CertificateParsingException {
    private Throwable cause;
    
    public ExCertificateParsingException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
