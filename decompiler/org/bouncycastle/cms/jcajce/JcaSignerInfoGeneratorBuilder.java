package org.bouncycastle.cms.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaSignerInfoGeneratorBuilder {
  private SignerInfoGeneratorBuilder builder;
  
  public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider paramDigestCalculatorProvider) {
    this(paramDigestCalculatorProvider, (CMSSignatureEncryptionAlgorithmFinder)new DefaultCMSSignatureEncryptionAlgorithmFinder());
  }
  
  public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider paramDigestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder paramCMSSignatureEncryptionAlgorithmFinder) {
    this.builder = new SignerInfoGeneratorBuilder(paramDigestCalculatorProvider, paramCMSSignatureEncryptionAlgorithmFinder);
  }
  
  public JcaSignerInfoGeneratorBuilder setDirectSignature(boolean paramBoolean) {
    this.builder.setDirectSignature(paramBoolean);
    return this;
  }
  
  public JcaSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator paramCMSAttributeTableGenerator) {
    this.builder.setSignedAttributeGenerator(paramCMSAttributeTableGenerator);
    return this;
  }
  
  public JcaSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator paramCMSAttributeTableGenerator) {
    this.builder.setUnsignedAttributeGenerator(paramCMSAttributeTableGenerator);
    return this;
  }
  
  public SignerInfoGenerator build(ContentSigner paramContentSigner, X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException {
    return this.builder.build(paramContentSigner, paramX509CertificateHolder);
  }
  
  public SignerInfoGenerator build(ContentSigner paramContentSigner, byte[] paramArrayOfbyte) throws OperatorCreationException {
    return this.builder.build(paramContentSigner, paramArrayOfbyte);
  }
  
  public SignerInfoGenerator build(ContentSigner paramContentSigner, X509Certificate paramX509Certificate) throws OperatorCreationException, CertificateEncodingException {
    return build(paramContentSigner, (X509CertificateHolder)new JcaX509CertificateHolder(paramX509Certificate));
  }
}
