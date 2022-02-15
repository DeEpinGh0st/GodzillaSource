package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaSimpleSignerInfoVerifierBuilder {
  private Helper helper = new Helper();
  
  public JcaSimpleSignerInfoVerifierBuilder setProvider(Provider paramProvider) {
    this.helper = new ProviderHelper(paramProvider);
    return this;
  }
  
  public JcaSimpleSignerInfoVerifierBuilder setProvider(String paramString) {
    this.helper = new NamedHelper(paramString);
    return this;
  }
  
  public SignerInformationVerifier build(X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException, CertificateException {
    return new SignerInformationVerifier((CMSSignatureAlgorithmNameGenerator)new DefaultCMSSignatureAlgorithmNameGenerator(), (SignatureAlgorithmIdentifierFinder)new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(paramX509CertificateHolder), this.helper.createDigestCalculatorProvider());
  }
  
  public SignerInformationVerifier build(X509Certificate paramX509Certificate) throws OperatorCreationException {
    return new SignerInformationVerifier((CMSSignatureAlgorithmNameGenerator)new DefaultCMSSignatureAlgorithmNameGenerator(), (SignatureAlgorithmIdentifierFinder)new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(paramX509Certificate), this.helper.createDigestCalculatorProvider());
  }
  
  public SignerInformationVerifier build(PublicKey paramPublicKey) throws OperatorCreationException {
    return new SignerInformationVerifier((CMSSignatureAlgorithmNameGenerator)new DefaultCMSSignatureAlgorithmNameGenerator(), (SignatureAlgorithmIdentifierFinder)new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(paramPublicKey), this.helper.createDigestCalculatorProvider());
  }
  
  private class Helper {
    private Helper() {}
    
    ContentVerifierProvider createContentVerifierProvider(PublicKey param1PublicKey) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).build(param1PublicKey);
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509Certificate param1X509Certificate) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).build(param1X509Certificate);
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder param1X509CertificateHolder) throws OperatorCreationException, CertificateException {
      return (new JcaContentVerifierProviderBuilder()).build(param1X509CertificateHolder);
    }
    
    DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
      return (new JcaDigestCalculatorProviderBuilder()).build();
    }
  }
  
  private class NamedHelper extends Helper {
    private final String providerName;
    
    public NamedHelper(String param1String) {
      this.providerName = param1String;
    }
    
    ContentVerifierProvider createContentVerifierProvider(PublicKey param1PublicKey) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.providerName).build(param1PublicKey);
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509Certificate param1X509Certificate) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.providerName).build(param1X509Certificate);
    }
    
    DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
      return (new JcaDigestCalculatorProviderBuilder()).setProvider(this.providerName).build();
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder param1X509CertificateHolder) throws OperatorCreationException, CertificateException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.providerName).build(param1X509CertificateHolder);
    }
  }
  
  private class ProviderHelper extends Helper {
    private final Provider provider;
    
    public ProviderHelper(Provider param1Provider) {
      this.provider = param1Provider;
    }
    
    ContentVerifierProvider createContentVerifierProvider(PublicKey param1PublicKey) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.provider).build(param1PublicKey);
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509Certificate param1X509Certificate) throws OperatorCreationException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.provider).build(param1X509Certificate);
    }
    
    DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
      return (new JcaDigestCalculatorProviderBuilder()).setProvider(this.provider).build();
    }
    
    ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder param1X509CertificateHolder) throws OperatorCreationException, CertificateException {
      return (new JcaContentVerifierProviderBuilder()).setProvider(this.provider).build(param1X509CertificateHolder);
    }
  }
}
