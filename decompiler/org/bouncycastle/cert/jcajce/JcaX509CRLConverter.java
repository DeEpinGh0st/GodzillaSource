package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import org.bouncycastle.cert.X509CRLHolder;

public class JcaX509CRLConverter {
  private CertHelper helper = new DefaultCertHelper();
  
  public JcaX509CRLConverter() {
    this.helper = new DefaultCertHelper();
  }
  
  public JcaX509CRLConverter setProvider(Provider paramProvider) {
    this.helper = new ProviderCertHelper(paramProvider);
    return this;
  }
  
  public JcaX509CRLConverter setProvider(String paramString) {
    this.helper = new NamedCertHelper(paramString);
    return this;
  }
  
  public X509CRL getCRL(X509CRLHolder paramX509CRLHolder) throws CRLException {
    try {
      CertificateFactory certificateFactory = this.helper.getCertificateFactory("X.509");
      return (X509CRL)certificateFactory.generateCRL(new ByteArrayInputStream(paramX509CRLHolder.getEncoded()));
    } catch (IOException iOException) {
      throw new ExCRLException("exception parsing certificate: " + iOException.getMessage(), iOException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new ExCRLException("cannot find required provider:" + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (CertificateException certificateException) {
      throw new ExCRLException("cannot create factory: " + certificateException.getMessage(), certificateException);
    } 
  }
  
  private class ExCRLException extends CRLException {
    private Throwable cause;
    
    public ExCRLException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
